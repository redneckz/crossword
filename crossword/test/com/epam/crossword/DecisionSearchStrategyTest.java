package com.epam.crossword;

import com.epam.commons.graph.Path;
import com.epam.commons.process.ProcessMonitor;
import com.epam.commons.process.ProcessMonitorAdapter;
import com.epam.crossword.dictionary.Dictionary;
import com.epam.crossword.dictionary.XDXFDictionaryCreator;
import com.epam.crossword.io.CrosswordParser;
import com.epam.crossword.io.DecisionEncoder;
import fj.F;
import static fj.data.List.iterableList;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Alexander_Alexandrov
 */
public class DecisionSearchStrategyTest {
	
	@Test
	public void testSearch() throws XMLStreamException, IOException {
		Crossword crossword = CrosswordParser.getInst().decode(getClass().getResourceAsStream("/com/epam/crossword/io/crossword_x4.txt"), "UTF-8");
		XDXFDictionaryCreator dictionaryCreator = new XDXFDictionaryCreator();
		dictionaryCreator.registerMonitor(new ProcessMonitorAdapter<String>() {

			@Override
			public void onStart() {
				System.out.println("XDXFDictionaryCreator onStart");
			}

			@Override
			public void onStep(String data) {
				System.out.println("XDXFDictionaryCreator onStep: " + data);
			}

			@Override
			public void onFail(Exception ex) {
				System.out.println("XDXFDictionaryCreator onFail");
			}

			@Override
			public void onEnd() {
				System.out.println("XDXFDictionaryCreator onEnd");
			}
		});
		Dictionary dictionary = dictionaryCreator.create(getClass().getResourceAsStream("/com/epam/crossword/dictionary/dict.xdxf"));
		DecisionSearchStrategy searchStrategy = new DecisionSearchStrategy();
		searchStrategy.registerMonitor(new ProcessMonitor<Decision>() {

			@Override
			public void onStart() {
				System.out.println("DecisionSearchStrategy onStart");
			}

			@Override
			public void onStep(Decision data) {
				System.out.println("DecisionSearchStrategy onStep: " + data.getEmptyPartCount());
			}

			@Override
			public void onFail(Exception ex) {
				System.out.println("DecisionSearchStrategy onFail");
			}

			@Override
			public void onEnd() {
				System.out.println("DecisionSearchStrategy onEnd");
			}
		});
		Path<Decision> result = searchStrategy.search(new DecisionsGraph(crossword, dictionary), new F<Path<Decision>, Boolean>() {

			@Override
			public Boolean f(Path<Decision> path) {
				return (path.isNotEmpty() && (0 == path.getTarget().getData().getEmptyPartCount()));
			}
		});
		assertEquals(0, result.getTarget().getData().getEmptyPartCount());
		assertEquals(4, iterableList(result.getTarget().getData().getDecision()).length());
		DecisionEncoder.getInst().encode(result.getTarget().getData(), new FileOutputStream("decision.txt"), "UTF-8");
	}
}
