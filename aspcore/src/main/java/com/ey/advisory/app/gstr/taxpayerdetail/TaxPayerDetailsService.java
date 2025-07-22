package com.ey.advisory.app.gstr.taxpayerdetail;

public interface TaxPayerDetailsService {
	
    public TaxPayerDetailsDto getTaxPayerDetails(String gstin, String groupCode);
    
}
