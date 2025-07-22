package com.ey.advisory.common;

public class ErrMsgEnhancerFactory {
	
	public static ErrMsgEnhancer getErrMsgEnhancer(
			ErrMsgEnhancementStrategy strategy) {
		return new DefaultErrMsgEnchancer();
	}
}
