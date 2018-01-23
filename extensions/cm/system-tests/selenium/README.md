# cm-selenium-test
**Running Locally:**

mvn clean verify -Pchrome -Dcucumber.options="--tags @smoketest" -Dfailsafe.fork.count="1" 

**Running individual scenario locally:**

* Ensure IntelliJ default Cucumber Java plugin/Glue is set to: com.elasticpath.cucumber.definitions
* Since the browser drivers live in target/webdriver/binaries, you need to build system-tests/selenium first:

    mvn clean install -DskipAllTests

Then you can right click on the scenario in cucumber.feature file, to just run the single test.

**Running remotely:**

mvn clean verify -Premote,chrome -Dcucumber.options="--tags @smoketest" -Dfailsafe.fork.count="1" -Dremote.web.driver.url="<REMOTE DRIVER IP>"
Example: remote.web.driver.url="http://10.10.2.113:4444/wd/hub"

*Updating Browser Driver Versions*
* You can download the latest browser driver from web. e.g. chromedriver.
* Update the RepositoryMap.xml for the driver version.
* Has value can be found locally if you run following in bash command locally.
```
openssl sha1 <filename>
```
* Example: https://github.com/Ardesco/Selenium-Maven-Template/blob/master/src/test/resources/RepositoryMap.xml