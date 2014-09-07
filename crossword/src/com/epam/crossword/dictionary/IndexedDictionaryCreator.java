package com.epam.crossword.dictionary;

import static com.epam.crossword.dictionary.Dictionary.Op.isWordFitPatternContent_;
import fj.Effect;
import fj.data.HashMap;
import fj.data.List;
import static fj.data.List.iterableList;
import fj.data.Option;

/**
 *
 * @author Александр
 */
public final class IndexedDictionaryCreator implements DictionaryCreator<Iterable<String>> {

	private static final DictionaryCreator<Iterable<String>> inst = new IndexedDictionaryCreator();

	public static DictionaryCreator<Iterable<String>> getInst() {
		return inst;
	}

	private IndexedDictionaryCreator() {
	}

	@Override
	public Dictionary create(final Iterable<String> items) {
		final HashMap<Integer, List<String>> itemsLengthIndex = createIndex(items);
		return new Dictionary() {

			@Override
			public Iterable<String> getWordsByPattern(final String pattern) {
				return itemsLengthIndex.get(pattern.length()).orSome(List.<String>nil()).filter(isWordFitPatternContent_(pattern));
			}

			@Override
			public Option<String> getFirstWordByPattern(final String pattern) {
				return itemsLengthIndex.get(pattern.length()).orSome(List.<String>nil()).toOption();
			}
		};
	}

	private static HashMap<Integer, List<String>> createIndex(final Iterable<String> items) {
		final HashMap<Integer, List<String>> result = HashMap.<Integer, List<String>>hashMap();
		iterableList(items).foreach(new Effect<String>() {

			@Override
			public void e(final String word) {
				result.set(word.length(), result.get(word.length()).orSome(List.<String>nil()).snoc(word));
			}
		});
		return result;
	}
}
