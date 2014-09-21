package com.epam.crossword.ui.desktop;

import com.epam.crossword.Decision;
import static com.epam.crossword.Decision.decisionChars_;
import com.epam.crossword.ui.ViewModel;
import fj.Effect;
import fj.P2;
import static fj.function.Booleans.not;
import static fj.function.Characters.isWhitespace;
import static java.awt.EventQueue.invokeLater;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;

/**
 *
 * @author Александр
 */
class DecisionCanvas extends JPanel implements Observer {

	private final ViewModel model;

	DecisionCanvas(ViewModel model) {
		this.model = model;
		init();
	}
	
	private void init() {
		model.addObserver(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Paint oldPaint = g2.getPaint();
		Stroke oldStroke = g2.getStroke();
		Font oldFont = g2.getFont();
		g2.setPaint(new Color(1f, 1f, 1f));
		g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
		Decision decision = model.getDecision();
		if ((decision.getWidth() > 0) && (decision.getHeight() > 0)) {
			final double scale = Math.min(getWidth() / (double) decision.getWidth(),
					getHeight() / (double) decision.getHeight());
			g2.setPaint(new Color(0f, 0f, 0f));
			g2.setStroke(new BasicStroke(1.5f));
			g2.setFont(new Font("Arial", Font.BOLD, (int) (0.9 * scale)));
			final FontMetrics fontMetrics = g2.getFontMetrics();
			decisionChars_.f(decision).filter(not(isWhitespace.o(P2.<Character, P2<Integer, Integer>>__1()))).foreach(new Effect<P2<Character, P2<Integer, Integer>>>() {

				@Override
				public void e(P2<Character, P2<Integer, Integer>> ch) {
					int x = ch._2()._1();
					int y = ch._2()._2();
					g2.draw(new Rectangle2D.Double(x * scale, y * scale, scale, scale));
					fontMetrics.stringWidth(ch._1().toString());
					g2.drawString(ch._1().toString(), (float) ((x + 0.5) * scale - fontMetrics.stringWidth(ch._1().toString()) / 2.),
							(float) ((y + 0.5) * scale + fontMetrics.getAscent() / 2.));
				}
			});
		}
		g2.setPaint(oldPaint);
		g2.setStroke(oldStroke);
		g2.setFont(oldFont);
	}

	@Override
	public void update(Observable o, Object arg) {
		invokeLater(new Runnable() {

			@Override
			public void run() {
				repaint();
			}
		});
	}
}
