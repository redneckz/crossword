package com.epam.crossword.io;

import com.epam.crossword.Crossword;
import com.epam.crossword.Word;
import fj.Equal;
import static fj.data.List.iterableList;
import static fj.data.List.list;
import java.io.IOException;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Alexander_Alexandrov
 */
public class CrosswordParserTest {
	
	/**
	 * Test of parse method, of class CrosswordParser.
	 */
	@Test
	public void testParse() throws IOException {
		Crossword crossword = CrosswordParser.getInst().decode(getClass().getResourceAsStream("/com/epam/crossword/io/crossword_x4.txt"), "UTF-8");
		assertTrue(list(new Word(1, 0, "*о****", true), new Word(5, 0, "*д****", true), new Word(0, 1, "монойд", false), new Word(0, 5, "******", false))
				.minus(Equal.<Word>anyEqual(), iterableList(crossword.getWords())).isEmpty());
	}
}
