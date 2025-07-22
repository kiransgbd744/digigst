package com.ey.advisory.core.springconfig;

import java.security.KeyStore;

public interface TrustStoreLoader {
	
	public KeyStore loadTrustStore();
}
