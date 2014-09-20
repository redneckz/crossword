package com.epam.crossword;

import fj.F;
import fj.F2;
import static fj.Function.curry;
import static fj.P.p;
import fj.P2;
import fj.data.Option;
import static fj.data.Option.none;
import static fj.data.Option.some;

/**
 *
 * @author Alexander_Alexandrov
 */
public final class Word {
	
	public static final F<Word, String> template_ = new F<Word, String>() {

		@Override
		public String f(final Word word) {
			return word.getTemplate();
		}
	};
	
	public static final F<Word, Integer> width_ = new F<Word, Integer>() {

		@Override
		public Integer f(final Word word) {
			return (word.isHorizontal() ? (word.getX() + word.getLength()) : 0);
		}
	};
	
	public static final F<Word, Integer> height_ = new F<Word, Integer>() {

		@Override
		public Integer f(final Word word) {
			return (word.isVertical() ? (word.getY() + word.getLength()) : 0);
		}
	};
	
	public static final F<Word, Word> rotate_ = new F<Word, Word>() {

		@Override
		public Word f(final Word word) {
			return new Word(word.getY(), word.getX(), word.getTemplate(), word.isHorizontal());
		}
	};
	
	public static final F<Word, F<P2<Integer, Integer>, Option<Integer>>> offset_ = curry(new F2<Word, P2<Integer, Integer>, Option<Integer>>() {

		@Override
		public Option<Integer> f(final Word word, final P2<Integer, Integer> point) {
			return word.getOffset(point);
		}
	});
	
	public static final F<Word, F<Word, Option<P2<Integer, Integer>>>> offsets_ = curry(new F2<Word, Word, Option<P2<Integer, Integer>>>() {

		@Override
		public Option<P2<Integer, Integer>> f(final Word word, final Word other) {
			return word.getOffsets(other);
		}
	});

	private final int x;
	private final int y;
	private final String template;
	private final boolean vertical;

	public Word(final int x, final int y, final String template, final boolean vertical) {
		if ((x < 0) || (y < 0) || (template == null) || (template.length() < 2)) {
			throw new IllegalArgumentException();
		}
		this.x = x;
		this.y = y;
		this.template = template;
		this.vertical = vertical;
	}
	
	public Word(final P2<Integer, Integer> point, final String template, final boolean vertical) {
		if ((point == null) || (template == null) || (template.length() < 2)) {
			throw new IllegalArgumentException();
		}
		this.x = point._1();
		this.y = point._2();
		this.template = template;
		this.vertical = vertical;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getLength() {
		return template.length();
	}

	public String getTemplate() {
		return template;
	}

	public boolean isVertical() {
		return vertical;
	}
	
	public boolean isHorizontal() {
		return !vertical;
	}
	
	/**
	 * 
	 * @param word
	 * @return (смещение внутри данного слова до пересечения, смещение внутри переданного слова до пересечения)
	 */
	public Option<P2<Integer, Integer>> getOffsets(final Word word) {
		Option<P2<Integer, Integer>> result;
		if (vertical != word.vertical) {
			if (vertical) {
				result = some(p(word.y - y, x - word.x));
			} else {
				result = some(p(word.x - x, y - word.y));
			}
		} else {
			result = none();
		}
		return result.filter(new F<P2<Integer, Integer>, Boolean>() {

			@Override
			public Boolean f(final P2<Integer, Integer> offsets) {
				return ((offsets._1() >= 0) && (offsets._1() < getLength()) && (offsets._2() >= 0) && (offsets._2() < word.getLength()));
			}
		});
	}
	
	/**
	 * 
	 * @param point
	 * @return смещение внутри данного слова до пересечения с переданной точкой
	 */
	public Option<Integer> getOffset(final P2<Integer, Integer> point) {
		Option<Integer> result;
		if (vertical && (point._1() == this.x)) {
			result = some(point._2() - this.y);
		} else if (!vertical && (point._2() == this.y)) {
			result = some(point._1() - this.x);
		} else {
			result = none();
		}
		return result.filter(new F<Integer, Boolean>() {

			@Override
			public Boolean f(final Integer offset) {
				return ((offset >= 0) && (offset < getLength()));
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Word other = (Word) obj;
		if (this.x != other.x) {
			return false;
		}
		if (this.y != other.y) {
			return false;
		}
		if ((this.template == null) ? (other.template != null) : !this.template.equals(other.template)) {
			return false;
		}
		if (this.vertical != other.vertical) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + this.x;
		hash = 29 * hash + this.y;
		hash = 29 * hash + (this.template != null ? this.template.hashCode() : 0);
		hash = 29 * hash + (this.vertical ? 1 : 0);
		return hash;
	}

	@Override
	public String toString() {
		return "Word{" + "x=" + x + ", y=" + y + ", template=" + template + ", vertical=" + vertical + '}';
	}
}
