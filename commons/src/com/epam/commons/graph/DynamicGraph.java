package com.epam.commons.graph;

/**
 *
 * @author Александр
 */
public interface DynamicGraph<VD> {

	boolean isEmpty();

	Iterable<Vertex<VD>> getVertices();

	Iterable<Edge<VD>> getEdgesBySource(Vertex<VD> source);
}
