package demo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases {
    ChromeDriver driver;

    /*
     * TODO: Write your tests here with testng @Test annotation.
     * Follow `testCase01` `testCase02`... format or what is provided in
     * instructions
     */

    /*
     * Do not change the provided methods unless necessary, they will help in
     * automation and assessment
     */
    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");
        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();
        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");
        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @Test
    public void testCase01() throws InterruptedException, IOException {
        System.out.println("TestCase01:Started");
        Wrappers homePage = new Wrappers(driver);
        homePage.navigateToScrape();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement hockeyPagination = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[normalize-space()='Hockey Teams: Forms, Searching and Pagination']")));
        homePage.clickOn(hockeyPagination);
        Thread.sleep(2000);
        // Initialize an ArrayList to store team data
        List<Map<String, Object>> teamData = new ArrayList<>();

        for (int page = 0; page < 4; page++) {
            List<WebElement> rows = driver.findElements(By.xpath("//table[@class='table']/tbody/tr"));

            // Iterate through table rows
            for (int i = 1; i < rows.size(); i++) {
                WebElement row = rows.get(i);
                List<WebElement> cells = row.findElements(By.tagName("td"));

                // Extract data from table cells
                String teamName = cells.get(0).getText();
                String year = cells.get(1).getText();
                String winPercentage = cells.get(2).getText();

                // Convert winPercentage to a double
                double winPercentageDouble = Double.parseDouble(winPercentage.replace("%", ""));

                // Check if win percentage is less than 40%
                if (winPercentageDouble < 40.0) {
                    // Create a HashMap to store team data
                    Map<String, Object> teamInfo = new HashMap<>();
                    teamInfo.put("Epoch Time of Scrape", System.currentTimeMillis());
                    teamInfo.put("Team Name", teamName);
                    teamInfo.put("Year", year);
                    teamInfo.put("Win %", winPercentage);

                    // Add the team data to the ArrayList
                    teamData.add(teamInfo);
                }
            }
            // Click the next page button
            WebElement nextButton = driver
                    .findElement(By.xpath("//div[@class='row pagination-area']/div/ul/li/a[@aria-label='Next']"));
            homePage.clickOn(nextButton);
             // Add a small delay to allow the page to load
            Thread.sleep(2000);

        }
        // Convert the ArrayList to JSON and store it in a file
        File file = homePage.convetArrayListIntoJsonFile(teamData, "hockey-team-data.json");
        // Assert that the file is present and not empty
        Assert.assertTrue(file.exists());
        Assert.assertTrue(file.length() > 0);
        System.out.println("JSON file created successfully!");
        System.out.println("TestCase01:End");

    }

    @Test
    public void testCase02() throws IOException, InterruptedException {
        System.out.println("TestCase02:Started");
        Wrappers homePage = new Wrappers(driver);
        homePage.navigateToScrape();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement oskarPagination = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[normalize-space()='Oscar Winning Films: AJAX and Javascript']")));
        homePage.clickOn(oskarPagination);
        Thread.sleep(2000);

        List<Map<String, Object>> oscarData = new ArrayList<>();

        // Get the list of years
        List<WebElement> yearElements = driver.findElements(By.xpath("//section[@id='oscars']/div/div[4]/div/a"));
        for (WebElement yearElement : yearElements) {
            String year = yearElement.getText();
            homePage.clickOn(yearElement);
            Thread.sleep(5000);

            // Get the top 5 movies for the year
            List<WebElement> movieElements = driver
                    .findElements(By.xpath("//tr[@class='film']/td[@class='film-title']"));
            for (int i = 0; i < 5; i++) {
                WebElement movieElement = movieElements.get(i);
                String title = movieElement.getText();
                Thread.sleep(1000);
                String nomination = movieElement.findElement(By.xpath("./following-sibling::td[1]")).getText();
                Thread.sleep(1000);
                String awards = movieElement.findElement(By.xpath("./following-sibling::td[2]")).getText();
                Thread.sleep(1000);
                boolean isWinner = false;
                if (i == 0) {
                    isWinner = true;
                }
                Map<String, Object> movieData = new HashMap<>();
                movieData.put("Epoch Time of Scrape", System.currentTimeMillis());
                movieData.put("Year", year);
                movieData.put("Title", title);
                movieData.put("Nomination", nomination);
                movieData.put("Awards", awards);
                movieData.put("isWinner", isWinner);
                oscarData.add(movieData);
            }
            driver.navigate().back();
            Thread.sleep(1000);
        }

        // Convert the ArrayList to JSON and store it in a file
        File file = homePage.convetArrayListIntoJsonFile(oscarData, "oscar-winner-data.json");
        // Assert that the file is present and not empty
        Assert.assertTrue(file.exists());
        Assert.assertTrue(file.length() > 0);
        System.out.println("JSON file created successfully!");
        System.out.println("TestCase02:End");
    }

    @AfterTest
    public void endTest() {
        driver.close();
        driver.quit();
    }
}