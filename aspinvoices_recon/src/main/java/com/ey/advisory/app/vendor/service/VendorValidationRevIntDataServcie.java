/**
 * 
 */
package com.ey.advisory.app.vendor.service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.VendorGstinFilingTypeEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GetGstinMasterDetailApiPushEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.VendorGstinFilingTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GstinValidatorApiPushRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorValidatorPayloadRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Service("VendorValidationRevIntDataServcie")
public class VendorValidationRevIntDataServcie {

	@Autowired
	@Qualifier("GstinValidatorApiPushRepository")
	private GstinValidatorApiPushRepository gstinValidatorRepo;

	@Autowired
	@Qualifier("VendorValidatorPayloadRepository")
	private VendorValidatorPayloadRepository payloadRepo;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnStatusRepo;

	@Autowired
	@Qualifier("VendorGstinFilingTypeRepository")
	private VendorGstinFilingTypeRepository vendorGstinFilingTypeRepo;

	public static final List<String> returnTypeList = Arrays.asList("GSTR3B",
			"GSTR1", "GSTR6", "GSTR7", "GSTR5", "GSTR8");

	// Static map
	private static final Map<String, String> fillingTypeMap;

	private static final Map<String, String> returnTypeMap;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("VendorValidationFilerDueDate")
	private VendorValidationFilerDueDate filerDueDateService;
	

	static {
		fillingTypeMap = new HashMap<>();
		fillingTypeMap.put("Tax Deductor", "TDS");
		fillingTypeMap.put("Tax Collector", "TCS");
		fillingTypeMap.put("Input Service Distributor", "ISD");
		fillingTypeMap.put("SEZ Developer", "SEZD");
		fillingTypeMap.put("SEZ Unit", "SEZU");
		fillingTypeMap.put("Non Resident", "NR");
		fillingTypeMap.put("Regular", "Regular");
		fillingTypeMap.put("Input Service Distributor (ISD)", "ISD");
		fillingTypeMap.put("Tax Collector (Electronic Commerce Operator)", "TCS");
	}
	
	static {
		returnTypeMap = new HashMap<>();
		returnTypeMap.put("TDS", "GSTR7");
		returnTypeMap.put("TCS", "");
		returnTypeMap.put("ISD","GSTR6");
		returnTypeMap.put("NR", "GSTR5");
		returnTypeMap.put("Regular", "GSTR1");
		
	}

	List<String> taxPayerType = Arrays.asList("Regular", "SEZ Developer",
			"SEZ Unit");

	List<String> allTaxPayerTypeList = Arrays.asList("Regular", "SEZ Developer",
			"SEZ Unit", "Tax Deductor", "Tax Collector",
			"Input Service Distributor", "Non Resident", 
			"Input Service Distributor (ISD)");

	public VendorValidationMetaDataDto xmlPayloadData(String payloadId) {

		Gson gson = new Gson();
		List<VendorDto> vendorList = new ArrayList<>();

		VendorValidationMetaDataDto metaDto = new VendorValidationMetaDataDto();

		VendorValidationBusinessMsgDto msgDto = new VendorValidationBusinessMsgDto();

		VendorValidatorResponseListDto respDto = new VendorValidatorResponseListDto();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("VendorValidationRevIntDataServcie"
					+ ".xmlPayloadData Begin");
		}

		VendorValidatorPayloadEntity payloadEntity = payloadRepo
				.getGstinValidatorPayload(payloadId);

		String companyCode = payloadEntity.getCompanyCode();
		Integer errorCount = payloadEntity.getErrorCount();
		String status = payloadEntity.getStatus();
		Integer totalCount = payloadEntity.getTotalCount();
		String errorMsg = payloadEntity.getJsonErrorResponse();
		
		if(!"E".equalsIgnoreCase(status)) {

		try {

			getData(payloadId, gson, vendorList, payloadEntity);

		} catch (Exception ex) {
			LOGGER.error(
					" ERROR occured in  rev integration meta data formation : {} ",
					ex);
			throw new AppException(ex);
		}
		}

		respDto.addAllItem(vendorList);

		msgDto.setStatus(status);
		msgDto.setCompanyCode(companyCode);
		msgDto.setErrorCount(errorCount);
		msgDto.setMessageInfo(errorMsg);
		if(status.equalsIgnoreCase("E") && errorMsg == null) {
			msgDto.setMessageInfo("Internal Technical Error");
			
		}
		msgDto.setModifiedOn(LocalDateTime.now());
		msgDto.setPayloadId(payloadId);
		msgDto.setProcessCount(totalCount - errorCount);
		
		msgDto.setTotalCount(totalCount);

		metaDto.setMsgDto(msgDto);
		metaDto.setResponseDto(respDto);

