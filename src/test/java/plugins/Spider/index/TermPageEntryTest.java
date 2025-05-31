package plugins.Spider.index;

import freenet.keys.FreenetURI;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;
import static plugins.Spider.index.TermEntry.EntryType.INDEX;

public class TermPageEntryTest {

	@Test
	public void canCreateTermPageEntry() {
		new TermPageEntry("", 1.0f, uri, emptyMap());
	}

	@Test
	public void createTermPageEntryWithNullSubjectThrowsException() {
		assertThrows(RuntimeException.class, () -> new TermPageEntry(null, 1.0f, uri, emptyMap()));
	}

	@Test
	public void createTermPageEntryWithTitleWithNullSubjectThrowsException() {
		assertThrows(RuntimeException.class, () -> new TermPageEntry(null, 1.0f, uri, "title", emptyMap()));
	}

	@Test
	public void createTermPageEntryWithNegativeRelevanceThrowsException() {
		assertThrows(RuntimeException.class, () -> new TermPageEntry("", -0.01f, uri, emptyMap()));
	}

	@Test
	public void createTermPageEntryWithTitleWithNegativeRelevanceThrowsException() {
		assertThrows(RuntimeException.class, () -> new TermPageEntry(null, -0.01f, uri, "title", emptyMap()));
	}

	@Test
	public void createTermPageEntryWithRelevanceLargerThanOneThrowsException() {
		assertThrows(RuntimeException.class, () -> new TermPageEntry("", 1.01f, uri, emptyMap()));
	}

	@Test
	public void createTermPageEntryWithTitleWithRelevanceLargerThanOneThrowsException() {
		assertThrows(RuntimeException.class, () -> new TermPageEntry(null, 1.01f, uri, "title", emptyMap()));
	}

	@Test
	public void createTermPageEntryWithNullUriThrowsException() {
		assertThrows(RuntimeException.class, () -> new TermPageEntry("", 1f, null, emptyMap()));
	}

	@Test
	public void createTermPageEntryWithTitleWithNullUriThrowsException() {
		assertThrows(RuntimeException.class, () -> new TermPageEntry(null, 1f, null, "title", emptyMap()));
	}

	@Test
	public void entryTypeIsPage() {
		assertThat(new TermPageEntry("", 1f, uri, emptyMap()).entryType(), equalTo(TermEntry.EntryType.PAGE));
	}

	@Test
	public void sizeEstimateContainsLengthOfSubject() {
		assertThat(new TermPageEntry("subject", 1f, uri, emptyMap()).sizeEstimate(), equalTo(15));
	}

	@Test
	public void sizeEstimateContainsLengthOfUri() {
		assertThat(new TermPageEntry("", 1f, uri, emptyMap()).sizeEstimate(), equalTo(8));
	}

	@Test
	public void sizeEstimateContainsLengthOfTitle() {
		assertThat(new TermPageEntry("", 1f, uri, "title", emptyMap()).sizeEstimate(), equalTo(13));
	}

	@Test
	public void sizeEstimateContainsNumberOfPositions() {
		assertThat(new TermPageEntry("", 1f, uri, positionsMap).sizeEstimate(), equalTo(20));
	}

	@Test
	public void termPageEntryWithPositionsMapHasPositions() {
		assertThat(new TermPageEntry("", 1f, uri, emptyMap()).hasPositions(), equalTo(true));
	}

	@Test
	public void termPageEntryWithoutPositionsMapDoesNotHavePositions() {
		assertThat(new TermPageEntry("", 1f, uri, null).hasPositions(), equalTo(false));
	}

	@Test
	public void positionsMapIsNullIfTermPageEntryWasNotCreatedWithPositions() {
		assertThat(new TermPageEntry("", 1f, uri, null).positionsMap(), nullValue());
	}

