package exercise1;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MojPierwszyTest {
    WebDriver driver;

//    @BeforeTest
//    public void setUp() throws MalformedURLException {
//        System.setProperty("webdriver.gecko.driver", "C:\\pliki\\geckodriver.exe");
//        System.setProperty("webdriver.chrome.driver", "C:\\pliki\\chromedriver.exe");
//        System.setProperty("webdriver.ie.driver", "C:\\pliki\\IEDriverServer.exe");
//        System.setProperty("webdriver.edge.driver", "C:\\pliki\\MicrosoftWebDriver.exe");
//        driver = new FirefoxDriver();
//        driver = new ChromeDriver();
//        driver = new InternetExplorerDriver();
//        driver = new EdgeDriver();

//        DesiredCapabilities chromeCaps = DesiredCapabilities.chrome();
//        chromeCaps.setCapability(CapabilityType.BROWSER_NAME, "chrome");
//        chromeCaps.setCapability(CapabilityType.VERSION, "62.w10");
//        chromeCaps.setCapability(CapabilityType.PLATFORM, "WIN10");
//        driver = new RemoteWebDriver(
//                new URL("http://212.106.131.202:4444/wd/hub"), chromeCaps);
//
//        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//        driver.get("https://www.helion.pl");
//    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }

    @BeforeMethod
    @Parameters ({"seleniumHub", "seleniumPort", "browser", "url"})
    public void setUp(String seleniumHub, int seleniumPort, String browser, String url)
        throws MalformedURLException {
        if (browser.equals("firefox")){
            System.setProperty("webdriver.gecko.driver", "C:\\pliki\\geckodriver.exe");
            DesiredCapabilities ffCaps = DesiredCapabilities.firefox();
            ffCaps.setCapability(CapabilityType.BROWSER_NAME, browser);
            driver = new RemoteWebDriver
                    (new URL("http://212.106.131.202:4444/wd/hub"), ffCaps);
        } else if (browser.equals("chrome")){
            System.setProperty("webdriver.chrome.driver", "C:\\pliki\\chromedriver.exe");
            DesiredCapabilities chromeCaps = DesiredCapabilities.chrome();
            chromeCaps.setCapability(CapabilityType.BROWSER_NAME, browser);
            driver = new RemoteWebDriver
                    (new URL("http://212.106.131.202:4444/wd/hub"),chromeCaps);
        } else if (browser.equals("edge")){
            System.setProperty("webdriver.edge.driver", "C:\\pliki\\MicrosoftWebDriver.exe");
            DesiredCapabilities edgeCaps = DesiredCapabilities.edge();
            edgeCaps.setCapability(CapabilityType.BROWSER_NAME, browser);
            driver = new RemoteWebDriver
                    (new URL("http://212.106.131.202:4444/wd/hub"), edgeCaps);
        }
        driver.navigate().to(url);
        driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
    }

    @Test
    public void checkTitleTrue(){
        Assert.assertTrue(driver.getTitle().contains("Helion"), "strona startuje z tytulem");
    }

    @Test
    public void checkTitleFalse(){
        Assert.assertFalse(driver.getTitle().contains("blablabla"), "tytul jest niewlasciwy");
    }

    @Test
    public void checkLogo() {
        WebElement logo = driver.findElement(By.cssSelector("p.logo"));
        Assert.assertTrue(logo.isDisplayed(),"brak logo");
    }

    @Test
    public void searchSeleniumBook() {
        WebElement search = driver.findElement(By.id("inputSearch"));
        search.sendKeys("selenium");
        WebElement searchButton = driver.findElement(By.cssSelector(".button"));
        searchButton.click();
        List<WebElement> seleniumResults = driver.findElements(By.partialLinkText("Selenium"));
        System.out.println("znaleziono " + seleniumResults.size() + " ksiazek o Selenium");
        Assert.assertTrue(seleniumResults.size() > 0, "brak ksiazek");

        List<WebElement> seleniumLazyResults = driver.findElements(By.cssSelector(".lazy"));
        System.out.println("znaleziono " + seleniumLazyResults.size() + " ksiazek o Selenium");
        Assert.assertTrue(seleniumLazyResults.size() > 0, "brak ksiazek");
    }

    @Test
    public void printSeleniumBook() throws IOException {
        WebElement search = driver.findElement(By.id("inputSearch"));
        search.clear();
        search.sendKeys("selenium");
        WebElement searchButton = driver.findElement(By.cssSelector(".button"));
        searchButton.click();
        List<WebElement> seleniumLazyResults = driver.findElements(By.cssSelector(".lazy"));
        System.out.println("znaleziono " + seleniumLazyResults.size() + " ksiazek o Selenium");
        Assert.assertTrue(seleniumLazyResults.size() > 0, "brak ksiazek");

        seleniumLazyResults.get(1).click();
        List<WebElement> title = driver.findElements(By.cssSelector(".title-group"));
        String bookTitle = title.get(0).getText();
        Assert.assertTrue(bookTitle.contains("Selenium"), "brak info o selenium 1");
        System.out.println("znaleziony tytul: " + bookTitle);
        try {
            Assert.assertTrue(bookTitle.contains("Selenium"), "brak info o selenium 2");
            System.out.println("znaleziony tytul ok: " + bookTitle);
        } catch (Throwable e) {
            System.out.println("UWAGA: COS POSZLO NIE TAK");
            File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("c:\\test\\cover.png"));
        }
    }

    @DataProvider
    public Object [][] dataForSearchTest() {
            return new Object[][]{
                    {"Selenium", 3}, {"Java", 21}, {"Kali", 4},
                    {"Jenkins", 3}, {"JavaScript", 20}, {"Git", 10}
            };
    }

    @Test (dataProvider = "dataForSearchTest")
    public void helionKsiazkiData(String title, int ilosc) throws InterruptedException {
        System.out.println("---------- Rozpoczynamy nowy test ----------");
        System.out.println("test dla tytulu: " + title + " oraz zalozonej ilosci: " + ilosc);
        driver.get("https://www.helion.pl");
        WebElement search = driver.findElement(By.cssSelector("input#inputSearch"));
        search.sendKeys(title);
        WebElement searchButton = driver.findElement(By.cssSelector(".button"));
        searchButton.click();
        Thread.sleep(100);
        List<WebElement> results = driver.findElements(By.partialLinkText(title));
        System.out.println("ilosc ksiazek: " + results.size());
        Assert.assertTrue(results.size() == ilosc);
//        search.clear();
    }
}
