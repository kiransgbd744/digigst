package com.ey.advisory.gstr9.jsontocsv.converters.itc04;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.itc04.GetDetailsForItc04ReqDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04ItemsDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04M2jwDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Table5ADto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Table5BDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Table5CDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Component("Itc04JsonToCsvConverter")
public class Itc04JsonToCsvConverter implements JsonToCsvConverter {

	private static final String ITC04_HDR = "FY,ReturnPeriod,SupplierGSTIN,"
			+ "TableNumber,DeliveryChallanNumber,DeliveryChallanDate,"
			+ "JWDeliveryChallanNumber,JWDeliveryChallanDate,InvoiceNumber,"
			+ "InvoiceDate,JobWorkerGSTIN,JobWorkerStateCode,JobWorkerType,"
			+ "JobWorkerName,TypeOfGoods,ItemSerialNumber,ProductDescription,"
			+ "NatureOfJW,UQC,Quantity,LossesUQC,LossesQuantity,"
			+ "ItemAssessableAmount,IGSTRate,CGSTRate,SGSTRate,CessRate,IsFiled\r\n";

	private static Map<String, String> RETURN_PERIOD_MAP = null;

	static {
		Map<String, String> map = new HashMap<>();

		map.put("13", "Apr-Jun");
		map.put("14", "Jul-Sep");
		map.put("15", "Oct-Dec");
		map.put("16", "Jan-Mar");
		map.put("17", "Apr-Sep");
		map.put("18", "Oct-Mar");
		RETURN_PERIOD_MAP = Collections.unmodifiableMap(map);
	}

	public static String getReturnPeriodForItc04(String month) {
		return RETURN_PERIOD_MAP.get(month);
	}

