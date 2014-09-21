package com.epam.crossword.ui;

import com.epam.commons.graph.Path;
import com.epam.commons.process.ProcessMonitor;
import com.epam.commons.process.ProcessMonitorAdapter;
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
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Александр
 */
public class Controller {
	
	private final ViewModel model;
	
	private volatile File crosswordFile;
	
	private final Executor executor = Executors.newSingleThreadExecutor();

	public Controller(ViewModel model) {
		this.model = model;
	}
	
	public File getCrosswordFile() {
		return crosswordFile;
	}
	
	public void openCrossword(final File crosswordFile) throws IOException {
		this.crosswordFile = crosswordFile;
		if (crosswordFile != null) {
			openCrossword(new FileInputStream(crosswordFile));
		}
	}
	
	public void openDefaultCrossword() {
		try {
			openCrossword(Controller.class.getResourceAsStream("crossword_x4.txt"));
		} catch (Exception ex) {
			Logger.getLogger(ViewModel.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	public void openCrossword(final InputStream input) throws IOException {
		model.setCrossword(CrosswordParser.inst().decode(input, "UTF-8"));
	}
	
	public void saveCrossword() throws IOException {
		if (crosswordFile != null) {
			DecisionEncoder.inst().encode(model.getDecision(), new FileOutputStream(crosswordFile), "UTF-8");
		}
	}
	
	public void saveCrosswordTo(final File crosswordFile) throws IOException {
		this.crosswordFile = crosswordFile;
		if (crosswordFile != null) {
			DecisionEncoder.inst().encode(model.getDecision(), new FileOutputStream(crosswordFile), "UTF-8");
		}
	}
	
	public void loadDictionary(final File dictFile, final ProcessMonitor<String> processMonitor) {
		try {
			loadDictionary(new FileInputStream(dictFile), processMonitor);
		} catch (Exception ex) {
			processMonitor.onFail(ex);
			Logger.getLogger(ViewModel.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	public void loadDefaultDictionary(final ProcessMonitor<String> processMonitor) {
		loadDictionary(Controller.class.getResourceAsStream("dict.xdxf"), processMonitor);
	}
	
	public void loadDictionary(final InputStream input, final ProcessMonitor<String> processMonitor) {
		final XDXFDictionaryCreator dictionaryCreator = new XDXFDictionaryCreator();
		dictionaryCreator.registerMonitor(processMonitor);
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					model.setDictionary(dictionaryCreator.create(input));
				} catch (Exception ex) {
					processMonitor.onFail(ex);
					Logger.getLogger(ViewModel.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
		});
	}
	
	public void clearDictionary() {
		model.setDictionary(Dictionary.NIL);
	}
	
	public void findDecision(final ProcessMonitor<Decision> processMonitor) {
		final DecisionSearchStrategy searchStrategy = new DecisionSearchStrategy();
		searchStrategy.registerMonitor(processMonitor);
		searchStrategy.registerMonitor(new ProcessMonitorAdapter<Decision>() {

			@Override
			public void onStep(final Decision decision) {
				model.setDecision(decision);
			}
		});
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Path<Decision> path = searchStrategy.search(new DecisionsGraph(
							model.getCrossword(), model.getDictionary()), new F<Path<Decision>, Boolean>() {

						@Override
						public Boolean f(final Path<Decision> path) {
							return (path.isNotEmpty() && (0 == path.getTarget().getData().getEmptyPartCount()));
						}
					});
					if (path.getTarget() != null) {
						model.setDecision(path.getTarget().getData());
					} else {
						model.resetDecision();
					}
				} catch (Exception ex) {
					processMonitor.onFail(ex);
					Logger.getLogger(ViewModel.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
		});
	}
}
