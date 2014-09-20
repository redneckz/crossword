package com.epam.crossword.io;

import com.epam.commons.function.Sort;
import com.epam.commons.io.OutputStreamEncoder;
import com.epam.crossword.Decision;
import static com.epam.crossword.Decision.decisionChars_;
import static com.epam.crossword.io.CrosswordParser.rotatePoint_;
import fj.Effect;
import static fj.Equal.intEqual;
import static fj.Equal.p2Equal;
import static fj.Ord.intOrd;
import static fj.Ord.p2Ord;
import fj.P2;
import fj.data.List;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Александр
 */
public final class DecisionEncoder implements OutputStreamEncoder<Decision> {
	
	private static OutputStreamEncoder<Decision> inst = new DecisionEncoder();

	public static OutputStreamEncoder<Decision> getInst() {
		return inst;
	}

	private DecisionEncoder() {
	}

	@Override
	public void encode(final Decision decision, final OutputStream output, final String encoding) throws UnsupportedEncodingException {
		final PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, encoding));
		try {
			DecisionEncoder.getLines(decision).foreach(new Effect<String>() {

				@Override
				public void e(final String line) {
					writer.println(line);
				}
			});
		} finally {
			writer.close();
		}
	}
	
	private static List<String> getLines(final Decision decision) {
		return decisionChars_.f(decision)
				// сортировка точек по строкам/столбцам
				.map(rotatePoint_).sort(p2Ord(Sort.<Character>indifferent(), p2Ord(intOrd, intOrd))).map(rotatePoint_)
				// группировка по строкам
				.group(p2Equal(Sort.<Character>indifferent().equal(), p2Equal(Sort.<Integer>indifferent().equal(), intEqual)))
				// объединение групп точек в строки
				.map(List.asString().o(List.<P2<Character, P2<Integer, Integer>>, Character>map_().f(P2.<Character, P2<Integer, Integer>>__1())));
	}
}
