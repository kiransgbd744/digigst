package com.ey.advisory.app.services.gstr1fileupload;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.annexure1fileupload.*;
import com.ey.advisory.app.services.common.Annexure1HeaderChecker;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("Itc04HeaderCheckService")
public class Itc04HeaderCheckService implements Annexure11HeaderCheckService {

	@Autowired
	@Qualifier("Annexure1HeaderChecker")
	private Annexure1HeaderChecker annexure1HeaderChecker;

	public static final String[] EXPECTED_HEADERS = { "TableNumber",
			"ActionType", "FY", "ReturnPeriod", "SupplierGSTIN",
			"DeliveryChallanNumber", "DeliveryChallanDate",
			"JWDeliveryChallanNumber", "JWDeliveryChallanDate",
			"GoodsReceivingDate", "InvoiceNumber", "InvoiceDate",
			"JobWorkerGSTIN", "JobWorkerStateCode", "JobWorkerType",
			"JobWorkerID", "JobWorkerName", "TypeOfGoods", "ItemSerialNumber",
			"ProductDescription", "ProductCode", "NatureOfJW", "HSN", "UQC",
			"Quantity", "LossesUQC", "LossesQuantity", "ItemAssessableAmount",
			"IGSTRate", "IGSTAmount", "CGSTRate", "CGSTAmount", "SGSTRate",
			"SGSTAmount", "CessAdvaloremRate", "CessAdvaloremAmount",
			"CessSpecificRate", "CessSpecificAmount", "StateCessAdvaloremRate",
			"StateCessAdvaloremAmount", "StateCessSpecificRate",
			"StateCessSpecificAmount", "TotalValue", "PostingDate", "UserID",
			"CompanyCode", "SourceIdentifier", "SourceFileName", "PlantCode",
			"Division", "ProfitCentre1", "ProfitCentre2",
			"AccountingVoucherNumber", "AccountingVoucherDate",
			"UserDefinedField1", "UserDefinedField2", "UserDefinedField3",
			"UserDefinedField4", "UserDefinedField5", "UserDefinedField6",
			"UserDefinedField7", "UserDefinedField8", "UserDefinedField9",
			"UserDefinedField10" };

	@Override
	public Pair<Boolean, String> validate(Object[] headerCols) {
		Pair<Boolean, String> pair = annexure1HeaderChecker
				.validateHeaders(EXPECTED_HEADERS, headerCols);
		return pair;
	}

}
