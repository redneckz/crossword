package com.epam.crossword.dictionary;

import static fj.data.List.iterableList;
import static fj.data.List.list;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Александр
 */
public class SimpleDictionaryTest {

	@Test
	public void testGetWordsByPattern() {
		Dictionary instance = SimpleDictionaryCreator.getInst().create(list("book", "coala", "table", "goal", "error", "monoid"));
		assertTrue(list("book", "goal").equals(iterableList(instance.getWordsByPattern("*o**"))));
		assertTrue(list("error").equals(iterableList(instance.getWordsByPattern("*rr**"))));
		assertTrue(list("coala", "table", "error").equals(iterableList(instance.getWordsByPattern("*****"))));
		assertTrue(instance.getFirstWordByPattern("coala").isSome());
		assertTrue(instance.getFirstWordByPattern("********").isNone());
	}
}
