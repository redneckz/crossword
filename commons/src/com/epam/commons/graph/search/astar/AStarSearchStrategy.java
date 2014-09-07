package com.epam.commons.graph.search.astar;

import com.epam.commons.function.Collections;
import com.epam.commons.graph.DynamicGraph;
import com.epam.commons.graph.Edge;
import com.epam.commons.graph.Path;
import com.epam.commons.graph.Vertex;
import com.epam.commons.graph.search.SearchStrategy;
import com.epam.commons.process.LongProcess;
import com.epam.commons.process.ProcessHelper;
import com.epam.commons.process.ProcessMonitor;
import fj.Effect;
import fj.F;
import fj.F2;
import static fj.Function.flip;
import static fj.Function.partialApply2;
import fj.Ord;
import static fj.Ord.optionOrd;
import fj.data.HashMap;
import static fj.data.List.iterableList;
import static fj.data.List.list;
import static fj.data.Option.some;
import java.util.*;

/**
 *
 * @author Александр
 */
public final class AStarSearchStrategy<VD> implements SearchStrategy<VD>, LongProcess<VD> {

	private final F2<Vertex<VD>, F<Path<VD>, Boolean>, Double> h;
	
	private final ProcessHelper<VD> processHelper;

	/**
	 * 
	 * @param h эвристическая функция оценки "расстояния" до цели из заданной вершины
	 */
	public AStarSearchStrategy(final F2<Vertex<VD>, F<Path<VD>, Boolean>, Double> h) {
		if (h == null) {
			throw new IllegalArgumentException();
		}
		this.h = h;
		this.processHelper = new ProcessHelper<VD>();
	}

	/**
	 * Реализация алгоритма поиска A*
	 * Смесь императивного и функционального подходов
	 * Императивная составляющая нужна для более точного контроля за ресурсами
	 * 
	 * @param graph граф поиска
	 * @param goal предикат фиксации цели поиска
	 * @return путь до цели
	 */
	@Override
	public Path<VD> search(final DynamicGraph<VD> graph, final F<Path<VD>, Boolean> goal) {
		if ((graph == null) || graph.isEmpty() || (goal == null)) {
			return null;
		}
		processHelper.fireStart();
		Path<VD> result = Path.nil();
		// множество обработанных вершин
		final Set<Vertex<VD>> closed = new HashSet<Vertex<VD>>();
		// очередь путей на обработку, упорядоченная оценочной функцией h
		final Queue<Path<VD>> openedQueue = new PriorityQueue<Path<VD>>(1, new Comparator<Path<VD>>() {

			@Override
			public int compare(final Path<VD> p0, final Path<VD> p1) {
				return (int) Math.signum(p0.getWeight() + h.f(p0.getTarget(), goal)
						- p1.getWeight() - h.f(p1.getTarget(), goal));
			}
		});
		// map путей на обработку
		final HashMap<Vertex<VD>, Path<VD>> openedMap = HashMap.<Vertex<VD>, Path<VD>>hashMap();
		// первая доступная вершина
		final Path<VD> start = new Path<VD>(graph.getVertices().iterator().next());
		openedQueue.offer(start);
		openedMap.set(start.getTarget(), start);
		while (!openedQueue.isEmpty()) {
			final Path<VD> current = openedQueue.poll();
			openedMap.delete(current.getTarget());
			closed.add(current.getTarget());
			processHelper.fireStep(current.getTarget().getData());
			// если цель достигнута то конец
			if (goal.f(current)) {
				result = current;
				break;
			}
			iterableList(graph.getEdgesBySource(current.getTarget()))
					// отфильтровать соседей, открытых на обработку
					.filter(partialApply2(flip(Collections.<Vertex<VD>>notContains_()).o(Edge.Op.<VD>target_()), closed))
					// преобразовать список соседей к списку соответствующих путей с учётом длины пути (weight)
					.map(bestNeighborPathMapper_(current, openedMap))
					// обновить список узлов на обработку наиболее выгодными путями до соседних вершин
					.foreach(new Effect<Path<VD>>() {

				@Override
				public void e(final Path<VD> neighborPath) {
					openedQueue.offer(neighborPath);
					openedMap.set(neighborPath.getTarget(), neighborPath);
				}
			});
		}
		processHelper.fireEnd();
		return result;
	}
	
	private static <VD> F<Edge<VD>, Path<VD>> bestNeighborPathMapper_(final Path<VD> current, final HashMap<Vertex<VD>, Path<VD>> openedMap) {
		return new F<Edge<VD>, Path<VD>>() {

			@Override
			public Path<VD> f(final Edge<VD> neighborEdge) {
				return list(some(new Path<VD>(current, neighborEdge)), openedMap.get(neighborEdge.getTarget()))
						.maximum(optionOrd(Ord.<Path<VD>>comparableOrd())).some();
			}
		};
	}

	@Override
	public void registerMonitor(ProcessMonitor<VD> monitor) {
		processHelper.registerMonitor(monitor);
	}

	@Override
	public void unregisterMonitor(ProcessMonitor<VD> monitor) {
		processHelper.unregisterMonitor(monitor);
	}
}