package com.epam.crossword.dictionary;

import static com.epam.commons.function.Func.join2;
import static fj.Equal.charEqual;
import static fj.Equal.intEqual;
import fj.F;
import fj.F2;
import fj.Function;
import static fj.Function.curry;
import fj.P2;
import fj.data.List;
import static fj.data.List.fromString;
import fj.data.Option;
import static fj.data.Option.none;
import static fj.function.Booleans.or;
import fj.function.Strings;

/**
 *
 * @author Александр
 */
public interface Dictionary {
	
	Dictionary NIL = new Dictionary() {

		@Override
		public Iterable<String> getWordsByPattern(final String pattern) {
			return List.<String>nil();
		}
		
		@Override
		public Option<String> getFirstWordByPattern(final String pattern) {
			return none();
		}
	};

	Character PATTERN_EMPTY_CHAR = '*';
	
	final class Op {

		public static F<Dictionary, F<String, Boolean>> isItemExistsInDictionary_ = curry(new F2<Dictionary, String, Boolean>() {

			@Override
			public Boolean f(final Dictionary dictionary, final String pattern) {
				return dictionary.getFirstWordByPattern(pattern).isSome();
			}
		});
		
		public static F<String, Boolean> isWordFitPatternLength_(final String pattern) {
			return intEqual.eq(pattern.length()).o(Strings.length);
		}

		public static F<String, Boolean> isWordFitPatternContent_(final String pattern) {
			return new F<String, Boolean>() {

				@Override
				public Boolean f(final String word) {
					return fromString(word).zip(fromString(pattern)).forall(Function.bind(
							charEqual.eq(PATTERN_EMPTY_CHAR).o(P2.<Character, Character>__2()),
							join2(curry(charEqual.eq())),
							or));
				}
			};
		}
		
		private Op() {
		}
	}

	Iterable<String> getWordsByPattern(String pattern);
	
	Option<String> getFirstWordByPattern(String pattern);
}
