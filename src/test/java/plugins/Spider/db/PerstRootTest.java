package plugins.Spider.db;

import freenet.keys.FreenetURI;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import plugins.Spider.org.garret.perst.StorageError;
import plugins.Spider.test.TestStorage;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static plugins.Spider.test.Matchers.isPage;

public class PerstRootTest {

	@Test
	public void perstRootIsCreatedInStorage() {
		assertThat(storage.getRoot(), equalTo(perstRoot));
	}

	@Test
	public void getPageReturnsNullForNonExistentPage() throws Exception {
		assertThat(perstRoot.getPageByURI(new FreenetURI("KSK@test"), false, null), nullValue());
	}

	@Test
	public void getPageReturnsPageForNonExistentPageIfCreateIsTrue() throws Exception {
		Page page = perstRoot.getPageByURI(new FreenetURI("KSK@test"), true, null);
		assertThat(page.uri, equalTo("KSK@test"));
	}

	@Test
	public void getPageReturnsExistingPage() throws Exception {
		Page firstPage = perstRoot.getPageByURI(new FreenetURI("KSK@test"), true, null);
		Page secondPage = perstRoot.getPageByURI(new FreenetURI("KSK@test"), true, null);
		assertThat(secondPage, equalTo(firstPage));
	}

	@Test
	public void getPageByIdReturnsNullIfPageIdIsInvalid() {
		assertThat(perstRoot.getPageById(12345), nullValue());
	}

	@Test
	public void getPageByIdReturnsCorrectPage() throws Exception {
		Page page = perstRoot.getPageByURI(new FreenetURI("KSK@test1"), true, null);
		perstRoot.getPageByURI(new FreenetURI("KSK@test2"), true, null);
		assertThat(perstRoot.getPageById(page.getId()), equalTo(page));
	}

	@Test
	public void exclusiveLockLocksIndexForFailedPagesCorrectly() throws InterruptedException {
		verifyExclusiveLockForStatus(Status.FAILED);
	}

	@Test
	public void exclusiveLockLocksIndexForSucceededPagesCorrectly() throws InterruptedException {
		verifyExclusiveLockForStatus(Status.SUCCEEDED);
	}

	@Test
	public void exclusiveLockLocksIndexForNotPushedPagesCorrectly() throws InterruptedException {
		verifyExclusiveLockForStatus(Status.NOT_PUSHED);
	}

	@Test
	public void exclusiveLockLocksIndexForIndexedPagesCorrectly() throws InterruptedException {
		verifyExclusiveLockForStatus(Status.INDEXED);
	}

	@Test
	public void exclusiveLockLocksIndexForQueuedPagesCorrectly() throws InterruptedException {
		verifyExclusiveLockForStatus(Status.QUEUED);
	}

	private void verifyExclusiveLockForStatus(Status status) throws InterruptedException {
		acquireLockAndCheckOtherLockCannotBeAcquired(status, perstRoot::exclusiveLock, perstRoot::sharedLockPages);
	}

	@Test
	public void sharedLockLocksIndexForFailedPagesCorrectly() throws InterruptedException {
		verifySharedLockForStatus(Status.FAILED);
	}

	@Test
	public void sharedLockLocksIndexForSucceededPagesCorrectly() throws InterruptedException {
		verifySharedLockForStatus(Status.SUCCEEDED);
	}

	@Test
	public void sharedLockLocksIndexForNotPushedPagesCorrectly() throws InterruptedException {
		verifySharedLockForStatus(Status.NOT_PUSHED);
	}

	@Test
	public void sharedLockLocksIndexForIndexedPagesCorrectly() throws InterruptedException {
		verifySharedLockForStatus(Status.INDEXED);
	}

	@Test
	public void sharedLockLocksIndexForQueuedPagesCorrectly() throws InterruptedException {
		verifySharedLockForStatus(Status.QUEUED);
	}

	private void verifySharedLockForStatus(Status status) throws InterruptedException {
		acquireLockAndCheckOtherLockCannotBeAcquired(status, perstRoot::sharedLockPages, perstRoot::exclusiveLock);
	}

	private void acquireLockAndCheckOtherLockCannotBeAcquired(Status status, Consumer<Status> firstLock, Consumer<Status> secondLock) throws InterruptedException {
		firstLock.accept(status);
		CountDownLatch latch = new CountDownLatch(1);
		Thread thread = new Thread(() -> {
			try {
				secondLock.accept(status);
				latch.countDown();
			} catch (StorageError e) {
				/* ignore. */
			}
		});
		thread.start();
		try {
			boolean lockWasAcquired = latch.await(2, TimeUnit.SECONDS);
			assertThat(lockWasAcquired, equalTo(false));
		} finally {
			thread.interrupt();
			perstRoot.unlockPages(status);
		}
	}

