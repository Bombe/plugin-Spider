package plugins.Spider.db;

import freenet.keys.FreenetURI;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import plugins.Spider.test.TestStorage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static plugins.Spider.test.Matchers.isPage;

public class PageTest {

	@Test
	public void newPageHasStatusQueued() {
		Page page = new Page("KSK@new-page", "some comment", storage);
		assertThat(page.getStatus(), equalTo(Status.QUEUED));
	}

	@Test
	public void setStatusOnAPageRetainsTheStatus() throws Exception {
		Page page = perstRoot.getPageByURI(new FreenetURI("KSK@new-page"), true, "some comment");
		page.setStatus(Status.INDEXED);
		assertThat(perstRoot.getPageById(page.getId()), isPage(equalTo("KSK@new-page"), equalTo(Status.INDEXED), anything(), anything()));
	}

	@Test
	public void setCommentOnAPageRetainsTheComment() throws Exception {
		Page page = perstRoot.getPageByURI(new FreenetURI("KSK@new-page"), true, "some comment");
		page.setComment("some other comment");
		assertThat(perstRoot.getPageById(page.getId()), isPage(equalTo("KSK@new-page"), anything(), equalTo("some other comment"), anything()));
	}

	@Test
	public void setPageTitleOnAPageRetainsThePageTitle() throws Exception {
		Page page = perstRoot.getPageByURI(new FreenetURI("KSK@new-page"), true, "some comment");
		page.setPageTitle("page title");
		assertThat(perstRoot.getPageById(page.getId()), isPage(equalTo("KSK@new-page"), anything(), anything(), equalTo("page title")));
	}

	@Before
	public void setRootOnStorage() {
		perstRoot = PerstRoot.createRoot(storage);
	}

	@Rule
	public final TestStorage storage = new TestStorage();
	private PerstRoot perstRoot;

}
