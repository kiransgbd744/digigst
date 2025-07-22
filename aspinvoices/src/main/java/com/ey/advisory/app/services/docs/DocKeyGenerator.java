package com.ey.advisory.app.services.docs;

public interface DocKeyGenerator<T, K> {
	
	public K generateKey(T doc);

	public K generateOrgKey(T doc);
	
}
