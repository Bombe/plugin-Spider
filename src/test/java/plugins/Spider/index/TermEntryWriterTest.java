package plugins.Spider.index;

import freenet.keys.FreenetURI;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class TermEntryWriterTest {

	@Test
	public void termEntryWriterCanWriteTermPageEntryToStream() throws IOException {
		TermPageEntry termPageEntry = new TermPageEntry("subject1", 0.2f, new FreenetURI("KSK@test"), "title", positionsMap);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			termEntryWriter.writeObject(termPageEntry, outputStream);
			try (DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
				verifyTermEntrySerialVersionUid(dataInputStream);
				verifyEntryTypeOrdinal(dataInputStream);
				verifySubject(dataInputStream, "subject1");
				verifyRelevance(dataInputStream, 0.2f);
				verifyPageUri(dataInputStream);
				verifyPositionCountAndTitle(dataInputStream, ~3, equalTo("title"));
				verifyPositionsAndFragments(dataInputStream, 0, "0", 1, "", 2, "2");
				assertThat(dataInputStream.read(), equalTo(-1));
			}
		}
	}

	@Test
	public void termEntryWriterCanWriteTermPageEntryWithoutPositionsToStream() throws IOException {
		TermPageEntry termPageEntry = new TermPageEntry("subject2", 0.3f, new FreenetURI("KSK@test"), "title", null);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			termEntryWriter.writeObject(termPageEntry, outputStream);
			try (DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
				verifyTermEntrySerialVersionUid(dataInputStream);
				verifyEntryTypeOrdinal(dataInputStream);
				verifySubject(dataInputStream, "subject2");
				verifyRelevance(dataInputStream, 0.3f);
				verifyPageUri(dataInputStream);
				verifyPositionCountAndTitle(dataInputStream, ~0, equalTo("title"));
				assertThat(dataInputStream.read(), equalTo(-1));
			}
		}
	}

	@Test
	public void termEntryWriterCanWriteTermPageEntryWithoutFragmentsToStream() throws IOException {
		TermPageEntry termPageEntry = new TermPageEntry("subject3", 0.4f, new FreenetURI("KSK@test"), "title", null);
		termPageEntry.putPosition(1);
		termPageEntry.putPosition(3);
		termPageEntry.putPosition(7);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			termEntryWriter.writeObject(termPageEntry, outputStream);
			try (DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
				verifyTermEntrySerialVersionUid(dataInputStream);
				verifyEntryTypeOrdinal(dataInputStream);
				verifySubject(dataInputStream, "subject3");
				verifyRelevance(dataInputStream, 0.4f);
				verifyPageUri(dataInputStream);
				verifyPositionCountAndTitle(dataInputStream, ~3, equalTo("title"));
				verifyPositionsAndFragments(dataInputStream, 1, "", 3, "", 7, "");
				assertThat(dataInputStream.read(), equalTo(-1));
			}
		}
	}

	@Test
	public void termEntryWriterCanWriteTermPageEntryWithoutTitleToStream() throws IOException {
		TermPageEntry termPageEntry = new TermPageEntry("subject4", 0.5f, new FreenetURI("KSK@test"), (String) null, positionsMap);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			termEntryWriter.writeObject(termPageEntry, outputStream);
			try (DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
				verifyTermEntrySerialVersionUid(dataInputStream);
				verifyEntryTypeOrdinal(dataInputStream);
				verifySubject(dataInputStream, "subject4");
				verifyRelevance(dataInputStream, 0.5f);
				verifyPageUri(dataInputStream);
				verifyPositionCountAndTitle(dataInputStream, 3, nullValue());
				verifyPositionsAndFragments(dataInputStream, 0, "0", 1, "", 2, "2");
				assertThat(dataInputStream.read(), equalTo(-1));
			}
		}
	}

	private static void verifyTermEntrySerialVersionUid(DataInputStream dataInputStream) throws IOException {
		long termEntrySerialVersionUid = dataInputStream.readLong();
		assertThat(termEntrySerialVersionUid, equalTo(0xF23194B7F015560CL));
	}

	private static void verifyEntryTypeOrdinal(DataInputStream dataInputStream) throws IOException {
		int entryTypeOrdinal = dataInputStream.readInt();
		assertThat(entryTypeOrdinal, equalTo(TermEntry.EntryType.PAGE.ordinal()));
	}

	private static void verifySubject(DataInputStream dataInputStream, String subject) throws IOException {
		assertThat(dataInputStream.readUTF(), equalTo(subject));
	}

	private static void verifyRelevance(DataInputStream dataInputStream, float relevance) throws IOException {
		assertThat(dataInputStream.readFloat(), equalTo(relevance));
	}

	private static void verifyPageUri(DataInputStream dataInputStream) throws IOException {
		short freenetUriByteLength = dataInputStream.readShort();
		byte[] freenetUriBytes = new byte[freenetUriByteLength];
		dataInputStream.readFully(freenetUriBytes);
		assertThat(freenetUriByteLength, equalTo((short) 11));
		assertThat(freenetUriBytes, equalTo(new byte[] { 0x03, 0x00, 0x04, 't', 'e', 's', 't', 0x00, 0x00, 0x00, 0x00 }));
	}

	private static void verifyPositionCountAndTitle(DataInputStream dataInputStream, int positionCount, Matcher<? super String> titleMatcher) throws IOException {
		int positions = dataInputStream.readInt();
		String title = null;
		if (positions < 0) {
			title = dataInputStream.readUTF();
		}
		assertThat(positions, equalTo(positionCount));
		assertThat(title, titleMatcher);
	}

	private static void verifyPositionsAndFragments(DataInputStream dataInputStream, int firstPosition, String firstFragment, int secondPosition, String secondFragment, int thirdPosition, String thirdFragment) throws IOException {
		assertThat(dataInputStream.readInt(), equalTo(firstPosition));
		assertThat(dataInputStream.readUTF(), equalTo(firstFragment));
		assertThat(dataInputStream.readInt(), equalTo(secondPosition));
		assertThat(dataInputStream.readUTF(), equalTo(secondFragment));
		assertThat(dataInputStream.readInt(), equalTo(thirdPosition));
		assertThat(dataInputStream.readUTF(), equalTo(thirdFragment));
	}

	private final TermEntryWriter termEntryWriter = new TermEntryWriter();
	private final Map<Integer, String> positionsMap = new HashMap<>();

	{
		positionsMap.put(0, "0");
		positionsMap.put(1, null);
		positionsMap.put(2, "2");
	}

}
