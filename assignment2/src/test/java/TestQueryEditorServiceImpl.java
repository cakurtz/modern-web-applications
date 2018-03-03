import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestQueryEditorServiceImpl {

	OpenStackMeetingsServiceImpl queryEditor = null;
	
	@Before
	public void setUp() {
		queryEditor = new OpenStackMeetingsServiceImpl();
	}
	
	/*
	 * Tests the URL reset method of the service
	 */
	@Test
	public void testResetURL() {
		this.queryEditor.baseURL = "thisurlisfake.com";
		this.queryEditor.resetURL();
		String assertionString = "http://eavesdrop.openstack.org/";
		assertEquals(assertionString, this.queryEditor.baseURL);
	}
	
	/*
	 * Tests the findErrors method of the service and provides
	 * an invalid project name
	 */
	@Test
	public void testFindErrorsProject() {
		
		try {
			String testProject = "solu";
			String testYear = "2017";
			String expectedResult = "Project with solu not found";

			// Create mock dependency: mock()
			this.queryEditor.jsoupHandler = mock(JSoupHandler.class);
		
			// Setting up the expectations
			when(this.queryEditor.getJSoupHandler().getElements(any(String.class))).thenReturn(null);
			String result = queryEditor.findError(testProject, testYear);
			assertEquals(expectedResult, result);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Tests the extractData method of the service
	 * Test breaks up sample HTML from eavesdrop.openstack.org by <a> tag
	 */
	@Test
	public void testExtractData() {
		
		try {
			String expectedResult = "Number of meeting files: 4";
			Document doc = Jsoup.parseBodyFragment("<table>\n" + 
					"   <tbody><tr><th valign=\"top\"><img src=\"/icons/blank.gif\" alt=\"[ICO]\"></th><th><a href=\"?C=N;O=D\">Name</a></th><th><a href=\"?C=M;O=A\">Last modified</a></th><th><a href=\"?C=S;O=A\">Size</a></th><th><a href=\"?C=D;O=A\">Description</a></th></tr>\n" + 
					"   <tr><th colspan=\"5\"><hr></th></tr>\n" + 
					"<tr><td valign=\"top\"><img src=\"/icons/back.gif\" alt=\"[PARENTDIR]\"></td><td><a href=\"/meetings/solum/\">Parent Directory</a></td><td>&nbsp;</td><td align=\"right\">  - </td><td>&nbsp;</td></tr>\n" + 
					"<tr><td valign=\"top\"><img src=\"/icons/text.gif\" alt=\"[TXT]\"></td><td><a href=\"solum.2014-07-29-16.03.html\">solum.2014-07-29-16.03.html</a></td><td align=\"right\">2014-07-29 16:34  </td><td align=\"right\">3.8K</td><td>&nbsp;</td></tr>\n" + 
					"<tr><td valign=\"top\"><img src=\"/icons/text.gif\" alt=\"[TXT]\"></td><td><a href=\"solum.2014-07-29-16.03.log.html\">solum.2014-07-29-16.03.log.html</a></td><td align=\"right\">2014-07-29 16:34  </td><td align=\"right\"> 15K</td><td>&nbsp;</td></tr>\n" + 
					"<tr><td valign=\"top\"><img src=\"/icons/text.gif\" alt=\"[TXT]\"></td><td><a href=\"solum.2014-07-29-16.03.log.txt\">solum.2014-07-29-16.03.log.txt</a></td><td align=\"right\">2014-07-29 16:34  </td><td align=\"right\">6.6K</td><td>&nbsp;</td></tr>\n" + 
					"<tr><td valign=\"top\"><img src=\"/icons/text.gif\" alt=\"[TXT]\"></td><td><a href=\"solum.2014-07-29-16.03.txt\">solum.2014-07-29-16.03.txt</a></td><td align=\"right\">2014-07-29 16:34  </td><td align=\"right\">1.4K</td><td>&nbsp;</td></tr>\n" + 
					"   <tr><th colspan=\"5\"><hr></th></tr>\n" + 
					"</tbody></table>");
			Elements links = doc.getElementsByTag("a");
		
			String result = queryEditor.extractData(links);
			assertEquals(expectedResult, result);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
