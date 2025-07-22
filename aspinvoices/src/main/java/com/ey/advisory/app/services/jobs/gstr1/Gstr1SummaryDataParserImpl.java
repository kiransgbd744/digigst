package com.ey.advisory.app.services.jobs.gstr1;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr1SummaryEntity;
import com.ey.advisory.app.data.entities.client.Gstr1SummaryDocIssuedEntity;
import com.ey.advisory.app.data.entities.client.Gstr1SummaryNilEntity;
import com.ey.advisory.app.data.entities.client.Gstr1SummaryRateEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ASummaryDocIssuedEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ASummaryNilEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ASummaryRateEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1SummaryAtGstnRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Component("gstr1SummaryDataParserImpl")
public class Gstr1SummaryDataParserImpl implements Gstr1SummaryDataParser {

	public static String DOC_ISSUE = "DOC_ISSUE";
	public static String NIL = "NIL";
	public static String SUPECOM = "SUPECOM";
	public static String SUPECOMA = "SUPECOMA";
	public static String SUPECOM_14A = "SUPECOM_14A";
	public static String SUPECOM_14B = "SUPECOM_14B";
	public static String SUPECOMA_14A = "SUPECOMA_14A";
	public static String SUPECOMA_14B = "SUPECOMA_14B";

	public static String ECOM = "ECOM";
	public static String ECOMA_UNREG = "ECOMA_UNREG";

	@Autowired
	private Gstr1SummaryAtGstnRepository repository;

	private static final List<String> EXCLUDED_SUBSECS = ImmutableList.of(
			"B2BA_4A", "B2BA_4B", "B2BA_6C", "B2BA_SEZWOP", "B2BA_SEZWP",
			"B2B_4A", "B2B_4B", "B2B_6C", "B2B_SEZWOP", "B2B_SEZWP", SUPECOM,
			SUPECOMA);

	private static final List<String> AMENDED_SECS = ImmutableList.of("B2BA",
			"EXPA", "CDNRA", "CDNURA", "TXPDA", "ATA", "B2CSA", "B2CLA");

	private static final List<String> TBL15AMENDEDONE_SECS = ImmutableList
			.of("ECOMA_REG", "ECOMA_DE", "ECOMA_SEZWOP", "ECOMA_SEZWP");