	@Test
	public void positionsMapIsReturnedIfTermPageEntryWasCreatedWithPositions() {
		assertThat(new TermPageEntry("", 1f, uri, positionsMap).positionsMap(), allOf(
				hasEntry(0, "0"), hasEntry(1, "1"), hasEntry(2, "2")
		));
	}

	@Test
	public void positionsMapIsBuiltWhenTermPageEntryWasCreatedWithoutPositions() {
		TermPageEntry termPageEntry = new TermPageEntry("", 1f, uri, null);
		termPageEntry.putPosition(3);
		termPageEntry.putPosition(4);
		termPageEntry.putPosition(5);
		assertThat(termPageEntry.positionsMap(), allOf(hasEntry(3, null), hasEntry(4, null), hasEntry(5, null)));
	}

	@Test
	public void hasPositionsReturnsTrueIfPositionIsInPositions() {
		assertThat(new TermPageEntry("", 1f, uri, positionsMap).hasPosition(1), equalTo(true));
	}

	@Test
	public void hasPositionsReturnsFalseIfPositionIsNotInPositions() {
		assertThat(new TermPageEntry("", 1f, uri, positionsMap).hasPosition(4), equalTo(false));
	}

	@Test
	public void positionsReturnsPositionsAsSortedList() {
		assertThat(new TermPageEntry("", 1f, uri, positionsMap).positions(), contains(0, 1, 2));
	}

	@Test
	public void rawPositionsReturnsPositionsAsSortedIntArray() {
		assertThat(new TermPageEntry("", 1f, uri, positionsMap).positionsRaw(), equalTo(new int[] { 0, 1, 2 }));
	}

	@Test
	public void positionSizeIsZeroIfTermPageEntryWasCreatedWithoutPositions() {
		assertThat(new TermPageEntry("", 1f, uri, null).positionsSize(), equalTo(0));
	}

	@Test
	public void positionSizeReturnsNumberOfPositionsIfTermPageEntryWasCreatedWithPositions() {
		assertThat(new TermPageEntry("", 1f, uri, positionsMap).positionsSize(), equalTo(3));
	}

	@Test
	public void termPageEntryDoesNotHaveFragmentsWhenCreatedWithoutPositions() {
		assertThat(new TermPageEntry("", 1f, uri, null).hasFragments(), equalTo(false));
	}

	@Test
	public void termPageEntryDoesHaveFragmentsWhenCreatedWithPositions() {
		assertThat(new TermPageEntry("", 1f, uri, positionsMap).hasFragments(), equalTo(true));
	}

	@Test
	public void putPositionAddsPositionToFragments() {
		TermPageEntry termPageEntry = new TermPageEntry("", 1f, uri, (Set<Integer>) null, positionsMap);
		termPageEntry.putPosition(4);
		assertThat(termPageEntry.positionsMap(), hasEntry(4, null));
	}

	@Test
	public void termPageEntryComparesEqualToItself() {
		TermPageEntry termPageEntry = new TermPageEntry("", 1f, uri, positionsMap);
		assertThat(termPageEntry.compareTo(termPageEntry), equalTo(0));
	}

	@Test
	public void termPageEntryIsEqualToItself() {
		TermPageEntry termPageEntry = new TermPageEntry("", 1f, uri, positionsMap);
		assertThat(termPageEntry, equalTo(termPageEntry));
	}

	@Test
	public void termPageEntryIsNotEqualToRandomObject() {
		TermPageEntry termPageEntry = new TermPageEntry("", 1f, uri, positionsMap);
		assertThat(termPageEntry, not(equalTo(new Object())));
	}

	@Test
	public void termPageEntryIsNotEqualToNull() {
		TermPageEntry termPageEntry = new TermPageEntry("", 1f, uri, positionsMap);
		assertThat(termPageEntry, not(equalTo(null)));
	}