	@Test
	public void getPagesForFailedPagesReturnsFailedPagesCorrectly() {
		createSomePagesWithAllStatuses();
		assertThat(toList(perstRoot.getPages(Status.FAILED)), contains(
				isPage(equalTo("KSK@failed1"))
		));
	}

	@Test
	public void getPagesForSucceededPagesReturnsSucceededPagesCorrectly() {
		createSomePagesWithAllStatuses();
		assertThat(toList(perstRoot.getPages(Status.SUCCEEDED)), containsInAnyOrder(
				isPage(equalTo("KSK@succeeded1")),
				isPage(equalTo("KSK@succeeded2"))
		));
	}

	@Test
	public void getPagesForNotPushedPagesReturnsNotPushedPagesCorrectly() {
		createSomePagesWithAllStatuses();
		assertThat(toList(perstRoot.getPages(Status.NOT_PUSHED)), containsInAnyOrder(
				isPage(equalTo("KSK@not_pushed1")),
				isPage(equalTo("KSK@not_pushed2")),
				isPage(equalTo("KSK@not_pushed3"))
		));
	}

	@Test
	public void getPagesForIndexedPagesReturnsIndexedPagesCorrectly() {
		createSomePagesWithAllStatuses();
		assertThat(toList(perstRoot.getPages(Status.INDEXED)), containsInAnyOrder(
				isPage(equalTo("KSK@indexed1")),
				isPage(equalTo("KSK@indexed2")),
				isPage(equalTo("KSK@indexed3")),
				isPage(equalTo("KSK@indexed4"))
		));
	}

	@Test
	public void getPagesForQueuedPagesReturnsQueuedPagesCorrectly() {
		createSomePagesWithAllStatuses();
		assertThat(toList(perstRoot.getPages(Status.QUEUED)), containsInAnyOrder(
				isPage(equalTo("KSK@queued1")),
				isPage(equalTo("KSK@queued2")),
				isPage(equalTo("KSK@queued3")),
				isPage(equalTo("KSK@queued4")),
				isPage(equalTo("KSK@queued5"))
		));
	}

	@Test
	public void getPageCountForStatusFailedReturns1() {
		createSomePagesWithAllStatuses();
		assertThat(perstRoot.getPageCount(Status.FAILED), equalTo(1));
	}

	@Test
	public void getPageCountForStatusSucceededReturns2() {
		createSomePagesWithAllStatuses();
		assertThat(perstRoot.getPageCount(Status.SUCCEEDED), equalTo(2));
	}

	@Test
	public void getPageCountForStatusNotPushedReturns3() {
		createSomePagesWithAllStatuses();
		assertThat(perstRoot.getPageCount(Status.NOT_PUSHED), equalTo(3));
	}

	@Test
	public void getPageCountForStatusIndexedReturns4() {
		createSomePagesWithAllStatuses();
		assertThat(perstRoot.getPageCount(Status.INDEXED), equalTo(4));
	}

	@Test
	public void getPageCountForStatusQueuedReturns5() {
		createSomePagesWithAllStatuses();
		assertThat(perstRoot.getPageCount(Status.QUEUED), equalTo(5));
	}

	private void createSomePagesWithAllStatuses() {
		addNPagesForStatus(1, Status.FAILED);
		addNPagesForStatus(2, Status.SUCCEEDED);
		addNPagesForStatus(3, Status.NOT_PUSHED);
		addNPagesForStatus(4, Status.INDEXED);
		addNPagesForStatus(5, Status.QUEUED);
	}

	private void addNPagesForStatus(int n, Status status) {
		IntStream.range(0, n).forEach(i -> {
			try {
				Page page = perstRoot.getPageByURI(new FreenetURI("KSK@" + status.name().toLowerCase() + (i + 1)), true, null);
				page.setStatus(status);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Test
	public void newPerstRootIsNotModified() {
		assertThat(perstRoot.isModified(), equalTo(false));
	}

	@Test
	public void settingAConfigRetainsTheConfig() {
		Config config = new Config();
		perstRoot.setConfig(config);
		assertThat(perstRoot.getConfig(), sameInstance(config));
	}

	@Test
	public void perstRootIsModifiedAfterSettingConfig() {
		perstRoot.setConfig(null);
		assertThat(perstRoot.isModified(), equalTo(true));
	}

	@Before
	public void createPerstRoot() {
		perstRoot = PerstRoot.createRoot(storage);
	}

	private static <T> List<T> toList(Iterator<T> iterator) {
		List<T> list = new ArrayList<>();
		iterator.forEachRemaining(list::add);
		return list;
	}

	@Rule
	public final TestStorage storage = new TestStorage();
	private PerstRoot perstRoot;

}
