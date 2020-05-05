package Util;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;


/**
 * 
 * @author NaveenKhunteta
 *
 */
public class AmazonOTPHandle {

	public static final String ACCOUNT_SID = "ACfe4fde6d3b48095c651cb2e65643c08a";
	public static final String AUTH_TOKEN = "eeca0b7623797e4d94ed0b06b48c23d8";

	public static void main(String[] args) {

		System.setProperty("webdriver.chrome.driver", "C:\\Users\\91821\\OneDrive\\Desktop\\My Workspace\\chromedriver_win32\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.get("https://www.amazon.in");
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		driver.findElement(By.cssSelector("a#nav-link-accountList>span>span")).click();
		driver.findElement(By.id("createAccountSubmit")).click();

		driver.findElement(By.id("ap_customer_name")).sendKeys("ViveklabsOtp");
		driver.findElement(By.id("auth-country-picker-container")).click();

		driver.findElement(By.xpath("//ul[@role='application']//li/a[contains(text(),'United States +1')]")).click();
		driver.findElement(By.id("ap_phone_number")).sendKeys("2057369308");
		driver.findElement(By.id("ap_password")).sendKeys("Labsotp@123");
		driver.findElement(By.id("continue")).click();

		// get the OTP using Twilio APIs:
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		String smsBody = getMessage();
		System.out.println(smsBody);
		String OTPNumber = smsBody.replaceAll("[^-?0-9]+", " ");
		System.out.println(OTPNumber);
		
		driver.findElement(By.id("auth-pv-enter-code")).sendKeys(OTPNumber);

	}

	public static String getMessage() {
		return getMessages().filter(m -> m.getDirection().compareTo(Message.Direction.INBOUND) == 0)
				.filter(m -> m.getTo().equals("+12057369308")).map(Message::getBody).findFirst()
				.orElseThrow(IllegalStateException::new);
	}

	private static Stream<Message> getMessages() {
		ResourceSet<Message> messages = Message.reader(ACCOUNT_SID).read();
		return StreamSupport.stream(messages.spliterator(), false);
	}

}
