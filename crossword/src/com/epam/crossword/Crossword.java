package com.epam.crossword;

import fj.Equal;
import static fj.Equal.listEqual;
import fj.Hash;
import static fj.Hash.listHash;
import static fj.Ord.intOrd;
import fj.data.List;
import static fj.data.List.iterableList;

/**
 *
 * @author Александр
 */
public final class Crossword {
	
	public static final Crossword NIL = new Crossword(List.<Word>nil());

	private final List<Word> words;

	public Crossword(final Iterable<Word> words) {
		if (words == null) {
			throw new IllegalArgumentException();
		}
		this.words = iterableList(words);
	}
	
	public boolean isEmpty() {
		return words.isEmpty();
	}
	
	public boolean isNotEmpty() {
		return words.isNotEmpty();
	}

	public Iterable<Word> getWords() {
		return words;
	}
	
	public int getWordCount() {
		return words.length();
	}
	
	public int getWidth() {
		return (words.isNotEmpty() ? words.map(Word.width_).maximum(intOrd) : 0);
	}
	
	public int getHeight() {
		return (words.isNotEmpty() ? words.map(Word.height_).maximum(intOrd) : 0);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Crossword other = (Crossword) obj;
		return listEqual(Equal.<Word>anyEqual()).eq(this.words, other.words);
	}

	@Override
	public int hashCode() {
		return listHash(Hash.<Word>anyHash()).hash(words);
	}
}
