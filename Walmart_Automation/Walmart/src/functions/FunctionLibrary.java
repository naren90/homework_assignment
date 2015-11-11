package functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public abstract class FunctionLibrary {
	
	/**
	 * Read Objects.properties file for identifying and using objects
	 * @return Object Properties prop
	 * @throws Exception
	 */
	public Properties readObjectRepository() throws Exception{
        // File Location
		File file = new File("C:\\Walmart_Automation\\Walmart\\src\\properties\\objects.properties");
		// Creating properties object
		Properties prop = new Properties();

		// Creating InputStream object to read data
		FileInputStream objInput = null;
		try {
			objInput = new FileInputStream(file);
			// Reading properties key/values in file
			prop.load(objInput);
			System.out.println(prop.getProperty("Walmart.URL"));
			// Closing the InputStream
			objInput.close();

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return prop;
	}
	
	/**
	 * Read application properties
	 * Contains the Test Data , Chrome Driver Location and Browser name
	 * @return Application Properties
	 * @throws Exception
	 */
	public Properties readApplicationProperties() throws Exception {
		
		//File Location
		String strFileName = new File(".\\src\\properties\\application.properties").getAbsolutePath();
		File file = new File(strFileName);
		// Creating properties object
		Properties propApp = null;
		FileInputStream appInput = null;

		try {
			propApp = new Properties();
			// Loading Application properties from file
			appInput = new FileInputStream(file);
			propApp.load(appInput);
			appInput.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return propApp;
	}	
	
	/**
	 * Opens Browser based on properties specified application.properties
	 * 
	 * @return WebDriver
	 * @throws Exception 
	 */
	public WebDriver openBrowser() throws Exception {
		
		WebDriver driver = null;
		Properties properties = null; 
		
		// Load Properties file 	
		properties = readApplicationProperties();
		
		System.out.println("Active Browser" + properties.getProperty("browser"));
		String strBrowser = properties.getProperty("browser"); // retrieving browser type
		try{
		if (strBrowser.equals("chrome")) {
			System.setProperty("webdriver.chrome.driver", properties.getProperty("chromeExe"));
			driver = new ChromeDriver();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		}		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return driver;
		// return chrome driver
			
	}

	/**
	 * Closes open browser
	 * 
	 * @param driver : WebDriver to be closed
	 */

	public void closeBrowser(WebDriver driver) {
		// TODO Auto-generated method stub
		driver.quit();
	}

}
