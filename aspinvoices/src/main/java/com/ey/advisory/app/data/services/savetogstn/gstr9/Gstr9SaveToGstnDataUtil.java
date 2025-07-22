package com.ey.advisory.app.data.services.savetogstn.gstr9;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr9.Gstr9GetSummaryEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnProcessEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9UserInputEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetSummaryRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9HsnProcessedRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9UserInputRepository;
import com.ey.advisory.app.data.services.gstr9.GSTR9InwardConstants;
import com.ey.advisory.app.data.services.gstr9.Gstr9InwardUtil;
import com.ey.advisory.app.data.services.gstr9.Gstr9OtherConstants;
import com.ey.advisory.app.data.services.gstr9.Gstr9PyTransInCyConstants;
import com.ey.advisory.app.data.services.gstr9.Gstr9TaxPaidConstants;
import com.ey.advisory.app.docs.dto.gstr9.*;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTR9Constants;
import com.ey.advisory.common.GenUtil;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("Gstr9SaveToGstnDataUtil")
public class Gstr9SaveToGstnDataUtil {

	@Autowired
	private Gstr9UserInputRepository gstr9UserInputRepository;

	@Autowired
	@Qualifier("Gstr9GetSummaryRepository")
	private Gstr9GetSummaryRepository gstr9GetSummaryRepository;

	@Autowired
	private Gstr9HsnProcessedRepository gstr9HsnProcessedRepository;

	private static final ImmutableList<String> SECTION6B_LIST = ImmutableList
			.of("6B1", "6B2", "6B3");

	private static final ImmutableList<String> SECTION6C_LIST = ImmutableList
			.of("6C1", "6C2", "6C3");

	private static final ImmutableList<String> SECTION6D_LIST = ImmutableList
			.of("6D1", "6D2", "6D3");

	private static final ImmutableList<String> SECTION6E_LIST = ImmutableList
			.of("6E1", "6E2");

	public GetDetailsForGstr9ReqDto populateGstr9SavePayload(String gstin,
			String fy, String isDigigst) {

		String retPeriod = Gstr9InwardUtil.getTaxperiodFromFY(fy);
		String formattedFY = GenUtil.getFormattedFy(fy);

		GetDetailsForGstr9ReqDto dto = null;
		Gstr9Table4ReqDto table4ReqDto = null;
		Gstr9Table5ReqDto table5ReqDto = null;
		Gstr9Table6ReqDto table6ReqDto = null;
		Gstr9Table7ReqDto table7ReqDto = null;
		Gstr9Table8ReqDto table8ReqDto = null;
		Gstr9Table9ReqDto table9ReqDto = null;
		Gstr9Table10ReqDto table10ReqDto = null;
		Gstr9Table14ReqDto table14ReqDto = null;
		Gstr9Table15ReqDto table15ReqDto = null;
		Gstr9Table16ReqDto table16ReqDto = null;
		Gstr9Table17ReqDto table17ReqDto = null;
		Gstr9Table18ReqDto table18ReqDto = null;

		Gstr9Table14IamtReqDto gstr9Table14IamtReqDto = null;
		Gstr9Table14SamtReqDto gstr9Table14SamtReqDto = null;
		Gstr9Table14CamtReqDto gstr9Table14CamtReqDto = null;
		Gstr9Table14CSamtReqDto gstr9Table14CSamtReqDto = null;
		Gstr9Table14IntrReqDto gstr9Table14IntrReqDto = null;
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Gstr9SaveToGstnDataUtil isDigigst : %s ",isDigigst);
			LOGGER.debug(msg);
		}

		if (isDigigst.equalsIgnoreCase("false")) {

			try {

				List<Gstr9GetSummaryEntity> gstr9UserInputEntityList = gstr9GetSummaryRepository
						.findByGstinAndFyAndIsActiveTrue(gstin, formattedFY);

				List<Gstr9GetSummaryEntity> gstr9HsnProcessEntityList = gstr9GetSummaryRepository
						.listAllGstr9ProcessedDetailsGet(gstin, formattedFY);
				
				if (gstr9UserInputEntityList != null) {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"List<Gstr9UserInputEntity> length %d",
								gstr9UserInputEntityList.size());
						LOGGER.debug(msg);
					}
				}
				
				if (gstr9HsnProcessEntityList != null) {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"List<Gstr9HsnProcessEntity> length %d",
								gstr9HsnProcessEntityList.size());
						LOGGER.debug(msg);
					}
				}

				if (!gstr9UserInputEntityList.isEmpty()) {

					dto = new GetDetailsForGstr9ReqDto();
					dto.setGstin(gstin);
					dto.setFp(retPeriod);

					for (Gstr9GetSummaryEntity e : gstr9UserInputEntityList) {
						
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Get Section from Gstr9GetSummaryEntity %s",
									e.getSection());
							LOGGER.debug(msg);
						}

						if (e.getSection().equals(GSTR9Constants.Table_4)) {
							table4ReqDto = convertGetEntityToTable4Dto(e,
									table4ReqDto);

						} else if (e.getSection()
								.equals(GSTR9Constants.Table_5)) {
							table5ReqDto = convertGetEntityToTable5Dto(e,
									table5ReqDto);
						} else if (e.getSection()
								.equals(GSTR9InwardConstants.Table_6)) {
							table6ReqDto = convertGetEntityToTable6Dto(e,
									table6ReqDto);

						} else if (e.getSection()
								.equals(GSTR9InwardConstants.Table_7)) {
							table7ReqDto = convertGetEntityToTable7Dto(e,
									table7ReqDto);

						} else if (e.getSection()
								.equals(GSTR9InwardConstants.Table_8)) {
							table8ReqDto = convertGetEntityToTable8Dto(e,
									table8ReqDto);

						} else if (e.getSection()
								.equals(Gstr9TaxPaidConstants.Table_9)) {
							table9ReqDto = convertGetEntityToTable9Dto(e,
									table9ReqDto);

						} else if (e.getSection()
								.equals(Gstr9PyTransInCyConstants.Table_10)
								|| e.getSection()
										.equals(Gstr9PyTransInCyConstants.Table_11)
								|| e.getSection()
										.equals(Gstr9PyTransInCyConstants.Table_12)
								|| e.getSection().equals(
										Gstr9PyTransInCyConstants.Table_13)) {
							table10ReqDto = convertGetEntityToTable10Dto(e,
									table10ReqDto);

						} else if (e.getSection()
								.equals(Gstr9OtherConstants.Table_14)) {
							table14ReqDto = convertGetEntityToTable14Dto(e,
									table14ReqDto, gstr9Table14IamtReqDto,
									gstr9Table14SamtReqDto,
									gstr9Table14CamtReqDto,
									gstr9Table14CSamtReqDto,
									gstr9Table14IntrReqDto);

						} else if (e.getSection()
								.equals(Gstr9OtherConstants.Table_15)) {
							table15ReqDto = convertGetEntityToTable15Dto(e,
									table15ReqDto);

						} else if (e.getSection()
								.equals(Gstr9OtherConstants.Table_16)) {
							table16ReqDto = convertGetEntityToTable16Dto(e,
									table16ReqDto);

						}

					}
				}
				if (!gstr9HsnProcessEntityList.isEmpty()) {

					if (dto == null)
						dto = new GetDetailsForGstr9ReqDto();

					dto.setGstin(gstin);
					dto.setFp(retPeriod);
					List<Gstr9GetSummaryEntity> hsnOutwardList = segregateGetHsnInwardOutward(
							gstr9HsnProcessEntityList,
							Gstr9OtherConstants.Table_17);

					List<Gstr9GetSummaryEntity> hsnInwardList = segregateGetHsnInwardOutward(
							gstr9HsnProcessEntityList,
							Gstr9OtherConstants.Table_18);

					for (Gstr9GetSummaryEntity hsnOutward : hsnOutwardList) {
						table17ReqDto = convertGetEntityToTable17Dto(hsnOutward,
								table17ReqDto);
					}

					for (Gstr9GetSummaryEntity hsnInward : hsnInwardList) {
						table18ReqDto = convertGetEntityToTable18Dto(hsnInward,
								table18ReqDto);
					}

				}

				dto.setTable4ReqDto(table4ReqDto);
				dto.setTable5ReqDto(table5ReqDto);
				dto.setTable6ReqDto(table6ReqDto);
				dto.setTable7ReqDto(table7ReqDto);
				dto.setTable8ReqDto(table8ReqDto);
//				dto.setTable9ReqDto(table9ReqDto);
				if(table9ReqDto!=null){
					dto.setTable9ReqDto(table9ReqDto);
				}else{
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Section 9 not found hence initializing default values");
						LOGGER.debug(msg);
					}
					dto.setTable9ReqDto(initializeTable9Dto());
				}
				dto.setTable10ReqDto(table10ReqDto);
//				dto.setTable14ReqDto(table14ReqDto);
				if(table14ReqDto!=null){
					dto.setTable14ReqDto(table14ReqDto);
				}else{
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Section 14 not found hence initializing default values");
						LOGGER.debug(msg);
					}
					dto.setTable14ReqDto(initializeTable14Dto());
				}
				dto.setTable15ReqDto(table15ReqDto);
				dto.setTable16ReqDto(table16ReqDto);
				dto.setTable17ReqDto(table17ReqDto);
				dto.setTable18ReqDto(table18ReqDto);

			} catch (Exception ex) {
				String msg = "Unexpected error while forming the Gstr9 Get Call data";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}

		} else {
			try {

				List<Gstr9UserInputEntity> gstr9UserInputEntityList = gstr9UserInputRepository
						.findByGstinAndFyAndIsActiveTrue(gstin, formattedFY);

				List<Gstr9HsnProcessEntity> gstr9HsnProcessEntityList = gstr9HsnProcessedRepository
						.listAllGstr9ProcessedDetails(gstin, formattedFY);

				if (gstr9UserInputEntityList != null) {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"List<Gstr9UserInputEntity> length %d",
								gstr9UserInputEntityList.size());
						LOGGER.debug(msg);
					}
				}
				
				if (gstr9HsnProcessEntityList != null) {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"List<Gstr9HsnProcessEntity> length %d",
								gstr9HsnProcessEntityList.size());
						LOGGER.debug(msg);
					}
				}

				if (!gstr9UserInputEntityList.isEmpty()) {

					dto = new GetDetailsForGstr9ReqDto();
					dto.setGstin(gstin);
					dto.setFp(retPeriod);

					for (Gstr9UserInputEntity e : gstr9UserInputEntityList) {
						
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Get Section from Gstr9UserInputEntity %s",
									e.getSection());
							LOGGER.debug(msg);
						}

						if (e.getSection().equals(GSTR9Constants.Table_4)) {
							table4ReqDto = convertEntityToTable4Dto(e,
									table4ReqDto);

						} else if (e.getSection()
								.equals(GSTR9Constants.Table_5)) {
							table5ReqDto = convertEntityToTable5Dto(e,
									table5ReqDto);
						} else if (e.getSection()
								.equals(GSTR9InwardConstants.Table_6)) {
							table6ReqDto = convertEntityToTable6Dto(e,
									table6ReqDto);

						} else if (e.getSection()
								.equals(GSTR9InwardConstants.Table_7)) {
							table7ReqDto = convertEntityToTable7Dto(e,
									table7ReqDto);

						} else if (e.getSection()
								.equals(GSTR9InwardConstants.Table_8)) {
							table8ReqDto = convertEntityToTable8Dto(e,
									table8ReqDto);

						} else if (e.getSection()
								.equals(Gstr9TaxPaidConstants.Table_9)) {
							table9ReqDto = convertEntityToTable9Dto(e,
									table9ReqDto);

						} else if (e.getSection()
								.equals(Gstr9PyTransInCyConstants.Table_10)
								|| e.getSection()
										.equals(Gstr9PyTransInCyConstants.Table_11)
								|| e.getSection()
										.equals(Gstr9PyTransInCyConstants.Table_12)
								|| e.getSection().equals(
										Gstr9PyTransInCyConstants.Table_13)) {
							table10ReqDto = convertEntityToTable10Dto(e,
									table10ReqDto);

						} else if (e.getSection()
								.equals(Gstr9OtherConstants.Table_14)) {
							table14ReqDto = convertEntityToTable14Dto(e,
									table14ReqDto, gstr9Table14IamtReqDto,
									gstr9Table14SamtReqDto,
									gstr9Table14CamtReqDto,
									gstr9Table14CSamtReqDto,
									gstr9Table14IntrReqDto, false);

						} else if (e.getSection()
								.equals(Gstr9OtherConstants.Table_15)) {
							table15ReqDto = convertEntityToTable15Dto(e,
									table15ReqDto);

						} else if (e.getSection()
								.equals(Gstr9OtherConstants.Table_16)) {
							table16ReqDto = convertEntityToTable16Dto(e,
									table16ReqDto);

						}

					}
				}
				if (!gstr9HsnProcessEntityList.isEmpty()) {

					if (dto == null)
						dto = new GetDetailsForGstr9ReqDto();

					dto.setGstin(gstin);
					dto.setFp(retPeriod);
					List<Gstr9HsnProcessEntity> hsnOutwardList = segregateHsnInwardOutward(
							gstr9HsnProcessEntityList,
							Gstr9OtherConstants.Table_17);

					List<Gstr9HsnProcessEntity> hsnInwardList = segregateHsnInwardOutward(
							gstr9HsnProcessEntityList,
							Gstr9OtherConstants.Table_18);

					for (Gstr9HsnProcessEntity hsnOutward : hsnOutwardList) {
						table17ReqDto = convertEntityToTable17Dto(hsnOutward,
								table17ReqDto);
					}

					for (Gstr9HsnProcessEntity hsnInward : hsnInwardList) {
						table18ReqDto = convertEntityToTable18Dto(hsnInward,
								table18ReqDto);
					}

				}

				dto.setTable4ReqDto(table4ReqDto);
				dto.setTable5ReqDto(table5ReqDto);
				dto.setTable6ReqDto(table6ReqDto);
				dto.setTable7ReqDto(table7ReqDto);
				dto.setTable8ReqDto(table8ReqDto);
//				dto.setTable9ReqDto(table9ReqDto);
				if(table9ReqDto!=null){
					dto.setTable9ReqDto(table9ReqDto);
				}else{
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Section 9 not found hence initializing default values");
						LOGGER.debug(msg);
					}
					dto.setTable9ReqDto(initializeTable9Dto());
				}
				dto.setTable10ReqDto(table10ReqDto);
