package com.ey.advisory.common.eyfileutils.tabular.impl;

public class TraverserConstants {
	
	private TraverserConstants() {}
	
	/**
	 * Used to indicate the traversal process to terminate reading after the
	 * header row is read. If this key is present in the map provided to the
	 * traverser, then it will stop execution after the header is read. Does
	 * not depend on the value of this key.
	 */
	public static final String READ_ONLY_HEADER_ROW = "ROHR";
}
