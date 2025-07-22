package com.ey.advisory.common;

/**
 * This interface represents an abstraction that is responsible for creating
 * meaningful messages out of the exception thrown.
 * 
 * @author Sai.Pakanati
 *
 */
@FunctionalInterface
public interface ErrMsgEnhancer {
	
	/**
	 * This is the only abstract method that should be present in the interface.
	 * This method should iterate over the nested causes and suppressed 
	 * exceptions and generate a meaningful message that can be logged for 
	 * later debugging.
	 */
	public abstract String enhanceErrorMessage(String errMsg, Throwable cause, 
				ErrMsgEnhancementStrategy strategy);

}