//				dto.setTable14ReqDto(table14ReqDto);
				if(table14ReqDto!=null){
					dto.setTable14ReqDto(table14ReqDto);
				}else{
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Section 14 not found hence initializing default values");
						LOGGER.debug(msg);
					}
					dto.setTable14ReqDto(initializeTable14Dto());
				}
				dto.setTable15ReqDto(table15ReqDto);
				dto.setTable16ReqDto(table16ReqDto);
				dto.setTable17ReqDto(table17ReqDto);
				dto.setTable18ReqDto(table18ReqDto);

			} catch (Exception ex) {
				String msg = "Unexpected error while forming the Save Gstr9 Payload";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}

		}

		return dto;

	}

	private List<Gstr9HsnProcessEntity> segregateHsnInwardOutward(
			List<Gstr9HsnProcessEntity> gstr9HsnProcessEntityList,
			String tabelType) {

		return gstr9HsnProcessEntityList.stream()
				.filter(e -> e.getTableNumber().equals(tabelType))
				.map(this::setUQCAsNA)
				.collect(Collectors.groupingBy(this::createKey,
						Collectors.collectingAndThen(
								Collectors.reducing(this::addDto),
								Optional::get)))
				.values().stream()
				.collect(Collectors.toCollection(ArrayList::new));

	}

	private List<Gstr9GetSummaryEntity> segregateGetHsnInwardOutward(
			List<Gstr9GetSummaryEntity> gstr9HsnProcessEntityList,
			String tabelType) {

		return gstr9HsnProcessEntityList.stream()
				.filter(e -> e.getSection().equals(tabelType))
				.map(this::setUQCAsNAGet)
				.collect(Collectors.groupingBy(this::createKeyGet,
						Collectors.collectingAndThen(
								Collectors.reducing(this::addDtoGet),
								Optional::get)))
				.values().stream()
				.collect(Collectors.toCollection(ArrayList::new));

	}

	private Gstr9HsnProcessEntity addDto(Gstr9HsnProcessEntity a,
			Gstr9HsnProcessEntity b) {
		Gstr9HsnProcessEntity dto = new Gstr9HsnProcessEntity();
		dto.setCess(defaultToZeroIfNull(a.getCess())
				.add(defaultToZeroIfNull(b.getCess())));
		dto.setCgst(defaultToZeroIfNull(a.getCgst())
				.add(defaultToZeroIfNull(b.getCgst())));
		dto.setIgst(defaultToZeroIfNull(a.getIgst())
				.add(defaultToZeroIfNull(b.getIgst())));
		dto.setSgst(defaultToZeroIfNull(a.getSgst())
				.add(defaultToZeroIfNull(b.getSgst())));
		dto.setTaxableVal(defaultToZeroIfNull(a.getTaxableVal())
				.add(defaultToZeroIfNull(b.getTaxableVal())));
		dto.setTotalQnt(defaultToZeroIfNull(a.getTotalQnt())
				.add(defaultToZeroIfNull(b.getTotalQnt())));
		dto.setHsn(a.getHsn());
		dto.setRateOfTax(a.getRateOfTax());
		dto.setDesc(a.getDesc() != null ? a.getDesc() : b.getDesc());
		dto.setUqc(a.getUqc());
		dto.setConRateFlag("N");
		dto.setTableNumber(a.getTableNumber());
		return dto;
	}

	private Gstr9GetSummaryEntity addDtoGet(Gstr9GetSummaryEntity a,
			Gstr9GetSummaryEntity b) {
		Gstr9GetSummaryEntity dto = new Gstr9GetSummaryEntity();
		dto.setCsamt(defaultToZeroIfNull(a.getCsamt())
				.add(defaultToZeroIfNull(b.getCsamt())));
		dto.setCamt(defaultToZeroIfNull(a.getCamt())
				.add(defaultToZeroIfNull(b.getCamt())));
		dto.setIamt(defaultToZeroIfNull(a.getIamt())
				.add(defaultToZeroIfNull(b.getIamt())));
		dto.setSamt(defaultToZeroIfNull(a.getSamt())
				.add(defaultToZeroIfNull(b.getSamt())));
		dto.setTxVal(defaultToZeroIfNull(a.getTxVal())
				.add(defaultToZeroIfNull(b.getTxVal())));
		dto.setQty(defaultToZeroIfNull(a.getQty())
				.add(defaultToZeroIfNull(b.getQty())));
		dto.setHsnSc(a.getHsnSc());
		dto.setRt(a.getRt());
		dto.setDesc(a.getDesc() != null ? a.getDesc() : b.getDesc());
		dto.setUqc(a.getUqc());
		dto.setIsConcesstional("N");
		dto.setSection(a.getSection());
		return dto;
	}

	private String createKey(Gstr9HsnProcessEntity e) {

		String taxRate = e.getRateOfTax() != null ? e.getRateOfTax().toString()
				: "0";
		String key = e.getHsn() + "-" + taxRate + "-" + e.getUqc();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Craeting key {} for HSN {} and taxRate{}", key,
					e.getHsn(), taxRate);
		}

		return key;
	}

	private String createKeyGet(Gstr9GetSummaryEntity e) {

		String taxRate = e.getRt() != null ? e.getRt().toString() : "0";
		String key = e.getHsnSc() + "-" + taxRate + "-" + e.getUqc();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Craeting key {} for HSN {} and taxRate{}", key,
					e.getHsnSc(), taxRate);
		}

		return key;
	}

	private Gstr9HsnProcessEntity setUQCAsNA(Gstr9HsnProcessEntity e) {

		if (e.getHsn().substring(0, 2).equals("99")) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Setting UQC as NA for HSN {}", e.getHsn());
			}
			e.setUqc("NA");
			return e;
		} else {
			return e;
		}
	}

	private Gstr9GetSummaryEntity setUQCAsNAGet(Gstr9GetSummaryEntity e) {

		if (e.getHsnSc().substring(0, 2).equals("99")) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Setting UQC as NA for HSN {}", e.getHsnSc());
			}
			e.setUqc("NA");
			return e;
		} else {
			return e;
		}
	}

	private BigDecimal defaultToZeroIfNull(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

	private Gstr9Table4ReqDto convertEntityToTable4Dto(Gstr9UserInputEntity e,
			Gstr9Table4ReqDto table4ReqDto) {

		if (table4ReqDto == null) {
			table4ReqDto = new Gstr9Table4ReqDto();

			Gstr9Table4B2CReqDto table4B2CReqDto = new Gstr9Table4B2CReqDto();
			Gstr9Table4B2BReqDto table4B2BReqDto = new Gstr9Table4B2BReqDto();
			Gstr9Table4ExpReqDto table4ExpReqDto = new Gstr9Table4ExpReqDto();
			Gstr9Table4SezReqDto table4SezReqDto = new Gstr9Table4SezReqDto();
			Gstr9Table4DeemedReqDto table4DeemedReqDto = new Gstr9Table4DeemedReqDto();
			Gstr9Table4AtReqDto table4AtReqDto = new Gstr9Table4AtReqDto();
			Gstr9Table4RchrgReqDto table4RchrgReqDto = new Gstr9Table4RchrgReqDto();
			Gstr9Table4RchrgReqDto table4RchrgG1ReqDto = new Gstr9Table4RchrgReqDto();
			Gstr9Table4CrNtReqDto table4CrntReqDto = new Gstr9Table4CrNtReqDto();
			Gstr9Table4DrNtReqDto table4drntReqDto = new Gstr9Table4DrNtReqDto();
			Gstr9Table4AmdPosReqDto table4AmdPosReqDto = new Gstr9Table4AmdPosReqDto();
			Gstr9Table4AmdNegReqDto table4AmdNegReqDto = new Gstr9Table4AmdNegReqDto();
			// Gstr9Table4SubTotalAGReqDto table4SubTotalAGReqDto = new
			// Gstr9Table4SubTotalAGReqDto();
			// Gstr9Table4SubTotalILReqDto table4SubTotalILReqDto = new
			// Gstr9Table4SubTotalILReqDto();
			// Gstr9Table4SubAdvReqDto table4SubAdvReqDto = new
			// Gstr9Table4SubAdvReqDto();

			// Setting the variables in the table4ReqDto object
			table4ReqDto.setTable4B2CReqDto(table4B2CReqDto);
			table4ReqDto.setTable4B2BReqDto(table4B2BReqDto);
			table4ReqDto.setTable4ExpReqDto(table4ExpReqDto);
			table4ReqDto.setTable4SezReqDto(table4SezReqDto);
			table4ReqDto.setTable4DeemedReqDto(table4DeemedReqDto);
			table4ReqDto.setTable4AtReqDto(table4AtReqDto);
			table4ReqDto.setTable4RchrgReqDto(table4RchrgReqDto);
			table4ReqDto.setTable4RchrgG1ReqDto(table4RchrgG1ReqDto);
			table4ReqDto.setTable4CrntReqDto(table4CrntReqDto);
			table4ReqDto.setTable4drntReqDto(table4drntReqDto);
			table4ReqDto.setTable4AmdPosReqDto(table4AmdPosReqDto);
			table4ReqDto.setTable4AmdNegReqDto(table4AmdNegReqDto);
			// table4ReqDto.setTable4SubTotalAGReqDto(table4SubTotalAGReqDto);
			// table4ReqDto.setTable4SubTotalILReqDto(table4SubTotalILReqDto);
			// table4ReqDto.setTable4SubAdvReqDto(table4SubAdvReqDto);

		}

		if (e.getSubSection().equalsIgnoreCase(GSTR9Constants.Table_4A)) {
			Gstr9Table4B2CReqDto gstr9Table4B2CReqDto = new Gstr9Table4B2CReqDto();
			gstr9Table4B2CReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4B2CReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table4B2CReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table4B2CReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table4B2CReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			table4ReqDto.setTable4B2CReqDto(gstr9Table4B2CReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4B)) {
			Gstr9Table4B2BReqDto gstr9Table4B2BReqDto = new Gstr9Table4B2BReqDto();
			gstr9Table4B2BReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4B2BReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table4B2BReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table4B2BReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table4B2BReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			table4ReqDto.setTable4B2BReqDto(gstr9Table4B2BReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4C)) {
			Gstr9Table4ExpReqDto gstr9Table4ExpReqDto = new Gstr9Table4ExpReqDto();
			gstr9Table4ExpReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4ExpReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table4ExpReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			table4ReqDto.setTable4ExpReqDto(gstr9Table4ExpReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4D)) {
			Gstr9Table4SezReqDto gstr9Table4SezReqDto = new Gstr9Table4SezReqDto();
			gstr9Table4SezReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4SezReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table4SezReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			table4ReqDto.setTable4SezReqDto(gstr9Table4SezReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4E)) {
			Gstr9Table4DeemedReqDto gstr9Table4DeemedReqDto = new Gstr9Table4DeemedReqDto();
			gstr9Table4DeemedReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4DeemedReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table4DeemedReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table4DeemedReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table4DeemedReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			table4ReqDto.setTable4DeemedReqDto(gstr9Table4DeemedReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4F)) {
			Gstr9Table4AtReqDto gstr9Table4AtReqDto = new Gstr9Table4AtReqDto();
			gstr9Table4AtReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4AtReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table4AtReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table4AtReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table4AtReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			table4ReqDto.setTable4AtReqDto(gstr9Table4AtReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4G)) {
			Gstr9Table4RchrgReqDto gstr9Table4RchrgReqDto = new Gstr9Table4RchrgReqDto();
			gstr9Table4RchrgReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4RchrgReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table4RchrgReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table4RchrgReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table4RchrgReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			table4ReqDto.setTable4RchrgReqDto(gstr9Table4RchrgReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4G1)) {
			Gstr9Table4RchrgReqDto gstr9Table4RchrgG1ReqDto = new Gstr9Table4RchrgReqDto();
			gstr9Table4RchrgG1ReqDto
					.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4RchrgG1ReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table4RchrgG1ReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table4RchrgG1ReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table4RchrgG1ReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			table4ReqDto.setTable4RchrgG1ReqDto(gstr9Table4RchrgG1ReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4I)) {
			Gstr9Table4CrNtReqDto gstr9Table4CrNtReqDto = new Gstr9Table4CrNtReqDto();
			gstr9Table4CrNtReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4CrNtReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table4CrNtReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table4CrNtReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table4CrNtReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			table4ReqDto.setTable4CrntReqDto(gstr9Table4CrNtReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4J)) {
			Gstr9Table4DrNtReqDto gstr9Table4DrNtReqDto = new Gstr9Table4DrNtReqDto();
			gstr9Table4DrNtReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4DrNtReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table4DrNtReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table4DrNtReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table4DrNtReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			table4ReqDto.setTable4drntReqDto(gstr9Table4DrNtReqDto);
		}

		else if (e.getSubSection().equalsIgnoreCase(GSTR9Constants.Table_4K)) {
			Gstr9Table4AmdPosReqDto gstr9Table4AmdPosReqDto = new Gstr9Table4AmdPosReqDto();
			gstr9Table4AmdPosReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4AmdPosReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table4AmdPosReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table4AmdPosReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table4AmdPosReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			table4ReqDto.setTable4AmdPosReqDto(gstr9Table4AmdPosReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4L)) {
			Gstr9Table4AmdNegReqDto gstr9Table4AmdNegReqDto = new Gstr9Table4AmdNegReqDto();
			gstr9Table4AmdNegReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4AmdNegReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table4AmdNegReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table4AmdNegReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table4AmdNegReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			table4ReqDto.setTable4AmdNegReqDto(gstr9Table4AmdNegReqDto);
		}

		return table4ReqDto;
	}

	private Gstr9Table4ReqDto convertGetEntityToTable4Dto(
			Gstr9GetSummaryEntity e, Gstr9Table4ReqDto table4ReqDto) {

		if (table4ReqDto == null) {
			table4ReqDto = new Gstr9Table4ReqDto();

			Gstr9Table4B2CReqDto table4B2CReqDto = new Gstr9Table4B2CReqDto();
			Gstr9Table4B2BReqDto table4B2BReqDto = new Gstr9Table4B2BReqDto();
			Gstr9Table4ExpReqDto table4ExpReqDto = new Gstr9Table4ExpReqDto();
			Gstr9Table4SezReqDto table4SezReqDto = new Gstr9Table4SezReqDto();
			Gstr9Table4DeemedReqDto table4DeemedReqDto = new Gstr9Table4DeemedReqDto();
			Gstr9Table4AtReqDto table4AtReqDto = new Gstr9Table4AtReqDto();
			Gstr9Table4RchrgReqDto table4RchrgReqDto = new Gstr9Table4RchrgReqDto();
			Gstr9Table4RchrgReqDto table4RchrgG1ReqDto = new Gstr9Table4RchrgReqDto();
			Gstr9Table4CrNtReqDto table4CrntReqDto = new Gstr9Table4CrNtReqDto();
			Gstr9Table4DrNtReqDto table4drntReqDto = new Gstr9Table4DrNtReqDto();
			Gstr9Table4AmdPosReqDto table4AmdPosReqDto = new Gstr9Table4AmdPosReqDto();
			Gstr9Table4AmdNegReqDto table4AmdNegReqDto = new Gstr9Table4AmdNegReqDto();
			// Gstr9Table4SubTotalAGReqDto table4SubTotalAGReqDto = new
			// Gstr9Table4SubTotalAGReqDto();
			// Gstr9Table4SubTotalILReqDto table4SubTotalILReqDto = new
			// Gstr9Table4SubTotalILReqDto();
			// Gstr9Table4SubAdvReqDto table4SubAdvReqDto = new
			// Gstr9Table4SubAdvReqDto();

			// Setting the variables in the table4ReqDto object
			table4ReqDto.setTable4B2CReqDto(table4B2CReqDto);
			table4ReqDto.setTable4B2BReqDto(table4B2BReqDto);
			table4ReqDto.setTable4ExpReqDto(table4ExpReqDto);
			table4ReqDto.setTable4SezReqDto(table4SezReqDto);
			table4ReqDto.setTable4DeemedReqDto(table4DeemedReqDto);
			table4ReqDto.setTable4AtReqDto(table4AtReqDto);
			table4ReqDto.setTable4RchrgReqDto(table4RchrgReqDto);
			table4ReqDto.setTable4RchrgG1ReqDto(table4RchrgG1ReqDto);
			table4ReqDto.setTable4CrntReqDto(table4CrntReqDto);
			table4ReqDto.setTable4drntReqDto(table4drntReqDto);
			table4ReqDto.setTable4AmdPosReqDto(table4AmdPosReqDto);
			table4ReqDto.setTable4AmdNegReqDto(table4AmdNegReqDto);
			// table4ReqDto.setTable4SubTotalAGReqDto(table4SubTotalAGReqDto);
			// table4ReqDto.setTable4SubTotalILReqDto(table4SubTotalILReqDto);
			// table4ReqDto.setTable4SubAdvReqDto(table4SubAdvReqDto);

		}

		if (e.getSubSection().equalsIgnoreCase(GSTR9Constants.Table_4A)) {
			Gstr9Table4B2CReqDto gstr9Table4B2CReqDto = new Gstr9Table4B2CReqDto();
			gstr9Table4B2CReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4B2CReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table4B2CReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table4B2CReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table4B2CReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			table4ReqDto.setTable4B2CReqDto(gstr9Table4B2CReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4B)) {
			Gstr9Table4B2BReqDto gstr9Table4B2BReqDto = new Gstr9Table4B2BReqDto();
			gstr9Table4B2BReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4B2BReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table4B2BReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table4B2BReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table4B2BReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			table4ReqDto.setTable4B2BReqDto(gstr9Table4B2BReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4C)) {
			Gstr9Table4ExpReqDto gstr9Table4ExpReqDto = new Gstr9Table4ExpReqDto();
			gstr9Table4ExpReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4ExpReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table4ExpReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			table4ReqDto.setTable4ExpReqDto(gstr9Table4ExpReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4D)) {
			Gstr9Table4SezReqDto gstr9Table4SezReqDto = new Gstr9Table4SezReqDto();
			gstr9Table4SezReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4SezReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table4SezReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			table4ReqDto.setTable4SezReqDto(gstr9Table4SezReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4E)) {
			Gstr9Table4DeemedReqDto gstr9Table4DeemedReqDto = new Gstr9Table4DeemedReqDto();
			gstr9Table4DeemedReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4DeemedReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table4DeemedReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table4DeemedReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table4DeemedReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			table4ReqDto.setTable4DeemedReqDto(gstr9Table4DeemedReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4F)) {
			Gstr9Table4AtReqDto gstr9Table4AtReqDto = new Gstr9Table4AtReqDto();
			gstr9Table4AtReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4AtReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table4AtReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table4AtReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table4AtReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			table4ReqDto.setTable4AtReqDto(gstr9Table4AtReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4G)) {
			Gstr9Table4RchrgReqDto gstr9Table4RchrgReqDto = new Gstr9Table4RchrgReqDto();
			gstr9Table4RchrgReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4RchrgReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table4RchrgReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table4RchrgReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table4RchrgReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			table4ReqDto.setTable4RchrgReqDto(gstr9Table4RchrgReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4G1)) {
			Gstr9Table4RchrgReqDto gstr9Table4RchrgG1ReqDto = new Gstr9Table4RchrgReqDto();
			gstr9Table4RchrgG1ReqDto
					.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4RchrgG1ReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table4RchrgG1ReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table4RchrgG1ReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table4RchrgG1ReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			table4ReqDto.setTable4RchrgReqDto(gstr9Table4RchrgG1ReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4I)) {
			Gstr9Table4CrNtReqDto gstr9Table4CrNtReqDto = new Gstr9Table4CrNtReqDto();
			gstr9Table4CrNtReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4CrNtReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table4CrNtReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table4CrNtReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table4CrNtReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			table4ReqDto.setTable4CrntReqDto(gstr9Table4CrNtReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4J)) {
			Gstr9Table4DrNtReqDto gstr9Table4DrNtReqDto = new Gstr9Table4DrNtReqDto();
			gstr9Table4DrNtReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4DrNtReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table4DrNtReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table4DrNtReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table4DrNtReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			table4ReqDto.setTable4drntReqDto(gstr9Table4DrNtReqDto);
		}

		else if (e.getSubSection().equalsIgnoreCase(GSTR9Constants.Table_4K)) {
			Gstr9Table4AmdPosReqDto gstr9Table4AmdPosReqDto = new Gstr9Table4AmdPosReqDto();
			gstr9Table4AmdPosReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4AmdPosReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table4AmdPosReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table4AmdPosReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table4AmdPosReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			table4ReqDto.setTable4AmdPosReqDto(gstr9Table4AmdPosReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_4L)) {
			Gstr9Table4AmdNegReqDto gstr9Table4AmdNegReqDto = new Gstr9Table4AmdNegReqDto();
			gstr9Table4AmdNegReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table4AmdNegReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table4AmdNegReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table4AmdNegReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table4AmdNegReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			table4ReqDto.setTable4AmdNegReqDto(gstr9Table4AmdNegReqDto);
		}

		return table4ReqDto;
	}

	private Gstr9Table5ReqDto convertEntityToTable5Dto(Gstr9UserInputEntity e,
			Gstr9Table5ReqDto gstr9Table5ReqDto) {

		if (gstr9Table5ReqDto == null) {
			gstr9Table5ReqDto = new Gstr9Table5ReqDto();

			// Instantiate and initialize individual DTO objects
			Gstr9Table5ZeroRtdReqDto table5ZeroRtdReqDto = new Gstr9Table5ZeroRtdReqDto();
			Gstr9Table5SezReqDto table5SezReqDto = new Gstr9Table5SezReqDto();
			Gstr9Table5RchrgReqDto table5RchrgReqDto = new Gstr9Table5RchrgReqDto();
			Gstr9Table5RchrgReqDto table5C1RchrgReqDto = new Gstr9Table5RchrgReqDto();
			Gstr9Table5ExmtReqDto table5ExmtReqDto = new Gstr9Table5ExmtReqDto();
			Gstr9Table5NilReqDto table5NilReqDto = new Gstr9Table5NilReqDto();
			Gstr9Table5NonGstReqDto table5NonGstReqDto = new Gstr9Table5NonGstReqDto();
			Gstr9Table5CrNtReqDto table5CrNtReqDto = new Gstr9Table5CrNtReqDto();
			Gstr9Table5DbNtReqDto table5DbNtReqDto = new Gstr9Table5DbNtReqDto();
			Gstr9Table5AmdPosReqDto table5AmdPosReqDto = new Gstr9Table5AmdPosReqDto();
			Gstr9Table5AmdNegReqDto table5AmdNegReqDto = new Gstr9Table5AmdNegReqDto();
			// Gstr9Table5SubTotalAfReqDto table5SubTotalAfReqDto = new
			// Gstr9Table5SubTotalAfReqDto();
			// Gstr9Table5SubTotalHkReqDto table5SubTotalHkReqDto = new
			// Gstr9Table5SubTotalHkReqDto();
			// Gstr9Table5ToverTaxNpReqDto table5ToverTaxNpReqDto = new
			// Gstr9Table5ToverTaxNpReqDto();
			// Gstr9Table5TotalToverReqDto table5TotalToverReqDto = new
			// Gstr9Table5TotalToverReqDto();

			// Set the initialized objects to the table5ReqDto object using
			// setter methods
			gstr9Table5ReqDto.setTable5ZeroRtdReqDto(table5ZeroRtdReqDto);
			gstr9Table5ReqDto.setTable5SezReqDto(table5SezReqDto);
			gstr9Table5ReqDto.setTable5RchrgReqDto(table5RchrgReqDto);
			gstr9Table5ReqDto.setTable5C1RchrgReqDto(table5C1RchrgReqDto);
			gstr9Table5ReqDto.setTable5ExmtReqDto(table5ExmtReqDto);
			gstr9Table5ReqDto.setTable5NilReqDto(table5NilReqDto);
			gstr9Table5ReqDto.setTable5NonGstReqDto(table5NonGstReqDto);
			gstr9Table5ReqDto.setTable5CrNtReqDto(table5CrNtReqDto);
			gstr9Table5ReqDto.setTable5DbNtReqDto(table5DbNtReqDto);
			gstr9Table5ReqDto.setTable5AmdPosReqDto(table5AmdPosReqDto);
			gstr9Table5ReqDto.setTable5AmdNegReqDto(table5AmdNegReqDto);
			// gstr9Table5ReqDto.setTable5SubTotalAfReqDto(table5SubTotalAfReqDto);
			// gstr9Table5ReqDto.setTable5SubTotalHkReqDto(table5SubTotalHkReqDto);
			// gstr9Table5ReqDto.setTable5ToverTaxNpReqDto(table5ToverTaxNpReqDto);
			// gstr9Table5ReqDto.setTable5TotalToverReqDto(table5TotalToverReqDto);
		}

		if (e.getSubSection().equalsIgnoreCase(GSTR9Constants.Table_5A)) {
			Gstr9Table5ZeroRtdReqDto table5ZeroRtdReqDto = new Gstr9Table5ZeroRtdReqDto();
			table5ZeroRtdReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5ZeroRtdReqDto(table5ZeroRtdReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5B)) {
			Gstr9Table5SezReqDto table5SezReqDto = new Gstr9Table5SezReqDto();
			table5SezReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5SezReqDto(table5SezReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5C)) {

			Gstr9Table5RchrgReqDto table5RchrgReqDto = new Gstr9Table5RchrgReqDto();
			table5RchrgReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5RchrgReqDto(table5RchrgReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5C1)) {

			Gstr9Table5RchrgReqDto table5C1RchrgReqDto = new Gstr9Table5RchrgReqDto();
			table5C1RchrgReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5C1RchrgReqDto(table5C1RchrgReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5D)) {
			Gstr9Table5ExmtReqDto table5ExmtReqDto = new Gstr9Table5ExmtReqDto();
			table5ExmtReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5ExmtReqDto(table5ExmtReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5E)) {
			Gstr9Table5NilReqDto table5NilReqDto = new Gstr9Table5NilReqDto();
			table5NilReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5NilReqDto(table5NilReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5F)) {
			Gstr9Table5NonGstReqDto table5NonGstReqDto = new Gstr9Table5NonGstReqDto();
			table5NonGstReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5NonGstReqDto(table5NonGstReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5H)) {
			Gstr9Table5CrNtReqDto table5CrNtReqDto = new Gstr9Table5CrNtReqDto();
			table5CrNtReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5CrNtReqDto(table5CrNtReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5I)) {
			Gstr9Table5DbNtReqDto table5DbNtReqDto = new Gstr9Table5DbNtReqDto();
			table5DbNtReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5DbNtReqDto(table5DbNtReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5J)) {
			Gstr9Table5AmdPosReqDto table5AmdPosReqDto = new Gstr9Table5AmdPosReqDto();
			table5AmdPosReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5AmdPosReqDto(table5AmdPosReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5K)) {
			Gstr9Table5AmdNegReqDto table5AmdNegReqDto = new Gstr9Table5AmdNegReqDto();
			table5AmdNegReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5AmdNegReqDto(table5AmdNegReqDto);
		}

		return gstr9Table5ReqDto;

	}

	private Gstr9Table5ReqDto convertGetEntityToTable5Dto(
			Gstr9GetSummaryEntity e, Gstr9Table5ReqDto gstr9Table5ReqDto) {

		if (gstr9Table5ReqDto == null) {
			gstr9Table5ReqDto = new Gstr9Table5ReqDto();

			// Instantiate and initialize individual DTO objects
			Gstr9Table5ZeroRtdReqDto table5ZeroRtdReqDto = new Gstr9Table5ZeroRtdReqDto();
			Gstr9Table5SezReqDto table5SezReqDto = new Gstr9Table5SezReqDto();
			Gstr9Table5RchrgReqDto table5RchrgReqDto = new Gstr9Table5RchrgReqDto();
			Gstr9Table5RchrgReqDto table5C1RchrgReqDto = new Gstr9Table5RchrgReqDto();
			Gstr9Table5ExmtReqDto table5ExmtReqDto = new Gstr9Table5ExmtReqDto();
			Gstr9Table5NilReqDto table5NilReqDto = new Gstr9Table5NilReqDto();
			Gstr9Table5NonGstReqDto table5NonGstReqDto = new Gstr9Table5NonGstReqDto();
			Gstr9Table5CrNtReqDto table5CrNtReqDto = new Gstr9Table5CrNtReqDto();
			Gstr9Table5DbNtReqDto table5DbNtReqDto = new Gstr9Table5DbNtReqDto();
			Gstr9Table5AmdPosReqDto table5AmdPosReqDto = new Gstr9Table5AmdPosReqDto();
			Gstr9Table5AmdNegReqDto table5AmdNegReqDto = new Gstr9Table5AmdNegReqDto();
			// Gstr9Table5SubTotalAfReqDto table5SubTotalAfReqDto = new
			// Gstr9Table5SubTotalAfReqDto();
			// Gstr9Table5SubTotalHkReqDto table5SubTotalHkReqDto = new
			// Gstr9Table5SubTotalHkReqDto();
			// Gstr9Table5ToverTaxNpReqDto table5ToverTaxNpReqDto = new
			// Gstr9Table5ToverTaxNpReqDto();
			// Gstr9Table5TotalToverReqDto table5TotalToverReqDto = new
			// Gstr9Table5TotalToverReqDto();

			// Set the initialized objects to the table5ReqDto object using
			// setter methods
			gstr9Table5ReqDto.setTable5ZeroRtdReqDto(table5ZeroRtdReqDto);
			gstr9Table5ReqDto.setTable5SezReqDto(table5SezReqDto);
			gstr9Table5ReqDto.setTable5RchrgReqDto(table5RchrgReqDto);
			gstr9Table5ReqDto.setTable5C1RchrgReqDto(table5C1RchrgReqDto);
			gstr9Table5ReqDto.setTable5ExmtReqDto(table5ExmtReqDto);
			gstr9Table5ReqDto.setTable5NilReqDto(table5NilReqDto);
			gstr9Table5ReqDto.setTable5NonGstReqDto(table5NonGstReqDto);
			gstr9Table5ReqDto.setTable5CrNtReqDto(table5CrNtReqDto);
			gstr9Table5ReqDto.setTable5DbNtReqDto(table5DbNtReqDto);
			gstr9Table5ReqDto.setTable5AmdPosReqDto(table5AmdPosReqDto);
			gstr9Table5ReqDto.setTable5AmdNegReqDto(table5AmdNegReqDto);
			// gstr9Table5ReqDto.setTable5SubTotalAfReqDto(table5SubTotalAfReqDto);
			// gstr9Table5ReqDto.setTable5SubTotalHkReqDto(table5SubTotalHkReqDto);
			// gstr9Table5ReqDto.setTable5ToverTaxNpReqDto(table5ToverTaxNpReqDto);
			// gstr9Table5ReqDto.setTable5TotalToverReqDto(table5TotalToverReqDto);
		}

		if (e.getSubSection().equalsIgnoreCase(GSTR9Constants.Table_5A)) {
			Gstr9Table5ZeroRtdReqDto table5ZeroRtdReqDto = new Gstr9Table5ZeroRtdReqDto();
			table5ZeroRtdReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5ZeroRtdReqDto(table5ZeroRtdReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5B)) {
			Gstr9Table5SezReqDto table5SezReqDto = new Gstr9Table5SezReqDto();
			table5SezReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5SezReqDto(table5SezReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5C)) {

			Gstr9Table5RchrgReqDto table5RchrgReqDto = new Gstr9Table5RchrgReqDto();
			table5RchrgReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5RchrgReqDto(table5RchrgReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5C1)) {

			Gstr9Table5RchrgReqDto table5RchrgReqDto = new Gstr9Table5RchrgReqDto();
			table5RchrgReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5RchrgReqDto(table5RchrgReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5D)) {
			Gstr9Table5ExmtReqDto table5ExmtReqDto = new Gstr9Table5ExmtReqDto();
			table5ExmtReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5ExmtReqDto(table5ExmtReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5E)) {
			Gstr9Table5NilReqDto table5NilReqDto = new Gstr9Table5NilReqDto();
			table5NilReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5NilReqDto(table5NilReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5F)) {
			Gstr9Table5NonGstReqDto table5NonGstReqDto = new Gstr9Table5NonGstReqDto();
			table5NonGstReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5NonGstReqDto(table5NonGstReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5H)) {
			Gstr9Table5CrNtReqDto table5CrNtReqDto = new Gstr9Table5CrNtReqDto();
			table5CrNtReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5CrNtReqDto(table5CrNtReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5I)) {
			Gstr9Table5DbNtReqDto table5DbNtReqDto = new Gstr9Table5DbNtReqDto();
			table5DbNtReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5DbNtReqDto(table5DbNtReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5J)) {
			Gstr9Table5AmdPosReqDto table5AmdPosReqDto = new Gstr9Table5AmdPosReqDto();
			table5AmdPosReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5AmdPosReqDto(table5AmdPosReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9Constants.Table_5K)) {
			Gstr9Table5AmdNegReqDto table5AmdNegReqDto = new Gstr9Table5AmdNegReqDto();
			table5AmdNegReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table5ReqDto.setTable5AmdNegReqDto(table5AmdNegReqDto);
		}

		return gstr9Table5ReqDto;

	}

	private Gstr9Table6ReqDto convertEntityToTable6Dto(Gstr9UserInputEntity e,
			Gstr9Table6ReqDto gstr9Table6ReqDto) {

		if (gstr9Table6ReqDto == null) {
			gstr9Table6ReqDto = new Gstr9Table6ReqDto();

			// Instantiate and initialize individual DTO objects
			List<Gstr9Table6SuppNonRchrgReqDto> table6SuppNonRchrgReqDto = new ArrayList<>();
			List<Gstr9Table6SuppRchrgUnRegReqDto> table6SuppRchrgUnRegReqDto = new ArrayList<>();
			List<Gstr9Table6SuppRchrgRegReqDto> table6SuppRchrgRegReqDto = new ArrayList<>();
			List<Gstr9Table6IogReqDto> table6IogReqDto = new ArrayList<>();
			// Gstr9Table6Itc3bReqDto table6Itc3bReqDto = new
			// Gstr9Table6Itc3bReqDto();
			Gstr9Table6IosReqDto table6IosReqDto = new Gstr9Table6IosReqDto();
			Gstr9Table6IsdReqDto table6IsdReqDto = new Gstr9Table6IsdReqDto();
			Gstr9Table6ItcClmdReqDto table6ItcClmdReqDto = new Gstr9Table6ItcClmdReqDto();
			Gstr9Table6Trans1ReqDto table6Trans1ReqDto = new Gstr9Table6Trans1ReqDto();
			Gstr9Table6Trans2ReqDto table6Trans2ReqDto = new Gstr9Table6Trans2ReqDto();
			Gstr9Table6OtherReqDto table6OtherReqDto = new Gstr9Table6OtherReqDto();
			// Gstr9Table6SubTotalBhReqDto table6SubTotalBhReqDto = new
			// Gstr9Table6SubTotalBhReqDto();
			// Gstr9Table6SubTotalKmReqDto table6SubTotalKmReqDto = new
			// Gstr9Table6SubTotalKmReqDto();
			// Gstr9Table6DifferenceReqDto table6DifferenceReqDto = new
			// Gstr9Table6DifferenceReqDto();
			// Gstr9Table6TotalItcAvailedReqDto totalItcAvailedReqDto = new
			// Gstr9Table6TotalItcAvailedReqDto();

			// Set the initialized objects to the table6ReqDto object using
			// setter methods
			gstr9Table6ReqDto
					.setTable6SuppNonRchrgReqDto(table6SuppNonRchrgReqDto);
			gstr9Table6ReqDto
					.setTable6SuppRchrgUnRegReqDto(table6SuppRchrgUnRegReqDto);
			gstr9Table6ReqDto
					.setTable6SuppRchrgRegReqDto(table6SuppRchrgRegReqDto);
			gstr9Table6ReqDto.setTable6IogReqDto(table6IogReqDto);
			// gstr9Table6ReqDto.setTable6Itc3bReqDto(table6Itc3bReqDto);
			gstr9Table6ReqDto.setTable6IosReqDto(table6IosReqDto);
			gstr9Table6ReqDto.setTable6IsdReqDto(table6IsdReqDto);
			gstr9Table6ReqDto.setTable6ItcClmdReqDto(table6ItcClmdReqDto);
			gstr9Table6ReqDto.setTable6Trans1ReqDto(table6Trans1ReqDto);
			gstr9Table6ReqDto.setTable6Trans2ReqDto(table6Trans2ReqDto);
			gstr9Table6ReqDto.setTable6OtherReqDto(table6OtherReqDto);
			// gstr9Table6ReqDto.setTable6SubTotalBhReqDto(table6SubTotalBhReqDto);
			// gstr9Table6ReqDto.setTable6SubTotalKmReqDto(table6SubTotalKmReqDto);
			// gstr9Table6ReqDto.setTable6DifferenceReqDto(table6DifferenceReqDto);
			// gstr9Table6ReqDto.setTotalItcAvailedReqDto(totalItcAvailedReqDto);
		}

		if (SECTION6B_LIST.contains(e.getSubSection())) {
			Gstr9Table6SuppNonRchrgReqDto suppNonRchrgReqDto6B = new Gstr9Table6SuppNonRchrgReqDto();

			suppNonRchrgReqDto6B.setItctyp(e.getItcTyp());
			suppNonRchrgReqDto6B.setIamt(defaultToZeroIfNull(e.getIgst()));
			suppNonRchrgReqDto6B.setCamt(defaultToZeroIfNull(e.getCgst()));
			suppNonRchrgReqDto6B.setSamt(defaultToZeroIfNull(e.getSgst()));
			suppNonRchrgReqDto6B.setCsamt(defaultToZeroIfNull(e.getCess()));

			if (gstr9Table6ReqDto.getTable6SuppNonRchrgReqDto() == null)
				gstr9Table6ReqDto
						.setTable6SuppNonRchrgReqDto(new ArrayList<>());

			gstr9Table6ReqDto.getTable6SuppNonRchrgReqDto()
					.add(suppNonRchrgReqDto6B);

			// chages done till here

		} else if (SECTION6C_LIST.contains(e.getSubSection())) {
			Gstr9Table6SuppRchrgUnRegReqDto suppRchrgUnRegReqDto6C = new Gstr9Table6SuppRchrgUnRegReqDto();
			suppRchrgUnRegReqDto6C.setItctyp(e.getItcTyp());
			suppRchrgUnRegReqDto6C.setIamt(defaultToZeroIfNull(e.getIgst()));
			suppRchrgUnRegReqDto6C.setCamt(defaultToZeroIfNull(e.getCgst()));
			suppRchrgUnRegReqDto6C.setSamt(defaultToZeroIfNull(e.getSgst()));
			suppRchrgUnRegReqDto6C.setCsamt(defaultToZeroIfNull(e.getCess()));
			if (gstr9Table6ReqDto.getTable6SuppRchrgUnRegReqDto() == null)
				gstr9Table6ReqDto
						.setTable6SuppRchrgUnRegReqDto(new ArrayList<>());

			gstr9Table6ReqDto.getTable6SuppRchrgUnRegReqDto()
					.add(suppRchrgUnRegReqDto6C);

		} else if (SECTION6D_LIST.contains(e.getSubSection())) {
			Gstr9Table6SuppRchrgRegReqDto suppRchrgRegReqDto6D = new Gstr9Table6SuppRchrgRegReqDto();
			suppRchrgRegReqDto6D.setItctyp(e.getItcTyp());
			suppRchrgRegReqDto6D.setIamt(defaultToZeroIfNull(e.getIgst()));
			suppRchrgRegReqDto6D.setCamt(defaultToZeroIfNull(e.getCgst()));
			suppRchrgRegReqDto6D.setSamt(defaultToZeroIfNull(e.getSgst()));
			suppRchrgRegReqDto6D.setCsamt(defaultToZeroIfNull(e.getCess()));

			if (gstr9Table6ReqDto.getTable6SuppRchrgRegReqDto() == null)
				gstr9Table6ReqDto
						.setTable6SuppRchrgRegReqDto(new ArrayList<>());

			gstr9Table6ReqDto.getTable6SuppRchrgRegReqDto()
					.add(suppRchrgRegReqDto6D);

		} else if (SECTION6E_LIST.contains(e.getSubSection())) {
			Gstr9Table6IogReqDto iogReqDto6E = new Gstr9Table6IogReqDto();
			iogReqDto6E.setItctyp(e.getItcTyp());
			iogReqDto6E.setIamt(defaultToZeroIfNull(e.getIgst()));
			iogReqDto6E.setCsamt(defaultToZeroIfNull(e.getCess()));

			if (gstr9Table6ReqDto.getTable6IogReqDto() == null)
				gstr9Table6ReqDto.setTable6IogReqDto(new ArrayList<>());

			gstr9Table6ReqDto.getTable6IogReqDto().add(iogReqDto6E);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_6F)) {
			Gstr9Table6IosReqDto table6IosReqDto = new Gstr9Table6IosReqDto();
			table6IosReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			table6IosReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table6ReqDto.setTable6IosReqDto(table6IosReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_6G)) {
			Gstr9Table6IsdReqDto table6IsdReqDto = new Gstr9Table6IsdReqDto();
			table6IsdReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			table6IsdReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			table6IsdReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			table6IsdReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table6ReqDto.setTable6IsdReqDto(table6IsdReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_6H)) {
			Gstr9Table6ItcClmdReqDto table6ItcClmdReqDto = new Gstr9Table6ItcClmdReqDto();
			table6ItcClmdReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			table6ItcClmdReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			table6ItcClmdReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			table6ItcClmdReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table6ReqDto.setTable6ItcClmdReqDto(table6ItcClmdReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_6K)) {
			Gstr9Table6Trans1ReqDto table6Trans1ReqDto = new Gstr9Table6Trans1ReqDto();
			table6Trans1ReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			table6Trans1ReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table6ReqDto.setTable6Trans1ReqDto(table6Trans1ReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_6L)) {
			Gstr9Table6Trans2ReqDto table6Trans2ReqDto = new Gstr9Table6Trans2ReqDto();
			table6Trans2ReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			table6Trans2ReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table6ReqDto.setTable6Trans2ReqDto(table6Trans2ReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_6M)) {
			Gstr9Table6OtherReqDto table6OtherReqDto = new Gstr9Table6OtherReqDto();
			table6OtherReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			table6OtherReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			table6OtherReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			table6OtherReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table6ReqDto.setTable6OtherReqDto(table6OtherReqDto);
		}
		return gstr9Table6ReqDto;

	}

	private Gstr9Table6ReqDto convertGetEntityToTable6Dto(
			Gstr9GetSummaryEntity e, Gstr9Table6ReqDto gstr9Table6ReqDto) {

		if (gstr9Table6ReqDto == null) {
			gstr9Table6ReqDto = new Gstr9Table6ReqDto();

			// Instantiate and initialize individual DTO objects
			List<Gstr9Table6SuppNonRchrgReqDto> table6SuppNonRchrgReqDto = new ArrayList<>();
			List<Gstr9Table6SuppRchrgUnRegReqDto> table6SuppRchrgUnRegReqDto = new ArrayList<>();
			List<Gstr9Table6SuppRchrgRegReqDto> table6SuppRchrgRegReqDto = new ArrayList<>();
			List<Gstr9Table6IogReqDto> table6IogReqDto = new ArrayList<>();
			// Gstr9Table6Itc3bReqDto table6Itc3bReqDto = new
			// Gstr9Table6Itc3bReqDto();
			Gstr9Table6IosReqDto table6IosReqDto = new Gstr9Table6IosReqDto();
			Gstr9Table6IsdReqDto table6IsdReqDto = new Gstr9Table6IsdReqDto();
			Gstr9Table6ItcClmdReqDto table6ItcClmdReqDto = new Gstr9Table6ItcClmdReqDto();
			Gstr9Table6Trans1ReqDto table6Trans1ReqDto = new Gstr9Table6Trans1ReqDto();
			Gstr9Table6Trans2ReqDto table6Trans2ReqDto = new Gstr9Table6Trans2ReqDto();
			Gstr9Table6OtherReqDto table6OtherReqDto = new Gstr9Table6OtherReqDto();
			// Gstr9Table6SubTotalBhReqDto table6SubTotalBhReqDto = new
			// Gstr9Table6SubTotalBhReqDto();
			// Gstr9Table6SubTotalKmReqDto table6SubTotalKmReqDto = new
			// Gstr9Table6SubTotalKmReqDto();
			// Gstr9Table6DifferenceReqDto table6DifferenceReqDto = new
			// Gstr9Table6DifferenceReqDto();
			// Gstr9Table6TotalItcAvailedReqDto totalItcAvailedReqDto = new
			// Gstr9Table6TotalItcAvailedReqDto();

			// Set the initialized objects to the table6ReqDto object using
			// setter methods
			gstr9Table6ReqDto
					.setTable6SuppNonRchrgReqDto(table6SuppNonRchrgReqDto);
			gstr9Table6ReqDto
					.setTable6SuppRchrgUnRegReqDto(table6SuppRchrgUnRegReqDto);
			gstr9Table6ReqDto
					.setTable6SuppRchrgRegReqDto(table6SuppRchrgRegReqDto);
			gstr9Table6ReqDto.setTable6IogReqDto(table6IogReqDto);
			// gstr9Table6ReqDto.setTable6Itc3bReqDto(table6Itc3bReqDto);
			gstr9Table6ReqDto.setTable6IosReqDto(table6IosReqDto);
			gstr9Table6ReqDto.setTable6IsdReqDto(table6IsdReqDto);
			gstr9Table6ReqDto.setTable6ItcClmdReqDto(table6ItcClmdReqDto);
			gstr9Table6ReqDto.setTable6Trans1ReqDto(table6Trans1ReqDto);
			gstr9Table6ReqDto.setTable6Trans2ReqDto(table6Trans2ReqDto);
			gstr9Table6ReqDto.setTable6OtherReqDto(table6OtherReqDto);
			// gstr9Table6ReqDto.setTable6SubTotalBhReqDto(table6SubTotalBhReqDto);
			// gstr9Table6ReqDto.setTable6SubTotalKmReqDto(table6SubTotalKmReqDto);
			// gstr9Table6ReqDto.setTable6DifferenceReqDto(table6DifferenceReqDto);
			// gstr9Table6ReqDto.setTotalItcAvailedReqDto(totalItcAvailedReqDto);
		}

		if (SECTION6B_LIST.contains(e.getSubSection())) {
			Gstr9Table6SuppNonRchrgReqDto suppNonRchrgReqDto6B = new Gstr9Table6SuppNonRchrgReqDto();

			suppNonRchrgReqDto6B.setItctyp(e.getItcTyp());
			suppNonRchrgReqDto6B.setIamt(defaultToZeroIfNull(e.getIamt()));
			suppNonRchrgReqDto6B.setCamt(defaultToZeroIfNull(e.getCamt()));
			suppNonRchrgReqDto6B.setSamt(defaultToZeroIfNull(e.getSamt()));
			suppNonRchrgReqDto6B.setCsamt(defaultToZeroIfNull(e.getCsamt()));

			if (gstr9Table6ReqDto.getTable6SuppNonRchrgReqDto() == null)
				gstr9Table6ReqDto
						.setTable6SuppNonRchrgReqDto(new ArrayList<>());

			gstr9Table6ReqDto.getTable6SuppNonRchrgReqDto()
					.add(suppNonRchrgReqDto6B);

		} else if (SECTION6C_LIST.contains(e.getSubSection())) {
			Gstr9Table6SuppRchrgUnRegReqDto suppRchrgUnRegReqDto6C = new Gstr9Table6SuppRchrgUnRegReqDto();
			suppRchrgUnRegReqDto6C.setItctyp(e.getItcTyp());
			suppRchrgUnRegReqDto6C.setIamt(defaultToZeroIfNull(e.getIamt()));
			suppRchrgUnRegReqDto6C.setCamt(defaultToZeroIfNull(e.getCamt()));
			suppRchrgUnRegReqDto6C.setSamt(defaultToZeroIfNull(e.getSamt()));
			suppRchrgUnRegReqDto6C.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			if (gstr9Table6ReqDto.getTable6SuppRchrgUnRegReqDto() == null)
				gstr9Table6ReqDto
						.setTable6SuppRchrgUnRegReqDto(new ArrayList<>());

			gstr9Table6ReqDto.getTable6SuppRchrgUnRegReqDto()
					.add(suppRchrgUnRegReqDto6C);

		} else if (SECTION6D_LIST.contains(e.getSubSection())) {
			Gstr9Table6SuppRchrgRegReqDto suppRchrgRegReqDto6D = new Gstr9Table6SuppRchrgRegReqDto();
			suppRchrgRegReqDto6D.setItctyp(e.getItcTyp());
			suppRchrgRegReqDto6D.setIamt(defaultToZeroIfNull(e.getIamt()));
			suppRchrgRegReqDto6D.setCamt(defaultToZeroIfNull(e.getCamt()));
			suppRchrgRegReqDto6D.setSamt(defaultToZeroIfNull(e.getSamt()));
			suppRchrgRegReqDto6D.setCsamt(defaultToZeroIfNull(e.getCsamt()));

			if (gstr9Table6ReqDto.getTable6SuppRchrgRegReqDto() == null)
				gstr9Table6ReqDto
						.setTable6SuppRchrgRegReqDto(new ArrayList<>());

			gstr9Table6ReqDto.getTable6SuppRchrgRegReqDto()
					.add(suppRchrgRegReqDto6D);

		} else if (SECTION6E_LIST.contains(e.getSubSection())) {
			Gstr9Table6IogReqDto iogReqDto6E = new Gstr9Table6IogReqDto();
			iogReqDto6E.setItctyp(e.getItcTyp());
			iogReqDto6E.setIamt(defaultToZeroIfNull(e.getIamt()));
			iogReqDto6E.setCsamt(defaultToZeroIfNull(e.getCsamt()));

			if (gstr9Table6ReqDto.getTable6IogReqDto() == null)
				gstr9Table6ReqDto.setTable6IogReqDto(new ArrayList<>());

			gstr9Table6ReqDto.getTable6IogReqDto().add(iogReqDto6E);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_6F)) {
			Gstr9Table6IosReqDto table6IosReqDto = new Gstr9Table6IosReqDto();
			table6IosReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			table6IosReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table6ReqDto.setTable6IosReqDto(table6IosReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_6G)) {
			Gstr9Table6IsdReqDto table6IsdReqDto = new Gstr9Table6IsdReqDto();
			table6IsdReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			table6IsdReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			table6IsdReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			table6IsdReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table6ReqDto.setTable6IsdReqDto(table6IsdReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_6H)) {
			Gstr9Table6ItcClmdReqDto table6ItcClmdReqDto = new Gstr9Table6ItcClmdReqDto();
			table6ItcClmdReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			table6ItcClmdReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			table6ItcClmdReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			table6ItcClmdReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table6ReqDto.setTable6ItcClmdReqDto(table6ItcClmdReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_6K)) {
			Gstr9Table6Trans1ReqDto table6Trans1ReqDto = new Gstr9Table6Trans1ReqDto();
			table6Trans1ReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			table6Trans1ReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table6ReqDto.setTable6Trans1ReqDto(table6Trans1ReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_6L)) {
			Gstr9Table6Trans2ReqDto table6Trans2ReqDto = new Gstr9Table6Trans2ReqDto();
			table6Trans2ReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			table6Trans2ReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table6ReqDto.setTable6Trans2ReqDto(table6Trans2ReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_6M)) {
			Gstr9Table6OtherReqDto table6OtherReqDto = new Gstr9Table6OtherReqDto();
			table6OtherReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			table6OtherReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			table6OtherReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			table6OtherReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table6ReqDto.setTable6OtherReqDto(table6OtherReqDto);
		}
		return gstr9Table6ReqDto;

	}

	private Gstr9Table7ReqDto convertEntityToTable7Dto(Gstr9UserInputEntity e,
			Gstr9Table7ReqDto gstr9Table7ReqDto) {

		if (gstr9Table7ReqDto == null) {
			gstr9Table7ReqDto = new Gstr9Table7ReqDto();
			// Instantiate and initialize individual DTO objects
			Gstr9Table7Rule37ReqDto gstr9Table7Rule37ReqDto = new Gstr9Table7Rule37ReqDto();
			Gstr9Table7Rule39ReqDto gstr9Table7Rule39ReqDto = new Gstr9Table7Rule39ReqDto();
			Gstr9Table7Rule42ReqDto gstr9Table7Rule42ReqDto = new Gstr9Table7Rule42ReqDto();
			Gstr9Table7Rule43ReqDto gstr9Table7Rule43ReqDto = new Gstr9Table7Rule43ReqDto();
			Gstr9Table7Sec17ReqDto gstr9Table7Sec17ReqDto = new Gstr9Table7Sec17ReqDto();
			Gstr9Table7RevsTrans1ReqDto gstr9Table7RevsTrans1ReqDto = new Gstr9Table7RevsTrans1ReqDto();
			Gstr9Table7RevsTrans2ReqDto gstr9Table7RevsTrans2ReqDto = new Gstr9Table7RevsTrans2ReqDto();
			List<Gstr9Table7OtherReqDto> gstr9Table7OtherReqDto = new ArrayList<>();
			// Gstr9Table7TotItcRevdReqDto gstr9Table7TotItcRevdReqDto = new
			// Gstr9Table7TotItcRevdReqDto();
			// Gstr9Table7NetItcAvalReqDto gstr9Table7NetItcAvalReqDto = new
			// Gstr9Table7NetItcAvalReqDto();

			// Set the initialized objects to the table7ReqDto object using
			// setter methods
			gstr9Table7ReqDto
					.setGstr9Table7Rule37ReqDto(gstr9Table7Rule37ReqDto);
			gstr9Table7ReqDto
					.setGstr9Table7Rule39ReqDto(gstr9Table7Rule39ReqDto);
			gstr9Table7ReqDto
					.setGstr9Table7Rule42ReqDto(gstr9Table7Rule42ReqDto);
			gstr9Table7ReqDto
					.setGstr9Table7Rule43ReqDto(gstr9Table7Rule43ReqDto);
			gstr9Table7ReqDto.setGstr9Table7Sec17ReqDto(gstr9Table7Sec17ReqDto);
			gstr9Table7ReqDto.setGstr9Table7RevsTrans1ReqDto(
					gstr9Table7RevsTrans1ReqDto);
			gstr9Table7ReqDto.setGstr9Table7RevsTrans2ReqDto(
					gstr9Table7RevsTrans2ReqDto);
			gstr9Table7ReqDto.setGstr9Table7OtherReqDto(gstr9Table7OtherReqDto);
			// gstr9Table7ReqDto.setGstr9Table7TotItcRevdReqDto(gstr9Table7TotItcRevdReqDto);
			// gstr9Table7ReqDto.setGstr9Table7NetItcAvalReqDto(gstr9Table7NetItcAvalReqDto);
		}

		if (e.getSubSection().equalsIgnoreCase(GSTR9InwardConstants.Table_7A)) {
			Gstr9Table7Rule37ReqDto gstr9Table7Rule37ReqDto = new Gstr9Table7Rule37ReqDto();
			gstr9Table7Rule37ReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table7Rule37ReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table7Rule37ReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table7Rule37ReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table7ReqDto
					.setGstr9Table7Rule37ReqDto(gstr9Table7Rule37ReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_7B)) {
			Gstr9Table7Rule39ReqDto gstr9Table7Rule39ReqDto = new Gstr9Table7Rule39ReqDto();
			gstr9Table7Rule39ReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table7Rule39ReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table7Rule39ReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table7Rule39ReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table7ReqDto
					.setGstr9Table7Rule39ReqDto(gstr9Table7Rule39ReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_7C)) {
			Gstr9Table7Rule42ReqDto gstr9Table7Rule42ReqDto = new Gstr9Table7Rule42ReqDto();
			gstr9Table7Rule42ReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table7Rule42ReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table7Rule42ReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table7Rule42ReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table7ReqDto
					.setGstr9Table7Rule42ReqDto(gstr9Table7Rule42ReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_7D)) {
			Gstr9Table7Rule43ReqDto gstr9Table7Rule43ReqDto = new Gstr9Table7Rule43ReqDto();
			gstr9Table7Rule43ReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table7Rule43ReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table7Rule43ReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table7Rule43ReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table7ReqDto
					.setGstr9Table7Rule43ReqDto(gstr9Table7Rule43ReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_7E)) {
			Gstr9Table7Sec17ReqDto gstr9Table7Sec17ReqDto = new Gstr9Table7Sec17ReqDto();
			gstr9Table7Sec17ReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table7Sec17ReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table7Sec17ReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table7Sec17ReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table7ReqDto.setGstr9Table7Sec17ReqDto(gstr9Table7Sec17ReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_7F)) {
			Gstr9Table7RevsTrans1ReqDto gstr9Table7RevsTrans1ReqDto = new Gstr9Table7RevsTrans1ReqDto();
			gstr9Table7RevsTrans1ReqDto
					.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table7RevsTrans1ReqDto
					.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table7ReqDto.setGstr9Table7RevsTrans1ReqDto(
					gstr9Table7RevsTrans1ReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_7G)) {
			Gstr9Table7RevsTrans2ReqDto gstr9Table7RevsTrans2ReqDto = new Gstr9Table7RevsTrans2ReqDto();
			gstr9Table7RevsTrans2ReqDto
					.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table7RevsTrans2ReqDto
					.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table7ReqDto.setGstr9Table7RevsTrans2ReqDto(
					gstr9Table7RevsTrans2ReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_7H)) {
			Gstr9Table7OtherReqDto gstr9Table7OtherReqDto = new Gstr9Table7OtherReqDto();
			gstr9Table7OtherReqDto
					.setDesc(e.getDesc() != null ? e.getDesc() : "");
			gstr9Table7OtherReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table7OtherReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table7OtherReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table7OtherReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));

			if (gstr9Table7ReqDto.getGstr9Table7OtherReqDto() == null)
				gstr9Table7ReqDto.setGstr9Table7OtherReqDto(new ArrayList<>());

			gstr9Table7ReqDto.getGstr9Table7OtherReqDto()
					.add(gstr9Table7OtherReqDto);

		}

		return gstr9Table7ReqDto;

	}

	private Gstr9Table7ReqDto convertGetEntityToTable7Dto(
			Gstr9GetSummaryEntity e, Gstr9Table7ReqDto gstr9Table7ReqDto) {

		if (gstr9Table7ReqDto == null) {
			gstr9Table7ReqDto = new Gstr9Table7ReqDto();

			// Instantiate and initialize individual DTO objects
			Gstr9Table7Rule37ReqDto gstr9Table7Rule37ReqDto = new Gstr9Table7Rule37ReqDto();
			Gstr9Table7Rule39ReqDto gstr9Table7Rule39ReqDto = new Gstr9Table7Rule39ReqDto();
			Gstr9Table7Rule42ReqDto gstr9Table7Rule42ReqDto = new Gstr9Table7Rule42ReqDto();
			Gstr9Table7Rule43ReqDto gstr9Table7Rule43ReqDto = new Gstr9Table7Rule43ReqDto();
			Gstr9Table7Sec17ReqDto gstr9Table7Sec17ReqDto = new Gstr9Table7Sec17ReqDto();
			Gstr9Table7RevsTrans1ReqDto gstr9Table7RevsTrans1ReqDto = new Gstr9Table7RevsTrans1ReqDto();
			Gstr9Table7RevsTrans2ReqDto gstr9Table7RevsTrans2ReqDto = new Gstr9Table7RevsTrans2ReqDto();
			List<Gstr9Table7OtherReqDto> gstr9Table7OtherReqDto = new ArrayList<>();
			// Gstr9Table7TotItcRevdReqDto gstr9Table7TotItcRevdReqDto = new
			// Gstr9Table7TotItcRevdReqDto();
			// Gstr9Table7NetItcAvalReqDto gstr9Table7NetItcAvalReqDto = new
			// Gstr9Table7NetItcAvalReqDto();

			// Set the initialized objects to the table7ReqDto object using
			// setter methods
			gstr9Table7ReqDto
					.setGstr9Table7Rule37ReqDto(gstr9Table7Rule37ReqDto);
			gstr9Table7ReqDto
					.setGstr9Table7Rule39ReqDto(gstr9Table7Rule39ReqDto);
			gstr9Table7ReqDto
					.setGstr9Table7Rule42ReqDto(gstr9Table7Rule42ReqDto);
			gstr9Table7ReqDto
					.setGstr9Table7Rule43ReqDto(gstr9Table7Rule43ReqDto);
			gstr9Table7ReqDto.setGstr9Table7Sec17ReqDto(gstr9Table7Sec17ReqDto);
			gstr9Table7ReqDto.setGstr9Table7RevsTrans1ReqDto(
					gstr9Table7RevsTrans1ReqDto);
			gstr9Table7ReqDto.setGstr9Table7RevsTrans2ReqDto(
					gstr9Table7RevsTrans2ReqDto);
			gstr9Table7ReqDto.setGstr9Table7OtherReqDto(gstr9Table7OtherReqDto);
			// gstr9Table7ReqDto.setGstr9Table7TotItcRevdReqDto(gstr9Table7TotItcRevdReqDto);
			// gstr9Table7ReqDto.setGstr9Table7NetItcAvalReqDto(gstr9Table7NetItcAvalReqDto);
		}

		if (e.getSubSection().equalsIgnoreCase(GSTR9InwardConstants.Table_7A)) {
			Gstr9Table7Rule37ReqDto gstr9Table7Rule37ReqDto = new Gstr9Table7Rule37ReqDto();
			gstr9Table7Rule37ReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table7Rule37ReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table7Rule37ReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table7Rule37ReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table7ReqDto
					.setGstr9Table7Rule37ReqDto(gstr9Table7Rule37ReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_7B)) {
			Gstr9Table7Rule39ReqDto gstr9Table7Rule39ReqDto = new Gstr9Table7Rule39ReqDto();
			gstr9Table7Rule39ReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table7Rule39ReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table7Rule39ReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table7Rule39ReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table7ReqDto
					.setGstr9Table7Rule39ReqDto(gstr9Table7Rule39ReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_7C)) {
			Gstr9Table7Rule42ReqDto gstr9Table7Rule42ReqDto = new Gstr9Table7Rule42ReqDto();
			gstr9Table7Rule42ReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table7Rule42ReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table7Rule42ReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table7Rule42ReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table7ReqDto
					.setGstr9Table7Rule42ReqDto(gstr9Table7Rule42ReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_7D)) {
			Gstr9Table7Rule43ReqDto gstr9Table7Rule43ReqDto = new Gstr9Table7Rule43ReqDto();
			gstr9Table7Rule43ReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table7Rule43ReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table7Rule43ReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table7Rule43ReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table7ReqDto
					.setGstr9Table7Rule43ReqDto(gstr9Table7Rule43ReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_7E)) {
			Gstr9Table7Sec17ReqDto gstr9Table7Sec17ReqDto = new Gstr9Table7Sec17ReqDto();
			gstr9Table7Sec17ReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table7Sec17ReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table7Sec17ReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table7Sec17ReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table7ReqDto.setGstr9Table7Sec17ReqDto(gstr9Table7Sec17ReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_7F)) {
			Gstr9Table7RevsTrans1ReqDto gstr9Table7RevsTrans1ReqDto = new Gstr9Table7RevsTrans1ReqDto();
			gstr9Table7RevsTrans1ReqDto
					.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table7RevsTrans1ReqDto
					.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table7ReqDto.setGstr9Table7RevsTrans1ReqDto(
					gstr9Table7RevsTrans1ReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_7G)) {
			Gstr9Table7RevsTrans2ReqDto gstr9Table7RevsTrans2ReqDto = new Gstr9Table7RevsTrans2ReqDto();
			gstr9Table7RevsTrans2ReqDto
					.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table7RevsTrans2ReqDto
					.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table7ReqDto.setGstr9Table7RevsTrans2ReqDto(
					gstr9Table7RevsTrans2ReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_7H)) {
			Gstr9Table7OtherReqDto gstr9Table7OtherReqDto = new Gstr9Table7OtherReqDto();
			gstr9Table7OtherReqDto
					.setDesc(e.getDesc() != null ? e.getDesc() : "");
			gstr9Table7OtherReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table7OtherReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table7OtherReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table7OtherReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));

			if (gstr9Table7ReqDto.getGstr9Table7OtherReqDto() == null)
				gstr9Table7ReqDto.setGstr9Table7OtherReqDto(new ArrayList<>());

			gstr9Table7ReqDto.getGstr9Table7OtherReqDto()
					.add(gstr9Table7OtherReqDto);

		}

		return gstr9Table7ReqDto;

	}

	private Gstr9Table8ReqDto convertEntityToTable8Dto(Gstr9UserInputEntity e,
			Gstr9Table8ReqDto gstr9Table8ReqDto) {

		if (gstr9Table8ReqDto == null) {
			gstr9Table8ReqDto = new Gstr9Table8ReqDto();
			// Initialize all other required objects
			Gstr9Table8Itc2AReqDto gstr9Table8Itc2AReqDto = new Gstr9Table8Itc2AReqDto();
			Gstr9Table8ItcTotReqDto gstr9Table8ItcTotReqDto = new Gstr9Table8ItcTotReqDto();
			Gstr9Table8ItcInwdSuppReqDto gstr9Table8ItcInwdSuppReqDto = new Gstr9Table8ItcInwdSuppReqDto();
			Gstr9Table8ItcNtAvaildReqDto gstr9Table8ItcNtAvaildReqDto = new Gstr9Table8ItcNtAvaildReqDto();
			Gstr9Table8ItcNtElegReqDto gstr9Table8ItcNtElegReqDto = new Gstr9Table8ItcNtElegReqDto();
			Gstr9Table8IogTaxPaidReqDto gstr9Table8IogTaxPaidReqDto = new Gstr9Table8IogTaxPaidReqDto();
			Gstr9Table8IogItcAvaildReqDto gstr9Table8IogItcAvaildReqDto = new Gstr9Table8IogItcAvaildReqDto();
			// Gstr9Table8IogItcNtAvaildReqDto gstr9Table8IogItcNtAvaildReqDto =
			// new Gstr9Table8IogItcNtAvaildReqDto();
			// Gstr9Table8DifferenceABCReqDto gstr9Table8DifferenceABCReqDto =
			// new Gstr9Table8DifferenceABCReqDto();
			// Gstr9Table8DifferenceGhReqDto gstr9Table8DifferenceGhReqDto = new
			// Gstr9Table8DifferenceGhReqDto();
			// Gstr9Table8TotItcLapsedReqDto gstr9Table8TotItcLapsedReqDto = new
			// Gstr9Table8TotItcLapsedReqDto();

			// Set the objects using the setter methods
			gstr9Table8ReqDto.setGstr9Table8Itc2AReqDto(gstr9Table8Itc2AReqDto);
			gstr9Table8ReqDto
					.setGstr9Table8ItcTotReqDto(gstr9Table8ItcTotReqDto);
			gstr9Table8ReqDto.setGstr9Table8ItcInwdSuppReqDto(
					gstr9Table8ItcInwdSuppReqDto);
			gstr9Table8ReqDto.setGstr9Table8ItcNtAvaildReqDto(
					gstr9Table8ItcNtAvaildReqDto);
			gstr9Table8ReqDto
					.setGstr9Table8ItcNtElegReqDto(gstr9Table8ItcNtElegReqDto);
			gstr9Table8ReqDto.setGstr9Table8IogTaxPaidReqDto(
					gstr9Table8IogTaxPaidReqDto);
			gstr9Table8ReqDto.setGstr9Table8IogItcAvaildReqDto(
					gstr9Table8IogItcAvaildReqDto);
			// gstr9Table8ReqDto.setGstr9Table8IogItcNtAvaildReqDto(gstr9Table8IogItcNtAvaildReqDto);
			// gstr9Table8ReqDto.setGstr9Table8DifferenceABCReqDto(gstr9Table8DifferenceABCReqDto);
			// gstr9Table8ReqDto.setGstr9Table8DifferenceGhReqDto(gstr9Table8DifferenceGhReqDto);
			// gstr9Table8ReqDto.setGstr9Table8TotItcLapsedReqDto(gstr9Table8TotItcLapsedReqDto);
		}

		if (e.getSubSection().equalsIgnoreCase(GSTR9InwardConstants.Table_8C)) {
			Gstr9Table8ItcInwdSuppReqDto gstr9Table8ItcInwdSuppReqDto = new Gstr9Table8ItcInwdSuppReqDto();
			gstr9Table8ItcInwdSuppReqDto
					.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table8ItcInwdSuppReqDto
					.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table8ItcInwdSuppReqDto
					.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table8ItcInwdSuppReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table8ReqDto.setGstr9Table8ItcInwdSuppReqDto(
					gstr9Table8ItcInwdSuppReqDto);
		}

		else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_8E)) {
			Gstr9Table8ItcNtAvaildReqDto gstr9Table8ItcNtAvaildReqDto = new Gstr9Table8ItcNtAvaildReqDto();
			gstr9Table8ItcNtAvaildReqDto
					.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table8ItcNtAvaildReqDto
					.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table8ItcNtAvaildReqDto
					.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table8ItcNtAvaildReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table8ReqDto.setGstr9Table8ItcNtAvaildReqDto(
					gstr9Table8ItcNtAvaildReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_8F)) {
			Gstr9Table8ItcNtElegReqDto gstr9Table8ItcNtElegReqDto = new Gstr9Table8ItcNtElegReqDto();
			gstr9Table8ItcNtElegReqDto
					.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table8ItcNtElegReqDto
					.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table8ItcNtElegReqDto
					.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table8ItcNtElegReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table8ReqDto
					.setGstr9Table8ItcNtElegReqDto(gstr9Table8ItcNtElegReqDto);
		}

		else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_8G)) {
			Gstr9Table8IogTaxPaidReqDto gstr9Table8IogTaxPaidReqDto = new Gstr9Table8IogTaxPaidReqDto();
			gstr9Table8IogTaxPaidReqDto
					.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table8IogTaxPaidReqDto
					.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table8IogTaxPaidReqDto
					.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table8IogTaxPaidReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table8ReqDto.setGstr9Table8IogTaxPaidReqDto(
					gstr9Table8IogTaxPaidReqDto);

		}

		return gstr9Table8ReqDto;

	}

	private Gstr9Table8ReqDto convertGetEntityToTable8Dto(
			Gstr9GetSummaryEntity e, Gstr9Table8ReqDto gstr9Table8ReqDto) {

		if (gstr9Table8ReqDto == null) {
			gstr9Table8ReqDto = new Gstr9Table8ReqDto();

			// Initialize all other required objects
			Gstr9Table8Itc2AReqDto gstr9Table8Itc2AReqDto = new Gstr9Table8Itc2AReqDto();
			Gstr9Table8ItcTotReqDto gstr9Table8ItcTotReqDto = new Gstr9Table8ItcTotReqDto();
			Gstr9Table8ItcInwdSuppReqDto gstr9Table8ItcInwdSuppReqDto = new Gstr9Table8ItcInwdSuppReqDto();
			Gstr9Table8ItcNtAvaildReqDto gstr9Table8ItcNtAvaildReqDto = new Gstr9Table8ItcNtAvaildReqDto();
			Gstr9Table8ItcNtElegReqDto gstr9Table8ItcNtElegReqDto = new Gstr9Table8ItcNtElegReqDto();
			Gstr9Table8IogTaxPaidReqDto gstr9Table8IogTaxPaidReqDto = new Gstr9Table8IogTaxPaidReqDto();
			Gstr9Table8IogItcAvaildReqDto gstr9Table8IogItcAvaildReqDto = new Gstr9Table8IogItcAvaildReqDto();
			// Gstr9Table8IogItcNtAvaildReqDto gstr9Table8IogItcNtAvaildReqDto =
			// new Gstr9Table8IogItcNtAvaildReqDto();
			// Gstr9Table8DifferenceABCReqDto gstr9Table8DifferenceABCReqDto =
			// new Gstr9Table8DifferenceABCReqDto();
			// Gstr9Table8DifferenceGhReqDto gstr9Table8DifferenceGhReqDto = new
			// Gstr9Table8DifferenceGhReqDto();
			// Gstr9Table8TotItcLapsedReqDto gstr9Table8TotItcLapsedReqDto = new
			// Gstr9Table8TotItcLapsedReqDto();

			// Set the objects using the setter methods
			gstr9Table8ReqDto.setGstr9Table8Itc2AReqDto(gstr9Table8Itc2AReqDto);
			gstr9Table8ReqDto
					.setGstr9Table8ItcTotReqDto(gstr9Table8ItcTotReqDto);
			gstr9Table8ReqDto.setGstr9Table8ItcInwdSuppReqDto(
					gstr9Table8ItcInwdSuppReqDto);
			gstr9Table8ReqDto.setGstr9Table8ItcNtAvaildReqDto(
					gstr9Table8ItcNtAvaildReqDto);
			gstr9Table8ReqDto
					.setGstr9Table8ItcNtElegReqDto(gstr9Table8ItcNtElegReqDto);
			gstr9Table8ReqDto.setGstr9Table8IogTaxPaidReqDto(
					gstr9Table8IogTaxPaidReqDto);
			gstr9Table8ReqDto.setGstr9Table8IogItcAvaildReqDto(
					gstr9Table8IogItcAvaildReqDto);
			// gstr9Table8ReqDto.setGstr9Table8IogItcNtAvaildReqDto(gstr9Table8IogItcNtAvaildReqDto);
			// gstr9Table8ReqDto.setGstr9Table8DifferenceABCReqDto(gstr9Table8DifferenceABCReqDto);
			// gstr9Table8ReqDto.setGstr9Table8DifferenceGhReqDto(gstr9Table8DifferenceGhReqDto);
			// gstr9Table8ReqDto.setGstr9Table8TotItcLapsedReqDto(gstr9Table8TotItcLapsedReqDto);

		}

		if (e.getSubSection().equalsIgnoreCase(GSTR9InwardConstants.Table_8C)) {
			Gstr9Table8ItcInwdSuppReqDto gstr9Table8ItcInwdSuppReqDto = new Gstr9Table8ItcInwdSuppReqDto();
			gstr9Table8ItcInwdSuppReqDto
					.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table8ItcInwdSuppReqDto
					.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table8ItcInwdSuppReqDto
					.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table8ItcInwdSuppReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table8ReqDto.setGstr9Table8ItcInwdSuppReqDto(
					gstr9Table8ItcInwdSuppReqDto);
		}

		else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_8E)) {
			Gstr9Table8ItcNtAvaildReqDto gstr9Table8ItcNtAvaildReqDto = new Gstr9Table8ItcNtAvaildReqDto();
			gstr9Table8ItcNtAvaildReqDto
					.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table8ItcNtAvaildReqDto
					.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table8ItcNtAvaildReqDto
					.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table8ItcNtAvaildReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table8ReqDto.setGstr9Table8ItcNtAvaildReqDto(
					gstr9Table8ItcNtAvaildReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_8F)) {
			Gstr9Table8ItcNtElegReqDto gstr9Table8ItcNtElegReqDto = new Gstr9Table8ItcNtElegReqDto();
			gstr9Table8ItcNtElegReqDto
					.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table8ItcNtElegReqDto
					.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table8ItcNtElegReqDto
					.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table8ItcNtElegReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table8ReqDto
					.setGstr9Table8ItcNtElegReqDto(gstr9Table8ItcNtElegReqDto);
		}

		else if (e.getSubSection()
				.equalsIgnoreCase(GSTR9InwardConstants.Table_8G)) {
			Gstr9Table8IogTaxPaidReqDto gstr9Table8IogTaxPaidReqDto = new Gstr9Table8IogTaxPaidReqDto();
			gstr9Table8IogTaxPaidReqDto
					.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table8IogTaxPaidReqDto
					.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table8IogTaxPaidReqDto
					.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table8IogTaxPaidReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table8ReqDto.setGstr9Table8IogTaxPaidReqDto(
					gstr9Table8IogTaxPaidReqDto);

		}

		return gstr9Table8ReqDto;

	}

	private Gstr9Table9ReqDto convertEntityToTable9Dto(Gstr9UserInputEntity e,
			Gstr9Table9ReqDto gstr9Table9ReqDto) {

		if (gstr9Table9ReqDto == null) {
			gstr9Table9ReqDto = new Gstr9Table9ReqDto();

			Gstr9Table9IamtReqDto gstr9Table9IamtReqDto = new Gstr9Table9IamtReqDto();
			Gstr9Table9CamtReqDto gstr9Table9CamtReqDto = new Gstr9Table9CamtReqDto();
			Gstr9Table9SamtReqDto gstr9Table9SamtReqDto = new Gstr9Table9SamtReqDto();
			Gstr9Table9CSamtReqDto gstr9Table9CsamtReqDto = new Gstr9Table9CSamtReqDto();
			Gstr9Table9IntrReqDto gstr9Table9IntrReqDto = new Gstr9Table9IntrReqDto();
			Gstr9Table9TeeReqDto gstr9Table9TeeReqDto = new Gstr9Table9TeeReqDto();
			Gstr9Table9PenReqDto gstr9Table9PenReqDto = new Gstr9Table9PenReqDto();
			Gstr9Table9OtherReqDto gstr9Table9OtherReqDto = new Gstr9Table9OtherReqDto();

			gstr9Table9ReqDto.setGstr9Table9IamtReqDto(gstr9Table9IamtReqDto);
			gstr9Table9ReqDto.setGstr9Table9CamtReqDto(gstr9Table9CamtReqDto);
			gstr9Table9ReqDto.setGstr9Table9SamtReqDto(gstr9Table9SamtReqDto);
			gstr9Table9ReqDto.setGstr9Table9CsamtReqDto(gstr9Table9CsamtReqDto);
			gstr9Table9ReqDto.setGstr9Table9IntrReqDto(gstr9Table9IntrReqDto);
			gstr9Table9ReqDto.setGstr9Table9TeeReqDto(gstr9Table9TeeReqDto);
			gstr9Table9ReqDto.setGstr9Table9PenReqDto(gstr9Table9PenReqDto);
			gstr9Table9ReqDto.setGstr9Table9OtherReqDto(gstr9Table9OtherReqDto);

		}

		if (e.getSubSection().equalsIgnoreCase(Gstr9TaxPaidConstants.Table_9)) {

			gstr9Table9ReqDto.getGstr9Table9IamtReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getIgst()));
			gstr9Table9ReqDto.getGstr9Table9CamtReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getCgst()));
			gstr9Table9ReqDto.getGstr9Table9SamtReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getSgst()));
			gstr9Table9ReqDto.getGstr9Table9CsamtReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getCess()));
			gstr9Table9ReqDto.getGstr9Table9IntrReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getIntr()));
			gstr9Table9ReqDto.getGstr9Table9TeeReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getFee()));
			gstr9Table9ReqDto.getGstr9Table9PenReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getPen()));
			gstr9Table9ReqDto.getGstr9Table9OtherReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getOth()));
		}
		return gstr9Table9ReqDto;

	}

	private Gstr9Table9ReqDto convertGetEntityToTable9Dto(
			Gstr9GetSummaryEntity e, Gstr9Table9ReqDto gstr9Table9ReqDto) {

		if (gstr9Table9ReqDto == null) {
			gstr9Table9ReqDto = new Gstr9Table9ReqDto();

			Gstr9Table9IamtReqDto gstr9Table9IamtReqDto = new Gstr9Table9IamtReqDto();
			Gstr9Table9CamtReqDto gstr9Table9CamtReqDto = new Gstr9Table9CamtReqDto();
			Gstr9Table9SamtReqDto gstr9Table9SamtReqDto = new Gstr9Table9SamtReqDto();
			Gstr9Table9CSamtReqDto gstr9Table9CsamtReqDto = new Gstr9Table9CSamtReqDto();
			Gstr9Table9IntrReqDto gstr9Table9IntrReqDto = new Gstr9Table9IntrReqDto();
			Gstr9Table9TeeReqDto gstr9Table9TeeReqDto = new Gstr9Table9TeeReqDto();
			Gstr9Table9PenReqDto gstr9Table9PenReqDto = new Gstr9Table9PenReqDto();
			Gstr9Table9OtherReqDto gstr9Table9OtherReqDto = new Gstr9Table9OtherReqDto();

			gstr9Table9ReqDto.setGstr9Table9IamtReqDto(gstr9Table9IamtReqDto);
			gstr9Table9ReqDto.setGstr9Table9CamtReqDto(gstr9Table9CamtReqDto);
			gstr9Table9ReqDto.setGstr9Table9SamtReqDto(gstr9Table9SamtReqDto);
			gstr9Table9ReqDto.setGstr9Table9CsamtReqDto(gstr9Table9CsamtReqDto);
			gstr9Table9ReqDto.setGstr9Table9IntrReqDto(gstr9Table9IntrReqDto);
			gstr9Table9ReqDto.setGstr9Table9TeeReqDto(gstr9Table9TeeReqDto);
			gstr9Table9ReqDto.setGstr9Table9PenReqDto(gstr9Table9PenReqDto);
			gstr9Table9ReqDto.setGstr9Table9OtherReqDto(gstr9Table9OtherReqDto);

		}

		if (e.getSubSection().equalsIgnoreCase(Gstr9TaxPaidConstants.Table_9)) {

			gstr9Table9ReqDto.getGstr9Table9IamtReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getIamt()));
			gstr9Table9ReqDto.getGstr9Table9CamtReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getCamt()));
			gstr9Table9ReqDto.getGstr9Table9SamtReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getSamt()));
			gstr9Table9ReqDto.getGstr9Table9CsamtReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table9ReqDto.getGstr9Table9IntrReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getIntr()));
			gstr9Table9ReqDto.getGstr9Table9TeeReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getFee()));
			gstr9Table9ReqDto.getGstr9Table9PenReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getPen()));
			gstr9Table9ReqDto.getGstr9Table9OtherReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getOth()));
		}
		return gstr9Table9ReqDto;

	}

	private Gstr9Table10ReqDto convertEntityToTable10Dto(Gstr9UserInputEntity e,
			Gstr9Table10ReqDto gstr9Table10ReqDto) {

		if (gstr9Table10ReqDto == null) {
			gstr9Table10ReqDto = new Gstr9Table10ReqDto();

			// Initialize all other required objects
			Gstr9Table10DbnAmdReqDto dbnAmdReqDto = new Gstr9Table10DbnAmdReqDto();
			Gstr9Table10CdnAmdReqDto cdnAmdReqDto = new Gstr9Table10CdnAmdReqDto();
			Gstr9Table10ItcRvslReqDto gstr9Table10ItcRvslReqDto = new Gstr9Table10ItcRvslReqDto();
			Gstr9Table10ItcAvaildReqDto gstr9Table10ItcAvaildReqDto = new Gstr9Table10ItcAvaildReqDto();
			// Gstr9Table10TotalTurnOverReqDto gstr9Table10TotalTurnOverReqDto =
			// new Gstr9Table10TotalTurnOverReqDto();

			// Set the objects using the setter methods
			gstr9Table10ReqDto.setDbnAmdReqDto(dbnAmdReqDto);
			gstr9Table10ReqDto.setCdnAmdReqDto(cdnAmdReqDto);
			gstr9Table10ReqDto
					.setGstr9Table10ItcRvslReqDto(gstr9Table10ItcRvslReqDto);
			gstr9Table10ReqDto.setGstr9Table10ItcAvaildReqDto(
					gstr9Table10ItcAvaildReqDto);
			// gstr9Table10ReqDto.setGstr9Table10TotalTurnOverReqDto(gstr9Table10TotalTurnOverReqDto);

		}

		if (e.getSection()
				.equalsIgnoreCase(Gstr9PyTransInCyConstants.Table_13)) {
			Gstr9Table10ItcAvaildReqDto gstr9Table10ItcAvaildReqDto = new Gstr9Table10ItcAvaildReqDto();
			gstr9Table10ItcAvaildReqDto
					.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table10ItcAvaildReqDto
					.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table10ItcAvaildReqDto
					.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table10ItcAvaildReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table10ReqDto.setGstr9Table10ItcAvaildReqDto(
					gstr9Table10ItcAvaildReqDto);

		} else if (e.getSection()
				.equalsIgnoreCase(Gstr9PyTransInCyConstants.Table_12)) {

			Gstr9Table10ItcRvslReqDto gstr9Table10ItcRvslReqDto = new Gstr9Table10ItcRvslReqDto();
			gstr9Table10ItcRvslReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table10ItcRvslReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table10ItcRvslReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table10ItcRvslReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table10ReqDto
					.setGstr9Table10ItcRvslReqDto(gstr9Table10ItcRvslReqDto);

		} else if (e.getSection()
				.equalsIgnoreCase(Gstr9PyTransInCyConstants.Table_10)) {
			Gstr9Table10DbnAmdReqDto dbnAmdReqDto = new Gstr9Table10DbnAmdReqDto();
			dbnAmdReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			dbnAmdReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			dbnAmdReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			dbnAmdReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			dbnAmdReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table10ReqDto.setDbnAmdReqDto(dbnAmdReqDto);
		} else if (e.getSection()
				.equalsIgnoreCase(Gstr9PyTransInCyConstants.Table_11)) {
			Gstr9Table10CdnAmdReqDto cdnAmdReqDto = new Gstr9Table10CdnAmdReqDto();
			cdnAmdReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			cdnAmdReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			cdnAmdReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			cdnAmdReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			cdnAmdReqDto.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table10ReqDto.setCdnAmdReqDto(cdnAmdReqDto);

		}

		return gstr9Table10ReqDto;

	}

	private Gstr9Table10ReqDto convertGetEntityToTable10Dto(
			Gstr9GetSummaryEntity e, Gstr9Table10ReqDto gstr9Table10ReqDto) {

		if (gstr9Table10ReqDto == null) {
			gstr9Table10ReqDto = new Gstr9Table10ReqDto();

			// Initialize all other required objects
			Gstr9Table10DbnAmdReqDto dbnAmdReqDto = new Gstr9Table10DbnAmdReqDto();
			Gstr9Table10CdnAmdReqDto cdnAmdReqDto = new Gstr9Table10CdnAmdReqDto();
			Gstr9Table10ItcRvslReqDto gstr9Table10ItcRvslReqDto = new Gstr9Table10ItcRvslReqDto();
			Gstr9Table10ItcAvaildReqDto gstr9Table10ItcAvaildReqDto = new Gstr9Table10ItcAvaildReqDto();
			// Gstr9Table10TotalTurnOverReqDto gstr9Table10TotalTurnOverReqDto =
			// new Gstr9Table10TotalTurnOverReqDto();

			// Set the objects using the setter methods
			gstr9Table10ReqDto.setDbnAmdReqDto(dbnAmdReqDto);
			gstr9Table10ReqDto.setCdnAmdReqDto(cdnAmdReqDto);
			gstr9Table10ReqDto
					.setGstr9Table10ItcRvslReqDto(gstr9Table10ItcRvslReqDto);
			gstr9Table10ReqDto.setGstr9Table10ItcAvaildReqDto(
					gstr9Table10ItcAvaildReqDto);
			// gstr9Table10ReqDto.setGstr9Table10TotalTurnOverReqDto(gstr9Table10TotalTurnOverReqDto);
		}

		if (e.getSection()
				.equalsIgnoreCase(Gstr9PyTransInCyConstants.Table_13)) {
			Gstr9Table10ItcAvaildReqDto gstr9Table10ItcAvaildReqDto = new Gstr9Table10ItcAvaildReqDto();
			gstr9Table10ItcAvaildReqDto
					.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table10ItcAvaildReqDto
					.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table10ItcAvaildReqDto
					.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table10ItcAvaildReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table10ReqDto.setGstr9Table10ItcAvaildReqDto(
					gstr9Table10ItcAvaildReqDto);

		} else if (e.getSection()
				.equalsIgnoreCase(Gstr9PyTransInCyConstants.Table_12)) {

			Gstr9Table10ItcRvslReqDto gstr9Table10ItcRvslReqDto = new Gstr9Table10ItcRvslReqDto();
			gstr9Table10ItcRvslReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table10ItcRvslReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table10ItcRvslReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table10ItcRvslReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table10ReqDto
					.setGstr9Table10ItcRvslReqDto(gstr9Table10ItcRvslReqDto);

		} else if (e.getSection()
				.equalsIgnoreCase(Gstr9PyTransInCyConstants.Table_10)) {
			Gstr9Table10DbnAmdReqDto dbnAmdReqDto = new Gstr9Table10DbnAmdReqDto();
			dbnAmdReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			dbnAmdReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			dbnAmdReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			dbnAmdReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			dbnAmdReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table10ReqDto.setDbnAmdReqDto(dbnAmdReqDto);
		} else if (e.getSection()
				.equalsIgnoreCase(Gstr9PyTransInCyConstants.Table_11)) {
			Gstr9Table10CdnAmdReqDto cdnAmdReqDto = new Gstr9Table10CdnAmdReqDto();
			cdnAmdReqDto.setTxval(defaultToZeroIfNull(e.getTxVal()));
			cdnAmdReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			cdnAmdReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			cdnAmdReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			cdnAmdReqDto.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table10ReqDto.setCdnAmdReqDto(cdnAmdReqDto);

		}

		return gstr9Table10ReqDto;

	}

	private Gstr9Table14ReqDto convertEntityToTable14Dto(Gstr9UserInputEntity e,
			Gstr9Table14ReqDto gstr9Table14ReqDto,
			Gstr9Table14IamtReqDto gstr9Table14IamtReqDto,
			Gstr9Table14SamtReqDto gstr9Table14SamtReqDto,
			Gstr9Table14CamtReqDto gstr9Table14CamtReqDto,
			Gstr9Table14CSamtReqDto gstr9Table14CSamtReqDto,
			Gstr9Table14IntrReqDto gstr9Table14IntrReqDto,
			boolean isTxpybleReq) {

		if (gstr9Table14ReqDto == null) {
			gstr9Table14ReqDto = new Gstr9Table14ReqDto();

			gstr9Table14IamtReqDto = new Gstr9Table14IamtReqDto();
			gstr9Table14SamtReqDto = new Gstr9Table14SamtReqDto();
			gstr9Table14CamtReqDto = new Gstr9Table14CamtReqDto();
			gstr9Table14CSamtReqDto = new Gstr9Table14CSamtReqDto();
			gstr9Table14IntrReqDto = new Gstr9Table14IntrReqDto();

			gstr9Table14ReqDto
					.setGstr9Table14IamtReqDto(gstr9Table14IamtReqDto);
			gstr9Table14ReqDto
					.setGstr9Table14SamtReqDto(gstr9Table14SamtReqDto);
			gstr9Table14ReqDto
					.setGstr9Table14CamtReqDto(gstr9Table14CamtReqDto);
			gstr9Table14ReqDto
					.setGstr9Table14CSamtReqDto(gstr9Table14CSamtReqDto);
			gstr9Table14ReqDto
					.setGstr9Table14IntrReqDto(gstr9Table14IntrReqDto);

		}

		if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_14_1)) {
			if (isTxpybleReq) {
				gstr9Table14ReqDto.getGstr9Table14IamtReqDto()
						.setTxpyble(defaultToZeroIfNull(e.getIgst()));
				gstr9Table14ReqDto.getGstr9Table14SamtReqDto()
						.setTxpyble(defaultToZeroIfNull(e.getSgst()));
				gstr9Table14ReqDto.getGstr9Table14CamtReqDto()
						.setTxpyble(defaultToZeroIfNull(e.getCgst()));
				gstr9Table14ReqDto.getGstr9Table14CSamtReqDto()
						.setTxpyble(defaultToZeroIfNull(e.getCess()));
			}
			gstr9Table14ReqDto.getGstr9Table14IntrReqDto()
					.setTxpyble(e.getIntr());

		}

		if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_14_2)) {
			gstr9Table14ReqDto.getGstr9Table14IamtReqDto()
					.setTxpaid(defaultToZeroIfNull(e.getIgst()));
			gstr9Table14ReqDto.getGstr9Table14SamtReqDto()
					.setTxpaid(defaultToZeroIfNull(e.getSgst()));
			gstr9Table14ReqDto.getGstr9Table14CamtReqDto()
					.setTxpaid(defaultToZeroIfNull(e.getCgst()));
			gstr9Table14ReqDto.getGstr9Table14CSamtReqDto()
					.setTxpaid(defaultToZeroIfNull(e.getCess()));
			gstr9Table14ReqDto.getGstr9Table14IntrReqDto()
					.setTxpaid(defaultToZeroIfNull(e.getIntr()));

		}

		return gstr9Table14ReqDto;

	}

	private Gstr9Table14ReqDto convertGetEntityToTable14Dto(
			Gstr9GetSummaryEntity e, Gstr9Table14ReqDto gstr9Table14ReqDto,
			Gstr9Table14IamtReqDto gstr9Table14IamtReqDto,
			Gstr9Table14SamtReqDto gstr9Table14SamtReqDto,
			Gstr9Table14CamtReqDto gstr9Table14CamtReqDto,
			Gstr9Table14CSamtReqDto gstr9Table14CSamtReqDto,
			Gstr9Table14IntrReqDto gstr9Table14IntrReqDto) {

		if (gstr9Table14ReqDto == null) {
			gstr9Table14ReqDto = new Gstr9Table14ReqDto();

			gstr9Table14IamtReqDto = new Gstr9Table14IamtReqDto();
			gstr9Table14SamtReqDto = new Gstr9Table14SamtReqDto();
			gstr9Table14CamtReqDto = new Gstr9Table14CamtReqDto();
			gstr9Table14CSamtReqDto = new Gstr9Table14CSamtReqDto();
			gstr9Table14IntrReqDto = new Gstr9Table14IntrReqDto();

			gstr9Table14ReqDto
					.setGstr9Table14IamtReqDto(gstr9Table14IamtReqDto);
			gstr9Table14ReqDto
					.setGstr9Table14SamtReqDto(gstr9Table14SamtReqDto);
			gstr9Table14ReqDto
					.setGstr9Table14CamtReqDto(gstr9Table14CamtReqDto);
			gstr9Table14ReqDto
					.setGstr9Table14CSamtReqDto(gstr9Table14CSamtReqDto);
			gstr9Table14ReqDto
					.setGstr9Table14IntrReqDto(gstr9Table14IntrReqDto);

		}

		if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_14_1)) {

			gstr9Table14ReqDto.getGstr9Table14IamtReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getIamt()));
			gstr9Table14ReqDto.getGstr9Table14SamtReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getSamt()));
			gstr9Table14ReqDto.getGstr9Table14CamtReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getCamt()));
			gstr9Table14ReqDto.getGstr9Table14CSamtReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table14ReqDto.getGstr9Table14IntrReqDto()
					.setTxpyble(defaultToZeroIfNull(e.getIntr()));

		}

		if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_14_2)) {
			gstr9Table14ReqDto.getGstr9Table14IamtReqDto()
					.setTxpaid(defaultToZeroIfNull(e.getIamt()));
			gstr9Table14ReqDto.getGstr9Table14SamtReqDto()
					.setTxpaid(defaultToZeroIfNull(e.getSamt()));
			gstr9Table14ReqDto.getGstr9Table14CamtReqDto()
					.setTxpaid(defaultToZeroIfNull(e.getCamt()));
			gstr9Table14ReqDto.getGstr9Table14CSamtReqDto()
					.setTxpaid(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table14ReqDto.getGstr9Table14IntrReqDto()
					.setTxpaid(defaultToZeroIfNull(e.getIntr()));

		}

		return gstr9Table14ReqDto;

	}

	private Gstr9Table15ReqDto convertEntityToTable15Dto(Gstr9UserInputEntity e,
			Gstr9Table15ReqDto gstr9Table15ReqDto) {

		if (gstr9Table15ReqDto == null) {
			gstr9Table15ReqDto = new Gstr9Table15ReqDto();

			// Initialize all other required objects
			Gstr9Table15RfdClmdReqDto gstr9Table15RfdClmdReqDto = new Gstr9Table15RfdClmdReqDto();
			Gstr9Table15RfdSancReqDto gstr9Table15RfdSancReqDto = new Gstr9Table15RfdSancReqDto();
			Gstr9Table15RfdRejtReqDto gstr9Table15RfdRejtReqDto = new Gstr9Table15RfdRejtReqDto();
			Gstr9Table15RfdPendReqDto gstr9Table15RfdPendReqDto = new Gstr9Table15RfdPendReqDto();
			Gstr9Table15TaxDmndtReqDto gstr9Table15TaxDmndtReqDto = new Gstr9Table15TaxDmndtReqDto();
			Gstr9Table15TaxPaidReqDto gstr9Table15TaxPaidReqDto = new Gstr9Table15TaxPaidReqDto();
			Gstr9Table15TotalDmndPendReqDto gstr9Table15TotalDmndPendReqDto = new Gstr9Table15TotalDmndPendReqDto();

			// Set the objects using the setter methods
			gstr9Table15ReqDto
					.setGstr9Table15RfdClmdReqDto(gstr9Table15RfdClmdReqDto);
			gstr9Table15ReqDto
					.setGstr9Table15RfdSancReqDto(gstr9Table15RfdSancReqDto);
			gstr9Table15ReqDto
					.setGstr9Table15RfdRejtReqDto(gstr9Table15RfdRejtReqDto);
			gstr9Table15ReqDto
					.setGstr9Table15RfdPendReqDto(gstr9Table15RfdPendReqDto);
			gstr9Table15ReqDto
					.setGstr9Table15TaxDmndtReqDto(gstr9Table15TaxDmndtReqDto);
			gstr9Table15ReqDto
					.setGstr9Table15TaxPaidReqDto(gstr9Table15TaxPaidReqDto);
			gstr9Table15ReqDto.setGstr9Table15TotalDmndPendReqDto(
					gstr9Table15TotalDmndPendReqDto);

		}

		if (e.getSubSection().equalsIgnoreCase(Gstr9OtherConstants.Table_15A)) {
			Gstr9Table15RfdClmdReqDto gstr9Table15RfdClmdReqDto = new Gstr9Table15RfdClmdReqDto();
			gstr9Table15RfdClmdReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table15RfdClmdReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table15RfdClmdReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table15RfdClmdReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table15ReqDto
					.setGstr9Table15RfdClmdReqDto(gstr9Table15RfdClmdReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_15B)) {
			Gstr9Table15RfdSancReqDto gstr9Table15RfdSancReqDto = new Gstr9Table15RfdSancReqDto();
			gstr9Table15RfdSancReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table15RfdSancReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table15RfdSancReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table15RfdSancReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table15ReqDto
					.setGstr9Table15RfdSancReqDto(gstr9Table15RfdSancReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_15C)) {
			Gstr9Table15RfdRejtReqDto gstr9Table15RfdRejtReqDto = new Gstr9Table15RfdRejtReqDto();
			gstr9Table15RfdRejtReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table15RfdRejtReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table15RfdRejtReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table15RfdRejtReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table15ReqDto
					.setGstr9Table15RfdRejtReqDto(gstr9Table15RfdRejtReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_15D)) {
			Gstr9Table15RfdPendReqDto gstr9Table15RfdPendReqDto = new Gstr9Table15RfdPendReqDto();
			gstr9Table15RfdPendReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table15RfdPendReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table15RfdPendReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table15RfdPendReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table15ReqDto
					.setGstr9Table15RfdPendReqDto(gstr9Table15RfdPendReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_15E)) {
			Gstr9Table15TaxDmndtReqDto gstr9Table15TaxDmndtReqDto = new Gstr9Table15TaxDmndtReqDto();
			gstr9Table15TaxDmndtReqDto
					.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table15TaxDmndtReqDto
					.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table15TaxDmndtReqDto
					.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table15TaxDmndtReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table15TaxDmndtReqDto
					.setIntr(defaultToZeroIfNull(e.getIntr()));
			gstr9Table15TaxDmndtReqDto.setFee(defaultToZeroIfNull(e.getFee()));
			gstr9Table15TaxDmndtReqDto.setPen(defaultToZeroIfNull(e.getPen()));
			gstr9Table15ReqDto
					.setGstr9Table15TaxDmndtReqDto(gstr9Table15TaxDmndtReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_15F)) {
			Gstr9Table15TaxPaidReqDto gstr9Table15TaxPaidReqDto = new Gstr9Table15TaxPaidReqDto();
			gstr9Table15TaxPaidReqDto.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table15TaxPaidReqDto.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table15TaxPaidReqDto.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table15TaxPaidReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table15TaxPaidReqDto.setIntr(defaultToZeroIfNull(e.getIntr()));
			gstr9Table15TaxPaidReqDto.setFee(defaultToZeroIfNull(e.getFee()));
			gstr9Table15TaxPaidReqDto.setPen(defaultToZeroIfNull(e.getPen()));

			gstr9Table15ReqDto
					.setGstr9Table15TaxPaidReqDto(gstr9Table15TaxPaidReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_15G)) {
			Gstr9Table15TotalDmndPendReqDto gstr9Table15TotalDmndPendReqDto = new Gstr9Table15TotalDmndPendReqDto();
			gstr9Table15TotalDmndPendReqDto
					.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table15TotalDmndPendReqDto
					.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table15TotalDmndPendReqDto
					.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table15TotalDmndPendReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table15TotalDmndPendReqDto
					.setIntr(defaultToZeroIfNull(e.getIntr()));
			gstr9Table15TotalDmndPendReqDto
					.setFee(defaultToZeroIfNull(e.getFee()));
			gstr9Table15TotalDmndPendReqDto
					.setPen(defaultToZeroIfNull(e.getPen()));

			gstr9Table15ReqDto.setGstr9Table15TotalDmndPendReqDto(
					gstr9Table15TotalDmndPendReqDto);

		}

		return gstr9Table15ReqDto;

	}

	private Gstr9Table15ReqDto convertGetEntityToTable15Dto(
			Gstr9GetSummaryEntity e, Gstr9Table15ReqDto gstr9Table15ReqDto) {

		if (gstr9Table15ReqDto == null) {
			gstr9Table15ReqDto = new Gstr9Table15ReqDto();

			// Initialize all other required objects
			Gstr9Table15RfdClmdReqDto gstr9Table15RfdClmdReqDto = new Gstr9Table15RfdClmdReqDto();
			Gstr9Table15RfdSancReqDto gstr9Table15RfdSancReqDto = new Gstr9Table15RfdSancReqDto();
			Gstr9Table15RfdRejtReqDto gstr9Table15RfdRejtReqDto = new Gstr9Table15RfdRejtReqDto();
			Gstr9Table15RfdPendReqDto gstr9Table15RfdPendReqDto = new Gstr9Table15RfdPendReqDto();
			Gstr9Table15TaxDmndtReqDto gstr9Table15TaxDmndtReqDto = new Gstr9Table15TaxDmndtReqDto();
			Gstr9Table15TaxPaidReqDto gstr9Table15TaxPaidReqDto = new Gstr9Table15TaxPaidReqDto();
			Gstr9Table15TotalDmndPendReqDto gstr9Table15TotalDmndPendReqDto = new Gstr9Table15TotalDmndPendReqDto();

			// Set the objects using the setter methods
			gstr9Table15ReqDto
					.setGstr9Table15RfdClmdReqDto(gstr9Table15RfdClmdReqDto);
			gstr9Table15ReqDto
					.setGstr9Table15RfdSancReqDto(gstr9Table15RfdSancReqDto);
			gstr9Table15ReqDto
					.setGstr9Table15RfdRejtReqDto(gstr9Table15RfdRejtReqDto);
			gstr9Table15ReqDto
					.setGstr9Table15RfdPendReqDto(gstr9Table15RfdPendReqDto);
			gstr9Table15ReqDto
					.setGstr9Table15TaxDmndtReqDto(gstr9Table15TaxDmndtReqDto);
			gstr9Table15ReqDto
					.setGstr9Table15TaxPaidReqDto(gstr9Table15TaxPaidReqDto);
			gstr9Table15ReqDto.setGstr9Table15TotalDmndPendReqDto(
					gstr9Table15TotalDmndPendReqDto);
		}

		if (e.getSubSection().equalsIgnoreCase(Gstr9OtherConstants.Table_15A)) {
			Gstr9Table15RfdClmdReqDto gstr9Table15RfdClmdReqDto = new Gstr9Table15RfdClmdReqDto();
			gstr9Table15RfdClmdReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table15RfdClmdReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table15RfdClmdReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table15RfdClmdReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table15ReqDto
					.setGstr9Table15RfdClmdReqDto(gstr9Table15RfdClmdReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_15B)) {
			Gstr9Table15RfdSancReqDto gstr9Table15RfdSancReqDto = new Gstr9Table15RfdSancReqDto();
			gstr9Table15RfdSancReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table15RfdSancReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table15RfdSancReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table15RfdSancReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table15ReqDto
					.setGstr9Table15RfdSancReqDto(gstr9Table15RfdSancReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_15C)) {
			Gstr9Table15RfdRejtReqDto gstr9Table15RfdRejtReqDto = new Gstr9Table15RfdRejtReqDto();
			gstr9Table15RfdRejtReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table15RfdRejtReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table15RfdRejtReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table15RfdRejtReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table15ReqDto
					.setGstr9Table15RfdRejtReqDto(gstr9Table15RfdRejtReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_15D)) {
			Gstr9Table15RfdPendReqDto gstr9Table15RfdPendReqDto = new Gstr9Table15RfdPendReqDto();
			gstr9Table15RfdPendReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table15RfdPendReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table15RfdPendReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table15RfdPendReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table15ReqDto
					.setGstr9Table15RfdPendReqDto(gstr9Table15RfdPendReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_15E)) {
			Gstr9Table15TaxDmndtReqDto gstr9Table15TaxDmndtReqDto = new Gstr9Table15TaxDmndtReqDto();
			gstr9Table15TaxDmndtReqDto
					.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table15TaxDmndtReqDto
					.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table15TaxDmndtReqDto
					.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table15TaxDmndtReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table15TaxDmndtReqDto
					.setIntr(defaultToZeroIfNull(e.getIntr()));
			gstr9Table15TaxDmndtReqDto.setFee(defaultToZeroIfNull(e.getFee()));
			gstr9Table15TaxDmndtReqDto.setPen(defaultToZeroIfNull(e.getPen()));
			gstr9Table15ReqDto
					.setGstr9Table15TaxDmndtReqDto(gstr9Table15TaxDmndtReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_15F)) {
			Gstr9Table15TaxPaidReqDto gstr9Table15TaxPaidReqDto = new Gstr9Table15TaxPaidReqDto();
			gstr9Table15TaxPaidReqDto.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table15TaxPaidReqDto.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table15TaxPaidReqDto.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table15TaxPaidReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table15TaxPaidReqDto.setIntr(defaultToZeroIfNull(e.getIntr()));
			gstr9Table15TaxPaidReqDto.setFee(defaultToZeroIfNull(e.getFee()));
			gstr9Table15TaxPaidReqDto.setPen(defaultToZeroIfNull(e.getPen()));

			gstr9Table15ReqDto
					.setGstr9Table15TaxPaidReqDto(gstr9Table15TaxPaidReqDto);

		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_15G)) {
			Gstr9Table15TotalDmndPendReqDto gstr9Table15TotalDmndPendReqDto = new Gstr9Table15TotalDmndPendReqDto();
			gstr9Table15TotalDmndPendReqDto
					.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table15TotalDmndPendReqDto
					.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table15TotalDmndPendReqDto
					.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table15TotalDmndPendReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table15TotalDmndPendReqDto
					.setIntr(defaultToZeroIfNull(e.getIntr()));
			gstr9Table15TotalDmndPendReqDto
					.setFee(defaultToZeroIfNull(e.getFee()));
			gstr9Table15TotalDmndPendReqDto
					.setPen(defaultToZeroIfNull(e.getPen()));

			gstr9Table15ReqDto.setGstr9Table15TotalDmndPendReqDto(
					gstr9Table15TotalDmndPendReqDto);

		}

		return gstr9Table15ReqDto;

	}

	private Gstr9Table16ReqDto convertEntityToTable16Dto(Gstr9UserInputEntity e,
			Gstr9Table16ReqDto gstr9Table16ReqDto) {

		if (gstr9Table16ReqDto == null) {
			gstr9Table16ReqDto = new Gstr9Table16ReqDto();

			// Initialize all other required objects
			Gstr9Table16CompSuppReqDto gstr9Table16CompSuppReqDto = new Gstr9Table16CompSuppReqDto();
			Gstr9Table16DeemedSuppReqDto gstr9Table16DeemedSuppReqDto = new Gstr9Table16DeemedSuppReqDto();
			Gstr9Table16NotReturnedReqDto gstr9Table16NotReturnedReqDto = new Gstr9Table16NotReturnedReqDto();

			// Set the objects using the setter methods
			gstr9Table16ReqDto
					.setGstr9Table16CompSuppReqDto(gstr9Table16CompSuppReqDto);
			gstr9Table16ReqDto.setGstr9Table16DeemedSuppReqDto(
					gstr9Table16DeemedSuppReqDto);
			gstr9Table16ReqDto.setGstr9Table16NotReturnedReqDto(
					gstr9Table16NotReturnedReqDto);

		}

		if (e.getSubSection().equalsIgnoreCase(Gstr9OtherConstants.Table_16A)) {
			Gstr9Table16CompSuppReqDto gstr9Table16CompSuppReqDto = new Gstr9Table16CompSuppReqDto();
			gstr9Table16CompSuppReqDto
					.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table16ReqDto
					.setGstr9Table16CompSuppReqDto(gstr9Table16CompSuppReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_16B)) {
			Gstr9Table16DeemedSuppReqDto gstr9Table16DeemedSuppReqDto = new Gstr9Table16DeemedSuppReqDto();
			gstr9Table16DeemedSuppReqDto
					.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table16DeemedSuppReqDto
					.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table16DeemedSuppReqDto
					.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table16DeemedSuppReqDto
					.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table16DeemedSuppReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table16ReqDto.setGstr9Table16DeemedSuppReqDto(
					gstr9Table16DeemedSuppReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_16C)) {
			Gstr9Table16NotReturnedReqDto gstr9Table16NotReturnedReqDto = new Gstr9Table16NotReturnedReqDto();
			gstr9Table16NotReturnedReqDto
					.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table16NotReturnedReqDto
					.setIamt(defaultToZeroIfNull(e.getIgst()));
			gstr9Table16NotReturnedReqDto
					.setCamt(defaultToZeroIfNull(e.getCgst()));
			gstr9Table16NotReturnedReqDto
					.setSamt(defaultToZeroIfNull(e.getSgst()));
			gstr9Table16NotReturnedReqDto
					.setCsamt(defaultToZeroIfNull(e.getCess()));
			gstr9Table16ReqDto.setGstr9Table16NotReturnedReqDto(
					gstr9Table16NotReturnedReqDto);
		}

		return gstr9Table16ReqDto;

	}

	private Gstr9Table16ReqDto convertGetEntityToTable16Dto(
			Gstr9GetSummaryEntity e, Gstr9Table16ReqDto gstr9Table16ReqDto) {

		if (gstr9Table16ReqDto == null) {
			gstr9Table16ReqDto = new Gstr9Table16ReqDto();

			// Initialize all other required objects
			Gstr9Table16CompSuppReqDto gstr9Table16CompSuppReqDto = new Gstr9Table16CompSuppReqDto();
			Gstr9Table16DeemedSuppReqDto gstr9Table16DeemedSuppReqDto = new Gstr9Table16DeemedSuppReqDto();
			Gstr9Table16NotReturnedReqDto gstr9Table16NotReturnedReqDto = new Gstr9Table16NotReturnedReqDto();

			// Set the objects using the setter methods
			gstr9Table16ReqDto
					.setGstr9Table16CompSuppReqDto(gstr9Table16CompSuppReqDto);
			gstr9Table16ReqDto.setGstr9Table16DeemedSuppReqDto(
					gstr9Table16DeemedSuppReqDto);
			gstr9Table16ReqDto.setGstr9Table16NotReturnedReqDto(
					gstr9Table16NotReturnedReqDto);

		}

		if (e.getSubSection().equalsIgnoreCase(Gstr9OtherConstants.Table_16A)) {
			Gstr9Table16CompSuppReqDto gstr9Table16CompSuppReqDto = new Gstr9Table16CompSuppReqDto();
			gstr9Table16CompSuppReqDto
					.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table16ReqDto
					.setGstr9Table16CompSuppReqDto(gstr9Table16CompSuppReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_16B)) {
			Gstr9Table16DeemedSuppReqDto gstr9Table16DeemedSuppReqDto = new Gstr9Table16DeemedSuppReqDto();
			gstr9Table16DeemedSuppReqDto
					.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table16DeemedSuppReqDto
					.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table16DeemedSuppReqDto
					.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table16DeemedSuppReqDto
					.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table16DeemedSuppReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table16ReqDto.setGstr9Table16DeemedSuppReqDto(
					gstr9Table16DeemedSuppReqDto);
		} else if (e.getSubSection()
				.equalsIgnoreCase(Gstr9OtherConstants.Table_16C)) {
			Gstr9Table16NotReturnedReqDto gstr9Table16NotReturnedReqDto = new Gstr9Table16NotReturnedReqDto();
			gstr9Table16NotReturnedReqDto
					.setTxval(defaultToZeroIfNull(e.getTxVal()));
			gstr9Table16NotReturnedReqDto
					.setIamt(defaultToZeroIfNull(e.getIamt()));
			gstr9Table16NotReturnedReqDto
					.setCamt(defaultToZeroIfNull(e.getCamt()));
			gstr9Table16NotReturnedReqDto
					.setSamt(defaultToZeroIfNull(e.getSamt()));
			gstr9Table16NotReturnedReqDto
					.setCsamt(defaultToZeroIfNull(e.getCsamt()));
			gstr9Table16ReqDto.setGstr9Table16NotReturnedReqDto(
					gstr9Table16NotReturnedReqDto);
		}

		return gstr9Table16ReqDto;

	}

	private Gstr9Table17ReqDto convertEntityToTable17Dto(
			Gstr9HsnProcessEntity e, Gstr9Table17ReqDto gstr9Table17ReqDto) {
		Gstr9Table17ItemsReqDto gstr9Table17ItemsReqDto = new Gstr9Table17ItemsReqDto();

		if (gstr9Table17ReqDto == null) {
			gstr9Table17ReqDto = new Gstr9Table17ReqDto();
			// List<Gstr9Table17ItemsReqDto> table6SuppNonRchrgReqDto = new
			// ArrayList<>();
			// gstr9Table17ReqDto.setGstr9Table17ItemsReqDtos(table6SuppNonRchrgReqDto);
		}

		gstr9Table17ItemsReqDto.setHsnSc(e.getHsn());
		if (!e.getHsn().substring(0, 2).equalsIgnoreCase("99")) {
			gstr9Table17ItemsReqDto
					.setUqc(e.getUqc() != null ? e.getUqc() : "");
			gstr9Table17ItemsReqDto.setQty(e.getTotalQnt() != null
					? e.getTotalQnt() : BigDecimal.ZERO);
		}
		gstr9Table17ItemsReqDto.setTxval(e.getTaxableVal() != null
				? e.getTaxableVal() : BigDecimal.ZERO);
		gstr9Table17ItemsReqDto.setIsconcesstional(e.getConRateFlag());
		gstr9Table17ItemsReqDto.setRt(
				e.getRateOfTax() != null ? e.getRateOfTax() : BigDecimal.ZERO);
		gstr9Table17ItemsReqDto
				.setIamt(e.getIgst() != null ? e.getIgst() : BigDecimal.ZERO);
		gstr9Table17ItemsReqDto
				.setCamt(e.getCgst() != null ? e.getCgst() : BigDecimal.ZERO);
		gstr9Table17ItemsReqDto
				.setSamt(e.getSgst() != null ? e.getSgst() : BigDecimal.ZERO);
		gstr9Table17ItemsReqDto
				.setCsamt(e.getCess() != null ? e.getCess() : BigDecimal.ZERO);

		if (gstr9Table17ReqDto.getGstr9Table17ItemsReqDtos() == null)
			gstr9Table17ReqDto.setGstr9Table17ItemsReqDtos(new ArrayList<>());

		gstr9Table17ReqDto.getGstr9Table17ItemsReqDtos()
				.add(gstr9Table17ItemsReqDto);

		return gstr9Table17ReqDto;

	}

	private Gstr9Table17ReqDto convertGetEntityToTable17Dto(
			Gstr9GetSummaryEntity e, Gstr9Table17ReqDto gstr9Table17ReqDto) {
		Gstr9Table17ItemsReqDto gstr9Table17ItemsReqDto = new Gstr9Table17ItemsReqDto();

		if (gstr9Table17ReqDto == null) {
			gstr9Table17ReqDto = new Gstr9Table17ReqDto();

			// List<Gstr9Table17ItemsReqDto> table6SuppNonRchrgReqDto = new
			// ArrayList<>();
			// gstr9Table17ReqDto.setGstr9Table17ItemsReqDtos(table6SuppNonRchrgReqDto);
		}

		gstr9Table17ItemsReqDto.setHsnSc(e.getHsnSc());
		if (!e.getHsnSc().substring(0, 2).equalsIgnoreCase("99")) {
			gstr9Table17ItemsReqDto
					.setUqc(e.getUqc() != null ? e.getUqc() : "");
			gstr9Table17ItemsReqDto
					.setQty(e.getQty() != null ? e.getQty() : BigDecimal.ZERO);
		}
		gstr9Table17ItemsReqDto.setTxval(
				e.getTxVal() != null ? e.getTxVal() : BigDecimal.ZERO);
		gstr9Table17ItemsReqDto.setIsconcesstional(e.getIsConcesstional());
		gstr9Table17ItemsReqDto
				.setRt(e.getRt() != null ? e.getRt() : BigDecimal.ZERO);
		gstr9Table17ItemsReqDto
				.setIamt(e.getIamt() != null ? e.getIamt() : BigDecimal.ZERO);
		gstr9Table17ItemsReqDto
				.setCamt(e.getCamt() != null ? e.getCamt() : BigDecimal.ZERO);
		gstr9Table17ItemsReqDto
				.setSamt(e.getSamt() != null ? e.getSamt() : BigDecimal.ZERO);
		gstr9Table17ItemsReqDto.setCsamt(
				e.getCsamt() != null ? e.getCsamt() : BigDecimal.ZERO);

		if (gstr9Table17ReqDto.getGstr9Table17ItemsReqDtos() == null)
			gstr9Table17ReqDto.setGstr9Table17ItemsReqDtos(new ArrayList<>());

		gstr9Table17ReqDto.getGstr9Table17ItemsReqDtos()
				.add(gstr9Table17ItemsReqDto);

		return gstr9Table17ReqDto;

	}

	private Gstr9Table18ReqDto convertEntityToTable18Dto(
			Gstr9HsnProcessEntity e, Gstr9Table18ReqDto gstr9Table18ReqDto) {
		Gstr9Table18ItemsReqDto gstr9Table18ItemsReqDto = new Gstr9Table18ItemsReqDto();

		if (gstr9Table18ReqDto == null) {
			gstr9Table18ReqDto = new Gstr9Table18ReqDto();
			// List<Gstr9Table18ItemsReqDto> gstr9Table18ItemsReqDtos = new
			// ArrayList<>();
			// gstr9Table18ReqDto.setGstr9Table18ItemsReqDtos(gstr9Table18ItemsReqDtos);
		}

		gstr9Table18ItemsReqDto.setHsnSc(e.getHsn());
		if (!e.getHsn().substring(0, 2).equalsIgnoreCase("99")) {
			gstr9Table18ItemsReqDto
					.setUqc(e.getUqc() != null ? e.getUqc() : "");
			gstr9Table18ItemsReqDto.setQty(e.getTotalQnt() != null
					? e.getTotalQnt() : BigDecimal.ZERO);
		}
		gstr9Table18ItemsReqDto.setTxval(e.getTaxableVal() != null
				? e.getTaxableVal() : BigDecimal.ZERO);
		gstr9Table18ItemsReqDto.setIsconcesstional(e.getConRateFlag());
		gstr9Table18ItemsReqDto.setRt(
				e.getRateOfTax() != null ? e.getRateOfTax() : BigDecimal.ZERO);
		gstr9Table18ItemsReqDto
				.setIamt(e.getIgst() != null ? e.getIgst() : BigDecimal.ZERO);
		gstr9Table18ItemsReqDto
				.setCamt(e.getCgst() != null ? e.getCgst() : BigDecimal.ZERO);
		gstr9Table18ItemsReqDto
				.setSamt(e.getSgst() != null ? e.getSgst() : BigDecimal.ZERO);
		gstr9Table18ItemsReqDto
				.setCsamt(e.getCess() != null ? e.getCess() : BigDecimal.ZERO);

		if (gstr9Table18ReqDto.getGstr9Table18ItemsReqDtos() == null)
			gstr9Table18ReqDto.setGstr9Table18ItemsReqDtos(new ArrayList<>());

		gstr9Table18ReqDto.getGstr9Table18ItemsReqDtos()
				.add(gstr9Table18ItemsReqDto);

		return gstr9Table18ReqDto;

	}

	private Gstr9Table18ReqDto convertGetEntityToTable18Dto(
			Gstr9GetSummaryEntity e, Gstr9Table18ReqDto gstr9Table18ReqDto) {
		Gstr9Table18ItemsReqDto gstr9Table18ItemsReqDto = new Gstr9Table18ItemsReqDto();

		if (gstr9Table18ReqDto == null) {
			gstr9Table18ReqDto = new Gstr9Table18ReqDto();
			// List<Gstr9Table18ItemsReqDto> gstr9Table18ItemsReqDtos = new
			// ArrayList<>();
			// gstr9Table18ReqDto.setGstr9Table18ItemsReqDtos(gstr9Table18ItemsReqDtos);
		}

		gstr9Table18ItemsReqDto.setHsnSc(e.getHsnSc());
		if (!e.getHsnSc().substring(0, 2).equalsIgnoreCase("99")) {
			gstr9Table18ItemsReqDto
					.setUqc(e.getUqc() != null ? e.getUqc() : "");
			gstr9Table18ItemsReqDto
					.setQty(e.getQty() != null ? e.getQty() : BigDecimal.ZERO);
		}
		gstr9Table18ItemsReqDto.setTxval(
				e.getTxVal() != null ? e.getTxVal() : BigDecimal.ZERO);
		gstr9Table18ItemsReqDto.setIsconcesstional(e.getIsConcesstional());
		gstr9Table18ItemsReqDto
				.setRt(e.getRt() != null ? e.getRt() : BigDecimal.ZERO);
		gstr9Table18ItemsReqDto
				.setIamt(e.getIamt() != null ? e.getIamt() : BigDecimal.ZERO);
		gstr9Table18ItemsReqDto
				.setCamt(e.getCamt() != null ? e.getCamt() : BigDecimal.ZERO);
		gstr9Table18ItemsReqDto
				.setSamt(e.getSamt() != null ? e.getSamt() : BigDecimal.ZERO);
		gstr9Table18ItemsReqDto.setCsamt(
				e.getCsamt() != null ? e.getCsamt() : BigDecimal.ZERO);

		if (gstr9Table18ReqDto.getGstr9Table18ItemsReqDtos() == null)
			gstr9Table18ReqDto.setGstr9Table18ItemsReqDtos(new ArrayList<>());

		gstr9Table18ReqDto.getGstr9Table18ItemsReqDtos()
				.add(gstr9Table18ItemsReqDto);

		return gstr9Table18ReqDto;

	}
	
	
	private Gstr9Table14ReqDto initializeTable14Dto(){
		
		Gstr9Table14ReqDto gstr9Table14ReqDto = new Gstr9Table14ReqDto();

		gstr9Table14ReqDto.setGstr9Table14IamtReqDto(new Gstr9Table14IamtReqDto());
	    gstr9Table14ReqDto.setGstr9Table14SamtReqDto(new Gstr9Table14SamtReqDto());
	    gstr9Table14ReqDto.setGstr9Table14CamtReqDto(new Gstr9Table14CamtReqDto());
	    gstr9Table14ReqDto.setGstr9Table14CSamtReqDto(new Gstr9Table14CSamtReqDto());
	    gstr9Table14ReqDto.setGstr9Table14IntrReqDto(new Gstr9Table14IntrReqDto());
		
		return gstr9Table14ReqDto;
		
	}
	
    private Gstr9Table9ReqDto initializeTable9Dto(){
		
    	Gstr9Table9ReqDto gstr9Table9ReqDto = new Gstr9Table9ReqDto();
		
		gstr9Table9ReqDto.setGstr9Table9IamtReqDto(new Gstr9Table9IamtReqDto());
	    gstr9Table9ReqDto.setGstr9Table9CamtReqDto(new Gstr9Table9CamtReqDto());
	    gstr9Table9ReqDto.setGstr9Table9SamtReqDto(new Gstr9Table9SamtReqDto());
	    gstr9Table9ReqDto.setGstr9Table9CsamtReqDto(new Gstr9Table9CSamtReqDto());
	    gstr9Table9ReqDto.setGstr9Table9IntrReqDto(new Gstr9Table9IntrReqDto());
	    gstr9Table9ReqDto.setGstr9Table9TeeReqDto(new Gstr9Table9TeeReqDto());
	    gstr9Table9ReqDto.setGstr9Table9PenReqDto(new Gstr9Table9PenReqDto());
	    gstr9Table9ReqDto.setGstr9Table9OtherReqDto(new Gstr9Table9OtherReqDto());
		
		return gstr9Table9ReqDto;
		
	}

	public static void main(String[] args) {
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();

		// Gstr9Table14ReqDto dto1 = new
		// Gstr9SaveToGstnDataUtil().convertGetEntityToTable14Dto(null,null,null,null,null,null,null);
		//

		Gstr9GetSummaryEntity gstr9GetSummaryEntity = new Gstr9GetSummaryEntity();
		gstr9GetSummaryEntity.setSubSection("Test");

		// convertEntityToTable14Dto
		Gstr9Table14ReqDto dto2 = new Gstr9SaveToGstnDataUtil()
				.convertGetEntityToTable14Dto(gstr9GetSummaryEntity, null,
						new Gstr9Table14IamtReqDto(),
						new Gstr9Table14SamtReqDto(),
						new Gstr9Table14CamtReqDto(),
						new Gstr9Table14CSamtReqDto(),
						new Gstr9Table14IntrReqDto());
		//
		// GetDetailsForGstr9ReqDto dto= new GetDetailsForGstr9ReqDto();
		//
		// dto.setTable14ReqDto(dto2);

		// Gstr9Table14CSamtReqDto

		Gstr9Table14IamtReqDto gstr9Table14IamtReqDto = new Gstr9Table14IamtReqDto();
		BigDecimal bd1 = new BigDecimal("124567890.0987654321");
		BigDecimal bd2 = new BigDecimal("987654321.123456789");
		gstr9Table14IamtReqDto.setTxpaid(bd1);

		Gstr9Table14ReqDto dto3 = new Gstr9Table14ReqDto();
		dto3.setGstr9Table14IamtReqDto(gstr9Table14IamtReqDto);
		dto3.setGstr9Table14SamtReqDto(new Gstr9Table14SamtReqDto());
		dto3.setGstr9Table14CSamtReqDto(new Gstr9Table14CSamtReqDto());

		String json = gson.toJson(dto3);

		System.out.println(json);

		// String reqJson = gson.toJson(dto);
	}
}
