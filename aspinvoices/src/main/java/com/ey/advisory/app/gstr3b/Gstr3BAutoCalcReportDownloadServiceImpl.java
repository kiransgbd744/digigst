package com.ey.advisory.app.gstr3b;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh N K
 *
 */
@Service("Gstr3BAutoCalcReportDownloadServiceImpl")
@Slf4j
public class Gstr3BAutoCalcReportDownloadServiceImpl
		implements Gstr3BAutoCalcReportDownloadService {

	@Autowired
	CommonUtility commonUtility;

	public static void main(String[] args) {
		Gstr3BAutoCalcReportDownloadServiceImpl impl = new Gstr3BAutoCalcReportDownloadServiceImpl();
		List<Gstr3BAutoCalcReportDownloadDto> getGstnResponseMap = impl
				.parseJsonResponseForAutoCalc(
						"{\n\"r3bautopop\":{\n\"r1fildt\":\"01-10-2020\",\n\"r2bgendt\":\"01-10-2020\",\n\"r3bgendt\":\"01-10-2020\",\n\"liabitc\":{\n\"gstin\":\"09SSAUP0009A1ZO\",\n\"ret_period\":\"092020\",\n\"sup_details\":{\n\"osup_3_1a\":{\n\"subtotal\":{\n\"txval\":0,\n\"iamt\":1300,\n\"camt\":1200,\n\"samt\":1400,\n\"csamt\":1500\n},\n\"det\":{\n\"tbl4a\":{\n\"txval\":250,\n\"iamt\":100,\n\"camt\":200,\n\"samt\":300,\n\"csamt\":400\n},\n\"tbl4b\":{\n\"txval\":250\n},\n\"tbl4c\":{\n\"txval\":250,\n\"iamt\":100,\n\"camt\":200,\n\"samt\":300,\n\"csamt\":400\n},\n\"tbl5a\":{\n\"txval\":250,\n\"iamt\":100,\n\"csamt\":400\n},\n\"tbl5b\":{\n\"txval\":250,\n\"iamt\":100,\n\"csamt\":400\n},\n\"tbl6c\":{\n\"txval\":250,\n\"iamt\":100,\n\"camt\":200,\n\"samt\":300,\n\"csamt\":400\n},\n\"tbl7a_1\":{\n\"txval\":250,\n\"camt\":200,\n\"samt\":300,\n\"csamt\":400\n},\n\"tbl7b_1\":{\n\"txval\":250,\n\"iamt\":100,\n\"csamt\":400\n},\n\"tbl9a\":{\n\"txval\":250,\n\"iamt\":100,\n\"camt\":200,\n\"samt\":300,\n\"csamt\":400\n},\n\"tbl9b\":{\n\"txval\":250,\n\"iamt\":100,\n\"camt\":200,\n\"samt\":300,\n\"csamt\":400\n},\n\"tbl9c\":{\n\"txval\":250,\n\"iamt\":100,\n\"camt\":200,\n\"samt\":300,\n\"csamt\":400\n},\n\"tbl10a\":{\n\"txval\":250,\n\"camt\":200,\n\"samt\":300,\n\"csamt\":400\n},\n\"tbl10b\":{\n\"txval\":250,\n\"iamt\":100,\n\"csamt\":400\n},\n\"tbl11_i_a_1\":{\n\"txval\":250,\n\"camt\":200,\n\"samt\":300,\n\"csamt\":400\n},\n\"tbl11_i_a_2\":{\n\"txval\":250,\n\"iamt\":100,\n\"csamt\":400\n},\n\"tbl11_i_b_1\":{\n\"txval\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n},\n\"tbl11_i_b_2\":{\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n},\n\"tbl11_ii\":{\n\"txval\":250,\n\"iamt\":100,\n\"camt\":200,\n\"samt\":300,\n\"csamt\":400\n}\n},\n\"det_q\":{\n\"tbl4a\":[\n{\n\"form\":\"IFF - July\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"IFF - August\",\n\"txval\":43534.76,\n\"iamt\":3265.11,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":56.66\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":3928979.98,\n\"iamt\":657878.98,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":1679.33\n}\n],\n\"tbl4b\":[\n{\n\"form\":\"IFF - July\",\n\"txval\":0\n},\n{\n\"form\":\"IFF - August\",\n\"txval\":0\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0\n}\n],\n\"tbl4c\":[\n{\n\"form\":\"IFF - July\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"IFF - August\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n}\n],\n\"tbl5a\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n}\n],\n\"tbl5b\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n}\n],\n\"tbl6c\":[\n{\n\"form\":\"IFF - July\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"IFF - August\",\n\"txval\":34534,\n\"iamt\":6216.12,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":455.66\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n}\n],\n\"tbl7a_1\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":5656.57,\n\"camt\":212.12,\n\"samt\":212.12,\n\"csamt\":65.77\n}\n],\n\"tbl7b_1\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":502101.76,\n\"iamt\":57057.01,\n\"csamt\":481.41\n}\n],\n\"tbl9a\":[\n{\n\"form\":\"IFF - July\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"IFF - August\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n}\n],\n\"tbl9b\":[\n{\n\"form\":\"IFF - July\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"IFF - August\",\n\"txval\":-4534.67,\n\"iamt\":-226.73,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":-767.76\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n}\n],\n\"tbl9c\":[\n{\n\"form\":\"IFF - July\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"IFF - August\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n}\n],\n\"tbl10a\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n}\n],\n\"tbl10b\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n}\n],\n\"tbl11_i_a_1\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n}\n],\n\"tbl11_i_a_2\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n}\n],\n\"tbl11_i_b_1\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n}\n],\n\"tbl11_i_b_2\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n}\n],\n\"tbl11_ii\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"camt\":0,\n\"samt\":0,\n\"csamt\":0\n}\n]\n}\n},\n\"osup_3_1b\":{\n\"subtotal\":{\n\"txval\":0,\n\"iamt\":1300,\n\"csamt\":1500\n},\n\"det\":{\n\"tbl6a\":{\n\"txval\":250,\n\"iamt\":100,\n\"csamt\":400\n},\n\"tbl6b\":{\n\"txval\":250,\n\"iamt\":100,\n\"csamt\":400\n},\n\"tbl9a\":{\n\"txval\":250,\n\"iamt\":100,\n\"csamt\":400\n},\n\"tbl9b\":{\n\"txval\":250,\n\"iamt\":100,\n\"csamt\":400\n},\n\"tbl9c\":{\n\"txval\":250,\n\"iamt\":100,\n\"csamt\":400\n}\n},\n\"det_q\":{\n\"tbl6a\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n}\n],\n\"tbl6b\":[\n{\n\"form\":\"IFF - July\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"IFF - August\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n}\n],\n\"tbl9a\":[\n{\n\"form\":\"IFF - July\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"IFF - August\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n}\n],\n\"tbl9b\":[\n{\n\"form\":\"IFF - July\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"IFF - August\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n}\n],\n\"tbl9c\":[\n{\n\"form\":\"IFF - July\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"IFF - August\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":0,\n\"iamt\":0,\n\"csamt\":0\n}\n]\n}\n},\n\"osup_3_1c\":{\n\"subtotal\":{\n\"txval\":0\n},\n\"det\":{\n\"tbl8\":{\n\"txval\":250\n}\n},\n\"det_q\":{\n\"tbl8\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":50191.52\n}\n]\n}\n},\n\"osup_3_1e\":{\n\"subtotal\":{\n\"txval\":0\n},\n\"det\":{\n\"tbl8\":{\n\"txval\":250\n}\n},\n\"det_q\":{\n\"tbl8\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"txval\":656.77\n}\n]\n}\n},\n\"isup_3_1d\":{\n\"subtotal\":{\n\"txval\":800,\n\"iamt\":111313900646.08,\n\"camt\":91231719843.1,\n\"samt\":113724963483.06,\n\"csamt\":86292076240.16\n},\n\"det\":{\n\"itcavl\":{\n\"txval\":800,\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n\"itcunavl\":{\n\"txval\":800,\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n}\n},\n\"det_q\":{\n\"itcavl\":[\n{\n\"form\":\"July\",\n\"txval\":800,\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n{\n\"form\":\"August\",\n\"txval\":800,\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n{\n\"form\":\"September\",\n\"txval\":800,\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n}\n],\n\"itcunavl\":[\n{\n\"form\":\"July\",\n\"txval\":800,\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n{\n\"form\":\"August\",\n\"txval\":800,\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n{\n\"form\":\"September\",\n\"txval\":800,\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n}\n]\n}\n}\n},\n\"inter_sup\":{\n\"osup_unreg_3_2\":{\n\"subtotal\":[\n{\n\"pos\":\"\",\n\"txval\":1100,\n\"iamt\":1000\n}\n],\n\"det\":{\n\"tbl5a\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl7b_1\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl9a\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl9b\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl9c\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl10b\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl11_i_a_2\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl11_i_b_2\":[\n{\n\"pos\":\"07\",\n\"txval\":0,\n\"iamt\":0\n},\n{\n\"pos\":\"08\",\n\"txval\":0,\n\"iamt\":0\n}\n],\n\"tbl11_ii\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n]\n},\n\"det_q\":{\n\"tbl5a\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n],\n\"tbl7b_1\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"07\",\n\"txval\":456456,\n\"iamt\":54774.72\n},\n{\n\"pos\":\"32\",\n\"txval\":45645.76,\n\"iamt\":2282.29\n}\n]\n}\n],\n\"tbl9a\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n],\n\"tbl9b\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n],\n\"tbl9c\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n],\n\"tbl10b\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n],\n\"tbl11_i_a_2\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n],\n\"tbl11_i_b_2\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n],\n\"tbl11_ii\":[\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n]\n}\n},\n\"osup_comp_3_2\":{\n\"subtotal\":[\n{\n\"pos\":\"\",\n\"txval\":1100,\n\"iamt\":1000\n}\n],\n\"det\":{\n\"tbl4a\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl4c\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl9a\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl9b\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl9c\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n]\n},\n\"det_q\":{\n\"tbl4a\":[\n{\n\"form\":\"IFF - July\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"IFF - August\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"05\",\n\"txval\":3454534,\n\"iamt\":621816.12\n},\n{\n\"pos\":\"03\",\n\"txval\":435345.57,\n\"iamt\":32650.92\n}\n]\n}\n],\n\"tbl4c\":[\n{\n\"form\":\"IFF - July\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"IFF - August\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n],\n\"tbl9a\":[\n{\n\"form\":\"IFF - July\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"IFF - August\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n],\n\"tbl9b\":[\n{\n\"form\":\"IFF - July\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"IFF - August\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n],\n\"tbl9c\":[\n{\n\"form\":\"IFF - July\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"IFF - August\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n]\n}\n},\n\"osup_uin_3_2\":{\n\"subtotal\":[\n{\n\"pos\":\"\",\n\"txval\":1100,\n\"iamt\":1000\n}\n],\n\"det\":{\n\"tbl4a\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl4c\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl9a\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl9b\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n],\n\"tbl9c\":[\n{\n\"pos\":\"07\",\n\"txval\":2000,\n\"iamt\":1600\n},\n{\n\"pos\":\"08\",\n\"txval\":2000,\n\"iamt\":1600\n}\n]\n},\n\"det_q\":{\n\"tbl4a\":[\n{\n\"form\":\"IFF - July\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"IFF - August\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"26\",\n\"txval\":4565.76,\n\"iamt\":821.84\n},\n{\n\"pos\":\"01\",\n\"txval\":34534.65,\n\"iamt\":2590.1\n}\n]\n}\n],\n\"tbl4c\":[\n{\n\"form\":\"IFF - July\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"IFF - August\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n],\n\"tbl9a\":[\n{\n\"form\":\"IFF - July\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"IFF - August\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n],\n\"tbl9b\":[\n{\n\"form\":\"IFF - July\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"IFF - August\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n],\n\"tbl9c\":[\n{\n\"form\":\"IFF - July\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"IFF - August\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n},\n{\n\"form\":\"GSTR-1 Quarterly\",\n\"pos_det\":[\n{\n\"pos\":\"\",\n\"txval\":0,\n\"iamt\":0\n}\n]\n}\n]\n}\n}\n},\n\"elgitc\":{\n\"itc4a1\":{\n\"subtotal\":{\n\"iamt\":910328503.06,\n\"csamt\":146038120.08\n},\n\"det\":{\n\"itcavl\":{\n\"igst\":200,\n\"cess\":200\n}\n},\n\"det_q\":{\n\"itcavl\":[\n{\n\"form\":\"July\",\n\"igst\":200,\n\"cess\":200\n},\n{\n\"form\":\"August\",\n\"igst\":200,\n\"cess\":200\n},\n{\n\"form\":\"September\",\n\"igst\":200,\n\"cess\":200\n}\n]\n}\n},\n\"itc4a3\":{\n\"subtotal\":{\n\"iamt\":110328503.06,\n\"camt\":8321391340.04,\n\"samt\":93403572143.02,\n\"csamt\":41146038120.08\n},\n\"det\":{\n\"itcavl\":{\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n}\n},\n\"det_q\":{\n\"itcavl\":[\n{\n\"form\":\"July\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n{\n\"form\":\"August\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n{\n\"form\":\"September\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n}\n]\n}\n},\n\"itc4a4\":{\n\"subtotal\":{\n\"iamt\":18910328503.06,\n\"camt\":80321391340.04,\n\"samt\":93403572143.02,\n\"csamt\":43146038120.08\n},\n\"det\":{\n\"itcavl\":{\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n}\n},\n\"det_q\":{\n\"itcavl\":[\n{\n\"form\":\"July\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n{\n\"form\":\"August\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n{\n\"form\":\"September\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n}\n]\n}\n},\n\"itc4a5\":{\n\"subtotal\":{\n\"iamt\":18910328503.06,\n\"camt\":80321391340.04,\n\"samt\":93403572143.02,\n\"csamt\":43146038120.08\n},\n\"det\":{\n\"itcavl\":{\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n}\n},\n\"det_q\":{\n\"itcavl\":[\n{\n\"form\":\"July\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n{\n\"form\":\"August\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n{\n\"form\":\"September\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n}\n]\n}\n},\n\"itc4d2\":{\n\"subtotal\":{\n\"iamt\":94.81,\n\"camt\":255.00,\n\"samt\":255.00,\n\"csamt\":0.00\n},\n\"det\":{\n\"itcunavl\":{\n\"igst\":94.81,\n\"cgst\":255.00,\n\"sgst\":255.00,\n\"cess\":0.00\n}\n}\n},\n\"itc4b2\":{\n\"subtotal\":{\n\"iamt\":18010328503.06,\n\"camt\":80021391340.04,\n\"samt\":93103572143.02,\n\"csamt\":41146038120.08\n},\n\"det\":{\n\"itcavl\":{\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n\"itcunavl\":{\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n\"itcrev\":{\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n}\n},\n\"det_q\":{\n\"itcavl\":[\n{\n\"form\":\"July\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n{\n\"form\":\"August\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n{\n\"form\":\"September\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n}\n],\n\"itcunavl\":[\n{\n\"form\":\"July\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n{\n\"form\":\"August\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n},\n{\n\"form\":\"September\",\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n}\n],\n\"itcrev\":[\n{\n\"igst\":200,\n\"cgst\":200,\n\"sgst\":200,\n\"cess\":200\n}\n]\n}\n}\n}\n}\n}\n}",
						"Gstn");
		System.out.println(getGstnResponseMap);

	}

	public List<Gstr3BAutoCalcReportDownloadDto> getGstrList(
			String gstnResponse, String gstin) {

		List<Gstr3BAutoCalcReportDownloadDto> getGstnResponseMap = parseJsonResponseForAutoCalc(
				gstnResponse, gstin);

		return getGstnResponseMap;
	}

	public List<Gstr3BAutoCalcReportDownloadDto> parseJsonResponseForAutoCalc(
			String gstnResponse, String gstin) {

		List<Gstr3BAutoCalcReportDownloadDto> listAutoCalc = new ArrayList<>();

		try {
			JsonObject tbl3Asup_4a = null;
			JsonObject tbl3Asup_4b = null;
			JsonObject tbl3Asup_4c = null;
			JsonObject tbl3Asup_5a = null;
			JsonObject tbl3Asup_5b = null;
			JsonObject tbl3Asup_6c = null;
			JsonObject tbl3Asup_7a = null;
			JsonObject tbl3Asup_7b = null;
			JsonObject tbl3Asup_9a = null;
			JsonObject tbl3Asup_9b = null;
			JsonObject tbl3Asup_9c = null;
			JsonObject tbl3Asup_10a = null;
			JsonObject tbl3Asup_10b = null;
			JsonObject tbl3Asup_11a1 = null;
			JsonObject tbl3Asup_11a2 = null;
			JsonObject tbl3Asup_11b1 = null;
			JsonObject tbl3Asup_11b2 = null;
			JsonObject tbl3Asup_11ii = null;
			JsonObject tbl3Bsup_6a = null;
			JsonObject tbl3Bsup_6b = null;
			JsonObject tbl3Bsup_9a = null;
			JsonObject tbl3Bsup_9b = null;
			JsonObject tbl3Bsup_9c = null;
			JsonObject tbl3Csup_8 = null;
			JsonObject tbl3Esup_8 = null;

			JsonObject itcAvl = null;
			JsonObject itcUnAvl = null;
//			JsonObject itcRev = null;

			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject parseResponse = new JsonObject();
			Gstr3BAutoCalcReportDownloadDto autoCalDto = null;
			if (gstnResponse != null) {

				parseResponse = (JsonObject) JsonParser
						.parseString(gstnResponse).getAsJsonObject();

				JsonObject r3bAutoPop = parseResponse
						.getAsJsonObject("r3bautopop");

				JsonObject liabItc = r3bAutoPop.has("liabitc")
						? r3bAutoPop.getAsJsonObject("liabitc") : null;

				JsonObject suppDetails3A = liabItc
						.getAsJsonObject("sup_details")
						.getAsJsonObject("osup_3_1a").getAsJsonObject("det");

				if (suppDetails3A != null) {
					tbl3Asup_4a = suppDetails3A.has("tbl4a")
							? suppDetails3A.getAsJsonObject("tbl4a") : null;
					autoCalDto = gson.fromJson(tbl3Asup_4a,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_4A);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_4b = suppDetails3A.has("tbl4b")
							? suppDetails3A.getAsJsonObject("tbl4b") : null;
					autoCalDto = gson.fromJson(tbl3Asup_4b,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_4B);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_4c = suppDetails3A.has("tbl4c")
							? suppDetails3A.getAsJsonObject("tbl4c") : null;
					autoCalDto = gson.fromJson(tbl3Asup_4c,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_4C);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_5a = suppDetails3A.has("tbl5a")
							? suppDetails3A.getAsJsonObject("tbl5a") : null;
					autoCalDto = gson.fromJson(tbl3Asup_5a,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_5A);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_5b = suppDetails3A.has("tbl5b")
							? suppDetails3A.getAsJsonObject("tbl5b") : null;
					autoCalDto = gson.fromJson(tbl3Asup_5b,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_5B);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_6c = suppDetails3A.has("tbl6c")
							? suppDetails3A.getAsJsonObject("tbl6c") : null;
					autoCalDto = gson.fromJson(tbl3Asup_6c,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_6C);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_7a = suppDetails3A.has("tbl7a_1")
							? suppDetails3A.getAsJsonObject("tbl7a_1") : null;
					autoCalDto = gson.fromJson(tbl3Asup_7a,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_7A1);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_7b = suppDetails3A.has("tbl7b_1")
							? suppDetails3A.getAsJsonObject("tbl7b_1") : null;
					autoCalDto = gson.fromJson(tbl3Asup_7b,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_7B1);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_9a = suppDetails3A.has("tbl9a")
							? suppDetails3A.getAsJsonObject("tbl9a") : null;
					autoCalDto = gson.fromJson(tbl3Asup_9a,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_9A);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_9b = suppDetails3A.has("tbl9b")
							? suppDetails3A.getAsJsonObject("tbl9b") : null;
					autoCalDto = gson.fromJson(tbl3Asup_9b,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_9B);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_9c = suppDetails3A.has("tbl9c")
							? suppDetails3A.getAsJsonObject("tbl9c") : null;
					autoCalDto = gson.fromJson(tbl3Asup_9c,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_9C);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_10a = suppDetails3A.has("tbl10a")
							? suppDetails3A.getAsJsonObject("tbl10a") : null;
					autoCalDto = gson.fromJson(tbl3Asup_10a,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_10A);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_10b = suppDetails3A.has("tbl10b")
							? suppDetails3A.getAsJsonObject("tbl10b") : null;
					autoCalDto = gson.fromJson(tbl3Asup_10b,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_10B);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_11a1 = suppDetails3A.has("tbl11_i_a_1")
							? suppDetails3A.getAsJsonObject("tbl11_i_a_1")
							: null;
					autoCalDto = gson.fromJson(tbl3Asup_11a1,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_11IA1);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_11a2 = suppDetails3A.has("tbl11_i_a_2")
							? suppDetails3A.getAsJsonObject("tbl11_i_a_2")
							: null;
					autoCalDto = gson.fromJson(tbl3Asup_11a2,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_11IA2);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_11b1 = suppDetails3A.has("tbl11_i_b_1")
							? suppDetails3A.getAsJsonObject("tbl11_i_b_1")
							: null;
					autoCalDto = gson.fromJson(tbl3Asup_11b1,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_11IB1);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_11b2 = suppDetails3A.has("tbl11_i_b_2")
							? suppDetails3A.getAsJsonObject("tbl11_i_b_2")
							: null;
					autoCalDto = gson.fromJson(tbl3Asup_11b2,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_11IB2);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3A != null) {
					tbl3Asup_11ii = suppDetails3A.has("tbl11_ii")
							? suppDetails3A.getAsJsonObject("tbl11_ii") : null;
					autoCalDto = gson.fromJson(tbl3Asup_11ii,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1A);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3A_11II);
					listAutoCalc.add(autoCalDto);
				}

				JsonObject suppDetails3B = liabItc
						.getAsJsonObject("sup_details")
						.getAsJsonObject("osup_3_1b").getAsJsonObject("det");

				if (suppDetails3B != null) {
					tbl3Bsup_6a = suppDetails3B.has("tbl6a")
							? suppDetails3B.getAsJsonObject("tbl6a") : null;
					autoCalDto = gson.fromJson(tbl3Bsup_6a,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1B);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3B_6A);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3B != null) {
					tbl3Bsup_6b = suppDetails3B.has("tbl6b")
							? suppDetails3B.getAsJsonObject("tbl6b") : null;
					autoCalDto = gson.fromJson(tbl3Bsup_6b,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1B);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3B_6B);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3B != null) {
					tbl3Bsup_9a = suppDetails3B.has("tbl9a")
							? suppDetails3B.getAsJsonObject("tbl9a") : null;
					autoCalDto = gson.fromJson(tbl3Bsup_9a,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1B);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3B_9A);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3B != null) {
					tbl3Bsup_9b = suppDetails3B.has("tbl9b")
							? suppDetails3B.getAsJsonObject("tbl9b") : null;
					autoCalDto = gson.fromJson(tbl3Bsup_9b,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1B);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3B_9B);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3B != null) {
					tbl3Bsup_9c = suppDetails3B.has("tbl9c")
							? suppDetails3B.getAsJsonObject("tbl9c") : null;
					autoCalDto = gson.fromJson(tbl3Bsup_9c,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1B);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3B_9C);
					listAutoCalc.add(autoCalDto);
				}

				JsonObject suppDetails3C = liabItc
						.getAsJsonObject("sup_details")
						.getAsJsonObject("osup_3_1c").getAsJsonObject("det");

				if (suppDetails3C != null) {
					tbl3Csup_8 = suppDetails3C.has("tbl8")
							? suppDetails3C.getAsJsonObject("tbl8") : null;
					autoCalDto = gson.fromJson(tbl3Csup_8,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1C);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3C_8);
					listAutoCalc.add(autoCalDto);
				}

				// v.2

				JsonObject suppDetails3d = liabItc
						.getAsJsonObject("sup_details").has("isup_3_1d")
								? liabItc.getAsJsonObject("sup_details")
										.getAsJsonObject("isup_3_1d")
										.getAsJsonObject("det")
								: null;

				if (suppDetails3d != null && suppDetails3C.has("itcavl")) {
					itcAvl = suppDetails3C.getAsJsonObject("itcavl");
					autoCalDto = gson.fromJson(itcAvl,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1D);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.itcavl);
					listAutoCalc.add(autoCalDto);
				}

				if (suppDetails3d != null && suppDetails3C.has("itcunavl")) {
					itcUnAvl = suppDetails3C.getAsJsonObject("itcunavl");
					autoCalDto = gson.fromJson(itcUnAvl,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1D);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.itcunavl);
					listAutoCalc.add(autoCalDto);
				}

				JsonObject suppDetails3E = liabItc
						.getAsJsonObject("sup_details")
						.getAsJsonObject("osup_3_1e").getAsJsonObject("det");

				if (suppDetails3E != null) {
					tbl3Esup_8 = suppDetails3E.has("tbl8")
							? suppDetails3E.getAsJsonObject("tbl8") : null;
					autoCalDto = gson.fromJson(tbl3Esup_8,
							Gstr3BAutoCalcReportDownloadDto.class);
					autoCalDto.setGstin(gstin);
					autoCalDto.setSectionNo(
							Gstr3BAutoCalcReportDownloadConstants.section3_1E);
					autoCalDto.setTableNo(
							Gstr3BAutoCalcReportDownloadConstants.Table3E_8);
					listAutoCalc.add(autoCalDto);
				}

				JsonObject osupUnreg = liabItc.getAsJsonObject("inter_sup")
						.getAsJsonObject("osup_unreg_3_2")
						.getAsJsonObject("det");

				JsonArray tablUnreg5a = osupUnreg.getAsJsonArray("tbl5a");
				JsonArray tablUnreg7b_1 = osupUnreg.getAsJsonArray("tbl7b_1");
				JsonArray tablUnreg9a = osupUnreg.getAsJsonArray("tbl9a");
				JsonArray tablUnreg9b = osupUnreg.getAsJsonArray("tbl9b");
				JsonArray tablUnreg9c = osupUnreg.getAsJsonArray("tbl9c");
				JsonArray tablUnreg10b = osupUnreg.getAsJsonArray("tbl10b");
				JsonArray tablUnreg11a = osupUnreg
						.getAsJsonArray("tbl11_i_a_2");
				JsonArray tablUnreg11b = osupUnreg
						.getAsJsonArray("tbl11_i_b_2");
				JsonArray tablUnreg11i = osupUnreg.getAsJsonArray("tbl11_ii");

				if (tablUnreg5a != null) {
					for (int i = 0; i < tablUnreg5a.size(); i++) {
						JsonObject value = tablUnreg5a.get(i).getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3UN_5A);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (tablUnreg7b_1 != null) {
					for (int i = 0; i < tablUnreg7b_1.size(); i++) {
						JsonObject value = tablUnreg7b_1.get(i)
								.getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3UN_7B1);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (tablUnreg9a != null) {
					for (int i = 0; i < tablUnreg9a.size(); i++) {
						JsonObject value = tablUnreg9a.get(i).getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3UN_9A);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (tablUnreg9b != null) {
					for (int i = 0; i < tablUnreg9b.size(); i++) {
						JsonObject value = tablUnreg9b.get(i).getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3UN_9B);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (tablUnreg9c != null) {
					for (int i = 0; i < tablUnreg9c.size(); i++) {
						JsonObject value = tablUnreg9c.get(i).getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3UN_9C);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (tablUnreg10b != null) {
					for (int i = 0; i < tablUnreg10b.size(); i++) {
						JsonObject value = tablUnreg10b.get(i)
								.getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3UN_10B);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (tablUnreg11a != null) {
					for (int i = 0; i < tablUnreg11a.size(); i++) {
						JsonObject value = tablUnreg11a.get(i)
								.getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3UN_11IA2);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (tablUnreg11b != null) {
					for (int i = 0; i < tablUnreg11b.size(); i++) {
						JsonObject value = tablUnreg11b.get(i)
								.getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3UN_11IB2);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (tablUnreg11i != null) {
					for (int i = 0; i < tablUnreg11i.size(); i++) {
						JsonObject value = tablUnreg11i.get(i)
								.getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3UN_11II);
						listAutoCalc.add(autoCalDto);
					}
				}

				JsonObject osupComp = liabItc.getAsJsonObject("inter_sup")
						.getAsJsonObject("osup_comp_3_2")
						.getAsJsonObject("det");

				JsonArray osuComp4a = osupComp.getAsJsonArray("tbl4a");
				JsonArray osuComp4c = osupComp.getAsJsonArray("tbl4c");
				JsonArray osuComp9a = osupComp.getAsJsonArray("tbl9a");
				JsonArray osuComp9b = osupComp.getAsJsonArray("tbl9b");
				JsonArray osuComp9c = osupComp.getAsJsonArray("tbl9c");

				if (osuComp4a != null) {
					for (int i = 0; i < osuComp4a.size(); i++) {
						JsonObject value = osuComp4a.get(i).getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionComp3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3COMP_114A);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (osuComp4c != null) {
					for (int i = 0; i < osuComp4c.size(); i++) {
						JsonObject value = osuComp4c.get(i).getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionComp3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3COMP_114C);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (osuComp9a != null) {
					for (int i = 0; i < osuComp9a.size(); i++) {
						JsonObject value = osuComp9a.get(i).getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionComp3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3COMP_119A);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (osuComp9b != null) {
					for (int i = 0; i < osuComp9b.size(); i++) {
						JsonObject value = osuComp9b.get(i).getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionComp3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3COMP_119B);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (osuComp9c != null) {
					for (int i = 0; i < osuComp9c.size(); i++) {
						JsonObject value = osuComp9c.get(i).getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionComp3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3COMP_119C);
						listAutoCalc.add(autoCalDto);
					}
				}

				JsonObject osupUin = liabItc.getAsJsonObject("inter_sup")
						.getAsJsonObject("osup_uin_3_2").getAsJsonObject("det");

				JsonArray osuUin4a = osupUin.getAsJsonArray("tbl4a");
				JsonArray osuUin4c = osupUin.getAsJsonArray("tbl4c");
				JsonArray osuUin9a = osupUin.getAsJsonArray("tbl9a");
				JsonArray osuUin9b = osupUin.getAsJsonArray("tbl9b");
				JsonArray osuUin9c = osupUin.getAsJsonArray("tbl9c");

				if (osuUin4a != null) {
					for (int i = 0; i < osuUin4a.size(); i++) {
						JsonObject value = osuUin4a.get(i).getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionUin3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3UIN_114A);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (osuUin4c != null) {
					for (int i = 0; i < osuUin4c.size(); i++) {
						JsonObject value = osuUin4c.get(i).getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionUin3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3UIN_114C);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (osuUin9a != null) {
					for (int i = 0; i < osuUin9a.size(); i++) {
						JsonObject value = osuUin9a.get(i).getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionUin3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3UIN_119A);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (osuUin9b != null) {
					for (int i = 0; i < osuUin9b.size(); i++) {
						JsonObject value = osuUin9b.get(i).getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionUin3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3UIN_119B);
						listAutoCalc.add(autoCalDto);
					}
				}

				if (osuUin9c != null) {
					for (int i = 0; i < osuUin9c.size(); i++) {
						JsonObject value = osuUin9c.get(i).getAsJsonObject();
						autoCalDto = gson.fromJson(value,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.sectionUin3_2);
						autoCalDto.setTableNo(
								Gstr3BAutoCalcReportDownloadConstants.Table3UIN_119C);
						listAutoCalc.add(autoCalDto);
					}
				}

				// v.2

				JsonObject elgItc = liabItc.has("elgitc")
						? liabItc.getAsJsonObject("elgitc") : null;

				if (elgItc != null) {

					JsonObject elgItc4a1 = elgItc.has("itc4a1")
							? elgItc.getAsJsonObject("itc4a1") : null;

					if (elgItc4a1 != null && elgItc4a1.has("subtotal")) {
						itcAvl = elgItc4a1.getAsJsonObject("subtotal");
						autoCalDto = gson.fromJson(itcAvl,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.section4_A1);
						// autoCalDto.setTableNo(
						// Gstr3BAutoCalcReportDownloadConstants.itcavl);
						listAutoCalc.add(autoCalDto);
					}

					JsonObject elgItc4a3 = elgItc.has("itc4a3")
							? elgItc.getAsJsonObject("itc4a3") : null;

					if (elgItc4a3 != null && elgItc4a3.has("subtotal")) {
						itcAvl = elgItc4a3.getAsJsonObject("subtotal");
						autoCalDto = gson.fromJson(itcAvl,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.section4_A3);
						// autoCalDto.setTableNo(
						// Gstr3BAutoCalcReportDownloadConstants.itcavl);
						listAutoCalc.add(autoCalDto);
					}

					JsonObject elgItc4a4 = elgItc.has("itc4a4")
							? elgItc.getAsJsonObject("itc4a4") : null;

					if (elgItc4a4 != null && elgItc4a4.has("subtotal")) {
						itcAvl = elgItc4a4.getAsJsonObject("subtotal");
						autoCalDto = gson.fromJson(itcAvl,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.section4_A4);
						// autoCalDto.setTableNo(
						// Gstr3BAutoCalcReportDownloadConstants.itcavl);
						listAutoCalc.add(autoCalDto);
					}

					JsonObject elgItc4a5 = elgItc.has("itc4a5")
							? elgItc.getAsJsonObject("itc4a5") : null;

					if (elgItc4a5 != null && elgItc4a5.has("subtotal")) {
						itcAvl = elgItc4a5.getAsJsonObject("subtotal");
						autoCalDto = gson.fromJson(itcAvl,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.section4_A5);
						// autoCalDto.setTableNo(
						// Gstr3BAutoCalcReportDownloadConstants.itcavl);
						listAutoCalc.add(autoCalDto);
					}

					JsonObject elgItc4b2 = elgItc.has("itc4b2")
							? elgItc.getAsJsonObject("itc4b2") : null;
					if (elgItc4b2 != null && elgItc4b2.has("subtotal")) {
						itcAvl = elgItc4b2.getAsJsonObject("subtotal");
						autoCalDto = gson.fromJson(itcAvl,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.section4_B2);
						// autoCalDto.setTableNo(
						// Gstr3BAutoCalcReportDownloadConstants.itcavl);
						listAutoCalc.add(autoCalDto);
					}

					JsonObject elgItc4d2 = elgItc.has("itc4d2")
							? elgItc.getAsJsonObject("itc4d2") : null;

					if (elgItc4d2 != null && elgItc4d2.has("subtotal")) {
						itcUnAvl = elgItc4d2.getAsJsonObject("subtotal");
						autoCalDto = gson.fromJson(itcUnAvl,
								Gstr3BAutoCalcReportDownloadDto.class);
						autoCalDto.setGstin(gstin);
						autoCalDto.setSectionNo(
								Gstr3BAutoCalcReportDownloadConstants.section4_D2);
						// autoCalDto.setTableNo(
						// Gstr3BAutoCalcReportDownloadConstants.itcunavl);
						listAutoCalc.add(autoCalDto);
					}
				}

			}
			// Constant template value if there is no response
			else if (gstin != null) {
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_4A);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_4B);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_4C);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_5A);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_5B);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_6C);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_7A1);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_7B1);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_9A);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_9B);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_9C);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_10A);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_10B);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_11IA1);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_11IA2);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_11IB1);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_11IB2);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1A);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3A_11II);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1B);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3B_6A);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1B);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3B_6B);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1B);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3B_6B);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1B);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3B_9A);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1B);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3B_9B);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1B);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3B_9C);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1C);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3C_8);
				listAutoCalc.add(autoCalDto);

				// v.2
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1D);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.itcavl);
				listAutoCalc.add(autoCalDto);

				// v.2
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1D);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.itcunavl);
				listAutoCalc.add(autoCalDto);

				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section3_1E);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3E_8);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3UN_5A);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3UN_7B1);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3UN_9A);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3UN_9B);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3UN_9C);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3UN_10B);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3UN_11IA2);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3UN_11IB2);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionUnreg3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3UN_11II);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionComp3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3COMP_114A);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionComp3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3COMP_114C);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionComp3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3COMP_119A);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionComp3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3COMP_119B);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionComp3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3COMP_119C);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionUin3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3UIN_114A);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionUin3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3UIN_114C);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionUin3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3UIN_119A);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionUin3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3UIN_119B);
				listAutoCalc.add(autoCalDto);
				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.sectionUin3_2);
				autoCalDto.setTableNo(
						Gstr3BAutoCalcReportDownloadConstants.Table3UIN_119C);
				listAutoCalc.add(autoCalDto);

				// v.2

				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section4_A1);
				// autoCalDto.setTableNo(
				// Gstr3BAutoCalcReportDownloadConstants.itcavl);
				listAutoCalc.add(autoCalDto);

				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section4_A3);
				// autoCalDto.setTableNo(
				// Gstr3BAutoCalcReportDownloadConstants.itcavl);
				listAutoCalc.add(autoCalDto);

				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section4_A4);
				// autoCalDto.setTableNo(
				// Gstr3BAutoCalcReportDownloadConstants.itcavl);
				listAutoCalc.add(autoCalDto);

				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section4_A5);
				// autoCalDto.setTableNo(
				// Gstr3BAutoCalcReportDownloadConstants.itcavl);
				listAutoCalc.add(autoCalDto);

				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section4_B2);
				// autoCalDto.setTableNo(
				// Gstr3BAutoCalcReportDownloadConstants.itcrev);
				listAutoCalc.add(autoCalDto);

				autoCalDto = new Gstr3BAutoCalcReportDownloadDto();
				autoCalDto.setGstin(gstin);
				autoCalDto.setSectionNo(
						Gstr3BAutoCalcReportDownloadConstants.section4_D2);
				// autoCalDto.setTableNo(
				// Gstr3BAutoCalcReportDownloadConstants.itcunavl);
				listAutoCalc.add(autoCalDto);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("Gstr3BAutoCalcReportDownloadServiceImpl"
							+ ".parseJsonResponseForAutoCalc "
							+ "No data found for gstin : " + gstin);
				}

			}
			return listAutoCalc;
		} catch (AppException ex) {
			LOGGER.error(gstnResponse);
			LOGGER.error(ex.getMessage(), ex);
			return listAutoCalc;

		} catch (Exception ex) {
			LOGGER.error(gstnResponse);
			LOGGER.error(ex.getMessage(), ex);
			return listAutoCalc;

		}
	}

}
