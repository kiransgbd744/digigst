package com.ey.advisory.app.data.services.gstr9;

import org.springframework.stereotype.Component;

/**
 * @author Jithendra.B
 *
 */
@Component("Gstr9PdfLabelUtil")
public class Gstr9PDFLabelUtil {

	public static final String FY = "1. Financial Year";
	public static final String GSTIN = "2. GSTIN";
	public static final String LEGAL_NAME = "3(a). Legal name of the registered person";
	public static final String ARN = "3(b). ARN";
	public static final String DOF = "3(c). Date of Filing";

	public static final String TABLE_4A = "Supplies made to un-registered persons (B2C) ";
	public static final String TABLE_4B = "Supplies made to registered persons (B2B) ";
	public static final String TABLE_4C = "Zero rated supply (Export) on payment of tax (Except supplies to SEZs) ";
	public static final String TABLE_4D = "Supplies to SEZs on payment of tax";
	public static final String TABLE_4E = "Deemed Exports";
	public static final String TABLE_4F = "Advances on which tax has been paid but invoice has not been issued (not covered under (A) to (E) above) ";
	public static final String TABLE_4G = "Inward supplies on which tax is to be paid on the reverse charge basis";
	public static final String TABLE_4G1 = "Supplies on which e-commerce operator is required to pay tax as per section 9(5) (including amendments, if any) [E-commerce operator to report] ";
	public static final String TABLE_4H = "Sub-total (A to G1 above)";
	public static final String TABLE_4I = "Credit notes issued in respect of transactions specified in (B) to (E) above(-)";
	public static final String TABLE_4J = "Debit notes issued in respect of transactions specified in (B) to (E) above (+)";
	public static final String TABLE_4K = "Supplies / tax declared through Amendments (+)";
	public static final String TABLE_4L = "Supplies / tax reduced through Amendments (-)";
	public static final String TABLE_4M = "Sub total (I to L above)";
	public static final String TABLE_4N = "Supplies and advances on which tax is to be paid (H + M) above";

	public static final String TABLE_5A = "Zero rated supply (Export) without payment of tax ";
	public static final String TABLE_5B = "Supply to SEZs without payment of tax ";
	public static final String TABLE_5C = "Supplies on which tax is to be paid by the recipient on reverse charge ";
	public static final String TABLE_5C1 = "Supplies on which tax is to be paid by e-commerce operators as per section 9(5) [Supplier to report] ";
	public static final String TABLE_5D = "Exempted";
	public static final String TABLE_5E = "Nil Rated";
	public static final String TABLE_5F = "Non-GST supply (includes ‘no supply’)";
	public static final String TABLE_5G = "Sub total (A to F above)";
	public static final String TABLE_5H = "Credit Notes issued in respect of transactions specified in A to F above (-)";
	public static final String TABLE_5I = "Debit Notes issued in respect of transactions specified in A to F above (+)";
	public static final String TABLE_5J = "Supplies declared through Amendments (+)";
	public static final String TABLE_5K = "Supplies reduced through Amendments (-)";
	public static final String TABLE_5L = "Sub-Total (H to K above)";
	public static final String TABLE_5M = "Turnover on which tax is not to be paid (G + L above)";
	public static final String TABLE_5N = "Total Turnover (including advances) (4N + 5M - 4G - 4G1) above";

	public static final String TABLE_6A = "Total amount of input tax credit availed through FORM GSTR-3B (sum total of Table 4A of FORM GSTR-3B)";
	public static final String TABLE_6B = "Inward supplies (other than imports and inward supplies liable  to reverse charge but includes services received from SEZs)";
	public static final String TABLE_6C = "Inward supplies received from unregistered persons liable to reverse charge (other than B above) on which tax is paid & ITC availed";
	public static final String TABLE_6D = "Inward supplies received from registered persons liable to reverse charge (other than B above) on which tax is paid and  ITC availed ";
	public static final String TABLE_6E = "Import of goods (including supplies from SEZs) ";
	public static final String TABLE_6F = "Import of services (excluding inward supplies from SEZs)";
	public static final String TABLE_6G = "Input Tax credit received from ISD ";
	public static final String TABLE_6H = "Amount of ITC reclaimed (other than B above) under the provisions of the Act ";
	public static final String TABLE_6I = "Sub-total (B to H above)";
	public static final String TABLE_6J = "Difference (I - A above)";
	public static final String TABLE_6K = "Transition Credit through TRAN-1 (including revisions if any)";
	public static final String TABLE_6L = "Transition Credit through TRAN-2 ";
	public static final String TABLE_6M = "Any other ITC availed but not specified above";
	public static final String TABLE_6N = "Sub-total (K to M□ above)";
	public static final String TABLE_6O = "Total ITC availed (I + N above) ";

	public static final String TABLE_7A = "As per Rule 37";
	public static final String TABLE_7B = "As per Rule 39";
	public static final String TABLE_7C = "As per Rule 42";
	public static final String TABLE_7D = "As per Rule 43";
	public static final String TABLE_7E = "As per section 17(5)";
	public static final String TABLE_7F = "Reversal of TRAN-1 credit";
	public static final String TABLE_7G = "Reversal of TRAN-2 credit";
	public static final String TABLE_7H = "Other Reversal";
	public static final String TABLE_7I = "Total ITC Reversed (Sum of A to H above)";
	public static final String TABLE_7J = "Net ITC Available for Utilization (6O - 7I) ";

