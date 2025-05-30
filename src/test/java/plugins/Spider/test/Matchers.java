package plugins.Spider.test;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import plugins.Spider.db.Page;

public class Matchers {

	/**
	 * Returns a {@link Matcher} that verifies a {@link Page}’s
	 * {@link Page#getURI() URI}.
	 *
	 * @param uriMatcher The URI matcher
	 * @return A {@link Matcher} that can match {@link Page} objects
	 */
	public static Matcher<Page> isPage(Matcher<? super String> uriMatcher) {
		return new TypeSafeDiagnosingMatcher<Page>() {
			@Override
			protected boolean matchesSafely(Page page, Description mismatchDescription) {
				if (!uriMatcher.matches(page.getURI())) {
					mismatchDescription.appendText("uri was").appendValue(page.getURI());
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
