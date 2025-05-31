package plugins.Spider.db;

import freenet.node.RequestStarter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import plugins.Spider.test.TestStorage;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThrows;

public class ConfigTest {

	@Test
	public void emptyConstructorCanBeCalled() {
		new Config();
	}

	@Test
	public void newConfigIsPersistent() {
		assertThat(config.isPersistent(), equalTo(true));
	}

	@Test
	public void newConfigHasDefaultTitle() {
		assertThat(config.getIndexTitle(), equalTo("Spider index"));
	}

	@Test
	public void newConfigHasDefaultOwner() {
		assertThat(config.getIndexOwner(), equalTo("Freenet"));
	}

	@Test
	public void newConfigHasDefaultOwnerEmail() {
		assertThat(config.getIndexOwnerEmail(), equalTo("(nil)"));
	}

	@Test
	public void newConfigHasDefaultMaxShownURIs() {
		assertThat(config.getMaxShownURIs(), equalTo(50));
	}

	@Test
	public void newConfigHasDefaultMaxParallelRequestsWorking() {
		assertThat(config.getMaxParallelRequestsWorking(), equalTo(0));
	}

	@Test
	public void newConfigHasDefaultMaxParallelRequestsNonWorking() {
		assertThat(config.getMaxParallelRequestsNonWorking(), equalTo(0));
	}

	@Test
	public void newConfigHasDefaultBeginWorkPeriod() {
		assertThat(config.getBeginWorkingPeriod(), equalTo(23));
	}

	@Test
	public void newConfigHasDefaultEndWorkPeriod() {
		assertThat(config.getEndWorkingPeriod(), equalTo(7));
	}

	@Test
	public void newConfigHasDefaultBadlistExtensions() {
		assertThat(config.getBadlistedExtensions(), arrayWithSize(87));
		/* just check some of them. */
		assertThat(asList(config.getBadlistedExtensions()), allOf(
				hasItem(".bmp"), hasItem(".deb"), hasItem(".wmv"), hasItem(".pps")
		));
	}

	@Test
	public void newConfigHasDefaultBadlistKeywords() {
		assertThat(config.getBadlistedKeywords(), emptyArray());
	}

	@Test
	public void newConfigHasDefaultRequestPriority() {
		assertThat(config.getRequestPriority(), equalTo(RequestStarter.IMMEDIATE_SPLITFILE_PRIORITY_CLASS));
	}

	@Test
	public void newConfigHasDefaultDebug() {
		assertThat(config.isDebug(), equalTo(false));
	}

	@Test
	public void newConfigHasDefaultNewFormatIndexBufferLimit() {
		assertThat(config.getNewFormatIndexBufferLimit(), equalTo(4));
	}

	@Test
	public void settingIndexTitleOnPersistedConfigThrowsException() {
		assertThrows(AssertionError.class, () -> config.setIndexTitle("New Index"));
	}

	@Test
	public void settingIndexTitleOnNotPersistedConfigRetainsIndexTitle() {
		configClone.setIndexTitle("New Index");
		assertThat(configClone.getIndexTitle(), equalTo("New Index"));
	}

	@Test
	public void settingIndexOwnerOnPersistedConfigThrowsException() {
		assertThrows(AssertionError.class, () -> config.setIndexOwner("New Owner"));
	}

	@Test
	public void settingIndexOwnerOnNotPersistedConfigRetainsIndexOwner() {
		configClone.setIndexOwner("New Owner");
		assertThat(configClone.getIndexOwner(), equalTo("New Owner"));
	}

	@Test
	public void settingIndexOwnerEmailOnPersistedConfigThrowsException() {
		assertThrows(AssertionError.class, () -> config.setIndexOwnerEmail("New OwnerEmail"));
	}

	@Test
	public void settingIndexOwnerEmailOnNotPersistedConfigRetainsIndexOwnerEmail() {
		configClone.setIndexOwnerEmail("New Owner Email");
		assertThat(configClone.getIndexOwnerEmail(), equalTo("New Owner Email"));
	}

