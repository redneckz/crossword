package com.epam.crossword.dictionary;

import static com.epam.crossword.dictionary.Dictionary.Op.isWordFitPatternContent_;
import static com.epam.crossword.dictionary.Dictionary.Op.isWordFitPatternLength_;
import fj.Function;
import static fj.data.List.iterableList;
import fj.data.Option;
import static fj.function.Booleans.and;
import static fj.function.Booleans.not;

/**
 *
 * @author Александр
 */
public final class SimpleDictionaryCreator implements DictionaryCreator<Iterable<String>> {
	
	private static final DictionaryCreator<Iterable<String>> inst = new SimpleDictionaryCreator();

	public static DictionaryCreator<Iterable<String>> getInst() {
		return inst;
	}

	private SimpleDictionaryCreator() {
	}

	@Override
	public Dictionary create(final Iterable<String> items) {
		return new Dictionary() {

			@Override
			public Iterable<String> getWordsByPattern(final String pattern) {
				// офильтровать слова по длине шаблона
				return iterableList(items).filter(Function.bind(isWordFitPatternLength_(pattern),
						isWordFitPatternContent_(pattern), and));
			}

			@Override
			public Option<String> getFirstWordByPattern(final String pattern) {
				return iterableList(items).dropWhile(not(Function.bind(isWordFitPatternLength_(pattern),
						isWordFitPatternContent_(pattern), and))).toOption();
			}
		};
	}
}
