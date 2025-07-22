package com.ey.advisory.globaleinv.common;

import java.util.List;

import org.springframework.stereotype.Component;

@Component("LineItemAmtExtractorServiceImpl")
public class LineItemAmtExtractorServiceImpl implements LineItemAmtExtractor {

	@Override
	public void extractAndPopulateLineAmt(InvoiceLine lineItemDtls,
			SAPERPItemParticular erpItemDtls,
			List<SAPERPCondition> conditions) {

		TaxTotal taxTotal = new TaxTotal();
		Item itemDto = lineItemDtls.getItem();
		int lineItemNo = erpItemDtls.getInvoicelineno();
		for (SAPERPCondition erpCondDtls : conditions) {

			int condInvoiceno = erpCondDtls.getKposn();
			String condInvoiceType = erpCondDtls.getKschl();
			ClassifiedTaxCategory classifiedTxCat = new ClassifiedTaxCategory();
			if (lineItemNo == condInvoiceno
					&& "JOIG".equalsIgnoreCase(condInvoiceType)) {

				int vatPercentage = erpCondDtls.getKbetr() / 10;

				classifiedTxCat.setPercent(vatPercentage);

				itemDto.setClassifiedTaxCategory(classifiedTxCat);

				double vatAmout = erpCondDtls.getKwert();

				taxTotal.setRoundingAmount(
						vatAmout + lineItemDtls.getPrice().getPriceAmount());
				taxTotal.setTaxAmount(vatAmout);

				lineItemDtls.setTaxTotal(taxTotal);
				lineItemDtls.setItem(itemDto);
			}
		}
	}

}
