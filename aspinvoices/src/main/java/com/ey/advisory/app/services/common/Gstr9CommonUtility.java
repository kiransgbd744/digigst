package com.ey.advisory.app.services.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.services.gstr9.GSTR9InwardConstants;
import com.ey.advisory.app.data.services.gstr9.Gstr9PyTransInCyConstants;
import com.ey.advisory.app.data.services.gstr9.Gstr9TaxPaidConstants;
import com.ey.advisory.common.GSTR9Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Gstr9CommonUtility {

	private static final String OUTWARD = "Outward";
	private static final String INWARD = "Inward";
	private static final String ITC_REVERSAL = "ITC Reversal";
	private static final String OTHER_ITC_INFO = "Other ITC Information";
	private static final String DEMANDS_REFUNDS = "Demands and Refunds";

	private static Map<String, String> NATURE_SUP_MAP = null;

	static {
		Map<String, String> map = new HashMap<>();

		map.put(GSTR9Constants.Table_4A,
				OUTWARD + " -Supplies made to un-registered persons (B2C)");
		map.put(GSTR9Constants.Table_4B,
				OUTWARD + " -Supplies made to registered persons (B2B)");
		map.put(GSTR9Constants.Table_4C, OUTWARD
				+ " -Zero rated supply (Export) on payment of tax (except supplies to SEZs)");
		map.put(GSTR9Constants.Table_4D,
				OUTWARD + " -Supply to SEZs on payment of tax");
		map.put(GSTR9Constants.Table_4E, OUTWARD + " -Deemed Exports");
		map.put(GSTR9Constants.Table_4F, OUTWARD
				+ " -Advances on which tax has been paid but invoice has not been issued (not covered under (A) to (E) above)");
		map.put(GSTR9Constants.Table_4G, OUTWARD
				+ " -Inward supplies on which tax is to be paid on reverse charge basis");
		map.put(GSTR9Constants.Table_4H,
				OUTWARD + " -Sub-total (A to G above)");
		map.put(GSTR9Constants.Table_4I, OUTWARD
				+ " -Credit Notes issued in respect of transactions specified in (B) to (E) above (-)");
		map.put(GSTR9Constants.Table_4J, OUTWARD
				+ " -Debit Notes issued in respect of transactions specified in (B) to (E) above (+)");
		map.put(GSTR9Constants.Table_4K,
				OUTWARD + " -Supplies / tax declared through Amendments (+)");
		map.put(GSTR9Constants.Table_4L,
				OUTWARD + " -Supplies / tax reduced through Amendments (-)");
		map.put(GSTR9Constants.Table_4M,
				OUTWARD + " -Sub-total (I to L above)");
		map.put(GSTR9Constants.Table_4N, OUTWARD
				+ " -Supplies and advances on which tax is to be paid (H + M) above");
		map.put(GSTR9Constants.Table_5A, OUTWARD
				+ " -Zero rated supply (Export) without payment of tax");
		map.put(GSTR9Constants.Table_5B,
				OUTWARD + " -Supply to SEZs without payment of tax");
		map.put(GSTR9Constants.Table_5C, OUTWARD
				+ " -Supplies on which tax is to be paid by recipient on reverse charge basis");
		map.put(GSTR9Constants.Table_5D, OUTWARD + " -Exempted");
		map.put(GSTR9Constants.Table_5E, OUTWARD + " -Nil Rated");
		map.put(GSTR9Constants.Table_5F,
				OUTWARD + " -Non-GST supply (includes 'no supply')");
		map.put(GSTR9Constants.Table_5G,
				OUTWARD + " -Sub-total (A to F above)");
		map.put(GSTR9Constants.Table_5H, OUTWARD
				+ " -Credit Notes issued in respect of transactions specified in A to F above (-)");
		map.put(GSTR9Constants.Table_5I, OUTWARD
				+ " -Debit Notes issued in respect of transactions specified in A to F above (+)");
		map.put(GSTR9Constants.Table_5J,
				OUTWARD + " -Supplies declared through Amendments (+)");
		map.put(GSTR9Constants.Table_5K,
				OUTWARD + " -Supplies reduced through Amendments (-)");
		map.put(GSTR9Constants.Table_5L,
				OUTWARD + " -Sub-Total (H to K above)");
		map.put(GSTR9Constants.Table_5M, OUTWARD
				+ " -Turnover on which tax is not to be paid  (G + L) above");
		map.put(GSTR9Constants.Table_5N, OUTWARD
				+ " -Total Turnover (including advances) (4N + 5M - 4G) above");

		map.put(GSTR9InwardConstants.Table_6A, INWARD
				+ " -Total amount of input tax credit availed through FORM GSTR-3B (Sum total of table 4A of FORM GSTR-3B)");
		map.put(GSTR9InwardConstants.Table_6B1, INWARD
				+ " -Inward supplies (other than imports and Inward supplies liable to reverse charge but includes services received from SEZs) - Inputs");
		map.put(GSTR9InwardConstants.Table_6B2, INWARD
				+ " -Inward supplies (other than imports and Inward supplies liable to reverse charge but includes services received from SEZs) - Capital Goods");
		map.put(GSTR9InwardConstants.Table_6B3, INWARD
				+ " -Inward supplies (other than imports and Inward supplies liable to reverse charge but includes services received from SEZs) - Input Services");
		map.put(GSTR9InwardConstants.Table_6C1, INWARD
				+ " -Inward supplies received from unregistered persons"
				+ " liable to reverse charge  (other than B above) on which tax is paid & ITC availed - Inputs");
		map.put(GSTR9InwardConstants.Table_6C2, INWARD
				+ " -Inward supplies received from unregistered persons"
				+ " liable to reverse charge  (other than B above) on which tax is paid & ITC availed - Capital Goods");
		map.put(GSTR9InwardConstants.Table_6C3, INWARD
				+ " -Inward supplies received from unregistered persons "
				+ "liable to reverse charge  (other than B above) on which tax is paid & ITC availed - Input Services");
		map.put(GSTR9InwardConstants.Table_6D1, INWARD
				+ " -Inward supplies received from registered persons"
				+ " liable to reverse charge (other than B above) on which tax is paid and ITC availed- Inputs");
		map.put(GSTR9InwardConstants.Table_6D2, INWARD
				+ " -Inward supplies received from registered persons "
				+ "liable to reverse charge (other than B above) on which tax is paid and ITC availed- Capital Goods");
		map.put(GSTR9InwardConstants.Table_6D3, INWARD
				+ " -Inward supplies received from registered persons"
				+ " liable to reverse charge (other than B above) on which tax is paid and ITC availed- Input Services");
		map.put(GSTR9InwardConstants.Table_6E1, INWARD
				+ " -Import of goods (including supplies from SEZ) - Inputs");
		map.put(GSTR9InwardConstants.Table_6E2, INWARD
				+ " -Import of goods (including supplies from SEZ) - Capital Goods");
		map.put(GSTR9InwardConstants.Table_6F, INWARD
				+ " -Import of services (excluding Inward supplies from SEZs)");
		map.put(GSTR9InwardConstants.Table_6G,
				INWARD + " -Input Tax  credit received from ISD");
		map.put(GSTR9InwardConstants.Table_6H, INWARD
				+ " -Amount of ITC reclaimed (other than B above) under the provisions of the Act");
		map.put(GSTR9InwardConstants.Table_6I,
				INWARD + " -Sub-total (B to H above)");
		map.put(GSTR9InwardConstants.Table_6J,
				INWARD + " -Difference (I - A) above");
		map.put(GSTR9InwardConstants.Table_6K, INWARD
				+ " -Transition Credit through TRAN-1 (including revisions if any)");
		map.put(GSTR9InwardConstants.Table_6L,
				INWARD + " -Transition Credit through TRAN-2");
		map.put(GSTR9InwardConstants.Table_6M,
				INWARD + " -Any other ITC availed but not specified above");
		map.put(GSTR9InwardConstants.Table_6N,
				INWARD + " -Sub-total (K to M above)");
		map.put(GSTR9InwardConstants.Table_6O,
				INWARD + " -Total ITC availed (I + N) above");

		map.put(GSTR9InwardConstants.Table_7A,
				ITC_REVERSAL + " -As per Rule 37");
		map.put(GSTR9InwardConstants.Table_7B,
				ITC_REVERSAL + " -As per Rule 39");
		map.put(GSTR9InwardConstants.Table_7C,
				ITC_REVERSAL + " -As per Rule 42");
		map.put(GSTR9InwardConstants.Table_7D,
				ITC_REVERSAL + " -As per Rule 43");
		map.put(GSTR9InwardConstants.Table_7E,
				ITC_REVERSAL + " -As per section 17(5)");
		map.put(GSTR9InwardConstants.Table_7F,
				ITC_REVERSAL + " -Reversal of TRAN-I credit");
		map.put(GSTR9InwardConstants.Table_7G,
				ITC_REVERSAL + " -Reversal of TRAN-II credit");
		map.put(GSTR9InwardConstants.Table_7H,
				ITC_REVERSAL + " -Other reversals");
		map.put(GSTR9InwardConstants.Table_7I,
				ITC_REVERSAL + " -Total ITC Reversed (Sum of A to H above)");
		map.put(GSTR9InwardConstants.Table_7J,
				ITC_REVERSAL + " -Net ITC Available for Utilization (6O - 7I)");

		map.put(GSTR9InwardConstants.Table_8A,
				OTHER_ITC_INFO + " -ITC as per GSTR-2A (Table 3 & 5 thereof)");
		map.put(GSTR9InwardConstants.Table_8B,
				OTHER_ITC_INFO + " -ITC as per sum total 6(B) and 6(H)  above");
		map.put(GSTR9InwardConstants.Table_8C, OTHER_ITC_INFO
				+ " -ITC on inward supplies "
				+ "(other than imports and inward supplies liable to reverse charge but includes services received from SEZs) received during the financial year but availed in the next financial year upto specified period");
		map.put(GSTR9InwardConstants.Table_8D,
				OTHER_ITC_INFO + " -Difference [A-(B+C)]");
		map.put(GSTR9InwardConstants.Table_8E,
				OTHER_ITC_INFO + " -ITC available but not availed");
		map.put(GSTR9InwardConstants.Table_8F,
				OTHER_ITC_INFO + " -ITC available but ineligible");
		map.put(GSTR9InwardConstants.Table_8G,
				OTHER_ITC_INFO + " -IGST paid  on import of goods "
						+ "(including supplies from SEZ)");
		map.put(GSTR9InwardConstants.Table_8H,
				OTHER_ITC_INFO + " -IGST credit availed on import of goods "
						+ "(as per 6(E) above)");
		map.put(GSTR9InwardConstants.Table_8I,
				OTHER_ITC_INFO + " -Difference (G-H)");
		map.put(GSTR9InwardConstants.Table_8J,
				OTHER_ITC_INFO + " -ITC available but not availed on import of "
						+ "goods (Equal to I)");
		map.put(GSTR9InwardConstants.Table_8K, OTHER_ITC_INFO
				+ " -Total ITC to be lapsed in current financial year (E + F + J)");

		map.put(Gstr9TaxPaidConstants.Table_9A, "IGST");
		map.put(Gstr9TaxPaidConstants.Table_9B, "CGST");
		map.put(Gstr9TaxPaidConstants.Table_9C, "SGST");
		map.put(Gstr9TaxPaidConstants.Table_9D, "Cess");
		map.put(Gstr9TaxPaidConstants.Table_9E, "Interest");
		map.put(Gstr9TaxPaidConstants.Table_9F, "Late fee");
		map.put(Gstr9TaxPaidConstants.Table_9G, "Penalty");
		map.put(Gstr9TaxPaidConstants.Table_9H, "Other");
		map.put(Gstr9PyTransInCyConstants.Table_10,
				"Particulars of the transactions for the financial year declared in returns of the next financial year till the specified period - Supplies / tax declared through Amendments (+) (net of debit notes)");
		map.put(Gstr9PyTransInCyConstants.Table_11,
				"Particulars of the transactions for the financial year declared in returns of the next financial year till the specified period - Supplies / tax reduced through Amendments (-) (net of credit notes)");
		map.put(Gstr9PyTransInCyConstants.Table_12,
				"Particulars of the transactions for the financial year declared in returns of the next financial year till the specified period - Reversal of ITC availed during previous financial year");
		map.put(Gstr9PyTransInCyConstants.Table_13,
				"Particulars of the transactions for the financial year declared in returns of the next financial year till the specified period - ITC availed for the previous financial year");
		map.put(GSTR9Constants.Table_14A, "IGST");
		map.put(GSTR9Constants.Table_14B, "CGST");
		map.put(GSTR9Constants.Table_14C, "SGST");
		map.put(GSTR9Constants.Table_14D, "Cess");
		map.put(GSTR9Constants.Table_14E, "Interest");
		map.put(GSTR9Constants.Table_15A,
				DEMANDS_REFUNDS + " -Total Refund claimed");
		map.put(GSTR9Constants.Table_15B,
				DEMANDS_REFUNDS + " -Total Refund sanctioned");
		map.put(GSTR9Constants.Table_15C,
				DEMANDS_REFUNDS + " -Total Refund Rejected");
		map.put(GSTR9Constants.Table_15D,
				DEMANDS_REFUNDS + " -Total Refund Pending");
		map.put(GSTR9Constants.Table_15E,
				DEMANDS_REFUNDS + " -Total demand of taxes");
		map.put(GSTR9Constants.Table_15F,
				DEMANDS_REFUNDS + " -Total taxes paid in respect of E above");
		map.put(GSTR9Constants.Table_15G,
				DEMANDS_REFUNDS + " -Total demands pending out of E above");

		map.put(GSTR9Constants.Table_16A,
				"Information on supplies received from composition taxpayers deemed supply under section 143 and goods sent on approval basis - Supplies received from Composition taxpayers");
		map.put(GSTR9Constants.Table_16B,
				"Information on supplies received from composition taxpayers deemed supply under section 143 and goods sent on approval basis - Deemed supply under section 143");
		map.put(GSTR9Constants.Table_16C,
				"Information on supplies received from composition taxpayers deemed supply under section 143 and goods sent on approval basis - Goods sent on approval basis but not returned");

		NATURE_SUP_MAP = Collections.unmodifiableMap(map);
	}

	public static String getNatureOfSuppliesForSubSection(String subSection) {
		return NATURE_SUP_MAP.get(subSection);
	}
}
