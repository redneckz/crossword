package com.epam.commons.graph.search.astar;

import com.epam.commons.graph.*;
import fj.F;
import fj.F2;
import fj.data.List;
import static fj.data.List.iterableList;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Александр
 */
public class AStarSearchStrategyTest {

	/**
	 * Test of search method, of class AStarSearchStrategy.
	 */
	@Test
	public void testSearch() {
		final String start = "z";
		DynamicGraph<String> graph = new DynamicGraph<String>() {

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public Iterable<Vertex<String>> getVertices() {
				return List.<Vertex<String>>list(new VertexImpl<String>(start));
			}

			@Override
			public Iterable<Edge<String>> getEdgesBySource(final Vertex<String> source) {
				return List.<Edge<String>>list(new EdgeImpl<String>(1d, source, new VertexImpl(source.getData() + "z")),
						new EdgeImpl<String>(1d, source, new VertexImpl(source.getData() + "zz")));
			}
		};
		final String expResult = "zzzzzzzzzzzzzzzzzzzzz";
		AStarSearchStrategy<String> searchStrategy = new AStarSearchStrategy<String>(new F2<Vertex<String>, F<Path<String>, Boolean>, Double>(){

			@Override
			public Double f(final Vertex<String> vertex, final F<Path<String>, Boolean> goal) {
				return (double) Math.abs(vertex.getData().length() - expResult.length());
			}
		});
		Path<String> result = searchStrategy.search(graph, new F<Path<String>, Boolean>() {

			@Override
			public Boolean f(final Path<String> path) {
				return (path.isNotEmpty() && expResult.equals(path.getTarget().getData()));
			}
		});
		assertEquals(start, result.getSource().getData());
		assertEquals((double) (expResult.length() / 2), result.getWeight(), 0.1d);
		assertEquals((expResult.length() / 2) + 1, iterableList(result.getEdges()).length());
		assertEquals(expResult, result.getTarget().getData());
	}
}
