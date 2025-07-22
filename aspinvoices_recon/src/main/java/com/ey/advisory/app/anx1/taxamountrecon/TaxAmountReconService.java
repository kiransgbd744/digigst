package com.ey.advisory.app.anx1.taxamountrecon;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.anx1.taxamountrecon.TaxAmountReconDto;


/**
 * @author Arun KA
 *
 **/

public interface TaxAmountReconService {
		
		public Pair<List<TaxAmountReconDto>,TaxAmountReconRet1Dto> getAllTaxAmountRecon
		(TaxAmountReconRequestDto reqDto);

}
