package com.samplemaven;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class SignInTest {

    WebDriver driver;
    WebDriverWait wait;
    SoftAssert a = new SoftAssert();

    Properties prop = new Properties();
    FileInputStream fs = new FileInputStream("./configuration.properties");

    public SignInTest() throws FileNotFoundException {
    }

    @BeforeSuite
    public void driverInitialize() throws IOException {

        prop.load(fs);
        String driverType = prop.getProperty("browser");
        String website = prop.getProperty("url");

        if(driverType.equalsIgnoreCase("chrome"))
        {
            //setting chrome driver path
            System.setProperty("webdriver.chrome.driver","./Driver/chromedriver.exe");
            //initializing driver with Chrome Driver
            driver = new ChromeDriver();
        }

        else if(driverType.equalsIgnoreCase("firefox"))
        {
            //setting gecko driver path
            System.setProperty("webdriver.firefox.driver","./Driver/geckodriver.exe");
            //initializing driver with Firefox Driver
            driver = new FirefoxDriver();
        }

        //initializing wait
        wait = new WebDriverWait(driver, 5);

        driver.manage().window().maximize();
        driver.get(website);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username-input")));

        //disabling notifications
        ChromeOptions co = new ChromeOptions();
        co.addArguments("--disable-notifications");

    }

    @Test(dataProvider = "getData")
    public void signIn(String username, String password) throws IOException {
        //input username and password
        driver.findElement(By.id("username-input")).sendKeys(username);
        driver.findElement(By.id("password-input")).sendKeys(password);
        driver.findElement(By.xpath("//div[@class = 'tb-action-button']/button")).click();

        //scrolling for error message
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.querySelector('mat-card.mat-card.mat-focus-indicator').scrollTop = 50");

        //taking screenshot of error message
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(src,new File("C:\\Shazeen\\authFail.png"));

        //Verify error message
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tb-snack-bar-component[class *= 'ng-tns-c165-']")));
        a.assertEquals(driver.findElement(By.cssSelector("tb-snack-bar-component[class *= 'ng-tns-c165-']  div div")).getText(),"Authentication failed");
        driver.findElement(By.cssSelector("tb-snack-bar-component[class *= 'ng-tns-c165-'] button")).click();

        //clearing input fields
        driver.findElement(By.id("username-input")).clear();
        driver.findElement(By.id("password-input")).clear();

        //displaying assert
        a.assertAll();
    }

    @Test(priority=1)
    public void forgotPassword() throws IOException {
        //navigate to Forgot Password screen
        driver.findElement(By.cssSelector("button[class*='tb-reset-password']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("mat-headline")));
        a.assertEquals(driver.findElement(By.className("mat-headline")).getText(), "Request Password Reset");

        //sending input to email field
        driver.findElement(By.cssSelector("input[id*='mat-input']")).sendKeys("abs");
        driver.findElement(By.cssSelector("button[type ='submit']")).click();

        //verifying error message
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-error[id*='mat-error']")));
        a.assertEquals(driver.findElement(By.cssSelector("mat-error[id*='mat-error']")).getText(), "Invalid email format.");

        //Taking screenshot
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(src, new File("C://Shazeen//forgotPasswordError.png"));

        //navigate back to sign in and verifying Login text
        driver.findElement(By.cssSelector("button[class *= 'mat-primary']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span[class='tb-login-message']")));
        a.assertEquals(driver.findElement(By.cssSelector("span[class='tb-login-message']")).getText(),"Log in to see ThingsBoard in action.");
        a.assertAll();
    }

    @AfterTest
    public void driverQuit()
    {
        //deleting cookies
        driver.manage().deleteAllCookies();

        //closing driver
        driver.quit();
    }

    @DataProvider
    public Object[][] getData()
    {
        Object[][] data = new Object[3][2];
        //first iteration
        data[0][0] = "shaz@dfsd.com";
        data[0][1] = "mah";

        //second iteration
        data[1][0] = "asb@sdfs.com";
        data[1][1] = "fss";

        //third iteration
        data[2][0] = "the@te.com";
        data[2][1]  = "wer";

        return data;
    }


}