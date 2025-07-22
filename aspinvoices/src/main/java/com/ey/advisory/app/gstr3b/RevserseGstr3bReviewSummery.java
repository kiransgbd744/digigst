/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.entities.client.Gstr3BSaveStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstnSaveToAspRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.ibm.icu.impl.Pair;

import lombok.extern.slf4j.Slf4j;

/**
 * @author siva Krishna
 *
 */
@Component("RevserseGstr3bReviewSummery")
@Slf4j
public class RevserseGstr3bReviewSummery
/* implements Gstr3BGstinDashboardService */ {

	@Autowired
	private Gstr3BGstinDashboardDao gstnDao;

	@Autowired
	private Gstr3bUtil gstr3bUtil;

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	private Gstr3BGstinAspUserInputRepository userRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("Gstr3bGstnSaveToAspServiceImpl")
	private Gstr3bGstnSaveToAspService aspService;

	@Autowired
	private Gstr3BGstinDashboardService dashBoardService;

	@Autowired
	Gstr3BSaveStatusRepository gstr3BSaveStatusRepo;

	@Autowired
	Gstr3BGstnSaveToAspRepository gstr3BGstnSaveToAspRepo;

	@Autowired
	GstnUserRequestRepository gstnUserRequestRepo;

	/*
	 * The below method is responsible For Fetching Response for
	 * Gstr3BGstinDashboar Screen.
	 */
	// @Override
	public Gstr3bDashboardDto getgstr3bgstnDashBoardList(String taxPeriod,
			String gstin) throws AppException {

		Gstr3bDashboardDto gstr3bResp = new Gstr3bDashboardDto();

		List<Gstr3BGstinDashboardDto> respList = null;
		try {

			String gstnResponse = null;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BGstinDashboardServiceImpl"
						+ ".getgstr3bgstnDashBoardList begin, fetching the data"
						+ "from compute table for gstin " + gstin
						+ " and taxPeriod" + " " + taxPeriod);
			}
			Map<String, Gstr3BGstinDashboardDto> gst3BMap = gstr3bUtil
					.getGstr3BGstinDashboardMap();

			List<Gstr3BGstinAspUserInputDto> computeDtoList = gstnDao
					.getGstinComputeDtoList(gstin, taxPeriod);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BGstinDashboardServiceImpl"
						+ ".getgstr3bgstnDashBoardList compute list size = "
						+ computeDtoList.size()
						+ " now fetching data from userInput " + "table");
			}
			List<Gstr3BGstinAspUserInputDto> userInputDtoList = gstnDao
					.getGstinUserInputDtoList(gstin, taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BGstinDashboardServiceImpl"
						+ ".getgstr3bgstnDashBoardList userInput list size = "
						+ userInputDtoList.size());
			}
			List<Gstr3BGstinAspUserInputDto> autoCalDtoList = gstnDao
					.getGstinAutoCalDtoList(gstin, taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BGstinDashboardServiceImpl"
						+ ".getgstr3bgstnDashBoardList autoCalDtoList list "
						+ "size = " + autoCalDtoList.size());
			}

			/*
			 * If there is no data in user input table saving data from compute
			 * table
			 */
			if (userInputDtoList.isEmpty()) {
				userInputDtoList = computeDtoList;

				List<Gstr3BGstinAspUserInputEntity> entities = computeDtoList
						.stream().map(o -> convertToEntity(o, gstin, taxPeriod))
						.collect(Collectors.toList());

				List<String> computeSectionNameList = computeDtoList.stream()
						.map(o -> o.getSectionName())
						.collect(Collectors.toCollection(ArrayList::new));

				List<String> allSectionNameList = gst3BMap.keySet().stream()
						.distinct().collect(Collectors.toList());

				allSectionNameList.removeAll(computeSectionNameList);

				LOGGER.info("Gstr3BGstinDashboardServiceImpl : setting as "
						+ "default zero value for all other sections ");

				List<String> sectionNameList = new ArrayList<>();
				sectionNameList.add(Gstr3BConstants.Table3_2_A);
				sectionNameList.add(Gstr3BConstants.Table3_2_B);
				sectionNameList.add(Gstr3BConstants.Table3_2_C);

				allSectionNameList.removeAll(sectionNameList);

				List<Gstr3BGstinAspUserInputEntity> DefaultEntities = allSectionNameList
						.stream().map(o -> setDefaultValue(o, gstin, taxPeriod,
								gst3BMap))
						.collect(Collectors.toList());

				entities.addAll(DefaultEntities);

				LOGGER.info("Gstr3BGstinDashboardServiceImpl : entity to be "
						+ "saved %s", entities);

				userRepo.saveAll(entities);

				LOGGER.info("Gstr3BGstinDashboardServiceImpl : "
						+ "successfully updated user input table");

			}
			// checking for 5(a) and 5(b)
			List<Gstr3BGstinAspUserInputDto> copiedUserInputList = gstnDao
					.getGstinUserInputDtoList(gstin, taxPeriod);

			List<String> userSectionNameList = copiedUserInputList.stream()
					.map(o -> o.getSectionName())
					.collect(Collectors.toCollection(ArrayList::new));

			if (!userSectionNameList.contains(Gstr3BConstants.Table5A)) {
				Gstr3BGstinAspUserInputEntity entity = setDefaultValue(
						Gstr3BConstants.Table5A, gstin, taxPeriod, gst3BMap);
				userRepo.save(entity);
			}
			if (!userSectionNameList.contains(Gstr3BConstants.Table5B)) {
				Gstr3BGstinAspUserInputEntity entity = setDefaultValue(
						Gstr3BConstants.Table5B, gstin, taxPeriod, gst3BMap);
				userRepo.save(entity);
			}

			/*
			 * The below code will merge the compute list by adding the rows
			 * which has same section
			 */
			Map<String, Gstr3BGstinAspUserInputDto> computeMap = computeDtoList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			/*
			 * The below code will merge the user input list by adding the rows
			 * which has same section
			 */
			Map<String, Gstr3BGstinAspUserInputDto> userMap = userInputDtoList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));
			/*
			 * The below code will merge the AutoCal list by adding the rows
			 * which has same section
			 */
			Map<String, Gstr3BGstinAspUserInputDto> autoCalMap = autoCalDtoList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			List<Gstr3BGstinAspUserInputDto> gstnDtoList = gstnDao
					.getGstnDtoList(gstin, taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BGstinDashboardServiceImpl"
						+ ".getgstr3bgstnDashBoardList Gstn list size = "
						+ gstnDtoList.size() + " Now calling the gstn api");
			}

			if (gstnDtoList.isEmpty()) {

				if (authTokenService.getAuthTokenStatusForGstin(gstin)
						.equalsIgnoreCase("A")) {
					GstnUserRequestEntity getRespone = gstnUserRequestRepo
							.findByGstinAndTaxPeriodAndReturnTypeAndRequestTypeAndRequestStatusAndIsDeleteFalse(
									gstin, taxPeriod, Gstr3BConstants.GSTR3B,
									"GET", 1);
					Clob getResponsePayload = getRespone
							.getGetResponsePayload();
					gstnResponse = getClobString(getResponsePayload);

					Map<String, JsonObject> getGstnResponseMap = gstr3bUtil
							.parseJsonResponse(gstnResponse);

					/*
					 * the below code will take the hard coded map for gstr3b
					 * and set the values (if available) from user input list,
					 * compute list, AutoCal and GSTN response into thegstr3b
					 * list
					 */
					gst3BMap.replaceAll((k, v) -> {
						if (v.getLastLevel()) {
							if (computeMap.containsKey(k))
								setComputedValues(v, computeMap.get(k));
							if (userMap.containsKey(k))
								setUserInputValues(v, userMap.get(k));
							if (autoCalMap.containsKey(k))
								setAutoCalValues(v, autoCalMap.get(k));
							if (getGstnResponseMap.containsKey(k)
									&& getGstnResponseMap.get(k) != null)
								setGstinvalues(v, getGstnResponseMap.get(k));
						}

						return v;
					});

					// }

				}
			}

			Map<String, Gstr3BGstinAspUserInputDto> gstnMap = gstnDtoList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			gst3BMap.replaceAll((k, v) -> {
				if (v.getLastLevel()) {
					if (computeMap.containsKey(k))
						setComputedValues(v, computeMap.get(k));
					if (userMap.containsKey(k))
						setUserInputValues(v, userMap.get(k));
					if (autoCalMap.containsKey(k))
						setAutoCalValues(v, autoCalMap.get(k));
					if (gstnMap.containsKey(k))
						setGstnValues(v, gstnMap.get(k));
				}

				return v;
			});

			respList = new ArrayList<>(gst3BMap.values());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BGstinDashboardServiceImpl"
						+ ".getgstr3bgstnDashBoardList returning response"
						+ ", response size = " + respList.size() + "");
			}

			Gstr3BSaveStatusEntity saveEntity = gstr3BSaveStatusRepo
					.findFirstByGstinAndTaxPeriodOrderByIdDesc(gstin,
							taxPeriod);

			if (saveEntity != null) {
				LocalDateTime recentSaveDate = saveEntity.getCreatedOn();
				Timestamp ts = Timestamp.valueOf(recentSaveDate);
				Date d1 = new Date(ts.getTime());
				String recentSavetoGstn = d1.toString();
				gstr3bResp.setRecentSavedDate(EYDateUtil
						.toISTDateTimeFromUTC(recentSaveDate).toString());
			} else {
				gstr3bResp.setRecentSavedDate("");
			}

			Pageable pageReq = PageRequest.of(0, 1, Direction.DESC, "id");

			List<Gstr3bGstnSaveToAspEntity> updateEntity = gstr3BGstnSaveToAspRepo
					.findByGstinAndTaxPeriod(gstin, taxPeriod, pageReq);

			if (updateEntity != null && !updateEntity.isEmpty()) {
				Gstr3bGstnSaveToAspEntity entity = updateEntity.get(0);
				LocalDateTime recentUpdateDate = entity.getCreateDate();
				Timestamp ts2 = Timestamp.valueOf(recentUpdateDate);
				Date d2 = new Date(ts2.getTime());
				String recentUpdatetoGstn = d2.toString();
				gstr3bResp.setRecentUpdatedDate(EYDateUtil
						.toISTDateTimeFromUTC(recentUpdateDate).toString());
			} else {
				gstr3bResp.setRecentUpdatedDate("");
			}

			gstr3bResp.setGstr3bGstinDashboard(respList);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);

		}
		return gstr3bResp;

	}

	String userName = (SecurityContext.getUser() != null
			&& SecurityContext.getUser().getUserPrincipalName() != null)
					? SecurityContext.getUser().getUserPrincipalName()
					: "SYSTEM";

	private Gstr3BGstinAspUserInputEntity setDefaultValue(String sectionName,
			String gstin, String taxPeriod,
			Map<String, Gstr3BGstinDashboardDto> gst3bMap) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr3BGstinDashboardServiceImpl"
					+ ".setDefaultValue() method :");
		}

		Gstr3BGstinAspUserInputEntity userEntity = new Gstr3BGstinAspUserInputEntity();

		BigDecimal defaultValue = BigDecimal.ZERO;
		userEntity.setCess(defaultValue);
		userEntity.setCgst(defaultValue);
		userEntity.setIgst(defaultValue);
		userEntity.setSgst(defaultValue);
		userEntity.setTaxableVal(defaultValue);
		userEntity.setGstin(gstin);
		userEntity.setTaxPeriod(taxPeriod);
		userEntity.setCreateDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		userEntity.setCreatedBy(userName);
		userEntity.setUpdateDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		userEntity.setUpdatedBy(userName);
		userEntity.setInterState(defaultValue);
		userEntity.setIntraState(defaultValue);
		userEntity.setSectionName(sectionName);
		userEntity.setSubSectionName(gst3bMap.get(sectionName).getSupplyType());
		userEntity.setIsActive(true);
		userEntity.setIsITCActive(true);
		userEntity.setPos(null);
		return userEntity;

	}

	private Gstr3BGstinAspUserInputDto addDto(Gstr3BGstinAspUserInputDto a,
			Gstr3BGstinAspUserInputDto b) {
		Gstr3BGstinAspUserInputDto dto = new Gstr3BGstinAspUserInputDto();

		if (a.getCess() == null) {
			a.setCess(BigDecimal.ZERO);
		}
		if (a.getCgst() == null) {
			a.setCgst(BigDecimal.ZERO);
		}
		if (a.getIgst() == null) {
			a.setIgst(BigDecimal.ZERO);
		}
		if (a.getSgst() == null) {
			a.setSgst(BigDecimal.ZERO);
		}
		if (a.getTaxableVal() == null) {
			a.setTaxableVal(BigDecimal.ZERO);
		}
		if (b.getCess() == null) {
			b.setCess(BigDecimal.ZERO);
		}
		if (b.getCgst() == null) {
			b.setCgst(BigDecimal.ZERO);
		}
		if (b.getIgst() == null) {
			b.setIgst(BigDecimal.ZERO);
		}
		if (b.getSgst() == null) {
			b.setSgst(BigDecimal.ZERO);
		}
		if (b.getTaxableVal() == null) {
			b.setTaxableVal(BigDecimal.ZERO);
		}
		dto.setCess(a.getCess().add(b.getCess()));
		dto.setCgst(a.getCgst().add(b.getCgst()));
		dto.setIgst(a.getIgst().add(b.getIgst()));
		dto.setSgst(a.getSgst().add(b.getSgst()));
		dto.setTaxableVal(a.getTaxableVal().add(b.getTaxableVal()));
		return dto;
	}

	private void setGstinvalues(Gstr3BGstinDashboardDto gstr3bgstnDto,
			JsonObject jsonObject) {
		if (jsonObject.has("txval"))
			gstr3bgstnDto.setGstinTaxableVal(
					jsonObject.get("txval").getAsBigDecimal());
		if (jsonObject.has("csamt"))
			gstr3bgstnDto
					.setGstinCess(jsonObject.get("csamt").getAsBigDecimal());
		if (jsonObject.has("camt"))
			gstr3bgstnDto
					.setGstinCgst(jsonObject.get("camt").getAsBigDecimal());
		if (jsonObject.has("samt"))
			gstr3bgstnDto
					.setGstinSgst(jsonObject.get("samt").getAsBigDecimal());
		if (jsonObject.has("iamt"))
			gstr3bgstnDto
					.setGstinIgst(jsonObject.get("iamt").getAsBigDecimal());
	}

	private void setUserInputValues(Gstr3BGstinDashboardDto gstr3bgstnDto,
			Gstr3BGstinAspUserInputDto gstr3bGstinAspUserInputDto) {
		gstr3bgstnDto.setUserInputCess(gstr3bGstinAspUserInputDto.getCess());
		gstr3bgstnDto.setUserInputCgst(gstr3bGstinAspUserInputDto.getCgst());
		gstr3bgstnDto.setUserInputIgst(gstr3bGstinAspUserInputDto.getIgst());
		gstr3bgstnDto.setUserInputSgst(gstr3bGstinAspUserInputDto.getSgst());
		gstr3bgstnDto.setUserInputTaxableVal(
				gstr3bGstinAspUserInputDto.getTaxableVal());

	}

	private void setGstnValues(Gstr3BGstinDashboardDto gstr3bgstnDto,
			Gstr3BGstinAspUserInputDto gstr3bGstinAspUserInputDto) {
		if (gstr3bGstinAspUserInputDto.getCess() != null)
			gstr3bgstnDto.setGstinCess(gstr3bGstinAspUserInputDto.getCess());
		else {
			gstr3bgstnDto.setGstinCess(BigDecimal.ZERO);
		}
		if (gstr3bGstinAspUserInputDto.getCgst() != null)
			gstr3bgstnDto.setGstinCgst(gstr3bGstinAspUserInputDto.getCgst());
		else {
			gstr3bgstnDto.setGstinCgst(BigDecimal.ZERO);
		}
		if (gstr3bGstinAspUserInputDto.getIgst() != null)
			gstr3bgstnDto.setGstinIgst(gstr3bGstinAspUserInputDto.getIgst());
		else {
			gstr3bgstnDto.setGstinIgst(BigDecimal.ZERO);
		}
		if (gstr3bGstinAspUserInputDto.getSgst() != null)
			gstr3bgstnDto.setGstinSgst(gstr3bGstinAspUserInputDto.getSgst());
		else {
			gstr3bgstnDto.setGstinSgst(BigDecimal.ZERO);
		}
		if (gstr3bGstinAspUserInputDto.getTaxableVal() != null)
			gstr3bgstnDto.setGstinTaxableVal(
					gstr3bGstinAspUserInputDto.getTaxableVal());
		else {
			gstr3bgstnDto.setGstinTaxableVal(BigDecimal.ZERO);
		}
	}

	private void setComputedValues(Gstr3BGstinDashboardDto gstr3bgstnDto,
			Gstr3BGstinAspUserInputDto gstr3bGstinAspUserInputDto) {
		gstr3bgstnDto.setComputdCess(gstr3bGstinAspUserInputDto.getCess());
		gstr3bgstnDto.setComputedCgst(gstr3bGstinAspUserInputDto.getCgst());
		gstr3bgstnDto.setComputedIgst(gstr3bGstinAspUserInputDto.getIgst());
		gstr3bgstnDto.setComputedSgst(gstr3bGstinAspUserInputDto.getSgst());
		gstr3bgstnDto.setComputedTaxableVal(
				gstr3bGstinAspUserInputDto.getTaxableVal());
	}

	private void setAutoCalValues(Gstr3BGstinDashboardDto gstr3AutoDto,
			Gstr3BGstinAspUserInputDto gstr3bGstinAspUserInputDto) {
		gstr3AutoDto.setAutoCalCess(gstr3bGstinAspUserInputDto.getCess());
		gstr3AutoDto.setAutoCalCgst(gstr3bGstinAspUserInputDto.getCgst());
		gstr3AutoDto.setAutoCalIgst(gstr3bGstinAspUserInputDto.getIgst());
		gstr3AutoDto.setAutoCalSgst(gstr3bGstinAspUserInputDto.getSgst());
		gstr3AutoDto.setAutoCalTaxableVal(
				gstr3bGstinAspUserInputDto.getTaxableVal());
	}

	// @Override
	public List<Gstr3BInterStateSuppliesDto> getInterStateSupplies(
			String taxPeriod, String gstin) {

		final List<String> sections = ImmutableList.of(
				Gstr3BConstants.Table3_2_A, Gstr3BConstants.Table3_2_B,
				Gstr3BConstants.Table3_2_C);

		List<Gstr3BInterStateSuppliesDto> respList = new ArrayList<>();

		try {
			List<Gstr3BGstinAspUserInputDto> respUserList = gstnDao
					.getUserInputDtoBySection(taxPeriod, gstin, sections);

			for (int i = 0; respUserList.size() > i; i++) {
				Gstr3BInterStateSuppliesDto userList = new Gstr3BInterStateSuppliesDto();
				Gstr3BGstinAspUserInputDto user = respUserList.get(i);

				userList.setSectionName(user.getSectionName());
				userList.setSubSectionName(user.getSubSectionName());
				userList.setCess(user.getCess());
				userList.setCgst(user.getCgst());
				userList.setIgst(user.getIgst());
				userList.setInterState(user.getInterState());
				userList.setIntraState(user.getIntraState());
				userList.setPos(user.getPos());
				userList.setSgst(user.getSgst());
				userList.setTaxableVal(user.getTaxableVal());
				userList.setFlag(i);
				respList.add(userList);

			}

			List<Gstr3BGstinAspUserInputDto> respComputeList = gstnDao
					.getComputeDtoBySection(taxPeriod, gstin, sections);

			for (int i = 0; respComputeList.size() > i; i++) {

				Gstr3BGstinAspUserInputDto respCompute = respComputeList.get(i);

				Gstr3BInterStateSuppliesDto computeList = new Gstr3BInterStateSuppliesDto();

				computeList.setSectionName(respCompute.getSectionName());
				computeList.setSubSectionName(respCompute.getSubSectionName());
				computeList.setPosCompute(respCompute.getPos());
				computeList.setIgstCompute(respCompute.getIgst());
				computeList.setTaxableValCompute(respCompute.getTaxableVal());
				computeList.setFlag(i);
				respList.add(computeList);

			}

			List<Gstr3BGstinAspUserInputDto> respAutoCalList = gstnDao
					.getAutoCalDtoBySection(taxPeriod, gstin, sections);

			for (int i = 0; respAutoCalList.size() > i; i++) {
				Gstr3BGstinAspUserInputDto respAutoCalc = respAutoCalList
						.get(i);

				Gstr3BInterStateSuppliesDto autoCalcList = new Gstr3BInterStateSuppliesDto();

				autoCalcList.setSectionName(respAutoCalc.getSectionName());
				autoCalcList
						.setSubSectionName(respAutoCalc.getSubSectionName());
				autoCalcList.setPosAutoCal(respAutoCalc.getPos());
				autoCalcList.setIgstAutoCal(respAutoCalc.getIgst());
				autoCalcList.setTaxableValAutoCal(respAutoCalc.getTaxableVal());
				autoCalcList.setFlag(i);
				respList.add(autoCalcList);

			}

			Map<Object, Object> resp = respList.stream()
					.collect(Collectors.groupingBy(
							o -> Pair.of(o.getFlag(), o.getSectionName()),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addInwards(a, b)),
									Optional::get)));

			List<Object> objList = new ArrayList<>(resp.values());

			respList = objList.stream().map(o -> convertUserInput(o))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			String msg = String.format("error occured while invoking "
					+ "getInterStateSupplies() ", ex);
			throw new AppException(msg);
		}

		return respList;
	}

	private Gstr3BInterStateSuppliesDto convertUserInput(Object o) {

		Gstr3BInterStateSuppliesDto v1 = new Gstr3BInterStateSuppliesDto();
		v1.setSectionName(((Gstr3BInterStateSuppliesDto) o).getSectionName());
		v1.setSubSectionName(
				((Gstr3BInterStateSuppliesDto) o).getSubSectionName());
		v1.setCess(((Gstr3BInterStateSuppliesDto) o).getCess());
		v1.setCgst(((Gstr3BInterStateSuppliesDto) o).getCgst());
		v1.setIgst(((Gstr3BInterStateSuppliesDto) o).getIgst());
		v1.setInterState(((Gstr3BInterStateSuppliesDto) o).getInterState());
		v1.setIntraState(((Gstr3BInterStateSuppliesDto) o).getIntraState());
		v1.setPos(((Gstr3BInterStateSuppliesDto) o).getPos());
		v1.setSgst(((Gstr3BInterStateSuppliesDto) o).getSgst());
		v1.setTaxableVal(((Gstr3BInterStateSuppliesDto) o).getTaxableVal());
		v1.setTaxableValCompute(
				((Gstr3BInterStateSuppliesDto) o).getTaxableValCompute());
		v1.setPosCompute(((Gstr3BInterStateSuppliesDto) o).getPosCompute());
		v1.setIgstCompute(((Gstr3BInterStateSuppliesDto) o).getIgstCompute());
		v1.setTaxableValAutoCal(
				((Gstr3BInterStateSuppliesDto) o).getTaxableValAutoCal());
		v1.setPosAutoCal(((Gstr3BInterStateSuppliesDto) o).getPosAutoCal());
		v1.setIgstAutoCal(((Gstr3BInterStateSuppliesDto) o).getIgstAutoCal());

		return v1;
	}

	// @Override
	public List<Gstr3BExcemptNilNonGstnDto> getExcemptNilNonGstIS(
			String taxPeriod, String gstin) {
		List<String> sections = ImmutableList.of(Gstr3BConstants.Table5A,
				Gstr3BConstants.Table5B);
		List<Gstr3BExcemptNilNonGstnDto> respList = null;
		try {

			List<Gstr3BGstinAspUserInputDto> respUser = gstnDao
					.getUserInputDtoBySection(taxPeriod, gstin, sections);

			List<Gstr3BGstinAspUserInputDto> respCompute = gstnDao
					.getComputeDtoBySection(taxPeriod, gstin, sections);

			Map<String, Gstr3BGstinAspUserInputDto> userMap = respUser.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			Map<String, Gstr3BGstinAspUserInputDto> computeMap = respCompute
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			Map<String, Gstr3BExcemptNilNonGstnDto> examptNilNonMap = gstr3bUtil
					.gstr3BExcemptNilNonGstnMap();

			examptNilNonMap.replaceAll((k, v) -> {
				if (computeMap.containsKey(k))
					setExcemptNilNonComputedValues(v, computeMap.get(k));
				if (userMap.containsKey(k))
					setExcemptNilNonUserInputValues(v, userMap.get(k));
				return v;
			});

			respList = new ArrayList<>(examptNilNonMap.values());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BGstinDashboardServiceImpl"
						+ ".getExcemptNilNonGstIS returning response"
						+ ", response size = " + respList.size() + "");
			}
		} catch (Exception ex) {
			String msg = String.format(
					"error occured while invoking getExcemptNilNonGstIS() ",
					ex);
			LOGGER.error(msg, ex);
		}
		return respList;

	}

	private void setExcemptNilNonUserInputValues(Gstr3BExcemptNilNonGstnDto v,
			Gstr3BGstinAspUserInputDto inputDto) {

		v.setCess(inputDto.getCess());
		v.setCgst(inputDto.getCgst());
		v.setIgst(inputDto.getIgst());
		v.setInterState(inputDto.getInterState());
		v.setIntraState(inputDto.getIntraState());
		v.setSgst(inputDto.getSgst());
		v.setTaxableVal(inputDto.getTaxableVal());

	}

	private void setExcemptNilNonComputedValues(Gstr3BExcemptNilNonGstnDto v,
			Gstr3BGstinAspUserInputDto inputDto) {
		v.setInterStateCompute(inputDto.getInterState());
		v.setIntraStateCompute(inputDto.getIntraState());
	}

	// @Override
	public List<Gstr3BGstinsDto> getGstrDtoList(APIResponse apiResponse) {

		String jsonResponseString = apiResponse.getResponse();

		// creating Map for tableSection as key and respective Json as value.

		Map<String, JsonObject> getGstnResponseMap = gstr3bUtil
				.parseJsonResponse(jsonResponseString);

		// Map to set sectionName and subSectionName
		Map<String, Gstr3BGstinDashboardDto> subMap = gstr3bUtil
				.getGstr3BGstinDashboardMap();

		List<Gstr3BGstinsDto> responseList = new ArrayList<>();

		for (Map.Entry<String, JsonObject> entry : getGstnResponseMap
				.entrySet()) {
			String key = entry.getKey();
			Gstr3BGstinsDto dto = new Gstr3BGstinsDto(key,
					subMap.get(key).getSupplyType());

			JsonObject value = entry.getValue();

			if (value != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info(" Before invoking "
							+ "Gstr3BGstinDashboardServiceImpl.setGstinvalues() "
							+ "method for value : " + value);
				}
				setGstinvalues(dto, value);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info(" After invoking "
							+ "Gstr3BGstinDashboardServiceImpl.setGstinvalues() "
							+ "method for value : " + value);
				}
				responseList.add(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("adding dto to responseList : " + responseList);
				}
			}
		}
		return responseList;

	}

	private void setGstinvalues(Gstr3BGstinsDto gstr3bgstnDto,
			JsonObject jsonObject) {
		if (jsonObject.has("txval"))
			gstr3bgstnDto
					.setTaxableVal(jsonObject.get("txval").getAsBigDecimal());
		else
			gstr3bgstnDto.setTaxableVal(BigDecimal.ZERO);
		if (jsonObject.has("csamt"))
			gstr3bgstnDto.setCess(jsonObject.get("csamt").getAsBigDecimal());
		else
			gstr3bgstnDto.setCess(BigDecimal.ZERO);
		if (jsonObject.has("camt"))
			gstr3bgstnDto.setCgst(jsonObject.get("camt").getAsBigDecimal());
		else
			gstr3bgstnDto.setCgst(BigDecimal.ZERO);
		if (jsonObject.has("samt"))
			gstr3bgstnDto.setSgst(jsonObject.get("samt").getAsBigDecimal());
		else
			gstr3bgstnDto.setSgst(BigDecimal.ZERO);
		if (jsonObject.has("iamt"))
			gstr3bgstnDto.setIgst(jsonObject.get("iamt").getAsBigDecimal());
		else
			gstr3bgstnDto.setIgst(BigDecimal.ZERO);
		if (jsonObject.has("inter"))
			gstr3bgstnDto
					.setInterState(jsonObject.get("inter").getAsBigDecimal());
		else
			gstr3bgstnDto.setInterState(BigDecimal.ZERO);
		if (jsonObject.has("intra"))
			gstr3bgstnDto
					.setIntraState(jsonObject.get("intra").getAsBigDecimal());
		else
			gstr3bgstnDto.setIntraState(BigDecimal.ZERO);
		if (jsonObject.has("pos"))
			gstr3bgstnDto.setPos(jsonObject.get("pos").getAsString());
		else
			gstr3bgstnDto.setPos("0");

	}

	private Gstr3BGstinAspUserInputEntity convertToEntity(
			Gstr3BGstinAspUserInputDto userInput, String gstin,
			String taxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr3BGstinDashboardServiceImpl"
					+ ".convertToEntity method :");
		}

		BigDecimal zero = BigDecimal.ZERO;

		Gstr3BGstinAspUserInputEntity userEntity = new Gstr3BGstinAspUserInputEntity();

		BigDecimal cess = (userInput.getCess()).signum() != -1
				? userInput.getCess() : zero;

		BigDecimal cgst = (userInput.getCgst()).signum() != -1
				? userInput.getCgst() : zero;

		BigDecimal igst = (userInput.getIgst()).signum() != -1
				? userInput.getIgst() : zero;

		BigDecimal sgst = (userInput.getSgst()).signum() != -1
				? userInput.getSgst() : zero;

		BigDecimal taxableVal = (userInput.getTaxableVal()).signum() != -1
				? userInput.getTaxableVal() : zero;

		BigDecimal interState = (userInput.getInterState()).signum() != -1
				? userInput.getInterState() : zero;

		BigDecimal intraState = (userInput.getIntraState()).signum() != -1
				? userInput.getIntraState() : zero;

		userEntity.setCess(cess);
		userEntity.setCgst(cgst);
		userEntity.setIgst(igst);
		userEntity.setSgst(sgst);
		userEntity.setTaxableVal(taxableVal);
		userEntity.setGstin(gstin);
		userEntity.setTaxPeriod(taxPeriod);
		userEntity.setCreateDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		userEntity.setCreatedBy(userName);
		userEntity.setUpdateDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		userEntity.setUpdatedBy(userName);
		userEntity.setInterState(interState);
		userEntity.setIntraState(intraState);
		userEntity.setSectionName(userInput.getSectionName());
		userEntity.setSubSectionName(userInput.getSubSectionName());
		userEntity.setIsActive(true);
		userEntity.setIsITCActive(true);
		userEntity.setPos(userInput.getPos());
		return userEntity;
	}

	// @Override
	public List<Gstr3bTaxPaymentDto> getTaxPayemntList(
			APIResponse apiResponse) {

		String jsonRes = apiResponse.getResponse();
		JsonParser jsonParser = new JsonParser();
		JsonArray tax_py = new JsonArray();
		JsonArray pd_cash = new JsonArray();
		JsonObject pd_itc = new JsonObject();
		List<Gstr3bTaxPaymentDto> taxPaymentList = new ArrayList<>();

		JsonObject tax_pmt = (JsonObject) jsonParser.parse(jsonRes)
				.getAsJsonObject().get("tx_pmt");
		if (tax_pmt != null) {

			tax_py = tax_pmt.has("tx_py") ? tax_pmt.getAsJsonArray("tx_py")
					: null;

			if (tax_py != null) {

				for (int i = 0; i <= 1; i++) {
					JsonObject taxpy = tax_py.get(i).getAsJsonObject();
					if (taxpy != null) {

						JsonObject igst = taxpy.getAsJsonObject("igst");

						if (igst != null) {
							Gstr3bTaxPaymentDto obj = new Gstr3bTaxPaymentDto();
							obj.setLiabilityLedgerId(
									taxpy.get("liab_ldg_id").getAsLong());
							obj.setSubSection("tx_py");
							obj.setTransactionType(
									taxpy.get("trans_typ").getAsString());
							obj.setTaxType("I");
							obj.setInterestAmt(
									igst.get("intr").getAsBigDecimal());
							obj.setTaxAmt(igst.get("tx").getAsBigDecimal());
							obj.setFeeAmt(igst.get("fee").getAsBigDecimal());
							// obj.setFy();
							taxPaymentList.add(obj);
						}

						JsonObject cgst = taxpy.getAsJsonObject("cgst");

						if (cgst != null) {
							Gstr3bTaxPaymentDto obj = new Gstr3bTaxPaymentDto();
							obj.setLiabilityLedgerId(
									taxpy.get("liab_ldg_id").getAsLong());
							obj.setSubSection("tx_py");
							obj.setTransactionType(
									taxpy.get("trans_typ").getAsString());
							obj.setTaxType("C");
							obj.setInterestAmt(
									igst.get("intr").getAsBigDecimal());
							obj.setTaxAmt(igst.get("tx").getAsBigDecimal());
							obj.setFeeAmt(igst.get("fee").getAsBigDecimal());
							// obj.setFy();
							taxPaymentList.add(obj);
						}

						JsonObject sgst = taxpy.getAsJsonObject("sgst");

						if (sgst != null) {
							Gstr3bTaxPaymentDto obj = new Gstr3bTaxPaymentDto();
							obj.setLiabilityLedgerId(
									taxpy.get("liab_ldg_id").getAsLong());
							obj.setSubSection("tx_py");
							obj.setTransactionType(
									taxpy.get("trans_typ").getAsString());
							obj.setTaxType("S");
							obj.setInterestAmt(
									igst.get("intr").getAsBigDecimal());
							obj.setTaxAmt(igst.get("tx").getAsBigDecimal());
							obj.setFeeAmt(igst.get("fee").getAsBigDecimal());
							// obj.setFy();
							taxPaymentList.add(obj);
						}

						JsonObject cess = taxpy.getAsJsonObject("cess");

						if (cess != null) {
							Gstr3bTaxPaymentDto obj = new Gstr3bTaxPaymentDto();
							obj.setLiabilityLedgerId(
									taxpy.get("liab_ldg_id").getAsLong());
							obj.setSubSection("tx_py");
							obj.setTransactionType(
									taxpy.get("trans_typ").getAsString());
							obj.setTaxType("CS");
							obj.setInterestAmt(
									igst.get("intr").getAsBigDecimal());
							obj.setTaxAmt(igst.get("tx").getAsBigDecimal());
							obj.setFeeAmt(igst.get("fee").getAsBigDecimal());
							// obj.setFy();
							taxPaymentList.add(obj);
						}
					}
				}
			}
			pd_cash = tax_pmt.has("pdcash") ? tax_pmt.getAsJsonArray("pdcash")
					: null;

			if (pd_cash != null) {

				for (int i = 0; i <= 1; i++) {
					JsonObject pdcash = pd_cash.get(i).getAsJsonObject();
					if (pdcash != null) {

						Gstr3bTaxPaymentDto obj1 = new Gstr3bTaxPaymentDto();
						obj1.setLiabilityLedgerId(
								pdcash.get("liab_ldg_id").getAsLong());
						obj1.setSubSection("pdcash");
						obj1.setTransactionType(
								pdcash.get("trans_typ").getAsString());
						obj1.setTaxType("I");
						obj1.setInterestAmt(
								pdcash.get("i_intrpd").getAsBigDecimal());
						obj1.setTaxAmt(pdcash.get("ipd").getAsBigDecimal());
						obj1.setFeeAmt(
								pdcash.get("i_lfeepd").getAsBigDecimal());
						// obj1.setFy();
						taxPaymentList.add(obj1);

						Gstr3bTaxPaymentDto obj2 = new Gstr3bTaxPaymentDto();
						obj2.setLiabilityLedgerId(
								pdcash.get("liab_ldg_id").getAsLong());
						obj2.setSubSection("pdcash");
						obj2.setTransactionType(
								pdcash.get("trans_typ").getAsString());
						obj2.setTaxType("C");
						obj2.setInterestAmt(
								pdcash.get("c_intrpd").getAsBigDecimal());
						obj2.setTaxAmt(pdcash.get("cpd").getAsBigDecimal());
						obj2.setFeeAmt(
								pdcash.get("c_lfeepd").getAsBigDecimal());
						// obj2.setFy();
						taxPaymentList.add(obj2);

						Gstr3bTaxPaymentDto obj3 = new Gstr3bTaxPaymentDto();
						obj3.setLiabilityLedgerId(
								pdcash.get("liab_ldg_id").getAsLong());
						obj3.setSubSection("pdcash");
						obj3.setTransactionType(
								pdcash.get("trans_typ").getAsString());
						obj3.setTaxType("S");
						obj3.setInterestAmt(
								pdcash.get("s_intrpd").getAsBigDecimal());
						obj3.setTaxAmt(pdcash.get("spd").getAsBigDecimal());
						obj3.setFeeAmt(
								pdcash.get("s_lfeepd").getAsBigDecimal());
						// obj3.setFy();
						taxPaymentList.add(obj3);

						Gstr3bTaxPaymentDto obj4 = new Gstr3bTaxPaymentDto();
						obj4.setLiabilityLedgerId(
								pdcash.get("liab_ldg_id").getAsLong());
						obj4.setSubSection("pdcash");
						obj4.setTransactionType(
								pdcash.get("trans_typ").getAsString());
						obj4.setTaxType("CS");
						obj4.setInterestAmt(
								pdcash.get("cs_intrpd").getAsBigDecimal());
						obj4.setTaxAmt(pdcash.get("cspd").getAsBigDecimal());
						obj4.setFeeAmt(
								pdcash.get("cs_lfeepd").getAsBigDecimal());
						// obj4.setFy();
						taxPaymentList.add(obj4);
					}
				}

			}

			pd_itc = tax_pmt.has("pditc") ? tax_pmt.getAsJsonObject("pditc")
					: null;

			if (pd_itc != null) {
				Gstr3bTaxPaymentDto obj1 = new Gstr3bTaxPaymentDto();
				obj1.setLiabilityLedgerId(
						pd_itc.get("liab_ldg_id").getAsLong());
				obj1.setSubSection("pditc");
				obj1.setTransactionType(pd_itc.get("trans_typ").getAsString());
				obj1.setTaxType("I");
				obj1.setPaidUsingIgst(pd_itc.get("i_pdi").getAsBigDecimal());
				obj1.setPaidUsingCgst(pd_itc.get("i_pdc").getAsBigDecimal());
				obj1.setPaidUsingSgst(pd_itc.get("i_pds").getAsBigDecimal());
				// obj.setFy();
				taxPaymentList.add(obj1);

				Gstr3bTaxPaymentDto obj2 = new Gstr3bTaxPaymentDto();
				obj2.setLiabilityLedgerId(
						pd_itc.get("liab_ldg_id").getAsLong());
				obj2.setSubSection("pditc");
				obj2.setTransactionType(pd_itc.get("trans_typ").getAsString());
				obj2.setTaxType("C");
				obj2.setPaidUsingIgst(pd_itc.get("c_pdi").getAsBigDecimal());
				obj2.setPaidUsingCgst(pd_itc.get("c_pdc").getAsBigDecimal());
				// obj2.setFy();
				taxPaymentList.add(obj2);

				Gstr3bTaxPaymentDto obj3 = new Gstr3bTaxPaymentDto();
				obj3.setLiabilityLedgerId(
						pd_itc.get("liab_ldg_id").getAsLong());
				obj3.setSubSection("pditc");
				obj3.setTransactionType(pd_itc.get("trans_typ").getAsString());
				obj3.setTaxType("S");
				obj3.setPaidUsingIgst(pd_itc.get("s_pdi").getAsBigDecimal());
				obj2.setPaidUsingSgst(pd_itc.get("s_pds").getAsBigDecimal());
				// obj3.setFy();
				taxPaymentList.add(obj3);

				Gstr3bTaxPaymentDto obj4 = new Gstr3bTaxPaymentDto();
				obj4.setLiabilityLedgerId(
						pd_itc.get("liab_ldg_id").getAsLong());
				obj4.setSubSection("pditc");
				obj4.setTransactionType(pd_itc.get("trans_typ").getAsString());
				obj4.setTaxType("CS");
				obj4.setPaidUsingCess(pd_itc.get("cs_pdcs").getAsBigDecimal());
				// obj4.setFy();
				taxPaymentList.add(obj4);
			}

		}

		return taxPaymentList;
	}

	private Gstr3BInterStateSuppliesDto addInwards(
			Gstr3BInterStateSuppliesDto a, Gstr3BInterStateSuppliesDto b) {
		Gstr3BInterStateSuppliesDto dto = new Gstr3BInterStateSuppliesDto();

		dto.setSectionName(b.getSectionName());
		dto.setSubSectionName(b.getSubSectionName());

		dto.setPos(b.getPos() != null ? b.getPos() : a.getPos());
		if (dto.getPos() != null && dto.getPos() != "0") {
			dto.setCess(a.getCess().add(b.getCess()));
			dto.setCgst(a.getCgst().add(b.getCgst()));
			dto.setIgst(a.getIgst().add(b.getIgst()));
			dto.setSgst(a.getSgst().add(b.getSgst()));
			dto.setTaxableVal(a.getTaxableVal().add(b.getTaxableVal()));
		}

		dto.setPosAutoCal(b.getPosAutoCal() != null ? b.getPosAutoCal()
				: a.getPosAutoCal());
		dto.setIgstAutoCal(a.getIgstAutoCal().add(b.getIgstAutoCal()));
		dto.setTaxableValAutoCal(
				a.getTaxableValAutoCal().add(b.getTaxableValAutoCal()));

		dto.setPosCompute(b.getPosCompute() != null ? b.getPosCompute()
				: a.getPosCompute());
		dto.setIgstCompute(a.getIgstCompute().add(b.getIgstCompute()));
		dto.setTaxableValCompute(
				a.getTaxableValCompute().add(b.getTaxableValCompute()));

		return dto;
	}

	// @Override
	public List<Gstr3BGstinAspUserInputDto> getUserInterStateSupplies(
			String taxPeriod, String gstin) {

		final List<String> sections = ImmutableList.of(
				Gstr3BConstants.Table3_2_A, Gstr3BConstants.Table3_2_B,
				Gstr3BConstants.Table3_2_C);

		List<Gstr3BGstinAspUserInputDto> respUserList = new ArrayList<>();

		try {
			respUserList = gstnDao.getUserInputDtoBySection(taxPeriod, gstin,
					sections);

		} catch (Exception ex) {
			String msg = String.format("error occured while invoking "
					+ "getInterStateSupplies() ", ex);
			throw new AppException(msg);
		}

		return respUserList;

	}

	public static String getClobString(Clob clob) throws Exception {
		BufferedReader stringReader = new BufferedReader(
				clob.getCharacterStream());
		String singleLine = null;
		StringBuffer strBuff = new StringBuffer();
		while ((singleLine = stringReader.readLine()) != null) {
			strBuff.append(singleLine);
		}
		return strBuff.toString();
	}
}