	@Override
	public List<GetGstr1SummaryEntity> parseSummaryData(String apiResp,
			Long batchId) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<GetGstr1SummaryEntity> childEntities = new ArrayList<GetGstr1SummaryEntity>();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 Summary GSTN GET call Json Parsing started.");
		}

		JsonObject respObject = JsonParser.parseString(apiResp)
				.getAsJsonObject();
		GetGstr1SummaryEntity baseEntity = gson.fromJson(respObject,
				GetGstr1SummaryEntity.class);
		GetGstr1SummaryEntity childEntity = null;

		if (isNilResponse(respObject)) {
			String errMsg = String.format(
					"GSTR1 Summary is not available for {} and {} "
							+ "because it is Nil Filed API Resp is {}",
					baseEntity.getGstin(), baseEntity.getRetPeriod(), apiResp);
			LOGGER.error(errMsg);
			return new ArrayList<>();
		}

		JsonArray array = respObject.get("sec_sum").getAsJsonArray();
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		// manipulating the original json and adding new section as 15A(i)
		array.add(createSumJsonObject(array));

		for (JsonElement arr : array) {
			String secName = arr.getAsJsonObject().get("sec_nm").getAsString();
			if (EXCLUDED_SUBSECS.contains(secName)) {
				LOGGER.debug(String.format(
						"Skipping the SubSection %s since they are not required ",
						secName));
				continue;
			}

			if (DOC_ISSUE.equals(secName)) {
				childEntity = gson.fromJson(arr.getAsJsonObject(),
						Gstr1SummaryDocIssuedEntity.class);
			} else if (NIL.equals(secName)) {
				childEntity = gson.fromJson(arr.getAsJsonObject(),
						Gstr1SummaryNilEntity.class);
			} else if (AMENDED_SECS.contains(secName)) {
				LOGGER.debug(String.format(
						"These are Amendments Section %s we are converting the actual "
								+ "to ttl  reusing the ttl columns ",
						secName));
				childEntity = gson.fromJson(arr.getAsJsonObject(),
						Gstr1SummaryRateEntity.class);
				convertActToTtl((Gstr1SummaryRateEntity) childEntity);
			} else {
				if (secName.equals(ECOM)) {
					arr.getAsJsonObject().addProperty("sec_nm",
							GSTConstants.GSTR1_15);
				} else if (secName.equals(ECOMA_UNREG)) {
					arr.getAsJsonObject().addProperty("sec_nm",
							GSTConstants.GSTR1_15AII);
				}
				childEntity = gson.fromJson(arr.getAsJsonObject(),
						Gstr1SummaryRateEntity.class);
			}
			childEntity.setChkSum(baseEntity.getChkSum());
			childEntity.setGstin(baseEntity.getGstin());
			childEntity.setRetPeriod(baseEntity.getRetPeriod());
			childEntity.setDervRetPeriod(
					GenUtil.convertTaxPeriodToInt(baseEntity.getRetPeriod()));
			childEntity.setBatchId(batchId);
			childEntity.setCreatedOn(now);
			childEntity.setCreatedBy(APIConstants.SYSTEM);
			childEntities.add(childEntity);
		}

		for (JsonElement arr : array) {
			if (childEntity == null) {
				childEntity = new GetGstr1SummaryEntity();
			}
			String secName = arr.getAsJsonObject().get("sec_nm").getAsString();

			if (secName.equals(SUPECOM)) {
				JsonArray subSectionArray = arr.getAsJsonObject()
						.get("sub_sections").getAsJsonArray();
				for (JsonElement jsonElement : subSectionArray) {
					if (jsonElement.getAsJsonObject().get("sec_nm")
							.getAsString().equalsIgnoreCase(SUPECOM_14A)) {
						jsonElement.getAsJsonObject().addProperty("sec_nm",
								GSTConstants.GSTR1_14I);
					} else {
						jsonElement.getAsJsonObject().addProperty("sec_nm",
								GSTConstants.GSTR1_14II);
					}
					childEntity = gson.fromJson(jsonElement.getAsJsonObject(),
							Gstr1SummaryRateEntity.class);
					childEntity.setChkSum(baseEntity.getChkSum());
					childEntity.setGstin(baseEntity.getGstin());
					childEntity.setRetPeriod(baseEntity.getRetPeriod());
					childEntity.setDervRetPeriod(GenUtil
							.convertTaxPeriodToInt(baseEntity.getRetPeriod()));
					childEntity.setBatchId(batchId);
					childEntity.setCreatedOn(now);
					childEntity.setCreatedBy(APIConstants.SYSTEM);
					childEntities.add(childEntity);
				}
			}
			if (secName.equals(SUPECOMA)) {
				JsonArray subSectionArray = arr.getAsJsonObject()
						.get("sub_sections").getAsJsonArray();
				for (JsonElement jsonElement : subSectionArray) {
					if (jsonElement.getAsJsonObject().get("sec_nm")
							.getAsString().equalsIgnoreCase(SUPECOMA_14A)) {
						jsonElement.getAsJsonObject().addProperty("sec_nm",
								GSTConstants.GSTR1_14AI);
					} else {
						jsonElement.getAsJsonObject().addProperty("sec_nm",
								GSTConstants.GSTR1_14AII);
					}
					childEntity = gson.fromJson(jsonElement.getAsJsonObject(),
							Gstr1SummaryRateEntity.class);
					childEntity.setChkSum(baseEntity.getChkSum());
					childEntity.setGstin(baseEntity.getGstin());
					childEntity.setRetPeriod(baseEntity.getRetPeriod());
					childEntity.setDervRetPeriod(GenUtil
							.convertTaxPeriodToInt(baseEntity.getRetPeriod()));
					childEntity.setBatchId(batchId);
					childEntity.setCreatedOn(now);
					childEntity.setCreatedBy(APIConstants.SYSTEM);
					childEntities.add(childEntity);
				}
			}
		}

		return childEntities;
	}

	@Override
	public void saveSummaryData(List<GetGstr1SummaryEntity> entities,
			String groupCode, String sGstin, String taxPeriod) {

		if (!entities.isEmpty()) {
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			TenantContext.setTenantId(groupCode);
			repository.softlyDeleteNilSumry(sGstin, taxPeriod, now);
			repository.softlyDeleteDocIssuedSumry(sGstin, taxPeriod, now);
			repository.softlyDeleteRateSumry(sGstin, taxPeriod, now);
			repository.saveAll(entities);
		}
	}

	private static void convertActToTtl(Gstr1SummaryRateEntity rtEntity) {
		LOGGER.debug("Converting the ACT to TTL");
		rtEntity.setTtlIgst(rtEntity.getActIgst());
		rtEntity.setTtlCgst(rtEntity.getActCgst());
		rtEntity.setTtlSgst(rtEntity.getActSgst());
		rtEntity.setTtlCess(rtEntity.getActCess());
		rtEntity.setTtlTax(rtEntity.getActTax());
		rtEntity.setTtlVal(rtEntity.getActVal());
		rtEntity.setTtlRec(rtEntity.getTtlRec());
	}

	private static void convertActToTtlForGSTR1A(
			Gstr1ASummaryRateEntity rtEntity) {
		LOGGER.debug("Converting the ACT to TTL");
		rtEntity.setTtlIgst(rtEntity.getActIgst());
		rtEntity.setTtlCgst(rtEntity.getActCgst());
		rtEntity.setTtlSgst(rtEntity.getActSgst());
		rtEntity.setTtlCess(rtEntity.getActCess());
		rtEntity.setTtlTax(rtEntity.getActTax());
		rtEntity.setTtlVal(rtEntity.getActVal());
		rtEntity.setTtlRec(rtEntity.getTtlRec());
	}

	private boolean isNilResponse(JsonObject respObject) {
		return respObject.has(APIIdentifiers.ISNIL)
				&& APIConstants.Y.equalsIgnoreCase(
						respObject.get(APIIdentifiers.ISNIL).getAsString());
	}

	private JsonObject createSumJsonObject(JsonArray array) {

		BigDecimal sumTtlTax = BigDecimal.ZERO;
		BigDecimal sumTtlIgst = BigDecimal.ZERO;
		BigDecimal sumTtlCgst = BigDecimal.ZERO;
		BigDecimal sumTtlSgst = BigDecimal.ZERO;
		BigDecimal sumTtlCess = BigDecimal.ZERO;
		BigDecimal sumTtlRec = BigDecimal.ZERO;
		BigDecimal sumTtlVal = BigDecimal.ZERO;

		BigDecimal sumActTax = BigDecimal.ZERO;
		BigDecimal sumActIgst = BigDecimal.ZERO;
		BigDecimal sumActCgst = BigDecimal.ZERO;
		BigDecimal sumActSgst = BigDecimal.ZERO;
		BigDecimal sumActCess = BigDecimal.ZERO;
		BigDecimal sumActVal = BigDecimal.ZERO;

		String chkSum = null;
		for (JsonElement arr : array) {
			String secName = arr.getAsJsonObject().get("sec_nm").getAsString();
			if (TBL15AMENDEDONE_SECS.contains(secName)) {
				JsonObject secObject = arr.getAsJsonObject();
				chkSum = arr.getAsJsonObject().get("chksum").getAsString();
				sumTtlTax = sumTtlTax.add(secObject.getAsJsonObject()
						.get("ttl_tax").getAsBigDecimal());
				sumTtlIgst = sumTtlIgst.add(secObject.getAsJsonObject()
						.get("ttl_igst").getAsBigDecimal());
				sumTtlCgst = sumTtlCgst.add(secObject.getAsJsonObject()
						.get("ttl_cgst").getAsBigDecimal());
				sumTtlSgst = sumTtlSgst.add(secObject.getAsJsonObject()
						.get("ttl_sgst").getAsBigDecimal());
				sumTtlCess = sumTtlCess.add(secObject.getAsJsonObject()
						.get("ttl_cess").getAsBigDecimal());
				sumTtlRec = sumTtlRec.add(secObject.getAsJsonObject()
						.get("ttl_rec").getAsBigDecimal());
				sumTtlVal = sumTtlVal.add(secObject.getAsJsonObject()
						.get("ttl_val").getAsBigDecimal());
				sumActTax = sumActTax
						.add(secObject.get("act_tax").getAsBigDecimal());
				sumActIgst = sumActIgst
						.add(secObject.get("act_igst").getAsBigDecimal());
				sumActCgst = sumActCgst
						.add(secObject.get("act_cgst").getAsBigDecimal());
				sumActSgst = sumActSgst
						.add(secObject.get("act_sgst").getAsBigDecimal());
				sumActCess = sumActCess
						.add(secObject.get("act_cess").getAsBigDecimal());
				sumActVal = sumActVal
						.add(secObject.get("act_val").getAsBigDecimal());
			}
		}

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("sec_nm", GSTConstants.GSTR1_15AI);
		jsonObject.addProperty("chksum", chkSum);
		jsonObject.addProperty("ttl_rec", sumTtlRec);
		jsonObject.addProperty("ttl_tax", sumTtlTax);
		jsonObject.addProperty("ttl_igst", sumTtlIgst);
		jsonObject.addProperty("ttl_sgst", sumTtlSgst);
		jsonObject.addProperty("ttl_cgst", sumTtlCgst);
		jsonObject.addProperty("ttl_val", sumTtlVal);
		jsonObject.addProperty("ttl_cess", sumTtlCess);
		jsonObject.addProperty("act_tax", sumActTax);
		jsonObject.addProperty("act_igst", sumActIgst);
		jsonObject.addProperty("act_sgst", sumActSgst);
		jsonObject.addProperty("act_cgst", sumActCgst);
		jsonObject.addProperty("act_val", sumActVal);
		jsonObject.addProperty("act_cess", sumActCess);
		return jsonObject;
	}

	@Override
	public List<GetGstr1SummaryEntity> parseGstr1ASummaryData(String apiResp,
			Long batchId) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<GetGstr1SummaryEntity> childEntities = new ArrayList<GetGstr1SummaryEntity>();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 Summary GSTN GET call Json Parsing started.");
		}

		JsonObject respObject = JsonParser.parseString(apiResp)
				.getAsJsonObject();
		GetGstr1SummaryEntity baseEntity = gson.fromJson(respObject,
				GetGstr1SummaryEntity.class);
		GetGstr1SummaryEntity childEntity = null;

		if (isNilResponse(respObject)) {
			String errMsg = String.format(
					"GSTR1 Summary is not available for {} and {} "
							+ "because it is Nil Filed API Resp is {}",
					baseEntity.getGstin(), baseEntity.getRetPeriod(), apiResp);
			LOGGER.error(errMsg);
			return new ArrayList<>();
		}

		JsonArray array = respObject.get("sec_sum").getAsJsonArray();
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		// manipulating the original json and adding new section as 15A(i)
		array.add(createSumJsonObject(array));

		for (JsonElement arr : array) {
			String secName = arr.getAsJsonObject().get("sec_nm").getAsString();
			if (EXCLUDED_SUBSECS.contains(secName)) {
				LOGGER.debug(String.format(
						"Skipping the SubSection %s since they are not required ",
						secName));
				continue;
			}

			if (DOC_ISSUE.equals(secName)) {
				childEntity = gson.fromJson(arr.getAsJsonObject(),
						Gstr1ASummaryDocIssuedEntity.class);
			} else if (NIL.equals(secName)) {
				childEntity = gson.fromJson(arr.getAsJsonObject(),
						Gstr1ASummaryNilEntity.class);
			} else if (AMENDED_SECS.contains(secName)) {
				LOGGER.debug(String.format(
						"These are Amendments Section %s we are converting the actual "
								+ "to ttl  reusing the ttl columns ",
						secName));
				childEntity = gson.fromJson(arr.getAsJsonObject(),
						Gstr1ASummaryRateEntity.class);
				convertActToTtlForGSTR1A((Gstr1ASummaryRateEntity) childEntity);
			} else {
				if (secName.equals(ECOM)) {
					arr.getAsJsonObject().addProperty("sec_nm",
							GSTConstants.GSTR1_15);
				} else if (secName.equals(ECOMA_UNREG)) {
					arr.getAsJsonObject().addProperty("sec_nm",
							GSTConstants.GSTR1_15AII);
				}
				childEntity = gson.fromJson(arr.getAsJsonObject(),
						Gstr1ASummaryRateEntity.class);
			}
			childEntity.setChkSum(baseEntity.getChkSum());
			childEntity.setGstin(baseEntity.getGstin());
			childEntity.setRetPeriod(baseEntity.getRetPeriod());
			childEntity.setDervRetPeriod(
					GenUtil.convertTaxPeriodToInt(baseEntity.getRetPeriod()));
			childEntity.setBatchId(batchId);
			childEntity.setCreatedOn(now);
			childEntity.setCreatedBy(APIConstants.SYSTEM);

			/*Gstr1ASummaryRateEntity specificChildEntity = null;
			if (childEntity instanceof Gstr1ASummaryRateEntity) {
				specificChildEntity = (Gstr1ASummaryRateEntity) childEntity;

				if (specificChildEntity.getSecName()
						.equalsIgnoreCase("HSN_B2B")) {
					specificChildEntity.setSecName("HSN");
					specificChildEntity.setRecordType("HSN_B2B");
				} else if (specificChildEntity.getSecName()
						.equalsIgnoreCase("HSN_B2C")) {
					specificChildEntity.setSecName("HSN");
					specificChildEntity.setRecordType("HSN_B2C");
				}

			}
			childEntities.add(specificChildEntity);
			*/
			childEntities.add(childEntity);
		}

		for (JsonElement arr : array) {
			if (childEntity == null) {
				childEntity = new GetGstr1SummaryEntity();
			}
			String secName = arr.getAsJsonObject().get("sec_nm").getAsString();

			if (secName.equals(SUPECOM)) {
				JsonArray subSectionArray = arr.getAsJsonObject()
						.get("sub_sections").getAsJsonArray();
				for (JsonElement jsonElement : subSectionArray) {
					if (jsonElement.getAsJsonObject().get("sec_nm")
							.getAsString().equalsIgnoreCase(SUPECOM_14B)) {
						jsonElement.getAsJsonObject().addProperty("sec_nm",
								GSTConstants.GSTR1_14I);
					} else {
						jsonElement.getAsJsonObject().addProperty("sec_nm",
								GSTConstants.GSTR1_14II);
					}
					childEntity = gson.fromJson(jsonElement.getAsJsonObject(),
							Gstr1ASummaryRateEntity.class);
					childEntity.setChkSum(baseEntity.getChkSum());
					childEntity.setGstin(baseEntity.getGstin());
					childEntity.setRetPeriod(baseEntity.getRetPeriod());
					childEntity.setDervRetPeriod(GenUtil
							.convertTaxPeriodToInt(baseEntity.getRetPeriod()));
					childEntity.setBatchId(batchId);
					childEntity.setCreatedOn(now);
					childEntity.setCreatedBy(APIConstants.SYSTEM);
					childEntities.add(childEntity);
				}
			}
			if (secName.equals(SUPECOMA)) {
				JsonArray subSectionArray = arr.getAsJsonObject()
						.get("sub_sections").getAsJsonArray();
				for (JsonElement jsonElement : subSectionArray) {
					if (jsonElement.getAsJsonObject().get("sec_nm")
							.getAsString().equalsIgnoreCase(SUPECOMA_14A)) {
						jsonElement.getAsJsonObject().addProperty("sec_nm",
								GSTConstants.GSTR1_14AI);
					} else {
						jsonElement.getAsJsonObject().addProperty("sec_nm",
								GSTConstants.GSTR1_14AII);
					}
					childEntity = gson.fromJson(jsonElement.getAsJsonObject(),
							Gstr1ASummaryRateEntity.class);
					childEntity.setChkSum(baseEntity.getChkSum());
					childEntity.setGstin(baseEntity.getGstin());
					childEntity.setRetPeriod(baseEntity.getRetPeriod());
					childEntity.setDervRetPeriod(GenUtil
							.convertTaxPeriodToInt(baseEntity.getRetPeriod()));
					childEntity.setBatchId(batchId);
					childEntity.setCreatedOn(now);
					childEntity.setCreatedBy(APIConstants.SYSTEM);
					childEntities.add(childEntity);
				}
			}
		}

		return childEntities;
	}

	@Override
	public void saveGstr1ASummaryData(List<GetGstr1SummaryEntity> entities,
			String groupCode, String sGstin, String taxPeriod) {

		if (!entities.isEmpty()) {
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			TenantContext.setTenantId(groupCode);
			repository.softlyDeleteGstr1ANilSumry(sGstin, taxPeriod, now);
			repository.softlyDeleteGstr1ADocIssuedSumry(sGstin, taxPeriod, now);
			repository.softlyDeleteGstr1ARateSumry(sGstin, taxPeriod, now);
			repository.saveAll(entities);
		}
	}

}
