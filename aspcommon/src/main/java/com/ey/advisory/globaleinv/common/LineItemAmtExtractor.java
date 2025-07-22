package com.ey.advisory.globaleinv.common;

import java.util.List;

public interface LineItemAmtExtractor {

	public void extractAndPopulateLineAmt(InvoiceLine lineItem,
			SAPERPItemParticular itemParticular,
			List<SAPERPCondition> conditions);
}