	@Test
	public void termPageEntryComparesAccordingToSubject() {
		TermPageEntry firstTermPageEntry = new TermPageEntry("first", 1f, uri, positionsMap);
		TermPageEntry secondTermPageEntry = new TermPageEntry("second", 1f, uri, positionsMap);
		assertThat(firstTermPageEntry.compareTo(secondTermPageEntry), lessThan(0));
	}

	@Test
	public void termPageEntryIsNotEqualToTermPageEntryWithDifferentSubject() {
		TermPageEntry firstTermPageEntry = new TermPageEntry("first", 1f, uri, positionsMap);
		TermPageEntry secondTermPageEntry = new TermPageEntry("second", 1f, uri, positionsMap);
		assertThat(firstTermPageEntry, not(equalTo(secondTermPageEntry)));
	}

	@Test
	public void termPageEntryComparesAccordingToDescendingRelevanceIfSubjectIsEqual() {
		TermPageEntry firstTermPageEntry = new TermPageEntry("term", 0.5f, uri, positionsMap);
		TermPageEntry secondTermPageEntry = new TermPageEntry("term", 1f, uri, positionsMap);
		assertThat(firstTermPageEntry.compareTo(secondTermPageEntry), greaterThan(0));
		assertThat(secondTermPageEntry.compareTo(firstTermPageEntry), lessThan(0));
	}

	@Test
	public void termPageEntryIsNotEqualToTermPageEntryWithDifferentRelevance() {
		TermPageEntry firstTermPageEntry = new TermPageEntry("term", 0.5f, uri, positionsMap);
		TermPageEntry secondTermPageEntry = new TermPageEntry("term", 1f, uri, positionsMap);
		assertThat(firstTermPageEntry, not(equalTo(secondTermPageEntry)));
	}

	@Test
	public void termPageEntryComparesAccordingToEntryTypeIfRelevanceIsEqual() {
		TermPageEntry firstTermPageEntry = new TermPageEntry("term", 1f, uri, positionsMap) {
			@Override
			public EntryType entryType() {
				return INDEX;
			}
		};
		TermPageEntry secondTermPageEntry = new TermPageEntry("term", 1f, uri, positionsMap);
		assertThat(firstTermPageEntry.compareTo(secondTermPageEntry), lessThan(0));
	}

	@Test
	public void termPageEntryIsNotEqualToTermPageEntryWithDifferentEntryType() {
		TermPageEntry firstTermPageEntry = new TermPageEntry("term", 1f, uri, positionsMap) {
			@Override
			public EntryType entryType() {
				return INDEX;
			}
		};
		TermPageEntry secondTermPageEntry = new TermPageEntry("term", 1f, uri, positionsMap);
		assertThat(firstTermPageEntry, not(equalTo(secondTermPageEntry)));
	}

	@Test
	public void termPageEntryComparesAccordingToPageIfEntryTrypeIsEqual() throws Exception {
		TermPageEntry firstTermPageEntry = new TermPageEntry("term", 1f, uri, positionsMap);
		TermPageEntry secondTermPageEntry = new TermPageEntry("term", 1f, new FreenetURI("KSK@value"), positionsMap);
		assertThat(firstTermPageEntry.compareTo(secondTermPageEntry), lessThan(0));
	}

	@Test
	public void termPageEntryIsNotEqualToTermPageEntryWithDifferentPage() throws Exception {
		TermPageEntry firstTermPageEntry = new TermPageEntry("term", 1f, uri, positionsMap);
		TermPageEntry secondTermPageEntry = new TermPageEntry("term", 1f, new FreenetURI("KSK@value"), positionsMap);
		assertThat(firstTermPageEntry, not(equalTo(secondTermPageEntry)));
	}

	private final FreenetURI uri = new FreenetURI("KSK@test");
	private final Map<Integer, String> positionsMap = range(0, 3).mapToObj(i -> new SimpleEntry<>(i, String.valueOf(i))).collect(toMap(Entry::getKey, Entry::getValue));

	public TermPageEntryTest() throws MalformedURLException {
	}

}
