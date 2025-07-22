package com.ey.advisory.common.multitenancy;

import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * This class is created as the PoolProperties do not expose some of its
 * configuration through public getter methods. An easy way to create a copy
 * of this instance with all the values set, is to use the clone method provided
 * as part of the PoolProperties implementation. Since this method is marked
 * as 'protected' the only way to use the clone method is to extend from 
 * PoolProperties and use the base clone method to create a copy of the 
 * PoolProperties. (Not the perfect way, but works for now!!)
 * 
 * Need to replace this in future with some other mechanism.
 * 
 * @author Sai.Pakanati
 *
 */
public class TomcatPoolPropertiesCopier extends PoolProperties{
	
	public TomcatPoolPropertiesCopier() {}
	
	private static final long serialVersionUID = 1L;

	public PoolProperties getBaseCopy() throws CloneNotSupportedException {
		return (PoolProperties) super.clone();
		
	}
}
