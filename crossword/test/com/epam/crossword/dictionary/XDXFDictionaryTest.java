package com.epam.crossword.dictionary;

import com.epam.commons.process.ProcessMonitorAdapter;
import static fj.data.List.iterableList;
import static fj.data.List.list;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Alexander_Alexandrov
 */
public class XDXFDictionaryTest {
	
	/**
	 * Test of getWordsByPattern method, of class XDXFDictionary.
	 */
	@Test
	public void testGetWordsByPattern() throws XMLStreamException, IOException {
		XDXFDictionaryCreator dictionaryCreator = new XDXFDictionaryCreator();
		dictionaryCreator.registerMonitor(new ProcessMonitorAdapter<String>() {

			@Override
			public void onStart() {
				System.out.println("onStart");
			}

			@Override
			public void onStep(String data) {
				System.out.println("onStep: " + data);
			}

			@Override
			public void onFail(Exception ex) {
				System.out.println("onFail");
			}

			@Override
			public void onEnd() {
				System.out.println("onEnd");
			}
		});
		Dictionary dictionary = dictionaryCreator.create(getClass().getResourceAsStream("/com/epam/crossword/dictionary/dict.xdxf"));
		assertTrue(list("стол").equals(iterableList(dictionary.getWordsByPattern("*тол"))));
	}
}
