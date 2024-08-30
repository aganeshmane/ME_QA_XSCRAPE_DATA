package demo.wrappers;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */
    WebDriver driver;

    public Wrappers(WebDriver driver) {
        this.driver = driver;
    }

    public File convetArrayListIntoJsonFile(List<Map<String, Object>> teamData, String fileNAme) throws IOException {
        try {
            // Convert the ArrayList to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonData = objectMapper.writeValueAsString(teamData);

            // Write the JSON data to a file
            File file = new File("output/'" + fileNAme + "'");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonData);
            fileWriter.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void navigateToScrape() {
        driver.get("https://www.scrapethissite.com/pages/");
    }

    public void clickOn(WebElement element) {
        element.click();
    }
}
