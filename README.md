# automation-framework
- **UI automation**: Java 21, Selenium, TestNG, Allure
- **API automation**: RestAssured + Jackson + Lombok, TestNG, Allure
- **Load**: Locust.io (Python). There are two files with tests:
    - Browser-based сценарій через Selenium (1 user, без реального навантаження)
    - [locustfile_http.py](load-tests/locust/locustfile_http.py) - contains HTTP requests. This tests will fail on local machine because of 403 response code (looks like requests are block by Cloudflare or something like that). To make tests work the machine should be whitelisted
    - [locustfile_browser.py](load-tests/locust/locustfile_browser.py) - uses real browser and interacts with UI elements using Selenium
    - Tests can be run using the command `locust -f load-tests/locust/$test_file.py` and open URL: http://localhost:8089/ where host should be added (https://www.n11.com)

## 1) Prerequisites

### UI/API tests
- **Java 21**
- **Maven 3.8+**
- **Google Chrome**
- **Mozilla Firefox**
- **Allure CLI** for reporting. Report will be generated to target/allure-results folder and HTML view can be created using `mvn allure:serve` command

### Load tests
- **Python**
- **Locust.io**
- **pip**

## 2) Configuration

All supported env variables are described in [ConfigProps.java](src/main/java/org/example/config/ConfigProps.java). They can be used as propert to run tests: `mvn clean test -Dbrowser=chrome`
### Important: you need to add **-Denv=test** parameter to mark which env file you want to use, this parameter is required for test run