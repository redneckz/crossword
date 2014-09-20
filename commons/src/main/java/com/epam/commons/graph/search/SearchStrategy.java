package com.epam.commons.graph.search;

import com.epam.commons.graph.DynamicGraph;
import com.epam.commons.graph.Path;
import fj.F;

/**
 *
 * @author Александр
 */
public interface SearchStrategy<VD> {

	Path<VD> search(DynamicGraph<VD> graph, F<Path<VD>, Boolean> goal);
}
