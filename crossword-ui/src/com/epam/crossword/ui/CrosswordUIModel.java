package com.epam.crossword.ui;

import com.epam.commons.graph.Path;
import com.epam.commons.process.ProcessMonitor;
import com.epam.commons.process.ProcessMonitorAdapter;
import com.epam.crossword.Crossword;
import com.epam.crossword.Decision;
import com.epam.crossword.DecisionSearchStrategy;
import com.epam.crossword.DecisionsGraph;
import com.epam.crossword.dictionary.Dictionary;
import com.epam.crossword.dictionary.XDXFDictionaryCreator;
import com.epam.crossword.io.CrosswordParser;
import com.epam.crossword.io.DecisionEncoder;
import fj.F;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Александр
 */
public class CrosswordUIModel extends Observable {

	private volatile Crossword crossword = Crossword.NIL;
	private volatile Decision decision = new Decision(crossword);
	private volatile Dictionary dictionary = Dictionary.NIL;
	
	private volatile File crosswordFile;
	
	private final Executor executor = Executors.newSingleThreadExecutor();

	public Crossword getCrossword() {
		return crossword;
	}

	public Decision getDecision() {
		return decision;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	public File getCrosswordFile() {
		return crosswordFile;
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

	public void openCrossword(final File crosswordFile) throws IOException {
		this.crosswordFile = crosswordFile;
		if (crosswordFile != null) {
			crossword = CrosswordParser.getInst().decode(new FileInputStream(crosswordFile), "UTF-8");
			decision = new Decision(crossword);
		}
		setChanged();
		notifyObservers();
	}
	
	public void saveCrossword() throws IOException {
		if (crosswordFile != null) {
			DecisionEncoder.getInst().encode(decision, new FileOutputStream(crosswordFile), "UTF-8");
			setChanged();
			notifyObservers();
		}
	}
	
	public void saveCrosswordTo(final File crosswordFile) throws IOException {
		this.crosswordFile = crosswordFile;
		if (crosswordFile != null) {
			DecisionEncoder.getInst().encode(decision, new FileOutputStream(crosswordFile), "UTF-8");
		}
		setChanged();
		notifyObservers();
	}
	
	public void loadDictionary(final File dictFile, final ProcessMonitor<String> processMonitor) {
		if (dictFile != null) {
			final XDXFDictionaryCreator dictionaryCreator = new XDXFDictionaryCreator();
			dictionaryCreator.registerMonitor(processMonitor);
			executor.execute(new Runnable() {

				@Override
				public void run() {
					try {
						dictionary = dictionaryCreator.create(new FileInputStream(dictFile));
						setChanged();
						notifyObservers();
					} catch (Exception ex) {
						processMonitor.onFail(ex);
						Logger.getLogger(CrosswordUIModel.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					}
				}
			});
		}
	}
	
	public void clearDictionary() {
		dictionary = Dictionary.NIL;
		setChanged();
		notifyObservers();
	}
	
	public void findDecision(final ProcessMonitor<Decision> processMonitor) {
		final DecisionSearchStrategy searchStrategy = new DecisionSearchStrategy();
		searchStrategy.registerMonitor(processMonitor);
		searchStrategy.registerMonitor(new ProcessMonitorAdapter<Decision>() {

			@Override
			public void onStep(final Decision data) {
				decision = data;
				setChanged();
				notifyObservers();
			}
		});
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Path<Decision> path = searchStrategy.search(new DecisionsGraph(crossword, dictionary), new F<Path<Decision>, Boolean>() {

						@Override
						public Boolean f(final Path<Decision> path) {
							return (path.isNotEmpty() && (0 == path.getTarget().getData().getEmptyPartCount()));
						}
					});
					if (path.getTarget() != null) {
						decision = path.getTarget().getData();
					} else {
						decision = new Decision(crossword);
					}
					setChanged();
					notifyObservers();
				} catch (Exception ex) {
					processMonitor.onFail(ex);
					Logger.getLogger(CrosswordUIModel.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
		});
	}
}
