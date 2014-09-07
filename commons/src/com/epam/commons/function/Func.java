package com.epam.commons.function;

import fj.F;
import static fj.Function.bind;
import fj.P2;

/**
 *
 * @author Александр
 */
public final class Func {
	
	public static <A, B, C> F<P2<A, B>, C> join2(final F<A, F<B, C>> f) {
		return bind(P2.<A, B>__1(), P2.<A, B>__2(), f);
	}
	
	private Func() {
	}
}