	@Override
	public String[] getCsvHeaderStrings() {
		return new String[] { ITC04_HDR };
	}

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriters) throws IOException {
		Gson gson = GsonUtil.newSAPGsonInstance();
		BufferedWriter csvWriter = csvWriters[0];
		GetDetailsForItc04ReqDto itc04GetReqDto = gson.fromJson(reader,
				GetDetailsForItc04ReqDto.class);

		String sGstin = itc04GetReqDto.getGstin();
		List<Itc04M2jwDto> m2jwDtoList = itc04GetReqDto.getM2jwDto();
		List<Itc04Table5ADto> table5ADtoList = itc04GetReqDto.getTable5ADto();
		List<Itc04Table5BDto> table5BDtoList = itc04GetReqDto.getTable5BDto();
		List<Itc04Table5CDto> table5CDtoList = itc04GetReqDto.getTable5CDto();

		String fy = GenUtil
				.getDerivedFyFromFy(itc04GetReqDto.getFp().substring(2, 6));
		String returnPeriod = getReturnPeriodForItc04(
				itc04GetReqDto.getFp().substring(0, 2));

		if (m2jwDtoList != null) {
			for (Itc04M2jwDto m2jwDto : m2jwDtoList) {
				writeM2jwToCSV(m2jwDto, sGstin, fy, returnPeriod, csvWriter);
			}
		}
		if (table5ADtoList != null) {
			for (Itc04Table5ADto table5aDto : table5ADtoList) {
				writeTable5AToCSV(table5aDto, sGstin, fy, returnPeriod,
						csvWriter);
			}
		}
		if (table5BDtoList != null) {
			for (Itc04Table5BDto table5bDto : table5BDtoList) {
				writeTable5BToCSV(table5bDto, sGstin, fy, returnPeriod,
						csvWriter);
			}
		}
		if (table5CDtoList != null) {
			for (Itc04Table5CDto table5cDto : table5CDtoList) {
				writeTable5CToCSV(table5cDto, sGstin, fy, returnPeriod,
						csvWriter);
			}
		}

	}

	private static void writeM2jwToCSV(Itc04M2jwDto m2jwDto, String sGstin,
			String fy, String returnPeriod, BufferedWriter bw)
			throws IOException {
		LOGGER.debug("Entering writeM2jwToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");

		// m2jw
		if (m2jwDto.getItc04ItemsDto() != null
				&& !m2jwDto.getItc04ItemsDto().isEmpty()) {
			int count = 0;
			for (Itc04ItemsDto itemDto : m2jwDto.getItc04ItemsDto()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(fy));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(returnPeriod));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(sGstin));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("4"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(m2jwDto.getChNum()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(m2jwDto.getChDate()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				// GenUtil.appendStringToJoiner(joiner,
				// GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(m2jwDto.getCtin()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(m2jwDto.getJwStcd()));

				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				String goodsType;
				if (itemDto.getGoodsType().equalsIgnoreCase("7b")) {
					goodsType = "CG";
				} else {
					goodsType = "IG";
				}
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(goodsType));

				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(String.valueOf(++count)));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getDesc()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getUqc()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDto.getQty())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDto.getTxval())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDto.getTxi())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDto.getTxc())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDto.getTxs())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDto.getTxcs())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				finalJoiner.add(joiner.toString());

			}
		}
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting writeM2jwToCSV");
	}

	private static void writeTable5AToCSV(Itc04Table5ADto table5aDto,
			String sGstin, String fy, String returnPeriod, BufferedWriter bw)
			throws IOException {
		LOGGER.debug("Entering writeTable5AToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");

		// table5A
		if (table5aDto.getItc04ItemsDto() != null
				&& !table5aDto.getItc04ItemsDto().isEmpty()) {
			int count = 0;
			for (Itc04ItemsDto itemDto : table5aDto.getItc04ItemsDto()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(fy));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(returnPeriod));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(sGstin));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("5A"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getOchnum()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getOchdt()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getJw2Chnum()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getJw2Chdate()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));

				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(table5aDto.getCtin()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(table5aDto.getJwStCd()));

				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				// count
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(String.valueOf(++count)));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getDesc()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getNatjw()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getUqc()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDto.getQty())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getLwuqc()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDto.getLwqty())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				finalJoiner.add(joiner.toString());

			}
		}
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting writeTable5AToCSV");
	}

	private static void writeTable5BToCSV(Itc04Table5BDto table5bDto,
			String sGstin, String fy, String returnPeriod, BufferedWriter bw)
			throws IOException {
		LOGGER.debug("Entering writeTable5BToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");

		// table5A
		if (table5bDto.getItc04ItemsDto() != null
				&& !table5bDto.getItc04ItemsDto().isEmpty()) {
			int count = 0;
			for (Itc04ItemsDto itemDto : table5bDto.getItc04ItemsDto()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(fy));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(returnPeriod));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(sGstin));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("5B"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getOchnum()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getOchdt()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getJw2Chnum()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getJw2Chdate()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));

				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(table5bDto.getCtin()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(table5bDto.getJwStCd()));

				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				// count
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(String.valueOf(++count)));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getDesc()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getNatjw()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getUqc()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDto.getQty())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getLwuqc()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDto.getLwqty())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				finalJoiner.add(joiner.toString());

			}
		}
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting writeTable5BToCSV");
	}

	private static void writeTable5CToCSV(Itc04Table5CDto table5cDto,
			String sGstin, String fy, String returnPeriod, BufferedWriter bw)
			throws IOException {
		LOGGER.debug("Entering writeTable5CToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");

		// table5A
		if (table5cDto.getItc04ItemsDto() != null
				&& !table5cDto.getItc04ItemsDto().isEmpty()) {
			int count = 0;
			for (Itc04ItemsDto itemDto : table5cDto.getItc04ItemsDto()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(fy));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(returnPeriod));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(sGstin));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("5C"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getOchnum()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getOchdt()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getInum()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getIdt()));

				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(table5cDto.getCtin()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(table5cDto.getJwStCd()));

				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));

				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(String.valueOf(++count)));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getDesc()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getNatjw()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getUqc()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDto.getQty())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDto.getLwuqc()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDto.getLwqty())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				finalJoiner.add(joiner.toString());

			}
		}
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting writeTable5CToCSV");
	}
}
