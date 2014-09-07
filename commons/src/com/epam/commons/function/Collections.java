package com.epam.commons.function;

import fj.F;
import fj.F2;
import static fj.Function.compose2;
import static fj.Function.curry;
import static fj.function.Booleans.not;
import java.util.Collection;

/**
 *
 * @author Александр
 */
public final class Collections {
	
	public static <T> F<Collection<T>, F<T, Boolean>> contains_() {
		return curry(new F2<Collection<T>, T, Boolean>() {

			@Override
			public Boolean f(final Collection<T> collection, T el) {
				return collection.contains(el);
			}
		});
	}
	
	public static <T> F<Collection<T>, F<T, Boolean>> notContains_() {
		return compose2(not, Collections.<T>contains_());
	}
	
	private Collections() {
	}
}
