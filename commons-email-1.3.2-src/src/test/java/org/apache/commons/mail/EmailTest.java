package org.apache.commons.mail;

import static org.junit.Assert.assertEquals;


import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//import java.net.Authenticator;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;




public class EmailTest {

	private Email email;

	private static final String[] TEST_EMAILS = { "ab@cd.com", "a.b@c.org",
			"abcdefghijklmnopqrst@abcdefghijklmnopqrst.com.bd" };

	// Setup method
	@Before
	public void Setup() {
		email = new EmailDummy();
	}
	
	//Test if there are emails in the bcc line
	@Test
	public void addBccTestFull() throws EmailException {
		email.addBcc(TEST_EMAILS);

		assertEquals(3, email.getBccAddresses().size());
	}

	//Tests if there are no emails in the bcc lines
	@Test(expected = EmailException.class)
	public void addBccTestEmpty() throws EmailException {
		String[] TEST_EMAILS = null;
		email.addBcc(TEST_EMAILS);
	}

	//Test if there are emails in the cc line
	@Test
	public void addCcTestFull() throws EmailException {
		email.addCc(TEST_EMAILS);

		assertEquals(3, email.getCcAddresses().size());
	}

	//Test if there are no emails in the cc line
	@Test(expected = EmailException.class)
	public void addCcTestEmpty() throws EmailException {
		String[] TEST_EMAILS = null;
		email.addCc(TEST_EMAILS);
	}

	//Tests if there is a value without a name being put into the header
	@Test(expected = IllegalArgumentException.class)
	public void addHeaderTestEmptyName() throws EmailException {
		email.addHeader(null, "Test_Value");
	}

	//Tests if there is an empty value without a name being put into the header
	@Test(expected = IllegalArgumentException.class)
	public void addHeaderTestEmptyValue() throws EmailException {
		email.addHeader("Test_Name", null);
	}

	//Tests for a valid header
	@Test
	public void addHeaderTest() throws EmailException {
		email.addHeader("Test_Name", "Test_Value");
	}

	//Tests to add a reply email and reply
	@Test
	public void addReplyToTest() throws EmailException {
		email.addReplyTo("test@test.com", "Test_Reply");
	}

	//Tests a working buildMimeMessage
	@Test
	public void buildMimeMessageTest() throws EmailException {
		Properties prop = new Properties(); //Creates an instance of property
		prop.setProperty(email.MAIL_HOST, "localhost"); //Set the property to a session name
		Session scn = Session.getInstance(prop); //Create a new session on localhost
		
		email.createMimeMessage(scn); //Creates MimeMessage
		email.setHostName("localhost"); //Creates a host
		email.setFrom("from@test.com"); //Sent from email is required to make it work
		
		//generate dummy emails for the subject and a dummy charset
		email.subject = "recieved@test.com"; 
		email.charset = "abcdefghijklmnopqrstuvwxyz";
		 
		// generate dummy emails to cover bcc, cc, and to
		email.addBcc("sendtoBcc@test.com");
		email.addCc("sendtoCc@test.com");
		email.addTo("SendTo@test.com");
		
		//add a replay address
		email.addReplyTo("replyto@test.com");
		
		//set a test message for content
		email.setContent(scn, "This Test Message");
		
		//setHeaders() requires a Map, create a map in order to successfully implement that method
		Map<String, String> Headers = new HashMap<String, String>(); 
		Headers.put("Header1", "Test Header 1");
		Headers.put("Header2", "Test Header 2");
		
		email.setHeaders(Headers);
		
		email.buildMimeMessage();
	}
	
	
	@Test(expected = EmailException.class) 
	public void buildMimeMessageTestToAddressNull() throws EmailException { 
		Properties prop = new Properties();
		prop.setProperty(email.MAIL_HOST, "localhost");
		Session scn = Session.getInstance(prop);
		
		email.createMimeMessage(scn);
		email.setHostName("localhost");
		//No from address will cause an expected error
		
		email.subject = "recieved@test.com"; 
		email.charset = "abcdefghijklmnopqrstuvwxyz";
		 
		email.addBcc("sendtoBcc@test.com");
		email.addCc("sendtoCc@test.com");
		
		
		email.addReplyTo("replyto@test.com");
		
		Map<String, String> Headers = new HashMap<String, String>();
		Headers.put("Header1", "Test Header 1");
		Headers.put("Header2", "Test Header 2");
		
		email.setHeaders(Headers);
		
		email.buildMimeMessage();
	}
	
