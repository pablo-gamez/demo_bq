package com.pablo.dropbook.utilities;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolQueuer {

	private static final int THREADS_NUM = 3;
	private static ExecutorService executor;

	private static ThreadPoolQueuer instance = null;

	private ThreadPoolQueuer(int n) {
		executor = Executors.newFixedThreadPool(n);
	}

	public static ThreadPoolQueuer getInstance() {
		if (instance == null) {
			instance = new ThreadPoolQueuer(THREADS_NUM);
		}
		return instance;
	}

	public void addAction(Runnable action) {
		executor.execute(action);
	}

}
