package com.epam.commons.graph;

import fj.F;

/**
 *
 * @author Александр
 */
public interface Edge<VD> {

	final class Op {
		
		public static <VD> F<Edge<VD>, Double> weight_() {
			return new F<Edge<VD>, Double>() {

				@Override
				public Double f(final Edge<VD> edge) {
					return edge.getWeight();
				}
			};
		}
		
		public static <VD> F<Edge<VD>, Vertex<VD>> source_() {
			return new F<Edge<VD>, Vertex<VD>>() {

				@Override
				public Vertex<VD> f(final Edge<VD> edge) {
					return edge.getSource();
				}
			};
		}
		
		public static <VD> F<Edge<VD>, Vertex<VD>> target_() {
			return new F<Edge<VD>, Vertex<VD>>() {

				@Override
				public Vertex<VD> f(final Edge<VD> edge) {
					return edge.getTarget();
				}
			};
		}
		
		private Op() {
		}
	}

	double getWeight();

	Vertex<VD> getSource();

	Vertex<VD> getTarget();
}