	@Test(expected = EmailException.class) 
	public void buildMimeMessageTestBccCcToListNull() throws EmailException { 
		Properties prop = new Properties();
		prop.setProperty(email.MAIL_HOST, "localhost");
		Session scn = Session.getInstance(prop);
		
		email.createMimeMessage(scn);
		email.setHostName("localhost");
		email.setFrom("from@test.com");
		
		email.subject = "recieved@test.com"; 
		email.charset = "abcdefghijklmnopqrstuvwxyz";
		 
		//No bcc, cc, or toList will envoke the null statements
		
		Map<String, String> Headers = new HashMap<String, String>();
		Headers.put("Header1", "Test Header 1");
		Headers.put("Header2", "Test Header 2");
		
		email.setHeaders(Headers);
		
		email.buildMimeMessage();
	}
	 
	//Tests if the session is not null
	@Test
	public void getHostNameTestSession() throws EmailException {
		Properties prop = new Properties(); //create an instance of property
		prop.setProperty(email.MAIL_HOST, "localhost"); //Create a hostname
		Session scn = Session.getInstance(prop); //set the session
		email.setMailSession(scn); //use the session
		email.getHostName(); //retrieve the host name
		assertEquals("localhost", email.getHostName()); //Test if host name is the same as set host name
	}

	//tests if the session is null but there is a host name
	@Test
	public void getHostNameTestEmailUtils() throws EmailException {
		email.setHostName("Test_name"); //set a host name

		String hostname = email.getHostName(); //retrieve the host name
		assertEquals("Test_name", hostname); //Test if they are the same
	}

	@Test
	public void getHostNameTestNull() throws EmailException {
		email.getHostName(); //test getHostName() when both fields are null
	}

	
	@Test(expected = EmailException.class)
	public void getMailSessionTestEmpty()throws EmailException {
		//Test getMailSession when the host name is null
		email.setHostName(null); 
		email.getMailSession();
	}
	
	@Test
	public void getMailSessionTest()throws EmailException {
		email.setHostName("localhost");
		
		email.setAuthentication("Andrew", "Password");
		
		//Set dummy data to test the different statements
		
		email.ssl = true;
		
		email.setTLS(true);
		
		email.setSSLCheckServerIdentity(true);

		email.setBounceAddress("return@test.com"); //A return email if the message fails
		
		email.getMailSession();
	}

	@Test
	public void getSentDateTest() throws EmailException {
		Date date = new Date(12312312); //Set a dummy date
		
		email.setSentDate(date);
		email.getSentDate();
		
		assertEquals(new Date(12312312), email.getSentDate()); // check date to see if they are the same
	}

	@Test
	public void getSentDateTestEmpty() throws EmailException {
		email.getSentDate(); //Checks if there is no date
	}

	@Test
	public void getSocketConnectionTimeoutTest() {
		email.setSocketConnectionTimeout(24); //set the SocketConnectionTimeout
		
		email.getSocketConnectionTimeout();
		
		assertEquals(24, email.getSocketConnectionTimeout()); //Check if the SocketConnectionTimeout is equal to the value set
	}

	@Test
	public void setFromTest() throws EmailException {
		
		 email.setFrom("test@test.com"); 
		 InternetAddress address = email.getFromAddress(); 
		 
		 assertEquals("test@test.com", address.getAddress()); //Test if the from address is the same
	}

	// Teardown method
	@After
	public void teardown() {

	}

}