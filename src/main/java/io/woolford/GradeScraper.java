package io.woolford;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Component
public class GradeScraper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ic.username}")
    String icUsername;

    @Value("${ic.password}")
    String icPassword;

    @Value("https://bvsd.infinitecampus.org/campus/resources/portal/grades?personID=${ic.person.id}")
    String icGradesUrl;

    @Autowired
    KafkaTemplate kafkaTemplate;

    // check 4x per day at 5 minutes and 37 seconds past the hour
    // this was done to avoid making the request when some other scheduled job puts load on the server
    @Scheduled(cron = "37 5 */6 * * *")
    private void scrapeGrades() throws JsonProcessingException {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1200");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        WebDriver driver = new ChromeDriver(options);

        // Try to login
        try {
            // Open BVSD Infinite Campus login page
            driver.get("https://bvsd.infinitecampus.org/campus/boulder.jsp");
            logger.info("Opened login page in browser");

            // Populate username/password, and click the sign-in button
            WebElement username = ((ChromeDriver) driver).findElementById("username");
            username.sendKeys(icUsername);

            WebElement password = ((ChromeDriver) driver).findElementById("password");
            password.sendKeys(icPassword);

            WebElement signInBtn = ((ChromeDriver) driver).findElementById("signinbtn");
            signInBtn.click();
            logger.info("Clicked sign in button");

        } catch (Exception e) {
            // Close browser and log error
            driver.close();
            logger.error("Unable to login: ", e.fillInStackTrace());
        }

        // get the grades JSON
        String responseJson = null;
        try {
            // Make a call to the URL that returns grades in JSON format.
            // This avoids having to parse the HTML.
            driver.get(icGradesUrl);
            logger.info("Retrieved grades URL");

            // Grab the JSON
            WebElement body = driver.findElement(By.tagName("body"));
            responseJson = body.getText();

        } catch (Exception e) {
            logger.error("Unable to get grades JSON: ", e.fillInStackTrace());
        } finally {
            // Close the browser
            driver.close();
            logger.info("Closed browser.");
        }

        // Parse the JSON into a list of grades
        GradeParser gradeParser = new GradeParser();
        List<GradeRecord> gradeRecordList = new ArrayList<GradeRecord>();

        try {
            gradeRecordList = gradeParser.parseGrades(responseJson);
            logger.info("Parsed grades");
        } catch (Exception e) {
            logger.error("Unable to parse grades: ", e.fillInStackTrace());
        }

        // Publish grades to Kafka
        for (GradeRecord gradeRecord : gradeRecordList){
            kafkaTemplate.send("miles-grades", gradeRecord);
        }
        logger.info("Grades published to Kafka");

    }

}
