package com.ey.advisory.common;

import org.javatuples.Pair;

public class PerformRetryException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final Pair<String, Integer> respPair;

	public PerformRetryException(Pair<String, Integer> respPair) {
		this.respPair = respPair;
	}

	public Pair<String, Integer> getRespPair() {
		return respPair;
	}

}
