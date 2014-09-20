package com.epam.commons.graph;

import static fj.Function.flip;
import fj.data.List;
import static fj.data.List.iterableList;
import fj.function.Doubles;

/**
 *
 * @author Александр
 */
public final class Path<VD> implements Edge<VD>, Comparable<Path<VD>> {

	public static <VD> Path<VD> nil() {
		return new Path<VD>();
	}
	
	private final List<Edge<VD>> edges;

	private Path() {
		edges = List.nil();
	}

	public Path(final Vertex<VD> vertex) {
		edges = List.<Edge<VD>>list(new EdgeImpl<VD>(0d, vertex, vertex));
	}

	public Path(final Iterable<Edge<VD>> edges) {
		if (!edges.iterator().hasNext()) {
			throw new IllegalArgumentException();
		}
		this.edges = iterableList(edges);
	}

	public Path(final Path<VD> path, final Edge<VD> nextEdge) {
		if ((path == null) || (nextEdge == null)) {
			throw new IllegalArgumentException();
		}
		this.edges = path.edges.snoc(nextEdge);
	}

	public boolean isEmpty() {
		return edges.isEmpty();
	}

	public boolean isNotEmpty() {
		return edges.isNotEmpty();
	}

	public Iterable<Edge<VD>> getEdges() {
		return edges;
	}

	@Override
	public double getWeight() {
		return edges.foldLeft(flip(Doubles.add.o(Edge.Op.<VD>weight_())), 0d);
	}

	@Override
	public Vertex<VD> getSource() {
		return (isNotEmpty() ? edges.head().getSource() : null);
	}

	@Override
	public Vertex<VD> getTarget() {
		return (isNotEmpty() ? edges.last().getTarget() : null);
	}

	@Override
	public int compareTo(Path<VD> other) {
		return (int) Math.signum(this.getWeight() - other.getWeight());
	}

	@Override
	public String toString() {
		return "Path{" + "edges=" + edges + '}';
	}
}
