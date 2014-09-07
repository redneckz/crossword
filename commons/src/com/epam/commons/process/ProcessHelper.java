package com.epam.commons.process;

import fj.Effect;
import fj.Equal;
import fj.data.List;

/**
 *
 * @author Александр
 */
public final class ProcessHelper<T> implements LongProcess<T> {
	
	private List<ProcessMonitor<T>> monitors = List.<ProcessMonitor<T>>nil();

	@Override
	public void registerMonitor(final ProcessMonitor<T> monitor) {
		monitors = monitors.cons(monitor);
	}

	@Override
	public void unregisterMonitor(final ProcessMonitor<T> monitor) {
		monitors = monitors.delete(monitor, Equal.<ProcessMonitor<T>>anyEqual());
	}
	
	public void fireStart() {
		monitors.foreach(new Effect<ProcessMonitor<T>>() {

			@Override
			public void e(final ProcessMonitor<T> monitor) {
				monitor.onStart();
			}
		});
	}
	
	public void fireStep(final T data) {
		monitors.foreach(new Effect<ProcessMonitor<T>>() {

			@Override
			public void e(final ProcessMonitor<T> monitor) {
				monitor.onStep(data);
			}
		});
	}
	
	public void fireFail(final Exception ex) {
		monitors.foreach(new Effect<ProcessMonitor<T>>() {

			@Override
			public void e(final ProcessMonitor<T> monitor) {
				monitor.onFail(ex);
			}
		});
	}
	
	public void fireEnd() {
		monitors.foreach(new Effect<ProcessMonitor<T>>() {

			@Override
			public void e(final ProcessMonitor<T> monitor) {
				monitor.onEnd();
			}
		});
	}
}
