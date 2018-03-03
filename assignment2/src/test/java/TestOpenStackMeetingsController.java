import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestOpenStackMeetingsController {
	
	OpenStackMeetingsController emailController = new OpenStackMeetingsController();
	QueryService mockEditor = null;
	
	@Before
	public void setUp1() {		
		mockEditor = mock(QueryService.class);		
		emailController.setQueryService(mockEditor);
	}
	
	/*
	 * Tests the controllers welcome message
	 */
	@Test
	public void testWelcomeMessage() {
		String reply = emailController.welcomeMessage();
		String assertionString = "Welcome to OpenStack meeting statistics calculation page. "
        		+ "Please provide project and year as query parameters.";
		assertEquals(assertionString, reply);
	}
		
	/*
	 * Tests a successful year validation
	 */
	@Test
	public void testValidateYearSuccess() {
		String year = "2017";
		boolean reply = emailController.validateYear(year);
		assertEquals(true, reply);
	}
	
	/*
	 * Tests an invalid year
	 */
	@Test
	public void testValidateYearFailure() {
		String year = "2017T";
		boolean reply = emailController.validateYear(year);
		assertEquals(false, reply);
	}
	
	/*
	 * Tests an invalid year
	 */
	@Test
	public void testValidateYearFailure2() {
		String year = "Sixty";
		boolean reply = emailController.validateYear(year);
		assertEquals(false, reply);
	}
}
