package com.epam.commons.graph;

import fj.F;
import fj.F2;
import fj.F3;
import static fj.Function.curry;

/**
 *
 * @author Александр
 */
public final class EdgeImpl<VD> implements Edge<VD> {
	
	public static <VD> F<Double, F<Vertex<VD>, F<Vertex<VD>, Edge<VD>>>> edge_() {
		return curry(new F3<Double, Vertex<VD>, Vertex<VD>, Edge<VD>>() {

			@Override
			public Edge<VD> f(final Double weight, final Vertex<VD> source, final Vertex<VD> target) {
				return new EdgeImpl(weight, source, target);
			}
		});
	}
	
	public static <VD> F<Vertex<VD>, F<Vertex<VD>, Edge<VD>>> edge_(final Double weight) {
		return curry(new F2<Vertex<VD>, Vertex<VD>, Edge<VD>>() {

			@Override
			public Edge<VD> f(final Vertex<VD> source, final Vertex<VD> target) {
				return new EdgeImpl(weight, source, target);
			}
		});
	}
	
	public static <VD> F<Vertex<VD>, Edge<VD>> edge_(final Double weight, final Vertex<VD> source) {
		return new F<Vertex<VD>, Edge<VD>>() {

			@Override
			public Edge<VD> f(final Vertex<VD> target) {
				return new EdgeImpl(weight, source, target);
			}
		};
	}

	private final double weight;
	private final Vertex<VD> source;
	private final Vertex<VD> target;

	public EdgeImpl(final double weight, final Vertex<VD> source, final Vertex<VD> target) {
		this.weight = weight;
		this.source = source;
		this.target = target;
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public Vertex<VD> getSource() {
		return source;
	}

	@Override
	public Vertex<VD> getTarget() {
		return target;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EdgeImpl<VD> other = (EdgeImpl<VD>) obj;
		if (Double.doubleToLongBits(this.weight) != Double.doubleToLongBits(other.weight)) {
			return false;
		}
		if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
			return false;
		}
		if (this.target != other.target && (this.target == null || !this.target.equals(other.target))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + (int) (Double.doubleToLongBits(this.weight) ^ (Double.doubleToLongBits(this.weight) >>> 32));
		hash = 79 * hash + (this.source != null ? this.source.hashCode() : 0);
		hash = 79 * hash + (this.target != null ? this.target.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return "EdgeImpl{" + "weight=" + weight + ", source=" + source + ", target=" + target + '}';
	}
}
