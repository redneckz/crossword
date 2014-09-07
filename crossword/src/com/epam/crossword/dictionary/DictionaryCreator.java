package com.epam.crossword.dictionary;

import com.epam.commons.creational.Creator;

/**
 *
 * @author Александр
 */
public interface DictionaryCreator<A> extends Creator<A, Dictionary> {

	@Override
	Dictionary create(A params);
}
