package com.epam.commons.graph;

import fj.F;

/**
 *
 * @author Александр
 */
public final class VertexImpl<VD> implements Vertex<VD> {
	
	public static <VD> F<VD, Vertex<VD>> vertex_() {
		return new F<VD, Vertex<VD>>() {

			@Override
			public Vertex<VD> f(final VD data) {
				return new VertexImpl(data);
			}
		};
	}

	private final VD data;

	public VertexImpl(final VD data) {
		this.data = data;
	}

	@Override
	public VD getData() {
		return data;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final VertexImpl<VD> other = (VertexImpl<VD>) obj;
		if (this.data != other.data && (this.data == null || !this.data.equals(other.data))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 23 * hash + (this.data != null ? this.data.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return "VertexImpl{" + "data=" + data + '}';
	}
}
