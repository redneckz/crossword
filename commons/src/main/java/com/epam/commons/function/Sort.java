package com.epam.commons.function;

import fj.F;
import fj.Function;
import fj.Ord;
import static fj.Ord.ord;
import fj.Ordering;

/**
 *
 * @author Alexander_Alexandrov
 */
public final class Sort {
	
	public static <T> Ord<T> indifferent() {
		return ord(Function.<T, F<T, Ordering>>constant(Function.<T, Ordering>constant(Ordering.EQ)));
	}
	
	private Sort() {
	}
}
