package com.ey.advisory.common;

import jakarta.annotation.PostConstruct;


/**
 * Since the InetAddress class of Java will rely on proper functioning of 
 * DNS lookup/reverse lookup, we cannot go by this class alone, to get the
 * host name. Since our VMs are running Linux, we can fallback to the use of
 * the 'hostname' command to get the actual hostname. This usually reads the
 * /etc/hostname file to get the localhost name, which will be sufficient for
 * our logging purposes in AsyncExec.
 * 
 * @author Sai.Pakanati
 *
 */
public class LinuxHostNameResolver implements HostNameResolver {
	
	/**
	 * This variable should be initialized only during the PostConstruct method
	 * Do not reset this variable any time during runtime.
	 * 
	 */
	private String localHostName;
	
	@Override
	public String getLocalHostName() {
		return localHostName;
	}
	
	/**
	 * This method uses the linux hostname command to get the name of the local
	 * host. This linux command reads the hostname configured in /etc/hostname
	 * and returns it.
	 * 
	 * @return The name of the linux host.
	 */
	@PostConstruct
	private void initLocalHostName() {
		/*localHostName = new SingleLineStdOutCommandExecutor()
				.executeCommand("sh", "-c", "hostname");*/
	}
	
}

