package com.ey.advisory.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This class uses the Java InetAddress class to resolve the local host name.
 * If an OS based alternative is available, then use it, as this class relies
 * on the proper functioning of DNS/reverse DNS lookups.
 * 
 * @author Sai.Pakanati
 *
 */
@Component("FallBackHostNameResolver")
public class FallBackHostNameResolver implements HostNameResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(
			FallBackHostNameResolver.class);
	
	
	@Override
	public String getLocalHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch(UnknownHostException ex) {
			String msg = "Error while looking up the Local Host Name using "
					+ "the Java InetAdress class. This might be due to "
					+ "a DNS error.";
			LOGGER.error(msg, ex);
		}
		return null;
	}
	
}