		return metaDto;
	}

	/**
	 * @param payloadId
	 * @param gson
	 * @param vendorList
	 * @param payloadEntity
	 */
	private void getData(String payloadId, Gson gson,
			List<VendorDto> vendorList,
			VendorValidatorPayloadEntity payloadEntity) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("VendorValidationRevIntDataServcie"
					+ ".xmlPayloadData payloadId = {} ", payloadId);
		}

		String jsonInput = GenUtil
				.convertClobtoString(payloadEntity.getReqPlayload());

		JsonObject requestObj = (new JsonParser()).parse(jsonInput)
				.getAsJsonObject();

		JsonArray json1 = requestObj.get("req").getAsJsonArray();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request Json {}", json1);
		}

		Type listType = new TypeToken<List<VendorReqDto>>() {
		}.getType();

		List<VendorReqDto> reqDto = gson.fromJson(json1, listType);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("VendorReqDto List {} ", reqDto);
		}

		Map<String, List<String>> resultMap = reqDto.stream()
				.collect(Collectors.groupingBy(VendorReqDto::getFy,
						Collectors.mapping(VendorReqDto::getGstin,
								Collectors.toList())));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Map<String, List<String>> resultMap  {} ",
					resultMap);
		}

		List<GetGstinMasterDetailApiPushEntity> gstinValiadationResultList = gstinValidatorRepo
				.getPayloadGstinDetailsForvendorAPI(payloadId);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("gstinValiadationResultList  {} ",
					gstinValiadationResultList);
		}

		Map<String, List<GetGstinMasterDetailApiPushEntity>> gstinValiadationMap = gstinValiadationResultList
				.stream()
				.collect(Collectors.groupingBy(
						GetGstinMasterDetailApiPushEntity::getGstin,
						Collectors.toList()));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Map<String, "
							+ "List<GetGstinMasterDetailApiPushEntity>> "
							+ "gstinValiadationMap =  {} ",
					gstinValiadationMap);
		}

		resultMap.entrySet().stream().forEach(entry -> {
			String fy = entry.getKey();
			List<String> gstins = entry.getValue();

			Map<String, List<VendorGstinFilingTypeEntity>> gstinFrequencyMap = null;

			Map<String, List<VendorProcRespDto>> complainceDataMap = getComplainceDataMap(
					gstins, fy, gstinValiadationMap);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Invoking VendorValidationRevIntDataServcie for "
								+ "gstins {} and financialYear{}",
						gstins, fy);
			}

			List<String> gstinList = gstinValidatorRepo
					.getGstinsByTaxPayerTypeInAndGstinAndPayloadId(
							taxPayerType, gstins, payloadId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Invoked TaxpayerType filter gstinList : {}",
						gstinList);
			}
			if (!gstinList.isEmpty()) {

				List<VendorGstinFilingTypeEntity> fillingFrequencyList = vendorGstinFilingTypeRepo
						.findByGstinInAndFyOrderByQuarterAsc(gstinList, fy);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("List<VendorGstinFilingTypeEntity>"
							+ " fillingFrequencyList =  {}, gstinList {}, fy "
							+ " {} ", fillingFrequencyList, gstinList, fy);
				}

				gstinFrequencyMap = fillingFrequencyList.stream()
						.collect(Collectors.groupingBy(o -> o.getGstin()));

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Map<String, "
							+ "List<VendorGstinFilingTypeEntity>> "
							+ "gstinFrequencyMap = {}, gstinList {}, fy {} ",
							gstinFrequencyMap, gstinList, fy);
				}

			} else {
				LOGGER.debug(
						"GstinList is Empty hense returning from frequency api");
			}

			List<GstrReturnStatusEntity> fillingDetails = returnStatusRepo
					.findByGstinInAndReturnTypeInAndTaxPeriodIn(gstins,
							returnTypeList, getAllMonths(fy));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"List<GstrReturnStatusEntity> fillingDetails "
								+ " = {}, gstins {}, fy {} ",
						fillingDetails, gstins, fy);
			}

			Map<String, List<GstrReturnStatusEntity>> gstinReturnTypeMap = fillingDetails
					.stream().collect(Collectors.groupingBy(
							o -> o.getGstin() + "_" + o.getReturnType()));// G1_GSTR1,
																			// G1_GSTR3B

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Map<String, List<GstrReturnStatusEntity>> "
						+ "gstinReturnTypeMap = {}, gstins {}, fy {} ",
						gstinReturnTypeMap, gstinList, fy);
			}

			for (String gstin : gstins) {

				List<GstrReturnStatusEntity> DetailsList3B = null;
				List<GstrReturnStatusEntity> DetailsListGstr1 = null;
				List<GstrReturnStatusEntity> returnTypeDetailList = null;
				List<VendorGstinFilingTypeEntity> frquencyList = null;

				GetGstinMasterDetailApiPushEntity gstinValidationObj = gstinValiadationMap
						.get(gstin).get(0);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"GetGstinMasterDetailApiPushEntity "
									+ "gstinValidationObj = {}, gstin {} ",
							gstinValidationObj, gstin);
				}

				if (gstinValidationObj.getTaxpayerType() == null
						|| !allTaxPayerTypeList.contains(
								gstinValidationObj.getTaxpayerType())) {

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"GetGstinMasterDetailApiPushEntity for "
										+ "gstinValidationObj = {}, "
										+ "gstin {} taxpayerType {} is not "
										+ "eligible Hence createding xml "
										+ "based on gstin validator "
										+ "api only ",
								gstinValidationObj, gstin,
								gstinValidationObj.getTaxpayerType());
					}
					VendorDto gstinValidationResponeXml = createXmlDataForGstinValidationRespone(
							vendorList, fy, gstin, gstinValidationObj);
					vendorList.add(gstinValidationResponeXml);

				} else if (gstinFrequencyMap != null) {

					frquencyList = gstinFrequencyMap.get(gstin);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"List<VendorGstinFilingTypeEntity> "
										+ "frquencyList = {}, gstin {} ",
								frquencyList, gstin);
					}
					DetailsList3B = gstinReturnTypeMap
							.get(gstin + "_GSTR3B");

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"List<GstrReturnStatusEntity> "
										+ "DetailsList3B = {}, gstin {} ",
								DetailsList3B, gstin);
					}
					DetailsListGstr1 = gstinReturnTypeMap
							.get(gstin + "_GSTR1");

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("List<GstrReturnStatusEntity> "
								+ "DetailsListGstr1 = {}, gstin {} ",
								DetailsListGstr1, gstin);
					}

					createXmlDataForR1And3B(vendorList, fy, gstin,
							frquencyList, DetailsList3B, DetailsListGstr1,
							gstinValidationObj, complainceDataMap);

				} else {
					String returnType = "";
					if (gstinValidationObj.getTaxpayerType() != null) {
						if (gstinValidationObj.getTaxpayerType()
								.equalsIgnoreCase("Tax Deductor")) {

							returnType = "GSTR7";

						} else if (gstinValidationObj.getTaxpayerType()
								.equalsIgnoreCase("Tax Collector")) {

							returnType = "GSTR8";

						}else if (gstinValidationObj.getTaxpayerType()
								.equalsIgnoreCase("Tax Collector (Electronic Commerce Operator)")) {

							returnType = "GSTR8";

						}
						if (gstinValidationObj.getTaxpayerType()
								.equalsIgnoreCase(
										"Input Service Distributor (ISD)")) {

							returnType = "GSTR6";

						}
						if (gstinValidationObj.getTaxpayerType()
								.equalsIgnoreCase("Non Resident")) {

							returnType = "GSTR5";

						}

						returnTypeDetailList = gstinReturnTypeMap
								.get(gstin + "_" + returnType);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("List<GstrReturnStatusEntity> "
									+ "returnTypeDetailList = {}, gstin {},"
									+ " returnType {} ",
									returnTypeDetailList, gstin,
									returnType);
						}

						VendorDto otherReturnTypesResponeXml = createXmlDataForOtherReturnTypesRespone(
								vendorList, fy, gstin, returnTypeDetailList,
								gstinValidationObj, returnType,
								complainceDataMap);

						vendorList.add(otherReturnTypesResponeXml);

					}
				}
			}

		});
	}

	private VendorDto createXmlDataForOtherReturnTypesRespone(
			List<VendorDto> vendorList, String fy, String gstin,
			List<GstrReturnStatusEntity> returnTypeDetailList,
			GetGstinMasterDetailApiPushEntity gstinValidationObj,
			String returnType,
			Map<String, List<VendorProcRespDto>> complainceDataMap) {

		VendorDto dto = new VendorDto();

		setGstinValidationData(fy, gstin, gstinValidationObj, dto);
		dto.setReturnType(returnType);

		dto.setFilingType("MONTHLY");

		Pair<Integer, Integer> setMonths = setMonths(returnTypeDetailList, dto,
				"Monthly",fy,returnType);

		Integer filedOnTime = setMonths.getValue1() != null
				? setMonths.getValue1()
				: 0;
		Integer totalFiled = setMonths.getValue0() != null
				? setMonths.getValue0()
				: 0;
		Integer lateFiled = totalFiled - filedOnTime;

		dto.setReturnFiledInTime(filedOnTime.toString());
		dto.setLateReturnFiled(lateFiled.toString());

		setOtherScoreRelatedData(gstin, returnType, complainceDataMap, dto);

		return dto;

	}

	private VendorDto createXmlDataForGstinValidationRespone(
			List<VendorDto> vendorList, String fy, String gstin,
			GetGstinMasterDetailApiPushEntity gstinValidationObj) {

		VendorDto dto = new VendorDto();

		setGstinValidationData(fy, gstin, gstinValidationObj, dto);

		return dto;

	}

	/**
	 * @param fy
	 * @param gstin
	 * @param gstinValidationObj
	 * @param dto
	 */
	private void setGstinValidationData(String fy, String gstin,
			GetGstinMasterDetailApiPushEntity gstinValidationObj,
			VendorDto dto) {
		dto.setBuildingName(gstinValidationObj.getBuildingName());
		dto.setCentreJurisdiction(gstinValidationObj.getCentreJurisdiction());
		dto.setConstitutionofBusiness(
				gstinValidationObj.getBusinessConstitution());

		dto.setDateofRegistration(
				gstinValidationObj.getRegistrationDate() != null
						? convertDateFormat(gstinValidationObj
								.getRegistrationDate().toString())
						: null);

		dto.setDateOfCancellation(
				gstinValidationObj.getCancellationDate() != null
						? convertDateFormat(gstinValidationObj
								.getCancellationDate().toString())
						: null);

		dto.setDoorNumber(gstinValidationObj.getDoorNum());
		dto.setEInvoiceAppliclibity(gstinValidationObj.getEinvApplicable());
		dto.setErrorMessage(gstinValidationObj.getErrorDiscription());

		dto.setFinancialYear(fy);
		dto.setFloorNumber(gstinValidationObj.getFloorNum());
		dto.setGstnStatus(gstinValidationObj.getGstinStatus());

		dto.setLastUpdatedAt(LocalDateTime.now());
		dto.setLocation(gstinValidationObj.getLocation());
		dto.setNatureofBusinessActivity(
				gstinValidationObj.getBusinessNatureActivity());
		dto.setPayloadId(gstinValidationObj.getPayloadId());
		dto.setPincode(gstinValidationObj.getPin());

		dto.setReturnType("NA");
		dto.setStateJurisdiction(gstinValidationObj.getStateJurisdiction());
		dto.setStateName(gstinValidationObj.getState());
		dto.setStreet(gstinValidationObj.getStreet());

		dto.setTaxpayerType(gstinValidationObj.getTaxpayerType());
		dto.setTradeName(gstinValidationObj.getTradeName());
		dto.setVendorGSTIN(gstin);
		dto.setVendorLegalName(gstinValidationObj.getLegalName());
		dto.setErrorMessage(gstinValidationObj.getErrorDiscription());
		dto.setPeriodForITCMatchingPoints(
				getLast12Months().toString());
	}

	/**
	 * @param vendorList
	 * @param fy
	 * @param gstin
	 * @param frquencyList
	 * @param DetailsList3B
	 * @param DetailsListGstr1
	 * @param gstinValidationObj
	 */
	private void createXmlDataForR1And3B(List<VendorDto> vendorList, String fy,
			String gstin, List<VendorGstinFilingTypeEntity> frquencyList,
			List<GstrReturnStatusEntity> DetailsList3B,
			List<GstrReturnStatusEntity> DetailsListGstr1,
			GetGstinMasterDetailApiPushEntity gstinValidationObj,
			Map<String, List<VendorProcRespDto>> complainceDataMap) {

		VendorDto xml3BDto = setXmlValues(gstin, fy, frquencyList,
				DetailsList3B, DetailsListGstr1, gstinValidationObj, "GSTR3B",
				complainceDataMap);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("xml3BDto = {}, gstin {}, fy {} ", xml3BDto, gstin,
					fy);
		}

		VendorDto xmlGstr1Dto = setXmlValues(gstin, fy, frquencyList,
				DetailsList3B, DetailsListGstr1, gstinValidationObj, "GSTR1",
				complainceDataMap);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("xmlGstr1Dto = {}, gstin {}, fy {} ", xmlGstr1Dto,
					gstin, fy);
		}

		vendorList.add(xml3BDto);
		vendorList.add(xmlGstr1Dto);
	}

	private VendorDto setXmlValues(String gstin, String fy,
			List<VendorGstinFilingTypeEntity> frquencyList,
			List<GstrReturnStatusEntity> detailsList3B,
			List<GstrReturnStatusEntity> detailsListGstr1,
			GetGstinMasterDetailApiPushEntity gstinValidationObj,
			String returnType,
			Map<String, List<VendorProcRespDto>> complainceDataMap) {

		VendorDto dto = new VendorDto();

		setGstinValidationData(fy, gstin, gstinValidationObj, dto);

		dto.setReturnType(returnType);

		dto.setFilingType(
				frquencyList != null ? frquencyList.get(0).getFilingType()
						: "MONTHLY");

		setOtherScoreRelatedData(gstin, returnType, complainceDataMap, dto);
		
		Pair<Integer, Integer> setMonths = null;

		if (returnType.equalsIgnoreCase("GSTR3B")) {
			setMonths = setMonths(detailsList3B, dto, dto.getFilingType(),fy,returnType);

		} else if (returnType.equalsIgnoreCase("GSTR1")) {
			setMonths = setMonths(detailsListGstr1, dto, dto.getFilingType(),fy,returnType);
		}

		Integer filedOnTime = setMonths.getValue1() != null ? setMonths.getValue1() :0;
		Integer totalFiled = setMonths.getValue0()!= null ? setMonths.getValue0() :0;
		Integer lateFiled = totalFiled- filedOnTime;
		
		dto.setReturnFiledInTime(filedOnTime.toString());
		dto.setLateReturnFiled(lateFiled.toString());
		return dto;

	}

	/**
	 * @param gstin
	 * @param returnType
	 * @param complainceDataMap
	 * @param dto
	 */
	private void setOtherScoreRelatedData(String gstin, String returnType,
			Map<String, List<VendorProcRespDto>> complainceDataMap,
			VendorDto dto) {

		if (complainceDataMap != null) {

			List<VendorProcRespDto> list = complainceDataMap
					.get(gstin + "_" + returnType);

			Map<String, List<VendorProcRespDto>> retTypeMap = list.stream()
					.collect(Collectors.groupingBy(o -> o.getRetType()));

			if (retTypeMap != null) {
				String itcMatchingScore = retTypeMap.get(returnType).get(0)
						.getItcMatchingScore();
				String complianceScore = retTypeMap.get(returnType).get(0)
						.getComplainceScore();
				String noOffiledRetured = retTypeMap.get(returnType).get(0)
						.getNoOffiledRetured() != null ? retTypeMap.get(returnType).get(0)
								.getNoOffiledRetured() : "0";
				String totalNoOfReturnsCanBeFiled = retTypeMap.get(returnType)
						.get(0).getTotalNoOfReturnsCanBeFiled() != null ? retTypeMap.get(returnType)
								.get(0).getTotalNoOfReturnsCanBeFiled() : "0" ;

				if (itcMatchingScore != null && complianceScore != null)
					dto.setPotentialNonComplianceCategory(
							calculateNonComplianceCategory(
									Double.parseDouble(itcMatchingScore),
									Double.parseDouble(complianceScore)));

				else
					dto.setPotentialNonComplianceCategory("NA");

				String requiredItcMatchingScore = "";				
				if (itcMatchingScore != null) {
					double itcMatchingScoreNumber = Double.parseDouble(itcMatchingScore);
					String formattedItcMatchingScore = String.format("%.2f", itcMatchingScoreNumber);
					requiredItcMatchingScore = formattedItcMatchingScore.equalsIgnoreCase("0")
							? "No data available" : formattedItcMatchingScore;

				} else{
					requiredItcMatchingScore = "No data available";

				}
				dto.setItcMatchingPointsFY(requiredItcMatchingScore);

				Integer diff = (Integer.parseInt(totalNoOfReturnsCanBeFiled))
						- Integer.parseInt(noOffiledRetured);

				dto.setTotalReturnFiled(noOffiledRetured);
				dto.setReturnNotFiled(diff.toString());

				double number = Double.parseDouble(complianceScore);
			    String formattedComplianceScore = String.format("%.2f", number);
				
				dto.setGstComplianceScoreFY(formattedComplianceScore);

				dto.setPeriodForITCMatchingPoints(
						getLast12Months().toString());

			}
		} else {
			dto.setPotentialNonComplianceCategory("NA");
			dto.setItcMatchingPointsFY("No data available");
		}

		

	}

	/**
	 * @param detailsList3B
	 * @param dto
	 */
	private Pair<Integer, Integer> setMonths(List<GstrReturnStatusEntity> returnDetailsList,
			VendorDto dto, String fillingFrequency,String fy,String returnType) {
		Integer fileOntimeCounter = 0;
		List<String> returnMonthList = new ArrayList<>();
		if(returnDetailsList != null && !returnDetailsList.isEmpty()) {
		for (GstrReturnStatusEntity entity : returnDetailsList) {
			returnMonthList.add(GenUtil
					.getDerivedTaxPeriod(entity.getTaxPeriod()).toString());
			if (entity.getTaxPeriod().substring(0, 2).equalsIgnoreCase("01")) {
				dto.setJanuary("Filed "
						+ convertDateFormat(entity.getFilingDate().toString()));
			} else if (entity.getTaxPeriod().substring(0, 2)
					.equalsIgnoreCase("02")) {
				dto.setFebruary("Filed "
						+ convertDateFormat(entity.getFilingDate().toString()));
			} else if (entity.getTaxPeriod().substring(0, 2)
					.equalsIgnoreCase("03")) {
				dto.setMarch("Filed "
						+ convertDateFormat(entity.getFilingDate().toString()));
			} else if (entity.getTaxPeriod().substring(0, 2)
					.equalsIgnoreCase("04")) {
				dto.setApril("Filed "
						+ convertDateFormat(entity.getFilingDate().toString()));
			} else if (entity.getTaxPeriod().substring(0, 2)
					.equalsIgnoreCase("05")) {
				dto.setMay("Filed "
						+ convertDateFormat(entity.getFilingDate().toString()));
			} else if (entity.getTaxPeriod().substring(0, 2)
					.equalsIgnoreCase("06")) {
				dto.setJune("Filed "
						+ convertDateFormat(entity.getFilingDate().toString()));
			} else if (entity.getTaxPeriod().substring(0, 2)
					.equalsIgnoreCase("07")) {
				dto.setJuly("Filed "
						+ convertDateFormat(entity.getFilingDate().toString()));
			} else if (entity.getTaxPeriod().substring(0, 2)
					.equalsIgnoreCase("08")) {
				dto.setAugust("Filed "
						+ convertDateFormat(entity.getFilingDate().toString()));
			} else if (entity.getTaxPeriod().substring(0, 2)
					.equalsIgnoreCase("09")) {
				dto.setSeptember("Filed "
						+ convertDateFormat(entity.getFilingDate().toString()));
			} else if (entity.getTaxPeriod().substring(0, 2)
					.equalsIgnoreCase("10")) {
				dto.setOctober("Filed "
						+ convertDateFormat(entity.getFilingDate().toString()));
			} else if (entity.getTaxPeriod().substring(0, 2)
					.equalsIgnoreCase("11")) {
				dto.setNovember("Filed "
						+ convertDateFormat(entity.getFilingDate().toString()));
			} else if (entity.getTaxPeriod().substring(0, 2)
					.equalsIgnoreCase("12")) {
				dto.setDecember("Filed "
						+ convertDateFormat(entity.getFilingDate().toString()));
			}

			if (filerDueDateService.isFiledOnTime(entity.getTaxPeriod(),
					entity.getReturnType(), fillingFrequency,
					entity.getFilingDate())) {

				++fileOntimeCounter;
				
				LOGGER.debug("inside vendor validation InTimefilingCounter"
						+ " before return {} : ", fileOntimeCounter);
			}
		}
		
		}
		
		List<String> allMonths = getAllTaxPeriods(fy);
		List<String> allDerivedTaxPeriod = new ArrayList<>();
		for(String month : allMonths){
			allDerivedTaxPeriod.add(GenUtil.getDerivedTaxPeriod(month).toString());
		}
		for(String derivedTaxPeriod : allDerivedTaxPeriod){
			if(!returnMonthList.contains(derivedTaxPeriod)){
				String month = derivedTaxPeriod.substring(4);
				boolean isValidMonth = false;
				if(!GenUtil.getCurrentFinancialYear().equalsIgnoreCase(fy)){
					isValidMonth = filerDueDateService.isDueDateAfterCurrentDate(derivedTaxPeriod, 
							returnType, fillingFrequency);
				} else {
					isValidMonth = true;
				}
								
				if (month.equalsIgnoreCase("01") &&
						fillingFrequency.equalsIgnoreCase("Monthly") && isValidMonth) {
					dto.setJanuary("Not Filed");
				} else if (month.equalsIgnoreCase("02") &&
						fillingFrequency.equalsIgnoreCase("Monthly") && isValidMonth) {
					dto.setFebruary("Not Filed");
				} else if (month.equalsIgnoreCase("03") && isValidMonth) {
					dto.setMarch("Not Filed");
				} else if (month.equalsIgnoreCase("04") &&
						fillingFrequency.equalsIgnoreCase("Monthly") && isValidMonth) {
					dto.setApril("Not Filed");
				} else if (month.equalsIgnoreCase("05") &&
						fillingFrequency.equalsIgnoreCase("Monthly") && isValidMonth) {
					dto.setMay("Not Filed");
				} else if (month.equalsIgnoreCase("06") && isValidMonth) {
					dto.setJune("Not Filed");
				} else if (month.equalsIgnoreCase("07") &&
						fillingFrequency.equalsIgnoreCase("Monthly") && isValidMonth) {
					dto.setJuly("Not Filed");
				} else if (month.equalsIgnoreCase("08") &&
						fillingFrequency.equalsIgnoreCase("Monthly") && isValidMonth) {
					dto.setAugust("Not Filed");
				} else if (month.equalsIgnoreCase("09") && isValidMonth) {
					dto.setSeptember("Not Filed");
				} else if (month.equalsIgnoreCase("10") &&
						fillingFrequency.equalsIgnoreCase("Monthly") && isValidMonth) {
					dto.setOctober("Not Filed");
				} else if (month.equalsIgnoreCase("11") &&
						fillingFrequency.equalsIgnoreCase("Monthly") && isValidMonth) {
					dto.setNovember("Not Filed");
				} else if (month.equalsIgnoreCase("12") && isValidMonth) {
					dto.setDecember("Not Filed");
				} 
			}
		}
		
		
		Integer totalFilling = returnDetailsList != null 
				? returnDetailsList.size() : 0;
				
		return  new Pair<>(totalFilling, fileOntimeCounter);
	}

	public static List<String> getAllMonths(String financialYear) {
		List<String> monthsList = new ArrayList<>();

		String[] splitVal = financialYear.split("-");
		financialYear = splitVal[0] + "-20" + splitVal[1];

		// Parse the input financial year string
		int startYear = Integer.parseInt(financialYear.substring(0, 4));
		int endYear = Integer.parseInt(financialYear.substring(5));

		// Iterate over each month from April to March for each year
		for (int year = startYear; year <= endYear; year++) {
			for (Month month : Month.values()) {
				// Check if it's April to March cycle
				if (month == Month.APRIL || monthsList.size() > 0) {
					// Format the month and year to "MMyyyy" format
					String monthYear = String.format("%02d%04d",
							month.getValue(), year);
					monthsList.add(monthYear);
				}
				// Break loop when reached March of the end year
				if (year == endYear && month == Month.MARCH) {
					break;
				}
			}
		}

		return monthsList;
	}
	
	public static List<String> getAllTaxPeriods(String fy) {
		List<String> monthsList = new ArrayList<>();

		String[] splitVal = fy.split("-");
		String financialYear = splitVal[0] + "-20" + splitVal[1];

		// Parse the input financial year string
		int startYear = Integer.parseInt(financialYear.substring(0, 4));
		int endYear = Integer.parseInt(financialYear.substring(5));
		
		if (!GenUtil.getCurrentFinancialYear().equalsIgnoreCase(fy)) {
			
			// Iterate over each month from April to March for each year
			for (int year = startYear; year <= endYear; year++) {
				for (Month month : Month.values()) {
					// Check if it's April to March cycle
					if (month == Month.APRIL || monthsList.size() > 0) {
						// Format the month and year to "MMyyyy" format
						String monthYear = String.format("%02d%04d", month.getValue(), year);
						monthsList.add(monthYear);
					}
					// Break loop when reached March of the end year
					if (year == endYear && month == Month.MARCH) {
						break;
					}
				}
			}
		} else {
			String currentTaxPeriod = GenUtil.getCurrentTaxPeriod();
			int currentDerivedTaxPeriod = GenUtil.getDerivedTaxPeriod(currentTaxPeriod);
			for (int year = startYear; year <= endYear; year++) {
				for (Month month : Month.values()) {
					// Check if it's April to March cycle
					if (month == Month.APRIL || monthsList.size() > 0) {
						// Format the month and year to "MMyyyy" format
						String monthYear = String.format("%02d%04d", month.getValue(), year);
						int derivedMonthYear = GenUtil.getDerivedTaxPeriod(monthYear);
						if(derivedMonthYear <= currentDerivedTaxPeriod)
							monthsList.add(monthYear);
					}
					// Break loop when reached March of the end year
					if (year == endYear && month == Month.MARCH) {
						break;
					}
				}
			}
		}
		return monthsList;
	}

	// Method to get the name by return type
	public static String getfillingTypeByTaxPayerName(String fillingType) {
		return fillingTypeMap.get(fillingType);
	}

	public static int getNumberOfMonths(String fy) {
		// Check if the financial year string is in the correct format

		// Parse the input financial year
		int startYear = Integer.parseInt(fy.substring(0, 4));
		int endYear = Integer.parseInt("20" + fy.substring(5));

		// Get the current date
		LocalDate currentDate = LocalDate.now();

		// Get the current financial year
		int currentStartYear = currentDate.getMonthValue() < 4
				? currentDate.getYear() - 1
				: currentDate.getYear();
		int currentEndYear = currentStartYear + 1;

		// Compare the input financial year with the current financial year
		if (startYear < currentStartYear || endYear < currentEndYear) {
			// If the input financial year is before the current financial year,
			// return 12
			return 12;
		} else if (startYear > currentStartYear || endYear > currentEndYear) {
			// If the input financial year is after the current financial year,
			// return 0
			return 0;
		} else {
			// If the input financial year is the same as the current financial
			// year,
			// calculate the number of months from April to the current month
			return currentDate.getMonthValue() < 4
					? currentDate.getMonthValue() + 9
					: currentDate.getMonthValue() - 3;
		}
	}

	private Map<String, List<VendorProcRespDto>> getComplainceDataMap(
			List<String> gstinList, String fy, Map<String, 
			List<GetGstinMasterDetailApiPushEntity>> gstinValiadationMap) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(
						"USP_VR_POTENTIAL_NON_COMPLAINCE_CATEGORY");

		String gstins = String.join(",", gstinList);

		storedProc.registerStoredProcedureParameter("P_GSTIN", String.class,
				ParameterMode.IN);

		storedProc.registerStoredProcedureParameter("P_FY", String.class,
				ParameterMode.IN);

		storedProc.setParameter("P_GSTIN", gstins);
		storedProc.setParameter("P_FY", fy);

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = storedProc.getResultList();

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Executed Stored proc to get ChunckData and "
							+ "got resultset of size: %d", resultList.size());
			LOGGER.debug(msg);
		}

		Map<String, List<VendorProcRespDto>> complainceMap = null;

		if (resultList.size() > 0) {

			List<VendorProcRespDto> list = resultList.stream()
					.map(o -> convert(o)).collect(Collectors.toList());

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Record count after converting object array to DTO {} ",
						list);
				LOGGER.debug(msg);
			}
			
			complainceMap = list.stream().collect(Collectors
					.groupingBy(o -> o.getGstin() + "_" + o.getRetType()));

		} else {
			List<VendorProcRespDto> defaultList = new ArrayList<>();
			for (String gstin : gstinList) {

				List<VendorProcRespDto> defaultObjs = getDefaultValues(gstin,
						fy, gstinValiadationMap);

				defaultList.addAll(defaultObjs);
			}

			complainceMap = defaultList.stream().collect(Collectors
					.groupingBy(o -> o.getGstin() + "_" + o.getRetType()));
		}
		
		return complainceMap;

	}

	private List<VendorProcRespDto> getDefaultValues(String o, String fy, Map<String, 
					List<GetGstinMasterDetailApiPushEntity>> gstinValiadationMap) {
		List<VendorProcRespDto> list = new ArrayList<>();

		VendorProcRespDto dto = new VendorProcRespDto();
		dto.setFy(fy);
		dto.setComplainceScore("0.00");
		dto.setGstin(o);
		dto.setItcMatchingScore("0.00");
		dto.setNoOffiledRetured("0");
		dto.setRetType(returnTypeMap.get(fillingTypeMap
				.get(gstinValiadationMap.get(o).get(0).getTaxpayerType())));
		dto.setTotalNoOfReturnsCanBeFiled("0");

		list.add(dto);

		if ("GSTR1".equalsIgnoreCase(dto.getRetType())) {//
			VendorProcRespDto dto1 = new VendorProcRespDto();

			dto1.setFy(fy);
			dto1.setComplainceScore("0.00");
			dto1.setGstin(o);
			dto1.setItcMatchingScore("0.00");
			dto1.setNoOffiledRetured("0");
			dto1.setRetType("GSTR3B");
			dto1.setTotalNoOfReturnsCanBeFiled("0");
			list.add(dto1);
		}

		return list;
	}

	private VendorProcRespDto convert(Object[] o) {

		VendorProcRespDto dto = new VendorProcRespDto();
		dto.setGstin(o[0] != null ? (String) o[0].toString() : null);
		dto.setRetType(o[2] != null ? (String) o[2].toString() : null);
		dto.setComplainceScore(o[5] != null ? (String) o[5].toString() : "0.00");
		dto.setFy(o[1] != null ? (String) o[1].toString() : null);
		dto.setItcMatchingScore(o[6] != null ? (String) o[6].toString() : "0.00");
		dto.setTotalNoOfReturnsCanBeFiled(
				o[4] != null ? (String) o[4].toString() : "0");
		dto.setNoOffiledRetured(o[3] != null ? (String) o[3].toString() : "0");

		return dto;
	}

	public String calculateNonComplianceCategory(double itcMatchingScore,
			double complianceScore) {
		if (itcMatchingScore > 90 && complianceScore > 90) {
			return "Very Low";
		} else if ((itcMatchingScore >= 70 && itcMatchingScore <= 90)
				&& (complianceScore >= 70 && complianceScore <= 90)) {
			return "Low";
		} else if ((itcMatchingScore >= 50 && itcMatchingScore < 70)
				&& (complianceScore >= 50 && complianceScore < 70)) {
			return "Medium";
		} else if (itcMatchingScore < 50 && complianceScore < 50) {
			return "High";
		} else if ((itcMatchingScore >= 70 && itcMatchingScore <= 90)
				&& complianceScore > 90) {
			return "Low";
		} else if ((itcMatchingScore >= 70 && itcMatchingScore <= 90)
				&& (complianceScore >= 50 && complianceScore < 70)) {
			return "Medium";
		} else if ((itcMatchingScore >= 70 && itcMatchingScore <= 90)
				&& complianceScore < 50) {
			return "High";
		} else if ((itcMatchingScore >= 50 && itcMatchingScore < 70)
				&& complianceScore > 90) {
			return "Medium";
		} else if ((itcMatchingScore >= 50 && itcMatchingScore < 70)
				&& (complianceScore >= 70 && complianceScore <= 90)) {
			return "Medium";
		} else if ((itcMatchingScore >= 50 && itcMatchingScore < 70)
				&& complianceScore < 50) {
			return "High";
		} else if (itcMatchingScore < 50 && complianceScore > 90) {
			return "High";
		} else if (itcMatchingScore < 50
				&& (complianceScore >= 70 && complianceScore <= 90)) {
			return "High";
		} else if (itcMatchingScore < 50
				&& (complianceScore >= 50 && complianceScore < 70)) {
			return "High";
		} else if (itcMatchingScore > 90
				&& (complianceScore >= 70 && complianceScore <= 90)) {
			return "Low";
		} else if (itcMatchingScore > 90
				&& (complianceScore >= 50 && complianceScore < 70)) {
			return "Medium";
		} else if (itcMatchingScore > 90 && complianceScore < 50) {
			return "High";
		} else {
			return "Unknown";
		}
	}

	public static String getLast12Months() {
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        
        // Create a formatter for the desired output format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM''yy");
        
        // List to store the months
        List<String> last12Months = new ArrayList<>();
        
        // Loop to generate the past 12 months including the current month
        for (int i = 0; i < 12; i++) {
            LocalDate date = currentDate.minusMonths(i);
            last12Months.add(date.format(formatter));
        }
        
        // Get the first and last month from the list
        String firstMonth = last12Months.get(11);  // Oldest month
        String lastMonth = last12Months.get(0);    // Most recent month
        
        // Return the formatted string
        return firstMonth + " to " + lastMonth;
    }

	private String convertDateFormat(String dt) {
		DateTimeFormatter originalFormatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd");

		LOGGER.debug(" dt - {}  ", dt);
		// Parse the original date string
		LocalDate originalDate = LocalDate.parse(dt, originalFormatter);

		// Define the formatter for the desired date format
		DateTimeFormatter desiredFormatter = DateTimeFormatter
				.ofPattern("dd-MM-yyyy");

		// Format the date to the desired format
		String desiredDateString = originalDate.format(desiredFormatter);
		LOGGER.debug(" desiredDateString - {}  ", desiredDateString);
		return desiredDateString;
	}
	
	public static void main(String[] args) {
		
	}

}