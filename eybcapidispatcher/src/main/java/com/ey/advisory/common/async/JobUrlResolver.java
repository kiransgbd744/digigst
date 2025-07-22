package com.ey.advisory.common.async;

import com.ey.advisory.common.Message;

/**
 * The implementation of this interface is responsible for resolving the
 * Job url responsible for processing a message.
 * 
 * @author Sai.Pakanati
 *
 */
public interface JobUrlResolver {
	
	public String resolveJobUrl(Message message);

}
