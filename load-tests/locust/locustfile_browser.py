import logging
import random
import re
import time

from locust import User, task, between, events

from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support import expected_conditions as ec
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.common.action_chains import ActionChains

log = logging.getLogger(__name__)

BASE_URL = "https://www.n11.com/"
SEARCH_SEEDS = ["iphone", "laptop", "samsung", "muzik", "kulaklik", "kitap", "parfum", "ayakkabi"]

SEARCH_COUNT_RE = re.compile(r"\|\s*([\d.]+)\s*ürün", re.IGNORECASE)

SEARCH_INPUT_COLLAPSED = (By.XPATH, "//div[contains(@class,'searchWrapper')]//input")
SEARCH_INPUT_EXPANDED = (By.XPATH, "//div[@class='searchForm-inputArea']/input")

def extract_products_count(text: str) -> int | None:
    m = SEARCH_COUNT_RE.search(text or "")
    if not m:
        return None
    digits = m.group(1).replace(".", "")
    try:
        return int(digits)
    except ValueError:
        return None


class N11BrowserUser(User):
    wait_time = between(1, 2)

    def on_start(self):
        chrome_options = Options()
        chrome_options.add_argument("--no-sandbox")
        chrome_options.add_argument("--disable-dev-shm-usage")
        chrome_options.add_argument("--ignore-ssl-errors=yes")
        chrome_options.add_argument("--ignore-certificate-errors")
        chrome_options.add_argument("--disable-gpu")
        chrome_options.add_argument("--window-size=1920,1080")

        self.driver = webdriver.Chrome(options=chrome_options)
        self.wait = WebDriverWait(self.driver, 25)

        self.driver.get(BASE_URL)
        self._accept_cookies_if_present()

    def on_stop(self):
        try:
            self.driver.quit()
        except Exception:
            pass

    def _accept_cookies_if_present(self):
        accept = (
            By.XPATH,
            "//a[normalize-space()='Accept All' or contains(normalize-space(),'Accept All')]"
            "|//button[normalize-space()='Accept All' or contains(normalize-space(),'Accept All')]",
        )
        try:
            btn = WebDriverWait(self.driver, 5).until(ec.element_to_be_clickable(accept))
            btn.click()
            time.sleep(0.5)
        except Exception:
            pass

    def _debug_active(self, label: str):
        try:
            ae = self.driver.switch_to.active_element
            log.warning(f"[DEBUG] {label}: active_element outerHTML={ (ae.get_attribute('outerHTML') or '')[:200] }")
        except Exception:
            pass

    def _open_search_and_get_active_input(self):
        collapsed = self.wait.until(ec.visibility_of_element_located(SEARCH_INPUT_COLLAPSED))

        self.driver.execute_script("arguments[0].scrollIntoView({block:'center'});", collapsed)
        ActionChains(self.driver).move_to_element(collapsed).click().perform()

        expanded = self.wait.until(ec.visibility_of_element_located(SEARCH_INPUT_EXPANDED))
        self.wait.until(ec.element_to_be_clickable(SEARCH_INPUT_EXPANDED))

        ActionChains(self.driver).move_to_element(expanded).click().perform()
        self.driver.execute_script("arguments[0].focus();", expanded)
        self.wait.until(lambda d: d.switch_to.active_element == expanded)

        return expanded

    def _type_and_submit(self, query: str):
        inp = self._open_search_and_get_active_input()

        inp.send_keys(Keys.CONTROL, "a")
        inp.send_keys(Keys.BACKSPACE)

        inp.send_keys(query)
        self.wait.until(lambda d: (inp.get_attribute("value") or "").lower().startswith(query.lower()))

        inp.send_keys(Keys.ENTER)

    def _wait_results_url(self):
        self.wait.until(lambda d: "/arama" in d.current_url)

    def _wait_results_marker_in_input(self):
        def cond(_):
            vals = []
            for loc in (SEARCH_INPUT_EXPANDED, SEARCH_INPUT_COLLAPSED):
                els = self.driver.find_elements(*loc)
                if els:
                    v = (els[0].get_attribute("value") or "")
                    vals.append(v)
                    if SEARCH_COUNT_RE.search(v):
                        return True
            return False

        self.wait.until(cond)

    def _get_marker_text(self) -> str:
        for loc in (SEARCH_INPUT_EXPANDED, SEARCH_INPUT_COLLAPSED):
            els = self.driver.find_elements(*loc)
            if els:
                v = (els[0].get_attribute("value") or "")
                if SEARCH_COUNT_RE.search(v):
                    return v
        return ""

    @task
    def search_journey(self):
        query = random.choice(SEARCH_SEEDS)
        start = time.perf_counter()

        self.driver.get(BASE_URL)
        self._accept_cookies_if_present()

        try:
            self._type_and_submit(query)

            self._wait_results_url()
            self._wait_results_marker_in_input()

            marker_text = self._get_marker_text()
            count = extract_products_count(marker_text)

            duration_ms = int((time.perf_counter() - start) * 1000)

            if count is None:
                raise AssertionError(f"Nothing is found. marker_text='{marker_text}'")

            events.request.fire(
                request_type="BROWSER",
                name="search_journey",
                response_time=duration_ms,
                response_length=len(self.driver.page_source or ""),
                exception=None,
            )

        except Exception as e:
            duration_ms = int((time.perf_counter() - start) * 1000)
            events.request.fire(
                request_type="BROWSER",
                name="search_journey",
                response_time=duration_ms,
                response_length=len(self.driver.page_source or ""),
                exception=e,
            )
            log.exception(e)

        time.sleep(2)
