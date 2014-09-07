package com.epam.commons.function;

import fj.F;
import fj.F2;
import fj.F3;
import static fj.Function.curry;

/**
 *
 * @author Александр
 */
public final class Chars {
	
	public static final F<String, F<Integer, Character>> charAt_ = curry(new F2<String, Integer, Character>() {

		@Override
		public Character f(final String str, final Integer index) {
			return str.charAt(index);
		}
	});
	
	public static final F3<String, Integer, Character, String> replaceCharAt_ = new F3<String, Integer, Character, String>() {

		@Override
		public String f(final String str, final Integer index, final Character ch) {
			return (str.substring(0, index) + ch + str.substring(index + 1));
		}
	};
	
	private Chars() {
	}
}
