package com.ey.advisory.app.anx1.taxamountrecon;

import java.util.List;


/**
 * @author Arun KA
 *
 **/

public interface TaxAmountReconDao {
	
	public List<TaxAmountReconDto> getAllTaxAmountReconDetails(
			TaxAmountReconRequestDto reqDto);

}
