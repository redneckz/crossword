package com.epam.commons.graph;

import fj.F;

/**
 *
 * @author Александр
 */
public interface Vertex<VD> {

	final class Op {
		
		public static <VD> F<Vertex<VD>, VD> data_() {
			return new F<Vertex<VD>, VD>() {

				@Override
				public VD f(final Vertex<VD> vertex) {
					return vertex.getData();
				}
			};
		}
		
		private Op() {
		}
	}

	VD getData();
}
