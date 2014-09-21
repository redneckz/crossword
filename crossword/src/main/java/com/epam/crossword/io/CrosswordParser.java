package com.epam.crossword.io;

import com.epam.commons.function.Sort;
import com.epam.commons.io.InputStreamDecoder;
import com.epam.crossword.Crossword;
import com.epam.crossword.Word;
import fj.F;
import fj.F2;
import static fj.Function.bind;
import static fj.Function.partialApply2;
import static fj.Ord.intOrd;
import static fj.Ord.p2Ord;
import fj.P;
import fj.P2;
import fj.data.List;
import static fj.data.List.*;
import static fj.function.Booleans.not;
import static fj.function.Characters.isWhitespace;
import java.io.InputStream;
import java.util.Scanner;

/**
 *
 * @author Alexander_Alexandrov
 */
public final class CrosswordParser implements InputStreamDecoder<Crossword> {
	
	public static final F<Character, F<P2<Integer, Integer>, P2<Character, P2<Integer, Integer>>>> complementPoint_
			= P.<Character, P2<Integer, Integer>>p2();
	
	public static final F<P2<Character, P2<Integer, Integer>>, P2<Character, P2<Integer, Integer>>> rotatePoint_ = bind(
			P2.<Character, P2<Integer, Integer>>__1(),
			P2.<Integer, Integer>swap_().o(P2.<Character, P2<Integer, Integer>>__2()),
			P.<Character, P2<Integer, Integer>>p2());
	
	private static class Holder {
		
		private static final InputStreamDecoder<Crossword> INST = new CrosswordParser();
	}

	public static InputStreamDecoder<Crossword> inst() {
		return Holder.INST;
	}
	
	private CrosswordParser() {
	}
	
	@Override
	public Crossword decode(final InputStream input, final String encoding) {
		final List<String> lines = CrosswordParser.readLines(input, encoding);
		return new Crossword(CrosswordParser.getVerticalWords(CrosswordParser.getChars(lines))
				.append(CrosswordParser.getHorizontalWords(CrosswordParser.getChars(lines))));
	}
	
	private static List<Word> getHorizontalWords(final List<P2<Character, P2<Integer, Integer>>> chars) {
		return CrosswordParser.getVerticalWords(chars.map(rotatePoint_)).map(Word.rotate_);
	}
	
	private static List<Word> getVerticalWords(final List<P2<Character, P2<Integer, Integer>>> chars) {
		return chars.sort(p2Ord(Sort.<Character>indifferent(), p2Ord(intOrd, intOrd)))
				// свёртка соседних точек по вертикали в группы
				.foldLeft(new F2<List<List<P2<Character, P2<Integer, Integer>>>>, P2<Character, P2<Integer, Integer>>, List<List<P2<Character, P2<Integer, Integer>>>>>() {

			@Override
			public List<List<P2<Character, P2<Integer, Integer>>>> f(final List<List<P2<Character, P2<Integer, Integer>>>> groups,
					final P2<Character, P2<Integer, Integer>> ch) {
				List<List<P2<Character, P2<Integer, Integer>>>> result;
				if (groups.isNotEmpty() && groups.head().last()._2()._1().equals(ch._2()._1())
						&& (Math.abs(groups.head().last()._2()._2() - ch._2()._2()) == 1)) {
					// добавить новую чтоку в конец головной группы
					result = groups.tail().conss(groups.head().snoc(ch));
				} else {
					// добавить новую группу из одной точки
					result = groups.conss(single(ch));
				}
				return result;
			}
		}, List.<List<P2<Character, P2<Integer, Integer>>>>nil()).filter(new F<List<P2<Character, P2<Integer, Integer>>>, Boolean>() {

			@Override
			public Boolean f(final List<P2<Character, P2<Integer, Integer>>> lineCharsGroup) {
				// фильтрация групп состоящих из более чем одной точки
				return (lineCharsGroup.length() > 1);
			}
		}).map(new F<List<P2<Character, P2<Integer, Integer>>>, Word>() {

			@Override
			public Word f(final List<P2<Character, P2<Integer, Integer>>> lineCharsGroup) {
				// преобразование групп соседних точек в слова
				return new Word(lineCharsGroup.head()._2(), asString(lineCharsGroup.map(P2.<Character, P2<Integer, Integer>>__1())), true);
			}
		});
	}
	
	private static List<P2<Character, P2<Integer, Integer>>> getChars(final List<String> lines) {
		return join(lines.zipIndex().map(new F<P2<String, Integer>, List<P2<Character, P2<Integer, Integer>>>>() {

			@Override
			public List<P2<Character, P2<Integer, Integer>>> f(final P2<String, Integer> line) {
				return CrosswordParser.getLineChars(line);
			}
		}));
	}
	
	private static List<P2<Character, P2<Integer, Integer>>> getLineChars(final P2<String, Integer> line) {
		return fromString(line._1()).zipIndex().filter(not(isWhitespace).o(P2.<Character, Integer>__1()))
				.map(bind(P2.<Character, Integer>__1(), partialApply2(P.<Integer, Integer>p2(), line._2()).o(P2.<Character, Integer>__2()),
				complementPoint_));
	}
	
	private static List<String> readLines(final InputStream input, final String encoding) {
		final Scanner scanner = new Scanner(input, encoding);
		List<String> result = List.<String>nil();
		try {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().toLowerCase();
				result = result.snoc(line.replaceAll("[^a-zа-я \\*]+", ""));
			}
		} finally {
			scanner.close();
		}
		return result;
	}
}
