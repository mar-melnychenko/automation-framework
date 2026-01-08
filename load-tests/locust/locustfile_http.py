import logging
import random
import re
from urllib.parse import quote_plus

from locust import HttpUser, task, between, tag

log = logging.getLogger(__name__)

SEARCH_SEEDS = ["iphone", "laptop", "samsung", "muzik", "kulaklik", "kitap", "parfum", "ayakkabi"]

SEARCH_COUNT_RE = re.compile(r"\|\s*([\d.]+)\s*ürün", re.IGNORECASE)

def extract_products_count(html: str) -> int | None:
    m = SEARCH_COUNT_RE.search(html or "")
    if not m:
        return None
    digits = m.group(1).replace(".", "")
    try:
        return int(digits)
    except ValueError:
        return None

class N11HttpUser(HttpUser):
    wait_time = between(0.5, 2.0)

    default_headers = {
        "User-Agent": (
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            "AppleWebKit/537.36 (KHTML, like Gecko) "
            "Chrome/121.0.0.0 Safari/537.36"
        ),
        "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "Accept-Language": "tr-TR,tr;q=0.9,en-US;q=0.8,en;q=0.7",
        "Referer": "https://www.n11.com/",
        "Connection": "keep-alive",
    }

    api_headers = {
        **default_headers,
        "Accept": "application/json, text/plain, */*",
        "Sec-Fetch-Site": "same-origin",
        "Sec-Fetch-Mode": "cors",
        "Sec-Fetch-Dest": "empty",
    }

    def _full_url(self, path: str) -> str:
        return f"{self.host}{path}"

    def _log_non_200(self, resp, name: str, path: str):
        full = self._full_url(path)
        cf_ray = resp.headers.get("CF-RAY")
        server = resp.headers.get("Server")
        snippet = (resp.text or "")[:250].replace("\n", " ").replace("\r", " ")
        msg = f"{name} -> {resp.status_code} for {full}. Server={server} CF-RAY={cf_ray}. Body[0:250]: {snippet}"
        resp.failure(msg)
        log.warning(msg)

    @tag("home")
    @task(1)
    def home(self):
        path = "/"
        with self.client.get(path, name="HOME /", headers=self.default_headers, catch_response=True) as resp:
            if resp.status_code != 200:
                self._log_non_200(resp, "HOME /", path)
                return
            resp.success()

    @tag("autocomplete")
    @task(5)
    def autocomplete(self):
        seed = random.choice(SEARCH_SEEDS)
        q = quote_plus(seed)
        path = f"/rest/v1/searchAutoCompleteService?q={q}&state=f"

        with self.client.get(path, name="API autocomplete", headers=self.api_headers, catch_response=True) as resp:
            if resp.status_code != 200:
                self._log_non_200(resp, "API autocomplete", path)
                return
            resp.success()

    @tag("results")
    @task(5)
    def results_listing(self):
        seed = random.choice(SEARCH_SEEDS)
        q = quote_plus(seed)
        path = f"/arama?q={q}"

        with self.client.get(path, name="UI results /arama?q=...", headers=self.default_headers, catch_response=True) as resp:
            if resp.status_code != 200:
                self._log_non_200(resp, "UI results", path)
                return

            count = extract_products_count(resp.text or "")
            if count is None:
                resp.failure(f"No '| <N> ürün' marker found for {self._full_url(path)}")
                return

            resp.success()
