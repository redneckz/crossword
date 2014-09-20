package com.epam.crossword;

import com.epam.commons.graph.DynamicGraph;
import com.epam.commons.graph.Path;
import com.epam.commons.graph.Vertex;
import com.epam.commons.graph.search.SearchStrategy;
import com.epam.commons.graph.search.astar.AStarSearchStrategy;
import com.epam.commons.process.LongProcess;
import com.epam.commons.process.ProcessMonitor;
import fj.F;
import fj.F2;

/**
 *
 * @author Alexander_Alexandrov
 */
public final class DecisionSearchStrategy implements SearchStrategy<Decision>, LongProcess<Decision> {

	private final AStarSearchStrategy<Decision> strategy;

	public DecisionSearchStrategy() {
		strategy = new AStarSearchStrategy<Decision>(new F2<Vertex<Decision>, F<Path<Decision>, Boolean>, Double>() {

			@Override
			public Double f(final Vertex<Decision> vertex, final F<Path<Decision>, Boolean> goal) {
				return (double) vertex.getData().getEmptyPartCount();
			}
		});
	}

	@Override
	public Path<Decision> search(final DynamicGraph<Decision> graph, final F<Path<Decision>, Boolean> goal) {
		return strategy.search(graph, goal);
	}

	@Override
	public void registerMonitor(ProcessMonitor<Decision> monitor) {
		strategy.registerMonitor(monitor);
	}

	@Override
	public void unregisterMonitor(ProcessMonitor<Decision> monitor) {
		strategy.unregisterMonitor(monitor);
	}
}
