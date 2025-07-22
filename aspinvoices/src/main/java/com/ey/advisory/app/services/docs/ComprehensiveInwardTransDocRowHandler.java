package com.ey.advisory.app.services.docs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for Handling Inward Trans Docs
 * 
 * @author Mahesh.Golla
 *
 * @param <K>
 */
@Slf4j
public class ComprehensiveInwardTransDocRowHandler<K> implements RowHandler {

	private Object[] headerData = null;

	public ComprehensiveInwardTransDocRowHandler() {
	}

	public Object[] getHeaderData() {
		return headerData;
	}

	@Override
	public void handleHeaderRow(int rowNo, Object[] row,
			TabularDataLayout layout) {
		// clone the shared array passed by the framework.
		Object[] arr = row.clone();
		headerData = arr;
	}

	/**
	 * The map to be populated with the the data from the file.
	 */
	private Map<String, List<Object[]>> documentMap = new LinkedHashMap<>();

	private DataBlockKeyBuilder<K> keyBuilder;

	public ComprehensiveInwardTransDocRowHandler(
			DataBlockKeyBuilder<K> keyBuilder) {
		this.keyBuilder = keyBuilder;
	}

	@Override
	public boolean handleRow(int rowNo, Object[] row,
			TabularDataLayout layout) {
		Object[] arr = row.clone();
		boolean isRowEmpty = true;
		for (Object object : arr) {
			if (object != null) {
				isRowEmpty = false;
			}
		}
		removeSpecialChars(arr);
		String key = (String) keyBuilder.buildDataBlockKey(arr, layout);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Dockey while traversing from Excel is {} ", key);
		}
		if (!isRowEmpty) {
			documentMap.computeIfAbsent(key, k -> new ArrayList<>()).add(arr);
		}
		return true;
	}

	public Map<String, List<Object[]>> getDocumentMap() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("documentMap in InwardTransDocRowHandler"
					+ documentMap.toString());
		}
		return documentMap;
	}

	private void removeSpecialChars(Object[] obj) {

		obj[3] = CommonUtility.capital(obj[3]); // supplyType
		obj[5] = CommonUtility.capital(obj[5]); // DocType
		//obj[6] = CommonUtility.capitalWithSpace(obj[6]); // DocNo
		Object obj6Before = obj[6]; // Store the value before transformation
		obj[6] = CommonUtility.capitalWithSpace(obj[6]); // DocNo
		LOGGER.debug("DocNo before: '{}', after capitalWithSpace: '{}'", obj6Before, obj[6]);

		obj[6] = CommonUtility.convertScientificNotationWithSpace(obj[6]);// DocNo
		obj[17] = CommonUtility.exponentialAndZeroCheck(obj[17]);// supphonecode
		obj[28] = CommonUtility.exponentialAndZeroCheck(obj[28]); // CustomerPhoneValidation
		obj[45] = CommonUtility.exponentialAndZeroCheck(obj[45]); // ItemSerialNumber
		obj[46] = CommonUtility.exponentialAndZeroCheck(obj[46]); // ProductSerialNumber
		obj[55] = CommonUtility.exponentialAndZeroCheck(obj[55]); // orderLineRef
		obj[57] = CommonUtility.exponentialAndZeroCheck(obj[57]); // Attributevalue
		obj[63] = CommonUtility.exponentialAndZeroCheck(obj[63]); // ItemAmountValidation
		obj[65] = CommonUtility.exponentialAndZeroCheck(obj[65]); // PreTaxAmountValidation
		obj[66] = CommonUtility.exponentialAndZeroCheck(obj[66]); // ItemAssessableAmountValidation
		obj[68] = CommonUtility.exponentialAndZeroCheck(obj[68]);// igstAmt
		obj[70] = CommonUtility.exponentialAndZeroCheck(obj[70]);// cgstAmt
		obj[72] = CommonUtility.exponentialAndZeroCheck(obj[72]);// sgstAmt
		obj[74] = CommonUtility.exponentialAndZeroCheck(obj[74]);// cessAdvAmt
		obj[76] = CommonUtility.exponentialAndZeroCheck(obj[76]);// cessSpeAmt
		obj[78] = CommonUtility.exponentialAndZeroCheck(obj[78]);// stateAdvAmt
		obj[80] = CommonUtility.exponentialAndZeroCheck(obj[80]);// stateSpeAmt
		obj[82] = CommonUtility.exponentialAndZeroCheck(obj[82]);// totalItemAmt
		obj[84] = CommonUtility.exponentialAndZeroCheck(obj[84]);// ItemAssessableAmt
		obj[85] = CommonUtility.exponentialAndZeroCheck(obj[85]);// invIgst
		obj[86] = CommonUtility.exponentialAndZeroCheck(obj[86]);// invCgst
		obj[87] = CommonUtility.exponentialAndZeroCheck(obj[87]);// invSgst
		obj[88] = CommonUtility.exponentialAndZeroCheck(obj[88]);// invCessAdvAmt
		obj[89] = CommonUtility.exponentialAndZeroCheck(obj[89]);// invCessSpeAmt
		obj[90] = CommonUtility.exponentialAndZeroCheck(obj[90]);// invStateCessAdvAmt
		obj[91] = CommonUtility.exponentialAndZeroCheck(obj[91]);// invStateSpeAmt
		obj[92] = CommonUtility.exponentialAndZeroCheck(obj[92]);// invValue
		obj[97] = CommonUtility.exponentialAndZeroCheck(obj[97]);// availableIgst
		obj[98] = CommonUtility.exponentialAndZeroCheck(obj[98]);// availableCgst
		obj[99] = CommonUtility.exponentialAndZeroCheck(obj[99]);// availableSgst
		obj[100] = CommonUtility.exponentialAndZeroCheck(obj[100]);// availableCess
		obj[110] = CommonUtility.exponentialAndZeroCheck(obj[110]); // BillEntryNo
		obj[115] = CommonUtility.exponentialAndZeroCheck(obj[115]);// OrgDocNo/preccendiNo
		obj[117] = CommonUtility.exponentialAndZeroCheck(obj[117]);// othReference
		obj[118] = CommonUtility.exponentialAndZeroCheck(obj[118]);// reciepientRef
		obj[120] = CommonUtility.exponentialAndZeroCheck(obj[120]);// tenderRef
		obj[121] = CommonUtility.exponentialAndZeroCheck(obj[121]);// contractRef
		obj[122] = CommonUtility.exponentialAndZeroCheck(obj[122]);// externalRef
		obj[123] = CommonUtility.exponentialAndZeroCheck(obj[123]);// projectRef
		obj[124] = CommonUtility.exponentialAndZeroCheck(obj[124]);// custPoRef
		obj[134] = CommonUtility.exponentialAndZeroCheck(obj[134]);// paidAmt
		obj[135] = CommonUtility.exponentialAndZeroCheck(obj[135]);// balanceAmt
		obj[137] = CommonUtility.exponentialAndZeroCheck(obj[137]);// AccountDetails
		obj[171] = CommonUtility.exponentialAndZeroCheck(obj[171]);// TcsIgst
		obj[172] = CommonUtility.exponentialAndZeroCheck(obj[172]);// TcsCgst
		obj[173] = CommonUtility.exponentialAndZeroCheck(obj[173]);// TcsSgst
		obj[175] = CommonUtility.exponentialAndZeroCheck(obj[175]);// TdsIgst
		obj[176] = CommonUtility.exponentialAndZeroCheck(obj[176]);// TdsCgst
		obj[177] = CommonUtility.exponentialAndZeroCheck(obj[177]);// TdsSgst
		obj[195] = CommonUtility.exponentialAndZeroCheck(obj[195]);// GlAssValue
		obj[196] = CommonUtility.exponentialAndZeroCheck(obj[196]);// GlIgst
		obj[197] = CommonUtility.exponentialAndZeroCheck(obj[197]);// GlCgst
		obj[198] = CommonUtility.exponentialAndZeroCheck(obj[198]);// GlSgst
		obj[199] = CommonUtility.exponentialAndZeroCheck(obj[199]);// GlCessAdvAmt
		obj[200] = CommonUtility.exponentialAndZeroCheck(obj[200]);// GlCessSpecAmt
		obj[201] = CommonUtility.exponentialAndZeroCheck(obj[201]);// GlStateCessAdvAmt
		obj[202] = CommonUtility.exponentialAndZeroCheck(obj[202]);// GlStateSpecAmt
		obj[204] = CommonUtility.exponentialAndZeroCheck(obj[204]);// ContractValue
		obj[205] = CommonUtility.exponentialAndZeroCheck(obj[205]);// EyBillNumber
		obj[207] = CommonUtility.exponentialAndZeroCheck(obj[207]); // AccVoucharNumber
		//singl equote check for all amount fields 
		obj[0] = CommonUtility.singleQuoteCheck(obj[0]);
		obj[1] = CommonUtility.singleQuoteCheck(obj[1]); 
		obj[2] = CommonUtility.singleQuoteCheck(obj[2]);
		obj[3] = CommonUtility.singleQuoteCheck(obj[3]);
		obj[4] = CommonUtility.singleQuoteCheck(obj[4]);
		obj[5] = CommonUtility.singleQuoteCheck(obj[5]);
		obj[6] = CommonUtility.singleQuoteCheckWithSpace(obj[6]);
		obj[7] = CommonUtility.singleQuoteCheck(obj[7]);
		obj[8] = CommonUtility.singleQuoteCheck(obj[8]);
		obj[9] = CommonUtility.singleQuoteCheck(obj[9]);
		obj[10] = CommonUtility.singleQuoteCheck(obj[10]);
		obj[11] = CommonUtility.singleQuoteCheck(obj[11]);
		obj[12] = CommonUtility.singleQuoteCheck(obj[12]);
		obj[13] = CommonUtility.singleQuoteCheck(obj[13]);
		obj[14] = CommonUtility.singleQuoteCheck(obj[14]);
		obj[15] = CommonUtility.singleQuoteCheck(obj[15]);
		obj[16] = CommonUtility.singleQuoteCheck(obj[16]);
		obj[17] = CommonUtility.singleQuoteCheck(obj[17]);
		obj[18] = CommonUtility.singleQuoteCheck(obj[18]);
		obj[19] = CommonUtility.singleQuoteCheck(obj[19]);
		obj[20] = CommonUtility.singleQuoteCheck(obj[20]);
		obj[21] = CommonUtility.singleQuoteCheck(obj[21]);
		obj[22] = CommonUtility.singleQuoteCheck(obj[22]);
		obj[23] = CommonUtility.singleQuoteCheck(obj[23]);
		obj[24] = CommonUtility.singleQuoteCheck(obj[24]);
		obj[25] = CommonUtility.singleQuoteCheck(obj[25]);
		obj[26] = CommonUtility.singleQuoteCheck(obj[26]);
		obj[27] = CommonUtility.singleQuoteCheck(obj[27]);
		obj[28] = CommonUtility.singleQuoteCheck(obj[28]);
		obj[29] = CommonUtility.singleQuoteCheck(obj[29]);
		obj[30] = CommonUtility.singleQuoteCheck(obj[30]);
		obj[31] = CommonUtility.singleQuoteCheck(obj[31]);
		obj[32] = CommonUtility.singleQuoteCheck(obj[32]);
		obj[33] = CommonUtility.singleQuoteCheck(obj[33]);
		obj[34] = CommonUtility.singleQuoteCheck(obj[34]);
		obj[35] = CommonUtility.singleQuoteCheck(obj[35]);
		obj[36] = CommonUtility.singleQuoteCheck(obj[36]);
		obj[37] = CommonUtility.singleQuoteCheck(obj[37]);
		obj[38] = CommonUtility.singleQuoteCheck(obj[38]);
		obj[39] = CommonUtility.singleQuoteCheck(obj[39]);
		obj[40] = CommonUtility.singleQuoteCheck(obj[40]);
		obj[41] = CommonUtility.singleQuoteCheck(obj[41]);
		obj[42] = CommonUtility.singleQuoteCheck(obj[42]);
		obj[43] = CommonUtility.singleQuoteCheck(obj[43]);
		obj[44] = CommonUtility.singleQuoteCheck(obj[44]);
		obj[45] = CommonUtility.singleQuoteCheck(obj[45]);
		obj[46] = CommonUtility.singleQuoteCheck(obj[46]);
		obj[47] = CommonUtility.singleQuoteCheck(obj[47]);
		obj[48] = CommonUtility.singleQuoteCheck(obj[48]);
		obj[49] = CommonUtility.singleQuoteCheck(obj[49]);
		obj[50] = CommonUtility.singleQuoteCheck(obj[50]);
		obj[51] = CommonUtility.singleQuoteCheck(obj[51]);
		obj[52] = CommonUtility.singleQuoteCheck(obj[52]);
		obj[53] = CommonUtility.singleQuoteCheck(obj[53]);
		obj[54] = CommonUtility.singleQuoteCheck(obj[54]);
		obj[55] = CommonUtility.singleQuoteCheck(obj[55]);
		obj[56] = CommonUtility.singleQuoteCheck(obj[56]);
		obj[57] = CommonUtility.singleQuoteCheck(obj[57]);
		obj[58] = CommonUtility.singleQuoteCheck(obj[58]);
		obj[59] = CommonUtility.singleQuoteCheck(obj[59]);
		obj[60] = CommonUtility.singleQuoteCheck(obj[60]);
		obj[61] = CommonUtility.singleQuoteCheck(obj[61]);
		obj[62] = CommonUtility.singleQuoteCheck(obj[62]);
		obj[63] = CommonUtility.singleQuoteCheck(obj[63]);
		obj[64] = CommonUtility.singleQuoteCheck(obj[64]);
		obj[65] = CommonUtility.singleQuoteCheck(obj[65]);
		obj[66] = CommonUtility.singleQuoteCheck(obj[66]);
		obj[67] = CommonUtility.singleQuoteCheck(obj[67]);
		obj[68] = CommonUtility.singleQuoteCheck(obj[68]);
		obj[69] = CommonUtility.singleQuoteCheck(obj[69]);
		obj[70] = CommonUtility.singleQuoteCheck(obj[70]);
		obj[71] = CommonUtility.singleQuoteCheck(obj[71]);
		obj[72] = CommonUtility.singleQuoteCheck(obj[72]);
		obj[73] = CommonUtility.singleQuoteCheck(obj[73]);
		obj[74] = CommonUtility.singleQuoteCheck(obj[74]);
		obj[75] = CommonUtility.singleQuoteCheck(obj[75]);
		obj[76] = CommonUtility.singleQuoteCheck(obj[76]);
		obj[77] = CommonUtility.singleQuoteCheck(obj[77]);
		obj[78] = CommonUtility.singleQuoteCheck(obj[78]);
		obj[79] = CommonUtility.singleQuoteCheck(obj[79]);
		obj[80] = CommonUtility.singleQuoteCheck(obj[80]);
		obj[81] = CommonUtility.singleQuoteCheck(obj[81]);
		obj[82] = CommonUtility.singleQuoteCheck(obj[82]);
		obj[83] = CommonUtility.singleQuoteCheck(obj[83]);
		obj[84] = CommonUtility.singleQuoteCheck(obj[84]);
		obj[85] = CommonUtility.singleQuoteCheck(obj[85]);
		obj[86] = CommonUtility.singleQuoteCheck(obj[86]);
		obj[87] = CommonUtility.singleQuoteCheck(obj[87]);
		obj[88] = CommonUtility.singleQuoteCheck(obj[88]);
		obj[89] = CommonUtility.singleQuoteCheck(obj[89]);
		obj[90] = CommonUtility.singleQuoteCheck(obj[90]);
		obj[91] = CommonUtility.singleQuoteCheck(obj[91]);
		obj[92] = CommonUtility.singleQuoteCheck(obj[92]);
		obj[93] = CommonUtility.singleQuoteCheck(obj[93]);
		obj[94] = CommonUtility.singleQuoteCheck(obj[94]);
		obj[95] = CommonUtility.singleQuoteCheck(obj[95]);
		obj[96] = CommonUtility.singleQuoteCheck(obj[96]);
		obj[97] = CommonUtility.singleQuoteCheck(obj[97]);
		obj[98] = CommonUtility.singleQuoteCheck(obj[98]);
		obj[99] = CommonUtility.singleQuoteCheck(obj[99]);
		obj[100] = CommonUtility.singleQuoteCheck(obj[100]);
		obj[101] = CommonUtility.singleQuoteCheck(obj[101]);
		obj[102] = CommonUtility.singleQuoteCheck(obj[102]);
		obj[103] = CommonUtility.singleQuoteCheck(obj[103]);
		obj[104] = CommonUtility.singleQuoteCheck(obj[104]);
		obj[105] = CommonUtility.singleQuoteCheck(obj[105]);
		obj[106] = CommonUtility.singleQuoteCheck(obj[106]);
		obj[107] = CommonUtility.singleQuoteCheck(obj[107]);
		obj[108] = CommonUtility.singleQuoteCheck(obj[108]);
		obj[109] = CommonUtility.singleQuoteCheck(obj[109]);
		obj[110] = CommonUtility.singleQuoteCheck(obj[110]);
		obj[111] = CommonUtility.singleQuoteCheck(obj[111]);
		obj[112] = CommonUtility.singleQuoteCheck(obj[112]);
		obj[113] = CommonUtility.singleQuoteCheck(obj[113]);
		obj[114] = CommonUtility.singleQuoteCheck(obj[114]);
		obj[115] = CommonUtility.singleQuoteCheck(obj[115]);
		obj[116] = CommonUtility.singleQuoteCheck(obj[116]);
		obj[117] = CommonUtility.singleQuoteCheck(obj[117]);
		obj[118] = CommonUtility.singleQuoteCheck(obj[118]);
		obj[119] = CommonUtility.singleQuoteCheck(obj[119]);
		obj[120] = CommonUtility.singleQuoteCheck(obj[120]);
		obj[121] = CommonUtility.singleQuoteCheck(obj[121]);
		obj[122] = CommonUtility.singleQuoteCheck(obj[122]);
		obj[123] = CommonUtility.singleQuoteCheck(obj[123]);
		obj[124] = CommonUtility.singleQuoteCheck(obj[124]);
		obj[125] = CommonUtility.singleQuoteCheck(obj[125]);
		obj[126] = CommonUtility.singleQuoteCheck(obj[126]);
		obj[127] = CommonUtility.singleQuoteCheck(obj[127]);
		obj[128] = CommonUtility.singleQuoteCheck(obj[128]);
		obj[129] = CommonUtility.singleQuoteCheck(obj[129]);
		obj[130] = CommonUtility.singleQuoteCheck(obj[130]);
		obj[131] = CommonUtility.singleQuoteCheck(obj[131]);
		obj[132] = CommonUtility.singleQuoteCheck(obj[132]);
		obj[133] = CommonUtility.singleQuoteCheck(obj[133]);
		obj[134] = CommonUtility.singleQuoteCheck(obj[134]);
		obj[135] = CommonUtility.singleQuoteCheck(obj[135]);
		obj[136] = CommonUtility.singleQuoteCheck(obj[136]);
		obj[137] = CommonUtility.singleQuoteCheck(obj[137]);
		obj[138] = CommonUtility.singleQuoteCheck(obj[138]);
		obj[139] = CommonUtility.singleQuoteCheck(obj[139]);
		obj[140] = CommonUtility.singleQuoteCheck(obj[140]);
		obj[141] = CommonUtility.singleQuoteCheck(obj[141]);
		obj[142] = CommonUtility.singleQuoteCheck(obj[142]);
		obj[143] = CommonUtility.singleQuoteCheck(obj[143]);
		obj[144] = CommonUtility.singleQuoteCheck(obj[144]);
		obj[145] = CommonUtility.singleQuoteCheck(obj[145]);
		obj[146] = CommonUtility.singleQuoteCheck(obj[146]);
		obj[147] = CommonUtility.singleQuoteCheck(obj[147]);
		obj[148] = CommonUtility.singleQuoteCheck(obj[148]);
		obj[149] = CommonUtility.singleQuoteCheck(obj[149]);
		obj[150] = CommonUtility.singleQuoteCheck(obj[150]);
		obj[151] = CommonUtility.singleQuoteCheck(obj[151]);
		obj[152] = CommonUtility.singleQuoteCheck(obj[152]);
		obj[153] = CommonUtility.singleQuoteCheck(obj[153]);
		obj[154] = CommonUtility.singleQuoteCheck(obj[154]);
		obj[155] = CommonUtility.singleQuoteCheck(obj[155]);
		obj[156] = CommonUtility.singleQuoteCheck(obj[156]);
		obj[157] = CommonUtility.singleQuoteCheck(obj[157]);
		obj[158] = CommonUtility.singleQuoteCheck(obj[158]);
		obj[159] = CommonUtility.singleQuoteCheck(obj[159]);
		obj[160] = CommonUtility.singleQuoteCheck(obj[160]);
		obj[161] = CommonUtility.singleQuoteCheck(obj[161]);
		obj[162] = CommonUtility.singleQuoteCheck(obj[162]);
		obj[163] = CommonUtility.singleQuoteCheck(obj[163]);
		obj[164] = CommonUtility.singleQuoteCheck(obj[164]);
		obj[165] = CommonUtility.singleQuoteCheck(obj[165]);
		obj[166] = CommonUtility.singleQuoteCheck(obj[166]);
		obj[167] = CommonUtility.singleQuoteCheck(obj[167]);
		obj[168] = CommonUtility.singleQuoteCheck(obj[168]);
		obj[169] = CommonUtility.singleQuoteCheck(obj[169]);
		obj[170] = CommonUtility.singleQuoteCheck(obj[170]);
		obj[171] = CommonUtility.singleQuoteCheck(obj[171]);
		obj[172] = CommonUtility.singleQuoteCheck(obj[172]);
		obj[173] = CommonUtility.singleQuoteCheck(obj[173]);
		obj[174] = CommonUtility.singleQuoteCheck(obj[174]);
		obj[175] = CommonUtility.singleQuoteCheck(obj[175]);
		obj[176] = CommonUtility.singleQuoteCheck(obj[176]);
		obj[177] = CommonUtility.singleQuoteCheck(obj[177]);
		obj[178] = CommonUtility.singleQuoteCheck(obj[178]);
		obj[179] = CommonUtility.singleQuoteCheck(obj[179]);
		obj[180] = CommonUtility.singleQuoteCheck(obj[180]);
		obj[181] = CommonUtility.singleQuoteCheck(obj[181]);
		obj[182] = CommonUtility.singleQuoteCheck(obj[182]);
		obj[183] = CommonUtility.singleQuoteCheck(obj[183]);
		obj[184] = CommonUtility.singleQuoteCheck(obj[184]);
		obj[185] = CommonUtility.singleQuoteCheck(obj[185]);
		obj[186] = CommonUtility.singleQuoteCheck(obj[186]);
		obj[187] = CommonUtility.singleQuoteCheck(obj[187]);
		obj[188] = CommonUtility.singleQuoteCheck(obj[188]);
		obj[189] = CommonUtility.singleQuoteCheck(obj[189]);
		obj[190] = CommonUtility.singleQuoteCheck(obj[190]);
		obj[191] = CommonUtility.singleQuoteCheck(obj[191]);
		obj[192] = CommonUtility.singleQuoteCheck(obj[192]);
		obj[193] = CommonUtility.singleQuoteCheck(obj[193]);
		obj[194] = CommonUtility.singleQuoteCheck(obj[194]);
		obj[195] = CommonUtility.singleQuoteCheck(obj[195]);
		obj[196] = CommonUtility.singleQuoteCheck(obj[196]);
		obj[197] = CommonUtility.singleQuoteCheck(obj[197]);
		obj[198] = CommonUtility.singleQuoteCheck(obj[198]);
		obj[199] = CommonUtility.singleQuoteCheck(obj[199]);
		obj[200] = CommonUtility.singleQuoteCheck(obj[200]);
		obj[201] = CommonUtility.singleQuoteCheck(obj[201]);
		obj[202] = CommonUtility.singleQuoteCheck(obj[202]);
		obj[203] = CommonUtility.singleQuoteCheck(obj[203]);
		obj[204] = CommonUtility.singleQuoteCheck(obj[204]);
		obj[205] = CommonUtility.singleQuoteCheck(obj[205]);
		obj[206] = CommonUtility.singleQuoteCheck(obj[206]);
		obj[207] = CommonUtility.singleQuoteCheck(obj[207]);
		obj[208] = CommonUtility.singleQuoteCheck(obj[208]);
		obj[209] = CommonUtility.singleQuoteCheck(obj[209]);
		obj[210] = CommonUtility.singleQuoteCheck(obj[210]);
		obj[211] = CommonUtility.singleQuoteCheck(obj[211]);
		obj[212] = CommonUtility.singleQuoteCheck(obj[212]);
		obj[213] = CommonUtility.singleQuoteCheck(obj[213]);
		obj[214] = CommonUtility.singleQuoteCheck(obj[214]);
		obj[215] = CommonUtility.singleQuoteCheck(obj[215]);
		obj[216] = CommonUtility.singleQuoteCheck(obj[216]);
		obj[217] = CommonUtility.singleQuoteCheck(obj[217]);
		obj[218] = CommonUtility.singleQuoteCheck(obj[218]);
		obj[219] = CommonUtility.singleQuoteCheck(obj[219]);
		obj[220] = CommonUtility.singleQuoteCheck(obj[220]);
		obj[221] = CommonUtility.singleQuoteCheck(obj[221]);
		obj[222] = CommonUtility.singleQuoteCheck(obj[222]);
		obj[223] = CommonUtility.singleQuoteCheck(obj[223]);
		obj[224] = CommonUtility.singleQuoteCheck(obj[224]);
		obj[225] = CommonUtility.singleQuoteCheck(obj[225]);
		obj[226] = CommonUtility.singleQuoteCheck(obj[226]);
		obj[227] = CommonUtility.singleQuoteCheck(obj[227]);
		obj[228] = CommonUtility.singleQuoteCheck(obj[228]);
		obj[229] = CommonUtility.singleQuoteCheck(obj[229]);
		obj[230] = CommonUtility.singleQuoteCheck(obj[230]);
		obj[231] = CommonUtility.singleQuoteCheck(obj[231]);
		obj[232] = CommonUtility.singleQuoteCheck(obj[232]);
		obj[233] = CommonUtility.singleQuoteCheck(obj[233]);
		obj[234] = CommonUtility.singleQuoteCheck(obj[234]);
		obj[235] = CommonUtility.singleQuoteCheck(obj[235]);
		obj[236] = CommonUtility.singleQuoteCheck(obj[236]);
		obj[237] = CommonUtility.singleQuoteCheck(obj[237]);
		obj[238] = CommonUtility.singleQuoteCheck(obj[238]);
		obj[239] = CommonUtility.singleQuoteCheck(obj[239]);
		

		
	}
}
