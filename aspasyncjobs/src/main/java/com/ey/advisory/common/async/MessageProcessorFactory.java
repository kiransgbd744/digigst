package com.ey.advisory.common.async;

import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

/**
 * The implementation of this interface is responsible for locating the
 * TaskProcessor instance responsible for processing a message.
 * 
 * @author Sai.Pakanati
 *
 */
public interface MessageProcessorFactory {
	
	public TaskProcessor getMessageTaskProcessor(Message message);

}
