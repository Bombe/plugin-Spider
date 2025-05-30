package plugins.Spider.test;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import plugins.Spider.org.garret.perst.Storage;
import plugins.Spider.org.garret.perst.impl.StorageImpl;

import java.io.File;

/**
 * A {@link Storage} implementation that functions as a {@link TestRule},
 * removing its file when the test has been run.
 * <h2>How to Use</h2>
 * <pre>
 * public class SomeTest {
 *     &#64;Rule
 *     public final TestStorage storage = new TestStorage();
 *
 *     &#64;Test
 *     public void testStorage() {
 *         assertThat(storage.isOpened(), equalTo(true));
 *     }
 * }
 * </pre>
 */
public class TestStorage extends StorageImpl implements TestRule {

	@Override
	public Statement apply(Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				File tempFile = File.createTempFile("test", ".perst");
				open(tempFile.getPath());
				try {
					base.evaluate();
				} finally {
					try {
						close();
					} finally {
						tempFile.delete();
					}
				}
			}
		};
	}

}