	@Test
	public void settingMaxShownURIsOnPersistedConfigThrowsException() {
		assertThrows(AssertionError.class, () -> config.setMaxShownURIs(17));
	}

	@Test
	public void settingMaxShownURIsOnNotPersistedConfigRetainsMaxShownURIs() {
		configClone.setMaxShownURIs(19);
		assertThat(configClone.getMaxShownURIs(), equalTo(19));
	}

	@Test
	public void settingMaxParallelRequestsWorkingOnPersistedConfigThrowsException() {
		assertThrows(AssertionError.class, () -> config.setMaxParallelRequestsWorking(17));
	}

	@Test
	public void settingMaxParallelRequestsWorkingOnNotPersistendConfigRetainsMaxParallelRequestsWorking() {
		configClone.setMaxParallelRequestsWorking(19);
		assertThat(configClone.getMaxParallelRequestsWorking(), equalTo(19));
	}

	@Test
	public void settingMaxParallelRequestsNonWorkingOnPersistedConfigThrowsException() {
		assertThrows(AssertionError.class, () -> config.setMaxParallelRequestsNonWorking(17));
	}

	@Test
	public void settingMaxParallelRequestsNonWorkingOnNotPersistendConfigRetainsMaxParallelRequestsNonWorking() {
		configClone.setMaxParallelRequestsNonWorking(19);
		assertThat(configClone.getMaxParallelRequestsNonWorking(), equalTo(19));
	}

	@Test
	public void settingBeginWorkPeriodOnPersistedConfigThrowsException() {
		assertThrows(AssertionError.class, () -> config.setBeginWorkingPeriod(13));
	}

	@Test
	public void settingBeginWorkPeriodOnNotPersistedConfigRetainsBeginWorkPeriod() {
		configClone.setBeginWorkingPeriod(17);
		assertThat(configClone.getBeginWorkingPeriod(), equalTo(17));
	}

	@Test
	public void settingEndWorkPeriodOnPersistedConfigThrowsException() {
		assertThrows(AssertionError.class, () -> config.setEndWorkingPeriod(13));
	}

	@Test
	public void settingEndWorkPeriodOnNotPersistedConfigRetainsEndWorkPeriod() {
		configClone.setEndWorkingPeriod(17);
		assertThat(configClone.getEndWorkingPeriod(), equalTo(17));
	}

	@Test
	public void settingBadlistedExtensionsOnPersistedConfigThrowsException() {
		assertThrows(AssertionError.class, () -> config.setBadlistedExtensions(new String[] { ".foo", ".bar", ".baz" }));
	}

	@Test
	public void settingBadlistedExtensionsOnNonPersistendConfigRetainsBadlistedExtensions() {
		configClone.setBadlistedExtensions(new String[] { ".foo", ".bar", ".baz" });
		assertThat(configClone.getBadlistedExtensions(), arrayContainingInAnyOrder(".foo", ".bar", ".baz"));
	}

	@Test
	public void settingBadlistedKeywordsRetainsBadlistedKeywords() {
		config.setBadlistedKeywords(new String[] { "foo", "bar", "baz" });
		assertThat(config.getBadlistedKeywords(), arrayContainingInAnyOrder("foo", "bar", "baz"));
	}

	@Test
	public void settingBadlistedKeywordsToNullRetainsAnEmptyArray() {
		config.setBadlistedKeywords(null);
		assertThat(config.getBadlistedKeywords(), emptyArray());
	}

	@Test
	public void settingBadlistedKeywordsToOneEmptyStringRetainsAnEmptyArray() {
		config.setBadlistedKeywords(new String[] { "" });
		assertThat(config.getBadlistedKeywords(), emptyArray());
	}

