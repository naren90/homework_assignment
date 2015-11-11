package functions;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


/**
 * Automate an end-to-end user e-commerce transaction flow: 
 * Login->Search Items -> Add Items to Cart -> Validate added Item 
 *
 * @author NarendranKP.
 */

public class WalmartPages extends FunctionLibrary {

	public static void main(String[] args) throws Exception {

		// Create a class instance to initialize variables
		WalmartPages wPages = new WalmartPages();
		
		// read object identifying 	
		final Properties objProp = wPages.readObjectRepository();
		final Properties  appProp = wPages.readApplicationProperties();
		WebDriver driver = wPages.openBrowser();
		
		wPages.loginApp(driver, objProp, appProp);
		wPages.searchAndVerifyItems(driver, objProp, appProp);
		wPages.closeBrowser(driver);
	}

	/**
	 * loginApp method will login to the application
	 * 
	 * @param driver : WebDriver object
	 * @param objProp:  is a Property object for Object Repository
	 * @param appProp:  is a Property object for Application Repository
	 */
	public void loginApp(WebDriver driver, Properties objProp, Properties appProp)  throws Exception {
		try {
			
			System.out.println("Opening URL :  " + objProp.getProperty("Walmart.URL"));
			
			// open up walmart.com page. property can be changed to Walmart.LoginURL if there is an exception.
			driver.get(objProp.getProperty("Walmart.URL"));			
			//driver.manage().window().maximize();

			driver.navigate().refresh();
			
			WebDriverWait wait = new WebDriverWait(driver, 80);			
						
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.className(objProp.getProperty("Login.SignIn"))));
			element.click();
			
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			
			// Entering the UserName
			element = driver.findElement(By.id(objProp.getProperty("Loginpage.txtUserName")));
			element.sendKeys(objProp.getProperty("UserName"));
			// Entering the Password
			element = driver.findElement(By.id(objProp.getProperty("Loginpage.txtPassword")));
			element.sendKeys(objProp.getProperty("Password"));
			// Clicking the SignIn button
			element = driver.findElement(By.cssSelector(objProp.getProperty("Loginpage.btnSignIn")));
			
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			element.click();		
			element = (new WebDriverWait(driver, 10)).until(ExpectedConditions
					.presenceOfElementLocated(By.className(objProp.getProperty("UserPage.textHeading"))));
			
			System.out.println("Login Completed!!");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * searchAndVerifyItems method search for the items from test data and verify 
	 * addition to cart
	 * @param driver : WebDriver object
	 * @param objProp:  is a Property object for Object Repository
	 * @param appProp:  is a Property object for Application Repository
	 */

	public void searchAndVerifyItems(WebDriver driver, Properties objProp, Properties appProp) throws Exception {
		try {
			WebElement element = null;
			String strSearchData = appProp.getProperty("searchData");
			String[] strSearchArr = strSearchData.split(",");
			// Running throw pool of search data and verify cart validation for
			// each item
			for (int i = 0; i < strSearchArr.length; i++) {

				String strCartItem = null;
				String strAddItem = null;

				// Find search text box and type in
				element = driver.findElement(By.id(objProp.getProperty("UserPage.txtSearch")));
				element.clear();
				element.sendKeys(strSearchArr[i]);
				// Click on search button
				element = driver.findElement(By.cssSelector(objProp.getProperty("UserPage.btnSearch")));
				element.click();
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				// Find the first item in the list and get its text
				element = driver.findElement(By.className(objProp.getProperty("UserPage.productTitle")));
				strAddItem = element.getText();
				// click on title to open add to cart page
				element.click();
				driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
                
				// Extra step for search term iphone to choose color of the product	
				
				if (strSearchArr[i].contains("iphone")) {
					driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
					WebDriverWait wait = new WebDriverWait(driver, 60);
					element = wait.until(ExpectedConditions
							.elementToBeClickable(By.cssSelector(objProp.getProperty("CartPage.Option"))));
					element = driver.findElement(By.cssSelector(objProp.getProperty("CartPage.Option")));
					element.click();
					driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
					}
				 
				// Looping through to identify add to cart button to get over staleObjectReference Exception
				for (int i1 = 0; i1 <= 10; i1++) {
					try {
						WebDriverWait wait = new WebDriverWait(driver, 60);
						element = wait.until(ExpectedConditions
								.elementToBeClickable(By.cssSelector(objProp.getProperty("ItemPage.btnAddToCart"))));
						element = driver.findElement(By.cssSelector(objProp.getProperty("ItemPage.btnAddToCart")));
						element.click();
						driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
						break;
					} catch (StaleElementReferenceException objNotIdentified) {
                      // Stale object reference exception . loop through till object is identifies
					     continue;
					}
				}
				// Looping through to identify item description text to get over staleObjectReference Exception
				for (int i1 = 0; i1 <= 10; i1++) {
					try {
						driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
						element = driver.findElement(By.xpath(objProp.getProperty("CartPage.textCartItemdual")));
						strCartItem = element.getAttribute("alt");
						break;
					} catch (StaleElementReferenceException objNotIdentified) {
	                      // Stale object reference exception . loop through till object is identifies
						     continue;
					}
				}
               
				// Validation that item selected is properly added to the cart
				try {
					Assert.assertEquals(strAddItem, strCartItem);
					System.out.println("Chosen Item " + strAddItem + " is correctly added into cart");
					element = driver.findElement(By.cssSelector(objProp.getProperty("UserPage.btnCart")));
					element.click();
					driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
					driver.navigate().refresh();

				} catch (Exception e) {
					System.out.println("Error: correct item is not added in cart");
					e.printStackTrace();
				}

				try {
					driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
					element = driver.findElement(By.cssSelector(objProp.getProperty("CartPage.ItemCount")));
					Assert.assertEquals(element.getText(), "1");
					System.out.println("One " + strAddItem + " is added and removed out of the cart");
					// remove added item from cart so that next item can be validated
					RemoveItemFromCart(driver, objProp);

				} catch (Exception e) {
					// error in adding any one item should not stop removing the item from the cart
					RemoveItemFromCart(driver, objProp);
					System.out.println("Error : Number of items added is not correct");
					e.printStackTrace();
				}
			}
		}
		catch(Exception message){
			
			message.printStackTrace();
		}
	}
	
	/**
	 * RemoveAllItemsInCart method removes items added to the cart.
	 * 
	 * @param driver:  WebDriver object
	 * @param prop:    is a Property object for Object Repository
	 */
	public void RemoveItemFromCart(WebDriver driver, Properties objProp)
	{
		try {
			WebElement element = driver.findElement(By.id(objProp.getProperty("CartPage.btnRemove")));;
			element.click();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}