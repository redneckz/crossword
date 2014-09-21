package com.epam.crossword.ui;

import com.epam.crossword.Crossword;
import com.epam.crossword.Decision;
import com.epam.crossword.dictionary.Dictionary;
import java.util.Observable;

/**
 *
 * @author Александр
 */
public class ViewModel extends Observable {

	private volatile Crossword crossword = Crossword.NIL;
	private volatile Decision decision = new Decision(crossword);
	private volatile Dictionary dictionary = Dictionary.NIL;
	
	public Crossword getCrossword() {
		return crossword;
	}
	
	public void setCrossword(Crossword crossword) {
		if (crossword != null) {
			this.crossword = crossword;
			decision = new Decision(crossword);
		} else {
			this.crossword = Crossword.NIL;
			decision = new Decision(Crossword.NIL);
		}
		setChanged();
		notifyObservers();
	}

	public Decision getDecision() {
		return decision;
	}

	public void setDecision(Decision decision) {
		this.decision = decision;
		setChanged();
		notifyObservers();
	}
	
	public void resetDecision() {
		setDecision(new Decision(crossword));
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = ((dictionary != null) ? dictionary : Dictionary.NIL);
		setChanged();
		notifyObservers();
	}
	
	public boolean isDecisionFound() {
		return (0 == decision.getEmptyPartCount());
	}
	
	public int getDecisionSearchProgress() {
		int result;
		int wordCount = crossword.getWordCount();
		if (wordCount > 0) {
			result = (decision.getFilledPartCount() * 100 / wordCount);
		} else {
			result = 100;
		}
		return result;
	}
}
