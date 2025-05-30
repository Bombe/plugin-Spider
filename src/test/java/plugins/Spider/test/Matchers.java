package plugins.Spider.test;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import plugins.Spider.db.Page;
import plugins.Spider.db.Status;

import static org.hamcrest.Matchers.anything;

public class Matchers {

	/**
	 * Returns a {@link Matcher} that verifies a {@link Page}’s
	 * {@link Page#getURI() URI}.
	 *
	 * @param uriMatcher The URI matcher
	 * @return A {@link Matcher} that can match {@link Page} objects
	 */
	public static Matcher<Page> isPage(Matcher<? super String> uriMatcher) {
		return isPage(uriMatcher, anything(), anything(), anything());
	}

	/**
	 * Returns a {@link Matcher} that verifies a {@link Page}’s properties,
	 * such as the {@link Page#getURI() URI},
	 * {@link Page#getStatus() status},
	 * {@link Page#getComment()},
	 * or {@link Page#getPageTitle()}.
	 *
	 * @param uriMatcher The URI matcher
	 * @param statusMatcher The status matcher
	 * @param commentMatcher The comment matcher
	 * @param pageTitleMatcher The page title matcher
	 * @return A {@link Matcher} that can match {@link Page} objects
	 */
	public static Matcher<Page> isPage(Matcher<? super String> uriMatcher, Matcher<? super Status> statusMatcher, Matcher<? super String> commentMatcher, Matcher<? super String> pageTitleMatcher) {
		return new TypeSafeDiagnosingMatcher<Page>() {
			@Override
			protected boolean matchesSafely(Page page, Description mismatchDescription) {
				if (!uriMatcher.matches(page.getURI())) {
					mismatchDescription.appendText("uri was").appendValue(page.getURI());
					return false;
				}
				if (!statusMatcher.matches(page.getStatus())) {
					mismatchDescription.appendText("status was").appendValue(page.getStatus());
					return false;
				}
				if (!commentMatcher.matches(page.getComment())) {
					mismatchDescription.appendText("comment was").appendValue(page.getComment());
					return false;
				}
				if (!pageTitleMatcher.matches(page.getPageTitle())) {
					mismatchDescription.appendText("page title was").appendValue(page.getPageTitle());
					return false;
				}
				return true;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("page with URI ").appendValue(uriMatcher);
			}
		};
	}

}
