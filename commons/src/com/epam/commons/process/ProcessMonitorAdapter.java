package com.epam.commons.process;

/**
 *
 * @author Александр
 */
public abstract class ProcessMonitorAdapter<T> implements ProcessMonitor<T> {

	@Override
	public void onStart() {
	}

	@Override
	public void onStep(T data) {
	}

	@Override
	public void onFail(Exception ex) {
	}

	@Override
	public void onEnd() {
	}
}
