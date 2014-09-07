package com.epam.crossword;

import com.epam.commons.graph.*;
import static com.epam.crossword.Decision.decisionPartCompletenessOrd_;
import com.epam.crossword.dictionary.Dictionary;
import static com.epam.crossword.dictionary.Dictionary.Op.isItemExistsInDictionary_;
import static fj.Equal.stringEqual;
import fj.F;
import static fj.Function.flip;
import static fj.Function.partialApply2;
import fj.P2;
import fj.data.List;
import static fj.data.List.iterableList;

/**
 *
 * @author Alexander_Alexandrov
 */
public final class DecisionsGraph implements DynamicGraph<Decision> {

	private final Decision head;
	private final Dictionary dictionary;

	public DecisionsGraph(final Crossword crossword, final Dictionary dictionary) {
		if (crossword == null) {
			throw new IllegalArgumentException();
		}
		this.head = new Decision(crossword);
		this.dictionary = dictionary;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Iterable<Vertex<Decision>> getVertices() {
		return List.<Vertex<Decision>>single(new VertexImpl<Decision>(head));
	}

	@Override
	public Iterable<Edge<Decision>> getEdgesBySource(final Vertex<Decision> source) {
		return getNextDecisions(source).map(EdgeImpl.<Decision>edge_(0.1d, source).o(VertexImpl.<Decision>vertex_()));
	}
	
	private List<Decision> getNextDecisions(final Vertex<Decision> source) {
		if (!hasNextDecisions(source.getData())) {
			return List.<Decision>nil();
		}
		return iterableList(source.getData().getEmptyParts()).sort(decisionPartCompletenessOrd_)
				.toOption().map(new F<P2<Word, String>, List<Decision>>() {

			@Override
			public List<Decision> f(final P2<Word, String> emptyDecisionPart) {
				return iterableList(dictionary.getWordsByPattern(emptyDecisionPart._2()))
						// вычитание из списка всех известных слов тех что уже в данном решении
						.minus(stringEqual, iterableList(source.getData().getFilledParts()).map(P2.<Word, String>__2()))
						// формирование из слов новых вариантов решения
						.map(Decision.decision_(source.getData(), emptyDecisionPart._1()));
			}
		}).orSome(List.<Decision>nil());
	}
	
	/**
	 * быстрая проверка на наличие решений из данной вершины
	 * 
	 * @param source
	 * @return 
	 */
	private boolean hasNextDecisions(final Decision decision) {
		return ((decision.getEmptyPartCount() > 0) && iterableList(decision.getEmptyParts()).forall(
				partialApply2(flip(isItemExistsInDictionary_), dictionary).o(P2.<Word, String>__2())));
	}
}
