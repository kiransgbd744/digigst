package com.ey.advisory.app.glrecon;

import java.util.List;


/**
 * 
 * @author Sakshi.jain
 *
 */
/*
 * 
 */
public interface GlReconSRFileCreationService {
	public String createSRFile(Long entityId, List<String> gstins, String fromTaxPerd, String toTaxPerd ,String transactionType);

}
