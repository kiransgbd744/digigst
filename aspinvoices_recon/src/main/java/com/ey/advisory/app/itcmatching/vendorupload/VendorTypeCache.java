package com.ey.advisory.app.itcmatching.vendorupload;

public interface VendorTypeCache {
	
	String findVendorType(String vendorType);
	
	boolean isVendorType(String vendorType);
	
	boolean isHSN(String hsn);
}
