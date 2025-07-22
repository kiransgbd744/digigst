package com.ey.advisory.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class gets the local host name by trying to resolve the name using a 
 * series of approaches. If none of the approaches work, then a null is returned
 * as the hostname. The result of the first successful resolving approach is
 * returned as the hostname. The class internally maintains a map of List of
 * resolvers for each OS type. Currently, only Linux has a native method to
 * determine the hostname. All other OSes fall back to the 
 * FallBackHostNameResolver. 
 * 
 * @author Sai.Pakanati
 *
 */

public class DefaultHostNameResolver implements HostNameResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(
						DefaultHostNameResolver.class);
	
	private static final Map<OSType, List<? extends HostNameResolver>> 
				nameResolverMap = new HashMap<>();
	
	static {
		// If it's linux, first use a Linux Native 'hostname' command to get
		// the host name and if it fails, then use the Fallback  mechanism 
		// based on InetAddress. For any other OS, use the fallback mechanism
		// by default. If other OS Specific approaches are there, crate new
		// name resolver instances and add it to this map.
		nameResolverMap.put(OSType.Linux, Arrays.asList(
				new FallBackHostNameResolver(), new LinuxHostNameResolver()));
	}
	
	@Override
	public String getLocalHostName() {

		// Get the Name Resolvers to be tried in order for the current
		// operating system. If no resolvers are configured, use the
		// fallback name resolver.
		OSType osType = OSCheck.getOSType();
		List<? extends HostNameResolver> mappedResolvers = 
				nameResolverMap.get(osType);
		List<? extends HostNameResolver> resolvers = 
				(mappedResolvers == null || mappedResolvers.isEmpty()) ?
						Arrays.asList(new FallBackHostNameResolver()) : 
							mappedResolvers;
		// Iterate over each resolver and try to get a non-null host name.
		// use a try/catch block to handle any implementation related errors.
		for(HostNameResolver resolver: resolvers) {
			
			// Ignore null resolvers.
			if(resolver == null) continue;
			
			// Get the host name from the resolver. If we get a non empty/
			// non null host name, then break the iteration by returning this
			// host name.
			try {
				String hostName = resolver.getLocalHostName();
				if(hostName != null && !hostName.trim().isEmpty()) {
					return hostName;
				}
			} catch(Exception ex) {
				String msg = String.format("Error occurred while trying to "
						+ "resolve the hostname using the Resolver: '%s'", 
						resolver.getClass().getName());
				LOGGER.error(msg, ex);
			}
		}
		return null;
	}
	
    public static void main(String[] args) {
		String hostName = new DefaultHostNameResolver().getLocalHostName();
		System.out.printf("HostName is: '%s'\n", hostName);
	}		
}
