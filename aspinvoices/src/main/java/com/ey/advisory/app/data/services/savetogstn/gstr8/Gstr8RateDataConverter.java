package com.ey.advisory.app.data.services.savetogstn.gstr8;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.gstr8.Gstr8SaveDto;
import com.ey.advisory.app.docs.dto.gstr8.TcsDto;
import com.ey.advisory.app.docs.dto.gstr8.TcsaDto;
import com.ey.advisory.app.docs.dto.gstr8.UrdDto;
import com.ey.advisory.app.docs.dto.gstr8.UrdaDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Service("Gstr8RateDataConverter")
@Slf4j
public class Gstr8RateDataConverter implements RateDataToGstr8Converter {

	@Override
	public SaveBatchProcessDto convertToGstr8Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType) {
		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<Gstr8SaveDto> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		List<TcsDto> tcsDtos = new ArrayList<>();
		List<TcsaDto> tcsaDtos = new ArrayList<>();
		List<UrdDto> urdDtos = new ArrayList<>();
		List<UrdaDto> urdaDtos = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				Gstr8SaveDto gstr8SaveDto = new Gstr8SaveDto();
				List<Long> idsList = new ArrayList<>();
				for (int counter = 0; counter < totSize; counter++) {

					Object[] resultSet = objects.get(counter);
					String txPriod = getStringValue(resultSet[0]);
					String ecomGstin = getStringValue(resultSet[1]);
					String sectionName = getStringValue(resultSet[2]);
					String orgRetPeriod = getStringValue(resultSet[3]);
//					BigDecimal orgNetSupp = getBigDecimalValue(resultSet[4]);
					String sGstin = getStringValue(resultSet[5]);
					String orgSGstin = getStringValue(resultSet[6]);
					BigDecimal suppsRegistered = getBigDecimalValue(
							resultSet[7]);
					BigDecimal retFromRegistered = getBigDecimalValue(
							resultSet[8]);
					BigDecimal suppsFromUnRegistered = getBigDecimalValue(
							resultSet[9]);
					BigDecimal retFromUnRegistered = getBigDecimalValue(
							resultSet[10]);
					BigDecimal netSupplies = getBigDecimalValue(resultSet[11]);
					BigDecimal igst = getBigDecimalValue(resultSet[12]);
					BigDecimal cgst = getBigDecimalValue(resultSet[13]);
					BigDecimal sgst = getBigDecimalValue(resultSet[14]);
					Long id = resultSet[15] != null
							? Long.valueOf(String.valueOf(resultSet[15]))
							: null;

					if (sectionName
							.equalsIgnoreCase(GSTConstants.TCS.toUpperCase())) {
						TcsDto tcsDto = createTcsDto(sGstin, suppsRegistered,
								suppsFromUnRegistered, retFromRegistered,
								retFromUnRegistered, netSupplies, igst, cgst,
								sgst, taxDocType);
						tcsDtos.add(tcsDto);
					}

					if (sectionName.equalsIgnoreCase(
							GSTConstants.TCSA.toUpperCase())) {
						TcsaDto tcsaDto = createTcsaDto(sGstin, suppsRegistered,
								suppsFromUnRegistered, retFromRegistered,
								retFromUnRegistered, netSupplies, igst, cgst,
								sgst, orgRetPeriod, orgSGstin, taxDocType);
						tcsaDtos.add(tcsaDto);
					}

					if (sectionName
							.equalsIgnoreCase(GSTConstants.URD.toUpperCase())) {
						UrdDto urdDto = createUrdDto(sGstin, netSupplies,
								suppsFromUnRegistered, retFromUnRegistered,
								taxDocType);
						urdDtos.add(urdDto);
					}

					if (sectionName.equalsIgnoreCase(
							GSTConstants.URDA.toUpperCase())) {
						UrdaDto urdaDto = createUrdaDto(orgRetPeriod, orgSGstin,
								sGstin, netSupplies, suppsFromUnRegistered,
								retFromUnRegistered, taxDocType);
						urdaDtos.add(urdaDto);
					}

					if (id != null) {
						idsList.add(id);
					}

					if (gstr8SaveDto == null) {
						gstr8SaveDto = new Gstr8SaveDto();
						gstr8SaveDto.setGstin(ecomGstin);
						gstr8SaveDto.setFp(txPriod);
						if (!tcsDtos.isEmpty()) {
							gstr8SaveDto.setTcs(tcsDtos);
						}
						if (!tcsaDtos.isEmpty()) {
							gstr8SaveDto.setTcsa(tcsaDtos);
						}
						if (!urdDtos.isEmpty()) {
							gstr8SaveDto.setUrd(urdDtos);
						}
						if (!urdaDtos.isEmpty()) {
							gstr8SaveDto.setUrda(urdaDtos);
						}
					}
				}
				batchesList.add(gstr8SaveDto);
				batchIdsList.add(idsList);
				idsList = new ArrayList<>();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr8 Dto {} ", gstr8SaveDto);
				}
			} else {
				String msg = "Zero eligible documents found to do Save to Gstn";
				LOGGER.warn(msg, objects);
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		batchDto.setGstr8(batchesList);
		batchDto.setIdsList(batchIdsList);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("batch Dto {} ", batchDto);
		}
		return batchDto;
	}

	private String getStringValue(Object obj) {
		return obj != null ? String.valueOf(obj) : null;
	}

	private BigDecimal getBigDecimalValue(Object obj) {
		return obj != null ? new BigDecimal(String.valueOf(obj))
				: BigDecimal.ZERO;
	}

	// Methods for creating DTOs
	private TcsDto createTcsDto(String sGstin, BigDecimal suppsRegistered,
			BigDecimal suppsFromUnRegistered, BigDecimal retFromRegistered,
			BigDecimal retFromUnRegistered, BigDecimal netSupplies,
			BigDecimal igst, BigDecimal cgst, BigDecimal sgst,
			String taxDocType) {
		TcsDto tcsDto = new TcsDto();
		tcsDto.setStin(sGstin);
		tcsDto.setSupR(suppsRegistered);
		tcsDto.setSupU(suppsFromUnRegistered);
		tcsDto.setRetsupR(retFromRegistered);
		tcsDto.setRetsupU(retFromUnRegistered);
		tcsDto.setAmt(netSupplies);
		tcsDto.setCamt(cgst);
		tcsDto.setIamt(igst);
		tcsDto.setSamt(sgst);
		if (taxDocType != null
				&& GSTConstants.CAN.equalsIgnoreCase(taxDocType)) {
			tcsDto.setFlag(APIConstants.D); // D-Delete, A-Accept, R-Reject
		}
		return tcsDto;
	}

	private TcsaDto createTcsaDto(String sGstin, BigDecimal suppsRegistered,
			BigDecimal suppsFromUnRegistered, BigDecimal retFromRegistered,
			BigDecimal retFromUnRegistered, BigDecimal netSupplies,
			BigDecimal igst, BigDecimal cgst, BigDecimal sgst,
			String orgRetPeriod, String orgSGstin, String taxDocType) {
		TcsaDto tcsaDto = new TcsaDto();
		tcsaDto.setStin(sGstin);
		tcsaDto.setSupR(suppsRegistered);
		tcsaDto.setSupU(suppsFromUnRegistered);
		tcsaDto.setRetsupR(retFromRegistered);
		tcsaDto.setRetsupU(retFromUnRegistered);
		tcsaDto.setAmt(netSupplies);
		tcsaDto.setCamt(cgst);
		tcsaDto.setIamt(igst);
		tcsaDto.setSamt(sgst);
		if (taxDocType != null
				&& GSTConstants.CAN.equalsIgnoreCase(taxDocType)) {
			tcsaDto.setFlag(APIConstants.D); // D-Delete, A-Accept, R-Reject
		}
		tcsaDto.setOfp(orgRetPeriod);
		tcsaDto.setOstin(orgSGstin);
		return tcsaDto;
	}

	private UrdDto createUrdDto(String sGstin, BigDecimal netSupplies,
			BigDecimal suppsFromUnRegistered, BigDecimal retFromUnRegistered,
			String taxDocType) {
		UrdDto urdDto = new UrdDto();
		urdDto.setEid(sGstin);
		urdDto.setAmt(netSupplies);
		urdDto.setGrsval(suppsFromUnRegistered);
		urdDto.setSupret(retFromUnRegistered);
		if (taxDocType != null
				&& GSTConstants.CAN.equalsIgnoreCase(taxDocType)) {
			urdDto.setFlag(APIConstants.D);
		}
		return urdDto;
	}

	private UrdaDto createUrdaDto(String orgRetPeriod, String orgSGstin,
			String sGstin, BigDecimal netSupplies,
			BigDecimal suppsFromUnRegistered, BigDecimal retFromUnRegistered,
			String taxDocType) {
		UrdaDto urdaDto = new UrdaDto();
		urdaDto.setOfp(orgRetPeriod);
		urdaDto.setOeid(orgSGstin);
		urdaDto.setEid(sGstin);
		urdaDto.setAmt(netSupplies);
		urdaDto.setGrsval(suppsFromUnRegistered);
		urdaDto.setSupret(retFromUnRegistered);
		if (taxDocType != null
				&& GSTConstants.CAN.equalsIgnoreCase(taxDocType)) {
			urdaDto.setFlag(APIConstants.D);
		}
		return urdaDto;
	}

}