	@Test
	public void settingBadlistedKeywordsToOneNonEmptyStringRetainsAnTheNonEmptyString() {
		config.setBadlistedKeywords(new String[] { "foo" });
		assertThat(config.getBadlistedKeywords(), arrayContaining("foo"));
	}

	@Test
	public void settingRequestPriorityOnPersistedConfigThrowsException() {
		assertThrows(AssertionError.class, () -> config.setRequestPriority((short) 1));
	}

	@Test
	public void settingRequestPriorityOnNonPersistendConfigRetainsRequestPriority() {
		configClone.setRequestPriority((short) 3);
		assertThat(configClone.getRequestPriority(), equalTo((short) 3));
	}

	@Test
	public void settingDebugOnPersistentConfigThrowsException() {
		assertThrows(AssertionError.class, () -> config.debug(true));
	}

	@Test
	public void settingDebugOnNonPersistedConfigRetainsDebug() {
		configClone.debug(true);
		assertThat(configClone.isDebug(), equalTo(true));
	}

	@Test
	public void settingNewFormatIndexBufferLimitRetainsNewFormatIndexBufferLimit() {
		config.setNewFormatIndexBufferLimit(19);
		assertThat(config.getNewFormatIndexBufferLimit(), equalTo(19));
	}

	@Test
	public void getMaxParallelRequestsReturnsWorkingValueWhenWithinWorkingHoursOverADay() {
		verifyThatMaxParallelRequestCorrespondToMaxForHourAndWorkingPeriod(10, 20, 19, 3, 15, 19);
	}

	@Test
	public void getMaxParallelRequestsReturnsNonWorkingValueWhenBeforeWorkingHoursOverADay() {
		verifyThatMaxParallelRequestCorrespondToMaxForHourAndWorkingPeriod(10, 20, 19, 3, 5, 3);
	}

	@Test
	public void getMaxParallelRequestsReturnsNonWorkingValueWhenAfterWorkingHoursOverADay() {
		verifyThatMaxParallelRequestCorrespondToMaxForHourAndWorkingPeriod(10, 20, 19, 3, 21, 3);
	}

	@Test
	public void getMaxParallelRequestsReturnsNonWorkingValueWhenOutsideOfWorkingHoursOverMidnight() {
		verifyThatMaxParallelRequestCorrespondToMaxForHourAndWorkingPeriod(20, 10, 19, 3, 15, 3);
	}

	@Test
	public void getMaxParallelRequestsReturnsWorkingValueWhenInWorkingHoursOverMidnightAfterMidnight() {
		verifyThatMaxParallelRequestCorrespondToMaxForHourAndWorkingPeriod(20, 10, 19, 3, 5, 19);
	}

	@Test
	public void getMaxParallelRequestsReturnsWorkingValueWhenInWorkingHoursOverMidnightBeforeMidnight() {
		verifyThatMaxParallelRequestCorrespondToMaxForHourAndWorkingPeriod(20, 10, 19, 3, 22, 19);
	}

	private static void verifyThatMaxParallelRequestCorrespondToMaxForHourAndWorkingPeriod(int beginWorkingPeriod, int endWorkingPeriod, int maxParallelRequestsWorking, int maxParallelRequestsNonWorking, final int currentHour, int expected) {
		Config config = new Config() {
			@Override
			protected int getCurrentHour() {
				return currentHour;
			}
		};
		config.setBeginWorkingPeriod(beginWorkingPeriod);
		config.setEndWorkingPeriod(endWorkingPeriod);
		config.setMaxParallelRequestsWorking(maxParallelRequestsWorking);
		config.setMaxParallelRequestsNonWorking(maxParallelRequestsNonWorking);
		assertThat(config.getMaxParallelRequests(), equalTo(expected));
	}

	@Before
	public void setupPerstRootAndConfig() {
		config = new Config(storage);
		configClone = this.config.clone();
	}

	@Rule
	public final TestStorage storage = new TestStorage();
	private Config config;
	private Config configClone;

}
