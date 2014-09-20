package com.epam.crossword;

import static com.epam.commons.function.Chars.charAt_;
import static com.epam.commons.function.Chars.replaceCharAt_;
import static com.epam.crossword.dictionary.Dictionary.PATTERN_EMPTY_CHAR;
import static com.epam.crossword.io.CrosswordParser.complementPoint_;
import static fj.Equal.*;
import static fj.Function.*;
import static fj.Hash.*;
import static fj.P.p;
import fj.*;
import fj.data.List;
import static fj.data.List.fromString;
import static fj.data.List.iterableList;
import fj.data.Option;
import static fj.function.Booleans.not;
import fj.function.Strings;

/**
 *
 * @author Alexander_Alexandrov
 */
public final class Decision {
	
	public static final F<P2<Word, String>, Boolean> isDecisionPartEmpty_ = partialApply2(flip(Strings.contains),
			PATTERN_EMPTY_CHAR.toString()).o(P2.<Word, String>__2());
	
	public static final F<P2<Word, String>, Boolean> isDecisionPartFilled_ = not(isDecisionPartEmpty_);
	
	public static final F<Word, P2<Word, String>> word2DecisionPartMapper_ = Function.bind(
			Function.<Word>identity(), Word.template_, P.<Word, String>p2());
	
	// степень заполненности части решения (кол-во заполенных клеток на общее кол-во клеток)
	public static final F<P2<Word, String>, Double> decisionPartCompleteness_ = new F<P2<Word, String>, Double>() {

		@Override
		public Double f(P2<Word, String> decisionPart) {
			return (fromString(decisionPart._2()).filter(not(Equal.charEqual.eq(PATTERN_EMPTY_CHAR))).length() / (double) decisionPart._2().length());
		}
	};
	
	// "сортировщик" по убыванию степени заполненности
	public static final Ord<P2<Word, String>> decisionPartCompletenessOrd_ = Ord.ord(
			flip(Ord.doubleOrd.compare().o(decisionPartCompleteness_)).o(decisionPartCompleteness_));
	
	public static final F<Decision, List<P2<Character, P2<Integer, Integer>>>> decisionChars_ = new F<Decision, List<P2<Character, P2<Integer, Integer>>>>() {

		@Override
		public List<P2<Character, P2<Integer, Integer>>> f(final Decision decision) {
			return List.range(0, decision.getWidth()).bind(List.range(0, decision.getHeight()), P.<Integer, Integer>p2())
				// дополнение каждой точки символом из решения
				.map(Function.bind(partialApply2(flip(decisionCharAt_), decision), Function.<P2<Integer, Integer>>identity(), complementPoint_));
		}
	};
	
	public static final F<Decision, F<P2<Integer, Integer>, Character>> decisionCharAt_ = curry(new F2<Decision, P2<Integer, Integer>, Character>() {

		@Override
		public Character f(final Decision decision, final P2<Integer, Integer> point) {
			
			return decision.getChar(point).orSome(' ');
		}
	});
	
	public static F<String, Decision> decision_(final Decision decision, final Word word) {
		return new F<String, Decision>() {

			@Override
			public Decision f(final String item) {
				return new Decision(decision, p(word, item));
			}
		};
	}
	
	public static F<P2<Word, String>, Option<Character>> decisionPartCharAt_(final P2<Integer, Integer> point) {
		return bind(charAt_.o(P2.<Word, String>__2()), partialApply2(Word.offset_, point).o(P2.<Word, String>__1()),
				Option.<Integer, Character>map());
	}
	
	private final Crossword crossword;
	private final List<P2<Word, String>> decision;

	public Decision(final Crossword crossword) {
		if (crossword == null) {
			throw new IllegalArgumentException();
		}
		this.crossword = crossword;
		this.decision = iterableList(crossword.getWords()).map(word2DecisionPartMapper_);
	}
	
	public Decision(final Decision decision, final P2<Word, String> newDecisionPart) {
		if ((decision == null) || (newDecisionPart == null)) {
			throw new IllegalArgumentException();
		}
		this.crossword = decision.crossword;
		this.decision = decision.decision.map(new F<P2<Word, String>, P2<Word, String>>() {

			@Override
			public P2<Word, String> f(final P2<Word, String> decisionPart) {
				return getDecisionPartIntersectedByOtherDecisionPart(decisionPart, newDecisionPart);
			}
		});
	}
	
	private static P2<Word, String> getDecisionPartIntersectedByOtherDecisionPart(final P2<Word, String> decisionPart,
			final P2<Word, String> otherDecisionPart) {
		P2<Word, String> result;
		final Option<P2<Integer, Integer>> offsets = decisionPart._1().getOffsets(otherDecisionPart._1());
		// если слова пересекаются
		if (offsets.isSome()) {
			// подставляем букву из newDecisionPart в decisionPart
			result = p(decisionPart._1(), replaceCharAt_.f(decisionPart._2(),
					offsets.some()._1(), otherDecisionPart._2().charAt(offsets.some()._2())));
		// если это одно и то же слово
		} else if (decisionPart._1().equals(otherDecisionPart._1())) {
			result = otherDecisionPart;
		} else {
			result = decisionPart;
		}
		return result;
	}

	public Crossword getCrossword() {
		return crossword;
	}

	public Iterable<P2<Word, String>> getDecision() {
		return decision;
	}
	
	public Option<Character> getChar(final P2<Integer, Integer> point) {
		return decision.find(Option.<Character>isSome_().o(decisionPartCharAt_(point)))
				.map(Option.<Character>fromSome().o(decisionPartCharAt_(point)));
	}

	public int getWidth() {
		return crossword.getWidth();
	}

	public int getHeight() {
		return crossword.getHeight();
	}
	
	public Iterable<P2<Word, String>> getFilledParts() {
		return getFilledPartsList();
	}
	
	public int getFilledPartCount() {
		return getFilledPartsList().length();
	}
	
	public Iterable<P2<Word, String>> getEmptyParts() {
		return getEmptyPartsList();
	}
	
	public int getEmptyPartCount() {
		return getEmptyPartsList().length();
	}
	
	private List<P2<Word, String>> getFilledPartsList() {
		return decision.filter(isDecisionPartFilled_);
	}
	
	private List<P2<Word, String>> getEmptyPartsList() {
		return decision.filter(isDecisionPartEmpty_);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Decision other = (Decision) obj;
		return (this.crossword.equals(other.crossword) && listEqual(p2Equal(Equal.<Word>anyEqual(), stringEqual))
				.eq(this.decision, other.decision));
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = (37 * hash + this.crossword.hashCode());
		hash = (37 * hash + listHash(p2Hash(Hash.<Word>anyHash(), stringHash)).hash(this.decision));
		return hash;
	}
}