	public static final String TABLE_8A = "ITC as per GSTR-2A (Table 3 & 5 thereof)";
	public static final String TABLE_8B = "ITC as per sum total of 6(B) and 6(H) above";
	public static final String TABLE_8C = "ITC on inward supplies (other than imports and inward supplies liable to reverse charge but includes services received from SEZs) received during the  financial year but availed in the next financial year upto specified period.";
	public static final String TABLE_8D = "Difference [A-(B+C)]";
	public static final String TABLE_8E = "ITC available but not availed";
	public static final String TABLE_8F = "ITC available but ineligible";
	public static final String TABLE_8G = "IGST paid on import of goods (including supplies from SEZ)";
	public static final String TABLE_8H = "IGST credit availed on import of goods (as per 6(E) above)";
	public static final String TABLE_8I = "Difference (G-H)";
	public static final String TABLE_8J = "ITC available but not availed on import of goods (Equal to I)";
	public static final String TABLE_8K = "Total ITC to be lapsed in current financial year (E + F + J)";

	public static final String TABLE_9A = "Integrated Tax";
	public static final String TABLE_9B = "Central Tax";
	public static final String TABLE_9C = "State/UT Tax";
	public static final String TABLE_9D = "Cess";
	public static final String TABLE_9E = "Interest";
	public static final String TABLE_9F = "Late Fees";
	public static final String TABLE_9G = "Penalty";
	public static final String TABLE_9H = "Other ";

	public static final String TABLE_10 = "Supplies / tax declared through Amendments (+) (net of debit notes) ";
	public static final String TABLE_11 = "Supplies / tax reduced through Amendments (-) (net of credit notes) ";
	public static final String TABLE_12 = "Reversal of ITC availed during previous financial year ";
	public static final String TABLE_13 = "ITC availed for the previous financial year";
	public static final String TABLE_10A = "Total turnover(5N + 10 - 11)";

	public static final String TABLE_14A = "Integrated Tax";
	public static final String TABLE_14B = "Central Tax";
	public static final String TABLE_14C = "State/UT Tax";
	public static final String TABLE_14D = "Cess";
	public static final String TABLE_14E = "Interest";

	public static final String TABLE_15A = "Total Refund claimed ";
	public static final String TABLE_15B = "Total Refund sanctioned ";
	public static final String TABLE_15C = "Total Refund Rejected ";
	public static final String TABLE_15D = "Total Refund Pending ";
	public static final String TABLE_15E = "Total demand of taxes ";
	public static final String TABLE_15F = "Total taxes paid in respect of E above";
	public static final String TABLE_15G = "Total demands pending out of E above ";

	public static final String TABLE_16A = "Supplies received from Composition taxpayers ";
	public static final String TABLE_16B = "Deemed supply under section 143";
	public static final String TABLE_16C = "Goods sent on approval basis but not returned";

	public static final String TABLE_17 = "HSN Outward";
	public static final String TABLE_18 = "HSN Inward";

	public static final String NOTE = "Note - Values in table 6A & 8A under “User Edited” tab are considered from “GSTN computed” tab, as these are computed by GSTN portal";
	public static final String FILED = "Filed";
	public static final String SUBMITTED = "Submitted";
	public static final String DRAFT = "Draft";
	public static final String BLANK = "-";

	public static final String INPUTS = "ip";
	public static final String CAPITAL_GOODS = "cg";
	public static final String INPUT_SERVICES = "is";

	public static final String IP = "Inputs";
	public static final String CG = "Capital Goods";
	public static final String IS = "Input Services";

	public static final String TAXABLE_VALUE = "taxableValue";
	public static final String CGST = "cgst";
	public static final String SGST = "sgst";
	public static final String IGST = "igst";
	public static final String CESS = "cess";

	public static final String A = "A";
	public static final String B = "B";
	public static final String C = "C";
	public static final String C51 = "C1";
	public static final String D = "D";
	public static final String E = "E";
	public static final String F = "F";
	public static final String G = "G";
	public static final String G1 = "G1";
	public static final String H = "H";
	public static final String I = "I";
	public static final String J = "J";
	public static final String K = "K";
	public static final String L = "L";
	public static final String M = "M";
	public static final String N = "N";
	public static final String O = "O";
	public static final String B1 = "6B1";
	public static final String B2 = "6B2";
	public static final String B3 = "6B3";
	public static final String C1 = "6C1";
	public static final String C2 = "6C2";
	public static final String C3 = "6C3";
	public static final String D1 = "6D1";
	public static final String D2 = "6D2";
	public static final String D3 = "6D3";
	public static final String E1 = "6E1";
	public static final String E2 = "6E2";

	public static final String TEN = "10";
	public static final String ELEVEN = "11";
	public static final String TWELEVE = "12";
	public static final String THIRTEEN = "13";
	public static final String SEVENTEEN = "17";
	public static final String EIGHTEEN = "18";

}
