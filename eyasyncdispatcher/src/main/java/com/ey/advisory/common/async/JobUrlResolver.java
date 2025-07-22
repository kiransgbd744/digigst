package com.ey.advisory.common.async;

import org.javatuples.Pair;

import com.ey.advisory.common.Message;

/**
 * The implementation of this interface is responsible for resolving the
 * Job url responsible for processing a message.
 * 
 * @author Sai.Pakanati
 *
 */
public interface JobUrlResolver {
	
	public Pair<String, String> resolveJobUrl(Message message);
	
	public String replaceTaskUrl(String jobName, String jobBundleName);


}
