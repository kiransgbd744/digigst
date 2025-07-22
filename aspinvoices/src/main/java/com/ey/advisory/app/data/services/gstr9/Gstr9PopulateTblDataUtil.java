package com.ey.advisory.app.data.services.gstr9;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr9.Gstr9GetSummaryEntity;
import com.ey.advisory.app.docs.dto.gstr9.GetDetailsForGstr9ReqDto;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component
public class Gstr9PopulateTblDataUtil {

	public List<Gstr9GetSummaryEntity> populateTbl4Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9GetSummaryEntity> listOfTbl4GetSummaryEntities,
			String userName) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("INSIDE Outward populateTbl4Data Method");
		}
		if (gstr9GetReqDto.getTable4ReqDto() != null) {
			if (gstr9GetReqDto.getTable4ReqDto().getTable4B2CReqDto() != null) {
				Gstr9GetSummaryEntity tbl4b2cgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl4b2cgetSummEntity.setChkSum(
						gstr9GetReqDto.getTable4ReqDto().getChksum());
				tbl4b2cgetSummEntity.setSection("4");
				tbl4b2cgetSummEntity.setSubSection("4A");
				tbl4b2cgetSummEntity.setSubSectionName("B2C");
				tbl4b2cgetSummEntity.setIamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4B2CReqDto().getIamt());
				tbl4b2cgetSummEntity.setSamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4B2CReqDto().getSamt());
				tbl4b2cgetSummEntity.setCamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4B2CReqDto().getCamt());
				tbl4b2cgetSummEntity.setCsamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4B2CReqDto().getCsamt());
				tbl4b2cgetSummEntity.setTxVal(gstr9GetReqDto.getTable4ReqDto()
						.getTable4B2CReqDto().getTxval());
				tbl4b2cgetSummEntity.setActive(true);
				tbl4b2cgetSummEntity.setCreatedBy(userName);
				tbl4b2cgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl4GetSummaryEntities.add(tbl4b2cgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto().getTable4B2BReqDto() != null) {
				Gstr9GetSummaryEntity tbl4b2bgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl4b2bgetSummEntity.setChkSum(
						gstr9GetReqDto.getTable4ReqDto().getChksum());
				tbl4b2bgetSummEntity.setSection("4");
				tbl4b2bgetSummEntity.setSubSection("4B");
				tbl4b2bgetSummEntity.setSubSectionName("B2B");
				tbl4b2bgetSummEntity.setIamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4B2BReqDto().getIamt());
				tbl4b2bgetSummEntity.setSamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4B2BReqDto().getSamt());
				tbl4b2bgetSummEntity.setCamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4B2BReqDto().getCamt());
				tbl4b2bgetSummEntity.setCsamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4B2BReqDto().getCsamt());
				tbl4b2bgetSummEntity.setTxVal(gstr9GetReqDto.getTable4ReqDto()
						.getTable4B2BReqDto().getTxval());
				tbl4b2bgetSummEntity.setActive(true);
				tbl4b2bgetSummEntity.setCreatedBy(userName);
				tbl4b2bgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl4GetSummaryEntities.add(tbl4b2bgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto().getTable4ExpReqDto() != null) {

				Gstr9GetSummaryEntity tbl4expgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl4expgetSummEntity.setChkSum(
						gstr9GetReqDto.getTable4ReqDto().getChksum());
				tbl4expgetSummEntity.setSection("4");
				tbl4expgetSummEntity.setSubSection("4C");
				tbl4expgetSummEntity.setSubSectionName("EXP");
				tbl4expgetSummEntity.setIamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4ExpReqDto().getIamt());
				tbl4expgetSummEntity.setCsamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4ExpReqDto().getCsamt());
				tbl4expgetSummEntity.setTxVal(gstr9GetReqDto.getTable4ReqDto()
						.getTable4ExpReqDto().getTxval());
				tbl4expgetSummEntity.setActive(true);
				tbl4expgetSummEntity.setCreatedBy(userName);
				tbl4expgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl4GetSummaryEntities.add(tbl4expgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto().getTable4SezReqDto() != null) {
				Gstr9GetSummaryEntity tbl4sezgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl4sezgetSummEntity.setChkSum(
						gstr9GetReqDto.getTable4ReqDto().getChksum());
				tbl4sezgetSummEntity.setSection("4");
				tbl4sezgetSummEntity.setSubSection("4D");
				tbl4sezgetSummEntity.setSubSectionName("SEZ");
				tbl4sezgetSummEntity.setIamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4SezReqDto().getIamt());
				tbl4sezgetSummEntity.setCsamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4SezReqDto().getCsamt());
				tbl4sezgetSummEntity.setTxVal(gstr9GetReqDto.getTable4ReqDto()
						.getTable4SezReqDto().getTxval());
				tbl4sezgetSummEntity.setActive(true);
				tbl4sezgetSummEntity.setCreatedBy(userName);
				tbl4sezgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl4GetSummaryEntities.add(tbl4sezgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4DeemedReqDto() != null) {
				Gstr9GetSummaryEntity tbl4deemedgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl4deemedgetSummEntity.setChkSum(
						gstr9GetReqDto.getTable4ReqDto().getChksum());
				tbl4deemedgetSummEntity.setSection("4");
				tbl4deemedgetSummEntity.setSubSection("4E");
				tbl4deemedgetSummEntity.setSubSectionName("DEEMED");
				tbl4deemedgetSummEntity.setIamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4DeemedReqDto().getIamt());
				tbl4deemedgetSummEntity.setSamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4DeemedReqDto().getSamt());
				tbl4deemedgetSummEntity.setCamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4DeemedReqDto().getCamt());
				tbl4deemedgetSummEntity.setCsamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4DeemedReqDto().getCsamt());
				tbl4deemedgetSummEntity.setTxVal(gstr9GetReqDto
						.getTable4ReqDto().getTable4DeemedReqDto().getTxval());
				tbl4deemedgetSummEntity.setActive(true);
				tbl4deemedgetSummEntity.setCreatedBy(userName);
				tbl4deemedgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl4GetSummaryEntities.add(tbl4deemedgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto().getTable4AtReqDto() != null) {
				Gstr9GetSummaryEntity tbl4atgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl4atgetSummEntity.setChkSum(
						gstr9GetReqDto.getTable4ReqDto().getChksum());
				tbl4atgetSummEntity.setSection("4");
				tbl4atgetSummEntity.setSubSection("4F");
				tbl4atgetSummEntity.setSubSectionName("AT");
				tbl4atgetSummEntity.setIamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4AtReqDto().getIamt());
				tbl4atgetSummEntity.setSamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4AtReqDto().getSamt());
				tbl4atgetSummEntity.setCamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4AtReqDto().getCamt());
				tbl4atgetSummEntity.setCsamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4AtReqDto().getCsamt());
				tbl4atgetSummEntity.setTxVal(gstr9GetReqDto.getTable4ReqDto()
						.getTable4AtReqDto().getTxval());
				tbl4atgetSummEntity.setActive(true);
				tbl4atgetSummEntity.setCreatedBy(userName);
				tbl4atgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl4GetSummaryEntities.add(tbl4atgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4RchrgReqDto() != null) {

				Gstr9GetSummaryEntity tbl4rchrggetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl4rchrggetSummEntity.setChkSum(
						gstr9GetReqDto.getTable4ReqDto().getChksum());
				tbl4rchrggetSummEntity.setSection("4");
				tbl4rchrggetSummEntity.setSubSection("4G");
				tbl4rchrggetSummEntity.setSubSectionName("RCHRG");
				tbl4rchrggetSummEntity.setIamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4RchrgReqDto().getIamt());
				tbl4rchrggetSummEntity.setSamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4RchrgReqDto().getSamt());
				tbl4rchrggetSummEntity.setCamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4RchrgReqDto().getCamt());
				tbl4rchrggetSummEntity.setCsamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4RchrgReqDto().getCsamt());
				tbl4rchrggetSummEntity.setTxVal(gstr9GetReqDto.getTable4ReqDto()
						.getTable4RchrgReqDto().getTxval());
				tbl4rchrggetSummEntity.setActive(true);
				tbl4rchrggetSummEntity.setCreatedBy(userName);
				tbl4rchrggetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl4GetSummaryEntities.add(tbl4rchrggetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4SubTotalAGReqDto() != null) {
				Gstr9GetSummaryEntity tbl4SubTtlAGgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl4SubTtlAGgetSummEntity.setChkSum(
						gstr9GetReqDto.getTable4ReqDto().getChksum());
				tbl4SubTtlAGgetSummEntity.setSection("4");
				tbl4SubTtlAGgetSummEntity.setSubSection("4H");
				tbl4SubTtlAGgetSummEntity.setSubSectionName("SUBTOTAL_AG");
				tbl4SubTtlAGgetSummEntity
						.setIamt(gstr9GetReqDto.getTable4ReqDto()
								.getTable4SubTotalAGReqDto().getIamt());
				tbl4SubTtlAGgetSummEntity
						.setSamt(gstr9GetReqDto.getTable4ReqDto()
								.getTable4SubTotalAGReqDto().getSamt());
				tbl4SubTtlAGgetSummEntity
						.setCamt(gstr9GetReqDto.getTable4ReqDto()
								.getTable4SubTotalAGReqDto().getCamt());
				tbl4SubTtlAGgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable4ReqDto()
								.getTable4SubTotalAGReqDto().getCsamt());
				tbl4SubTtlAGgetSummEntity
						.setTxVal(gstr9GetReqDto.getTable4ReqDto()
								.getTable4SubTotalAGReqDto().getTxval());
				tbl4SubTtlAGgetSummEntity.setActive(true);
				tbl4SubTtlAGgetSummEntity.setCreatedBy(userName);
				tbl4SubTtlAGgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl4GetSummaryEntities.add(tbl4SubTtlAGgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4CrntReqDto() != null) {
				Gstr9GetSummaryEntity tbl4creditNotegetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl4creditNotegetSummEntity.setChkSum(
						gstr9GetReqDto.getTable4ReqDto().getChksum());
				tbl4creditNotegetSummEntity.setSection("4");
				tbl4creditNotegetSummEntity.setSubSection("4I");
				tbl4creditNotegetSummEntity.setSubSectionName("CR_NT");
				tbl4creditNotegetSummEntity.setIamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4CrntReqDto().getIamt());
				tbl4creditNotegetSummEntity.setSamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4CrntReqDto().getSamt());
				tbl4creditNotegetSummEntity.setCamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4CrntReqDto().getCamt());
				tbl4creditNotegetSummEntity.setCsamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4CrntReqDto().getCsamt());
				tbl4creditNotegetSummEntity.setTxVal(gstr9GetReqDto
						.getTable4ReqDto().getTable4CrntReqDto().getTxval());
				tbl4creditNotegetSummEntity.setActive(true);
				tbl4creditNotegetSummEntity.setCreatedBy(userName);
				tbl4creditNotegetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl4GetSummaryEntities.add(tbl4creditNotegetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4drntReqDto() != null) {
				Gstr9GetSummaryEntity tbl4debitNotegetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl4debitNotegetSummEntity.setChkSum(
						gstr9GetReqDto.getTable4ReqDto().getChksum());
				tbl4debitNotegetSummEntity.setSection("4");
				tbl4debitNotegetSummEntity.setSubSection("4J");
				tbl4debitNotegetSummEntity.setSubSectionName("DR_NT");
				tbl4debitNotegetSummEntity.setIamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4drntReqDto().getIamt());
				tbl4debitNotegetSummEntity.setSamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4drntReqDto().getSamt());
				tbl4debitNotegetSummEntity.setCamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4drntReqDto().getCamt());
				tbl4debitNotegetSummEntity.setCsamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4drntReqDto().getCsamt());
				tbl4debitNotegetSummEntity.setTxVal(gstr9GetReqDto
						.getTable4ReqDto().getTable4drntReqDto().getTxval());
				tbl4debitNotegetSummEntity.setActive(true);
				tbl4debitNotegetSummEntity.setCreatedBy(userName);
				tbl4debitNotegetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl4GetSummaryEntities.add(tbl4debitNotegetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4AmdPosReqDto() != null) {
				Gstr9GetSummaryEntity tbl4AmdPosgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl4AmdPosgetSummEntity.setChkSum(
						gstr9GetReqDto.getTable4ReqDto().getChksum());
				tbl4AmdPosgetSummEntity.setSection("4");
				tbl4AmdPosgetSummEntity.setSubSection("4K");
				tbl4AmdPosgetSummEntity.setSubSectionName("AMD_POS");
				tbl4AmdPosgetSummEntity.setIamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4AmdPosReqDto().getIamt());
				tbl4AmdPosgetSummEntity.setSamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4AmdPosReqDto().getSamt());
				tbl4AmdPosgetSummEntity.setCamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4AmdPosReqDto().getCamt());
				tbl4AmdPosgetSummEntity.setCsamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4AmdPosReqDto().getCsamt());
				tbl4AmdPosgetSummEntity.setTxVal(gstr9GetReqDto
						.getTable4ReqDto().getTable4AmdPosReqDto().getTxval());
				tbl4AmdPosgetSummEntity.setActive(true);
				tbl4AmdPosgetSummEntity.setCreatedBy(userName);
				tbl4AmdPosgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl4GetSummaryEntities.add(tbl4AmdPosgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4AmdNegReqDto() != null) {
				Gstr9GetSummaryEntity tbl4AmdNeggetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl4AmdNeggetSummEntity.setChkSum(
						gstr9GetReqDto.getTable4ReqDto().getChksum());
				tbl4AmdNeggetSummEntity.setSection("4");
				tbl4AmdNeggetSummEntity.setSubSection("4L");
				tbl4AmdNeggetSummEntity.setSubSectionName("AMD_NEG");
				tbl4AmdNeggetSummEntity.setIamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4AmdNegReqDto().getIamt());
				tbl4AmdNeggetSummEntity.setSamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4AmdNegReqDto().getSamt());
				tbl4AmdNeggetSummEntity.setCamt(gstr9GetReqDto.getTable4ReqDto()
						.getTable4AmdNegReqDto().getCamt());
				tbl4AmdNeggetSummEntity.setCsamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4AmdNegReqDto().getCsamt());
				tbl4AmdNeggetSummEntity.setTxVal(gstr9GetReqDto
						.getTable4ReqDto().getTable4AmdNegReqDto().getTxval());
				tbl4AmdNeggetSummEntity.setActive(true);
				tbl4AmdNeggetSummEntity.setCreatedBy(userName);
				tbl4AmdNeggetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl4GetSummaryEntities.add(tbl4AmdNeggetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4SubTotalILReqDto() != null) {
				Gstr9GetSummaryEntity tbl4SubTtlILgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl4SubTtlILgetSummEntity.setSection("4");
				tbl4SubTtlILgetSummEntity.setSubSection("4M");
				tbl4SubTtlILgetSummEntity.setSubSectionName("SUB_TOTALIL");
				tbl4SubTtlILgetSummEntity
						.setIamt(gstr9GetReqDto.getTable4ReqDto()
								.getTable4SubTotalILReqDto().getIamt());
				tbl4SubTtlILgetSummEntity
						.setSamt(gstr9GetReqDto.getTable4ReqDto()
								.getTable4SubTotalILReqDto().getSamt());
				tbl4SubTtlILgetSummEntity
						.setCamt(gstr9GetReqDto.getTable4ReqDto()
								.getTable4SubTotalILReqDto().getCamt());
				tbl4SubTtlILgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable4ReqDto()
								.getTable4SubTotalILReqDto().getCsamt());
				tbl4SubTtlILgetSummEntity
						.setTxVal(gstr9GetReqDto.getTable4ReqDto()
								.getTable4SubTotalILReqDto().getTxval());
				tbl4SubTtlILgetSummEntity.setActive(true);
				tbl4SubTtlILgetSummEntity.setCreatedBy(userName);
				tbl4SubTtlILgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl4GetSummaryEntities.add(tbl4SubTtlILgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4SubAdvReqDto() != null) {

				Gstr9GetSummaryEntity tbl4SubTtlAdvgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl4SubTtlAdvgetSummEntity.setChkSum(
						gstr9GetReqDto.getTable4ReqDto().getChksum());
				tbl4SubTtlAdvgetSummEntity.setSection("4");
				tbl4SubTtlAdvgetSummEntity.setSubSection("4N");
				tbl4SubTtlAdvgetSummEntity.setSubSectionName("SUB_ADV");
				tbl4SubTtlAdvgetSummEntity.setIamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4SubAdvReqDto().getIamt());
				tbl4SubTtlAdvgetSummEntity.setSamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4SubAdvReqDto().getSamt());
				tbl4SubTtlAdvgetSummEntity.setCamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4SubAdvReqDto().getCamt());
				tbl4SubTtlAdvgetSummEntity.setCsamt(gstr9GetReqDto
						.getTable4ReqDto().getTable4SubAdvReqDto().getCsamt());
				tbl4SubTtlAdvgetSummEntity.setTxVal(gstr9GetReqDto
						.getTable4ReqDto().getTable4SubAdvReqDto().getTxval());
				tbl4SubTtlAdvgetSummEntity.setActive(true);
				tbl4SubTtlAdvgetSummEntity.setCreatedBy(userName);
				tbl4SubTtlAdvgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl4GetSummaryEntities.add(tbl4SubTtlAdvgetSummEntity);
			}
		}
		return listOfTbl4GetSummaryEntities;
	}

	public List<Gstr9GetSummaryEntity> populateTbl5Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9GetSummaryEntity> listOfTbl5GetSummaryEntities,
			String userName) {
		if (gstr9GetReqDto.getTable5ReqDto() != null) {

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5ZeroRtdReqDto() != null) {
				Gstr9GetSummaryEntity tbl5ZerortdSupgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5ZerortdSupgetSummEntity.setChkSum(
						gstr9GetReqDto.getTable5ReqDto().getChksum());
				tbl5ZerortdSupgetSummEntity.setSection("5");
				tbl5ZerortdSupgetSummEntity.setSubSection("5A");
				tbl5ZerortdSupgetSummEntity
						.setSubSectionName("Zero rated supply");
				tbl5ZerortdSupgetSummEntity.setTxVal(gstr9GetReqDto
						.getTable5ReqDto().getTable5ZeroRtdReqDto().getTxval());
				tbl5ZerortdSupgetSummEntity.setActive(true);
				tbl5ZerortdSupgetSummEntity.setCreatedBy(userName);
				tbl5ZerortdSupgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5GetSummaryEntities.add(tbl5ZerortdSupgetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto().getTable5SezReqDto() != null) {
				Gstr9GetSummaryEntity tbl5SezgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5SezgetSummEntity.setSection("5");
				tbl5SezgetSummEntity.setSubSection("5B");
				tbl5SezgetSummEntity.setSubSectionName("SEZ");
				tbl5SezgetSummEntity.setTxVal(gstr9GetReqDto.getTable5ReqDto()
						.getTable5SezReqDto().getTxval());
				tbl5SezgetSummEntity.setActive(true);
				tbl5SezgetSummEntity.setCreatedBy(userName);
				tbl5SezgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5GetSummaryEntities.add(tbl5SezgetSummEntity);
			}
			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5RchrgReqDto() != null) {
				Gstr9GetSummaryEntity tbl5RchrggetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5RchrggetSummEntity.setSection("5");
				tbl5RchrggetSummEntity.setSubSection("5C");
				tbl5RchrggetSummEntity.setSubSectionName("RCHRG");
				tbl5RchrggetSummEntity.setTxVal(gstr9GetReqDto.getTable5ReqDto()
						.getTable5RchrgReqDto().getTxval());
				tbl5RchrggetSummEntity.setActive(true);
				tbl5RchrggetSummEntity.setCreatedBy(userName);
				tbl5RchrggetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5GetSummaryEntities.add(tbl5RchrggetSummEntity);
			}
			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5ExmtReqDto() != null) {
				Gstr9GetSummaryEntity tbl5exempgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5exempgetSummEntity.setSection("5");
				tbl5exempgetSummEntity.setSubSection("5D");
				tbl5exempgetSummEntity.setSubSectionName("Exempted");
				tbl5exempgetSummEntity.setTxVal(gstr9GetReqDto.getTable5ReqDto()
						.getTable5ExmtReqDto().getTxval());
				tbl5exempgetSummEntity.setActive(true);
				tbl5exempgetSummEntity.setCreatedBy(userName);
				tbl5exempgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5GetSummaryEntities.add(tbl5exempgetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto().getTable5NilReqDto() != null) {
				Gstr9GetSummaryEntity tbl5NilRtdgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5NilRtdgetSummEntity.setSection("5");
				tbl5NilRtdgetSummEntity.setSubSection("5E");
				tbl5NilRtdgetSummEntity.setSubSectionName("Nil Rated");
				tbl5NilRtdgetSummEntity.setTxVal(gstr9GetReqDto
						.getTable5ReqDto().getTable5NilReqDto().getTxval());
				tbl5NilRtdgetSummEntity.setActive(true);
				tbl5NilRtdgetSummEntity.setCreatedBy(userName);
				tbl5NilRtdgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5GetSummaryEntities.add(tbl5NilRtdgetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5NonGstReqDto() != null) {
				Gstr9GetSummaryEntity tbl5NonGstgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5NonGstgetSummEntity.setSection("5");
				tbl5NonGstgetSummEntity.setSubSection("5F");
				tbl5NonGstgetSummEntity.setSubSectionName("Non-GST");
				tbl5NonGstgetSummEntity.setTxVal(gstr9GetReqDto
						.getTable5ReqDto().getTable5NonGstReqDto().getTxval());
				tbl5NonGstgetSummEntity.setActive(true);
				tbl5NonGstgetSummEntity.setCreatedBy(userName);
				tbl5NonGstgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5GetSummaryEntities.add(tbl5NonGstgetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5SubTotalAfReqDto() != null) {
				Gstr9GetSummaryEntity tbl5SubTtlAFgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5SubTtlAFgetSummEntity.setSection("5");
				tbl5SubTtlAFgetSummEntity.setSubSection("5G");
				tbl5SubTtlAFgetSummEntity.setSubSectionName("SUB_TOTALAF");
				tbl5SubTtlAFgetSummEntity
						.setTxVal(gstr9GetReqDto.getTable5ReqDto()
								.getTable5SubTotalAfReqDto().getTxval());
				tbl5SubTtlAFgetSummEntity.setActive(true);
				tbl5SubTtlAFgetSummEntity.setCreatedBy(userName);
				tbl5SubTtlAFgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5GetSummaryEntities.add(tbl5SubTtlAFgetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5CrNtReqDto() != null) {
				Gstr9GetSummaryEntity tbl5creditNotegetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5creditNotegetSummEntity.setSection("5");
				tbl5creditNotegetSummEntity.setSubSection("5H");
				tbl5creditNotegetSummEntity.setSubSectionName("CR_NT");
				tbl5creditNotegetSummEntity.setTxVal(gstr9GetReqDto
						.getTable5ReqDto().getTable5CrNtReqDto().getTxval());
				tbl5creditNotegetSummEntity.setActive(true);
				tbl5creditNotegetSummEntity.setCreatedBy(userName);
				tbl5creditNotegetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5GetSummaryEntities.add(tbl5creditNotegetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5DbNtReqDto() != null) {
				Gstr9GetSummaryEntity tbl5debitNotegetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5debitNotegetSummEntity.setSection("5");
				tbl5debitNotegetSummEntity.setSubSection("5I");
				tbl5debitNotegetSummEntity.setSubSectionName("DR_NT");
				tbl5debitNotegetSummEntity.setTxVal(gstr9GetReqDto
						.getTable5ReqDto().getTable5DbNtReqDto().getTxval());
				tbl5debitNotegetSummEntity.setActive(true);
				tbl5debitNotegetSummEntity.setCreatedBy(userName);
				tbl5debitNotegetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5GetSummaryEntities.add(tbl5debitNotegetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5AmdPosReqDto() != null) {
				Gstr9GetSummaryEntity tbl5AmdPosgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5AmdPosgetSummEntity.setSection("5");
				tbl5AmdPosgetSummEntity.setSubSection("5J");
				tbl5AmdPosgetSummEntity.setSubSectionName("AMD_POS");
				tbl5AmdPosgetSummEntity.setTxVal(gstr9GetReqDto
						.getTable5ReqDto().getTable5AmdPosReqDto().getTxval());
				tbl5AmdPosgetSummEntity.setActive(true);
				tbl5AmdPosgetSummEntity.setCreatedBy(userName);
				tbl5AmdPosgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5GetSummaryEntities.add(tbl5AmdPosgetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5AmdNegReqDto() != null) {
				Gstr9GetSummaryEntity tbl5AmdNeggetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5AmdNeggetSummEntity.setSection("5");
				tbl5AmdNeggetSummEntity.setSubSection("5K");
				tbl5AmdNeggetSummEntity.setSubSectionName("AMD_NEG");
				tbl5AmdNeggetSummEntity.setTxVal(gstr9GetReqDto
						.getTable5ReqDto().getTable5AmdNegReqDto().getTxval());
				tbl5AmdNeggetSummEntity.setActive(true);
				tbl5AmdNeggetSummEntity.setCreatedBy(userName);
				tbl5AmdNeggetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5GetSummaryEntities.add(tbl5AmdNeggetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5SubTotalHkReqDto() != null) {
				Gstr9GetSummaryEntity tbl5SubTtlHkgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5SubTtlHkgetSummEntity.setSection("5");
				tbl5SubTtlHkgetSummEntity.setSubSection("5L");
				tbl5SubTtlHkgetSummEntity.setSubSectionName("SUB_TOTALHK");
				tbl5SubTtlHkgetSummEntity
						.setTxVal(gstr9GetReqDto.getTable5ReqDto()
								.getTable5SubTotalHkReqDto().getTxval());
				tbl5SubTtlHkgetSummEntity.setActive(true);
				tbl5SubTtlHkgetSummEntity.setCreatedBy(userName);
				tbl5SubTtlHkgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5GetSummaryEntities.add(tbl5SubTtlHkgetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5ToverTaxNpReqDto() != null) {
				Gstr9GetSummaryEntity tbl5TurnovergetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5TurnovergetSummEntity.setSection("5");
				tbl5TurnovergetSummEntity.setSubSection("5M");
				tbl5TurnovergetSummEntity.setSubSectionName("Turnover");
				tbl5TurnovergetSummEntity
						.setTxVal(gstr9GetReqDto.getTable5ReqDto()
								.getTable5ToverTaxNpReqDto().getTxval());
				tbl5TurnovergetSummEntity.setActive(true);
				tbl5TurnovergetSummEntity.setCreatedBy(userName);
				tbl5TurnovergetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5GetSummaryEntities.add(tbl5TurnovergetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5TotalToverReqDto() != null) {
				Gstr9GetSummaryEntity tbl5TotalTurnOvergetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5TotalTurnOvergetSummEntity.setSection("5");
				tbl5TotalTurnOvergetSummEntity.setSubSection("5N");
				tbl5TotalTurnOvergetSummEntity
						.setSubSectionName("Total_TurnOver");
				tbl5TotalTurnOvergetSummEntity
						.setIamt(gstr9GetReqDto.getTable5ReqDto()
								.getTable5TotalToverReqDto().getIamt());
				tbl5TotalTurnOvergetSummEntity
						.setSamt(gstr9GetReqDto.getTable5ReqDto()
								.getTable5TotalToverReqDto().getSamt());
				tbl5TotalTurnOvergetSummEntity
						.setCamt(gstr9GetReqDto.getTable5ReqDto()
								.getTable5TotalToverReqDto().getCamt());
				tbl5TotalTurnOvergetSummEntity
						.setCsamt(gstr9GetReqDto.getTable5ReqDto()
								.getTable5TotalToverReqDto().getCsamt());
				tbl5TotalTurnOvergetSummEntity
						.setTxVal(gstr9GetReqDto.getTable5ReqDto()
								.getTable5TotalToverReqDto().getTxval());
				tbl5TotalTurnOvergetSummEntity.setActive(true);
				tbl5TotalTurnOvergetSummEntity.setCreatedBy(userName);
				tbl5TotalTurnOvergetSummEntity
						.setCreatedOn(LocalDateTime.now());
				listOfTbl5GetSummaryEntities
						.add(tbl5TotalTurnOvergetSummEntity);
			}
		}
		return listOfTbl5GetSummaryEntities;
	}

	public List<Gstr9GetSummaryEntity> populateTbl6Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9GetSummaryEntity> listOfTbl6GetSummaryEntities,
			String userName) {
		if (gstr9GetReqDto.getTable6ReqDto() != null) {
			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6Itc3bReqDto() != null) {
				Gstr9GetSummaryEntity tbl6Itc3bgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl6Itc3bgetSummEntity.setChkSum(
						gstr9GetReqDto.getTable5ReqDto().getChksum());
				tbl6Itc3bgetSummEntity.setSection("6");
				tbl6Itc3bgetSummEntity.setSubSection("6A");
				tbl6Itc3bgetSummEntity.setSubSectionName("ITC_3B");
				tbl6Itc3bgetSummEntity.setIamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6Itc3bReqDto().getIamt());
				tbl6Itc3bgetSummEntity.setSamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6Itc3bReqDto().getSamt());
				tbl6Itc3bgetSummEntity.setCamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6Itc3bReqDto().getCamt());
				tbl6Itc3bgetSummEntity.setCsamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6Itc3bReqDto().getCsamt());
				tbl6Itc3bgetSummEntity.setActive(true);
				tbl6Itc3bgetSummEntity.setCreatedBy(userName);
				tbl6Itc3bgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl6GetSummaryEntities.add(tbl6Itc3bgetSummEntity);
			}

			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6SuppNonRchrgReqDto() != null
					&& !gstr9GetReqDto.getTable6ReqDto()
							.getTable6SuppNonRchrgReqDto().isEmpty()) {

				for (int i = 0; i < gstr9GetReqDto.getTable6ReqDto()
						.getTable6SuppNonRchrgReqDto().size(); i++) {
					Gstr9GetSummaryEntity tbl6SuppNonRchrggetSummEntity = new Gstr9GetSummaryEntity(
							gstin, fy, taxPeriod, derviedTaxPeriod);
					String itcType = gstr9GetReqDto.getTable6ReqDto()
							.getTable6SuppNonRchrgReqDto().get(i).getItctyp();

					if ("ip".equalsIgnoreCase(itcType)) {
						tbl6SuppNonRchrggetSummEntity.setSubSection("6B1");

					} else if ("cg".equalsIgnoreCase(itcType)) {
						tbl6SuppNonRchrggetSummEntity.setSubSection("6B2");

					} else if ("is".equalsIgnoreCase(itcType)) {
						tbl6SuppNonRchrggetSummEntity.setSubSection("6B3");
					}
					tbl6SuppNonRchrggetSummEntity.setSection("6");
					tbl6SuppNonRchrggetSummEntity
							.setSubSectionName("SUPP_NON_RCHRG");
					tbl6SuppNonRchrggetSummEntity.setIamt(gstr9GetReqDto
							.getTable6ReqDto().getTable6SuppNonRchrgReqDto()
							.get(i).getIamt());
					tbl6SuppNonRchrggetSummEntity.setSamt(gstr9GetReqDto
							.getTable6ReqDto().getTable6SuppNonRchrgReqDto()
							.get(i).getSamt());
					tbl6SuppNonRchrggetSummEntity.setCamt(gstr9GetReqDto
							.getTable6ReqDto().getTable6SuppNonRchrgReqDto()
							.get(i).getCamt());
					tbl6SuppNonRchrggetSummEntity.setCsamt(gstr9GetReqDto
							.getTable6ReqDto().getTable6SuppNonRchrgReqDto()
							.get(i).getCsamt());
					tbl6SuppNonRchrggetSummEntity.setItcTyp(itcType);
					tbl6SuppNonRchrggetSummEntity.setActive(true);
					tbl6SuppNonRchrggetSummEntity.setCreatedBy(userName);
					tbl6SuppNonRchrggetSummEntity
							.setCreatedOn(LocalDateTime.now());
					listOfTbl6GetSummaryEntities
							.add(tbl6SuppNonRchrggetSummEntity);

				}
			}
			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6SuppRchrgUnRegReqDto() != null
					&& !gstr9GetReqDto.getTable6ReqDto()
							.getTable6SuppRchrgUnRegReqDto().isEmpty()) {

				for (int i = 0; i < gstr9GetReqDto.getTable6ReqDto()
						.getTable6SuppRchrgUnRegReqDto().size(); i++) {
					Gstr9GetSummaryEntity tbl6SuppRchrgUnReggetSummEntity = new Gstr9GetSummaryEntity(
							gstin, fy, taxPeriod, derviedTaxPeriod);
					tbl6SuppRchrgUnReggetSummEntity.setSection("6");
					String itcType = gstr9GetReqDto.getTable6ReqDto()
							.getTable6SuppRchrgUnRegReqDto().get(i).getItctyp();

					if ("ip".equalsIgnoreCase(itcType)) {
						tbl6SuppRchrgUnReggetSummEntity.setSubSection("6C1");

					} else if ("cg".equalsIgnoreCase(itcType)) {
						tbl6SuppRchrgUnReggetSummEntity.setSubSection("6C2");

					} else if ("is".equalsIgnoreCase(itcType)) {
						tbl6SuppRchrgUnReggetSummEntity.setSubSection("6C3");
					}
					tbl6SuppRchrgUnReggetSummEntity
							.setSubSectionName("SUPP_RCHRG_UNREG");
					tbl6SuppRchrgUnReggetSummEntity.setIamt(gstr9GetReqDto
							.getTable6ReqDto().getTable6SuppRchrgUnRegReqDto()
							.get(i).getIamt());
					tbl6SuppRchrgUnReggetSummEntity.setSamt(gstr9GetReqDto
							.getTable6ReqDto().getTable6SuppRchrgUnRegReqDto()
							.get(i).getSamt());
					tbl6SuppRchrgUnReggetSummEntity.setCamt(gstr9GetReqDto
							.getTable6ReqDto().getTable6SuppRchrgUnRegReqDto()
							.get(i).getCamt());
					tbl6SuppRchrgUnReggetSummEntity.setCsamt(gstr9GetReqDto
							.getTable6ReqDto().getTable6SuppRchrgUnRegReqDto()
							.get(i).getCsamt());
					tbl6SuppRchrgUnReggetSummEntity.setItcTyp(itcType);
					tbl6SuppRchrgUnReggetSummEntity.setActive(true);
					tbl6SuppRchrgUnReggetSummEntity.setCreatedBy(userName);
					tbl6SuppRchrgUnReggetSummEntity
							.setCreatedOn(LocalDateTime.now());
					listOfTbl6GetSummaryEntities
							.add(tbl6SuppRchrgUnReggetSummEntity);
				}
			}
			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6SuppRchrgRegReqDto() != null
					&& !gstr9GetReqDto.getTable6ReqDto()
							.getTable6SuppRchrgRegReqDto().isEmpty()) {

				for (int i = 0; i < gstr9GetReqDto.getTable6ReqDto()
						.getTable6SuppRchrgRegReqDto().size(); i++) {
					Gstr9GetSummaryEntity tbl6SuppRchrgUnReggetSummEntity = new Gstr9GetSummaryEntity(
							gstin, fy, taxPeriod, derviedTaxPeriod);
					tbl6SuppRchrgUnReggetSummEntity.setSection("6");
					String itcType = gstr9GetReqDto.getTable6ReqDto()
							.getTable6SuppRchrgRegReqDto().get(i).getItctyp();

					if ("ip".equalsIgnoreCase(itcType)) {
						tbl6SuppRchrgUnReggetSummEntity.setSubSection("6D1");

					} else if ("cg".equalsIgnoreCase(itcType)) {
						tbl6SuppRchrgUnReggetSummEntity.setSubSection("6D2");

					} else if ("is".equalsIgnoreCase(itcType)) {
						tbl6SuppRchrgUnReggetSummEntity.setSubSection("6D3");
					}
					tbl6SuppRchrgUnReggetSummEntity
							.setSubSectionName("SUPP_RCHRG_REG");
					tbl6SuppRchrgUnReggetSummEntity.setIamt(gstr9GetReqDto
							.getTable6ReqDto().getTable6SuppRchrgRegReqDto()
							.get(i).getIamt());
					tbl6SuppRchrgUnReggetSummEntity.setSamt(gstr9GetReqDto
							.getTable6ReqDto().getTable6SuppRchrgRegReqDto()
							.get(i).getSamt());
					tbl6SuppRchrgUnReggetSummEntity.setCamt(gstr9GetReqDto
							.getTable6ReqDto().getTable6SuppRchrgRegReqDto()
							.get(i).getCamt());
					tbl6SuppRchrgUnReggetSummEntity.setCsamt(gstr9GetReqDto
							.getTable6ReqDto().getTable6SuppRchrgRegReqDto()
							.get(i).getCsamt());
					tbl6SuppRchrgUnReggetSummEntity.setItcTyp(itcType);
					tbl6SuppRchrgUnReggetSummEntity.setItcTyp(itcType);
					tbl6SuppRchrgUnReggetSummEntity.setActive(true);
					tbl6SuppRchrgUnReggetSummEntity.setCreatedBy(userName);
					tbl6SuppRchrgUnReggetSummEntity
							.setCreatedOn(LocalDateTime.now());
					listOfTbl6GetSummaryEntities
							.add(tbl6SuppRchrgUnReggetSummEntity);
				}
			}

			if (gstr9GetReqDto.getTable6ReqDto().getTable6IogReqDto() != null
					&& !gstr9GetReqDto.getTable6ReqDto().getTable6IogReqDto()
							.isEmpty()) {

				for (int i = 0; i < gstr9GetReqDto.getTable6ReqDto()
						.getTable6IogReqDto().size(); i++) {
					Gstr9GetSummaryEntity tbl6SuppRchrgUnReggetSummEntity = new Gstr9GetSummaryEntity(
							gstin, fy, taxPeriod, derviedTaxPeriod);
					tbl6SuppRchrgUnReggetSummEntity.setSection("6");
					String itcType = gstr9GetReqDto.getTable6ReqDto()
							.getTable6IogReqDto().get(i).getItctyp();

					if ("ip".equalsIgnoreCase(itcType)) {
						tbl6SuppRchrgUnReggetSummEntity.setSubSection("6E1");

					} else if ("cg".equalsIgnoreCase(itcType)) {
						tbl6SuppRchrgUnReggetSummEntity.setSubSection("6E2");

					} else if ("is".equalsIgnoreCase(itcType)) {
						tbl6SuppRchrgUnReggetSummEntity.setSubSection("6E3");
					}
					tbl6SuppRchrgUnReggetSummEntity.setSubSectionName("IOG");
					tbl6SuppRchrgUnReggetSummEntity
							.setIamt(gstr9GetReqDto.getTable6ReqDto()
									.getTable6IogReqDto().get(i).getIamt());
					tbl6SuppRchrgUnReggetSummEntity
							.setCsamt(gstr9GetReqDto.getTable6ReqDto()
									.getTable6IogReqDto().get(i).getCsamt());
					tbl6SuppRchrgUnReggetSummEntity.setItcTyp(itcType);
					tbl6SuppRchrgUnReggetSummEntity.setActive(true);
					tbl6SuppRchrgUnReggetSummEntity.setCreatedBy(userName);
					tbl6SuppRchrgUnReggetSummEntity
							.setCreatedOn(LocalDateTime.now());
					listOfTbl6GetSummaryEntities
							.add(tbl6SuppRchrgUnReggetSummEntity);
				}
			}
			if (gstr9GetReqDto.getTable6ReqDto().getTable6IosReqDto() != null) {

				Gstr9GetSummaryEntity tbl6IosgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl6IosgetSummEntity.setSection("6");
				tbl6IosgetSummEntity.setSubSection("6F");
				tbl6IosgetSummEntity.setSubSectionName("IOS");
				tbl6IosgetSummEntity.setIamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6IosReqDto().getIamt());
				tbl6IosgetSummEntity.setCsamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6IosReqDto().getCsamt());
				tbl6IosgetSummEntity.setActive(true);
				tbl6IosgetSummEntity.setCreatedBy(userName);
				tbl6IosgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl6GetSummaryEntities.add(tbl6IosgetSummEntity);
			}
			if (gstr9GetReqDto.getTable6ReqDto().getTable6IsdReqDto() != null) {
				Gstr9GetSummaryEntity tbl6IsdgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl6IsdgetSummEntity.setSection("6");
				tbl6IsdgetSummEntity.setSubSection("6G");
				tbl6IsdgetSummEntity.setSubSectionName("ISD");
				tbl6IsdgetSummEntity.setIamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6IsdReqDto().getIamt());
				tbl6IsdgetSummEntity.setSamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6IsdReqDto().getSamt());
				tbl6IsdgetSummEntity.setCamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6IsdReqDto().getCamt());
				tbl6IsdgetSummEntity.setCsamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6IsdReqDto().getCsamt());
				tbl6IsdgetSummEntity.setActive(true);
				tbl6IsdgetSummEntity.setCreatedBy(userName);
				tbl6IsdgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl6GetSummaryEntities.add(tbl6IsdgetSummEntity);
			}
			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6ItcClmdReqDto() != null) {
				Gstr9GetSummaryEntity tbl6ItcClmdgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl6ItcClmdgetSummEntity.setSection("6");
				tbl6ItcClmdgetSummEntity.setSubSection("6H");
				tbl6ItcClmdgetSummEntity.setSubSectionName("ITC_CLMD");
				tbl6ItcClmdgetSummEntity.setIamt(gstr9GetReqDto
						.getTable6ReqDto().getTable6ItcClmdReqDto().getIamt());
				tbl6ItcClmdgetSummEntity.setSamt(gstr9GetReqDto
						.getTable6ReqDto().getTable6ItcClmdReqDto().getSamt());
				tbl6ItcClmdgetSummEntity.setCamt(gstr9GetReqDto
						.getTable6ReqDto().getTable6ItcClmdReqDto().getCamt());
				tbl6ItcClmdgetSummEntity.setCsamt(gstr9GetReqDto
						.getTable6ReqDto().getTable6ItcClmdReqDto().getCsamt());
				tbl6ItcClmdgetSummEntity.setActive(true);
				tbl6ItcClmdgetSummEntity.setCreatedBy(userName);
				tbl6ItcClmdgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl6GetSummaryEntities.add(tbl6ItcClmdgetSummEntity);
			}
			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6SubTotalBhReqDto() != null) {
				Gstr9GetSummaryEntity tbl6SubTtlBhgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl6SubTtlBhgetSummEntity.setSection("6");
				tbl6SubTtlBhgetSummEntity.setSubSection("6I");
				tbl6SubTtlBhgetSummEntity.setSubSectionName("SUB-TOTALBH");
				tbl6SubTtlBhgetSummEntity
						.setIamt(gstr9GetReqDto.getTable6ReqDto()
								.getTable6SubTotalBhReqDto().getIamt());
				tbl6SubTtlBhgetSummEntity
						.setSamt(gstr9GetReqDto.getTable6ReqDto()
								.getTable6SubTotalBhReqDto().getSamt());
				tbl6SubTtlBhgetSummEntity
						.setCamt(gstr9GetReqDto.getTable6ReqDto()
								.getTable6SubTotalBhReqDto().getCamt());
				tbl6SubTtlBhgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable6ReqDto()
								.getTable6SubTotalBhReqDto().getCsamt());
				tbl6SubTtlBhgetSummEntity.setActive(true);
				tbl6SubTtlBhgetSummEntity.setCreatedBy(userName);
				tbl6SubTtlBhgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl6GetSummaryEntities.add(tbl6SubTtlBhgetSummEntity);
			}
			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6DifferenceReqDto() != null) {
				Gstr9GetSummaryEntity tbl6DiffergetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl6DiffergetSummEntity.setSection("6");
				tbl6DiffergetSummEntity.setSubSection("6J");
				tbl6DiffergetSummEntity.setSubSectionName("DIFFERENCE");
				tbl6DiffergetSummEntity.setIamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6DifferenceReqDto().getIamt());
				tbl6DiffergetSummEntity.setSamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6DifferenceReqDto().getSamt());
				tbl6DiffergetSummEntity.setCamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6DifferenceReqDto().getCamt());
				tbl6DiffergetSummEntity
						.setCsamt(gstr9GetReqDto.getTable6ReqDto()
								.getTable6DifferenceReqDto().getCsamt());
				tbl6DiffergetSummEntity.setActive(true);
				tbl6DiffergetSummEntity.setCreatedBy(userName);
				tbl6DiffergetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl6GetSummaryEntities.add(tbl6DiffergetSummEntity);
			}
			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6Trans1ReqDto() != null) {
				Gstr9GetSummaryEntity tbl6Trans1getSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl6Trans1getSummEntity.setSection("6");
				tbl6Trans1getSummEntity.setSubSection("6K");
				tbl6Trans1getSummEntity.setSubSectionName("TRAN-1");
				tbl6Trans1getSummEntity.setSamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6Trans1ReqDto().getSamt());
				tbl6Trans1getSummEntity.setCamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6Trans1ReqDto().getCamt());
				tbl6Trans1getSummEntity.setActive(true);
				tbl6Trans1getSummEntity.setCreatedBy(userName);
				tbl6Trans1getSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl6GetSummaryEntities.add(tbl6Trans1getSummEntity);
			}
			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6Trans2ReqDto() != null) {
				Gstr9GetSummaryEntity tbl6Trans2getSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl6Trans2getSummEntity.setSection("6");
				tbl6Trans2getSummEntity.setSubSection("6L");
				tbl6Trans2getSummEntity.setSubSectionName("TRAN-2");
				tbl6Trans2getSummEntity.setSamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6Trans2ReqDto().getSamt());
				tbl6Trans2getSummEntity.setCamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6Trans2ReqDto().getCamt());
				tbl6Trans2getSummEntity.setActive(true);
				tbl6Trans2getSummEntity.setCreatedBy(userName);
				tbl6Trans2getSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl6GetSummaryEntities.add(tbl6Trans2getSummEntity);
			}
			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6OtherReqDto() != null) {
				Gstr9GetSummaryEntity tbl6OthergetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl6OthergetSummEntity.setSection("6");
				tbl6OthergetSummEntity.setSubSection("6M");
				tbl6OthergetSummEntity.setSubSectionName("OTHER");
				tbl6OthergetSummEntity.setSamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6OtherReqDto().getSamt());
				tbl6OthergetSummEntity.setCamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6OtherReqDto().getCamt());
				tbl6OthergetSummEntity.setIamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6OtherReqDto().getIamt());
				tbl6OthergetSummEntity.setCsamt(gstr9GetReqDto.getTable6ReqDto()
						.getTable6OtherReqDto().getCsamt());
				tbl6OthergetSummEntity.setActive(true);
				tbl6OthergetSummEntity.setCreatedBy(userName);
				tbl6OthergetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl6GetSummaryEntities.add(tbl6OthergetSummEntity);
			}

			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6SubTotalKmReqDto() != null) {
				Gstr9GetSummaryEntity tbl6SubTtlKmgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl6SubTtlKmgetSummEntity.setSection("6");
				tbl6SubTtlKmgetSummEntity.setSubSection("6N");
				tbl6SubTtlKmgetSummEntity.setSubSectionName("SUB_TOTALKM");
				tbl6SubTtlKmgetSummEntity
						.setSamt(gstr9GetReqDto.getTable6ReqDto()
								.getTable6SubTotalKmReqDto().getSamt());
				tbl6SubTtlKmgetSummEntity
						.setCamt(gstr9GetReqDto.getTable6ReqDto()
								.getTable6SubTotalKmReqDto().getCamt());
				tbl6SubTtlKmgetSummEntity
						.setIamt(gstr9GetReqDto.getTable6ReqDto()
								.getTable6SubTotalKmReqDto().getIamt());
				tbl6SubTtlKmgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable6ReqDto()
								.getTable6SubTotalKmReqDto().getCsamt());
				tbl6SubTtlKmgetSummEntity.setActive(true);
				tbl6SubTtlKmgetSummEntity.setCreatedBy(userName);
				tbl6SubTtlKmgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl6GetSummaryEntities.add(tbl6SubTtlKmgetSummEntity);
			}
			if (gstr9GetReqDto.getTable6ReqDto()
					.getTotalItcAvailedReqDto() != null) {
				Gstr9GetSummaryEntity tbl6TtlItcAvailgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl6TtlItcAvailgetSummEntity.setSection("6");
				tbl6TtlItcAvailgetSummEntity.setSubSection("6O");
				tbl6TtlItcAvailgetSummEntity.setSubSectionName("ITC_AVAILED");
				tbl6TtlItcAvailgetSummEntity
						.setSamt(gstr9GetReqDto.getTable6ReqDto()
								.getTotalItcAvailedReqDto().getSamt());
				tbl6TtlItcAvailgetSummEntity
						.setCamt(gstr9GetReqDto.getTable6ReqDto()
								.getTotalItcAvailedReqDto().getCamt());
				tbl6TtlItcAvailgetSummEntity
						.setIamt(gstr9GetReqDto.getTable6ReqDto()
								.getTotalItcAvailedReqDto().getIamt());
				tbl6TtlItcAvailgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable6ReqDto()
								.getTotalItcAvailedReqDto().getCsamt());
				tbl6TtlItcAvailgetSummEntity.setActive(true);
				tbl6TtlItcAvailgetSummEntity.setCreatedBy(userName);
				tbl6TtlItcAvailgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl6GetSummaryEntities.add(tbl6TtlItcAvailgetSummEntity);
			}

		}
		return listOfTbl6GetSummaryEntities;
	}

	public List<Gstr9GetSummaryEntity> populateTbl7Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9GetSummaryEntity> listOfTbl7GetSummaryEntities,
			String userName) {
		if (gstr9GetReqDto.getTable7ReqDto() != null) {
			if (gstr9GetReqDto.getTable7ReqDto()
					.getGstr9Table7Rule37ReqDto() != null) {
				Gstr9GetSummaryEntity tbl7rule37getSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl7rule37getSummEntity.setChkSum(
						gstr9GetReqDto.getTable7ReqDto().getChksum());
				tbl7rule37getSummEntity.setSection("7");
				tbl7rule37getSummEntity.setSubSection("7A");
				tbl7rule37getSummEntity.setSubSectionName("RULE_37");
				tbl7rule37getSummEntity.setIamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Rule37ReqDto().getIamt());
				tbl7rule37getSummEntity.setSamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Rule37ReqDto().getSamt());
				tbl7rule37getSummEntity.setCamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Rule37ReqDto().getCamt());
				tbl7rule37getSummEntity
						.setCsamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7Rule37ReqDto().getCsamt());
				tbl7rule37getSummEntity.setActive(true);
				tbl7rule37getSummEntity.setCreatedBy(userName);
				tbl7rule37getSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl7GetSummaryEntities.add(tbl7rule37getSummEntity);
			}
			if (gstr9GetReqDto.getTable7ReqDto()
					.getGstr9Table7Rule39ReqDto() != null) {
				Gstr9GetSummaryEntity tbl7rule39getSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl7rule39getSummEntity.setChkSum(
						gstr9GetReqDto.getTable7ReqDto().getChksum());
				tbl7rule39getSummEntity.setSection("7");
				tbl7rule39getSummEntity.setSubSection("7B");
				tbl7rule39getSummEntity.setSubSectionName("RULE_39");
				tbl7rule39getSummEntity.setIamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Rule39ReqDto().getIamt());
				tbl7rule39getSummEntity.setSamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Rule39ReqDto().getSamt());
				tbl7rule39getSummEntity.setCamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Rule39ReqDto().getCamt());
				tbl7rule39getSummEntity
						.setCsamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7Rule39ReqDto().getCsamt());
				tbl7rule39getSummEntity.setActive(true);
				tbl7rule39getSummEntity.setCreatedBy(userName);
				tbl7rule39getSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl7GetSummaryEntities.add(tbl7rule39getSummEntity);
			}
			if (gstr9GetReqDto.getTable7ReqDto()
					.getGstr9Table7Rule42ReqDto() != null) {
				Gstr9GetSummaryEntity tbl7rule42getSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl7rule42getSummEntity.setChkSum(
						gstr9GetReqDto.getTable7ReqDto().getChksum());
				tbl7rule42getSummEntity.setSection("7");
				tbl7rule42getSummEntity.setSubSection("7C");
				tbl7rule42getSummEntity.setSubSectionName("RULE_42");
				tbl7rule42getSummEntity.setIamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Rule42ReqDto().getIamt());
				tbl7rule42getSummEntity.setSamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Rule42ReqDto().getSamt());
				tbl7rule42getSummEntity.setCamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Rule42ReqDto().getCamt());
				tbl7rule42getSummEntity
						.setCsamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7Rule42ReqDto().getCsamt());
				tbl7rule42getSummEntity.setActive(true);
				tbl7rule42getSummEntity.setCreatedBy(userName);
				tbl7rule42getSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl7GetSummaryEntities.add(tbl7rule42getSummEntity);
			}
			if (gstr9GetReqDto.getTable7ReqDto()
					.getGstr9Table7Rule43ReqDto() != null) {
				Gstr9GetSummaryEntity tbl7rule43getSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl7rule43getSummEntity.setChkSum(
						gstr9GetReqDto.getTable7ReqDto().getChksum());
				tbl7rule43getSummEntity.setSection("7");
				tbl7rule43getSummEntity.setSubSection("7D");
				tbl7rule43getSummEntity.setSubSectionName("RULE_43");
				tbl7rule43getSummEntity.setIamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Rule43ReqDto().getIamt());
				tbl7rule43getSummEntity.setSamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Rule43ReqDto().getSamt());
				tbl7rule43getSummEntity.setCamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Rule43ReqDto().getCamt());
				tbl7rule43getSummEntity
						.setCsamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7Rule43ReqDto().getCsamt());
				tbl7rule43getSummEntity.setActive(true);
				tbl7rule43getSummEntity.setCreatedBy(userName);
				tbl7rule43getSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl7GetSummaryEntities.add(tbl7rule43getSummEntity);
			}
			if (gstr9GetReqDto.getTable7ReqDto()
					.getGstr9Table7Sec17ReqDto() != null) {
				Gstr9GetSummaryEntity tbl7sec17getSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl7sec17getSummEntity.setChkSum(
						gstr9GetReqDto.getTable7ReqDto().getChksum());
				tbl7sec17getSummEntity.setSection("7");
				tbl7sec17getSummEntity.setSubSection("7E");
				tbl7sec17getSummEntity.setSubSectionName("SEC_17");
				tbl7sec17getSummEntity.setIamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Sec17ReqDto().getIamt());
				tbl7sec17getSummEntity.setSamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Sec17ReqDto().getSamt());
				tbl7sec17getSummEntity.setCamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Sec17ReqDto().getCamt());
				tbl7sec17getSummEntity.setCsamt(gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7Sec17ReqDto().getCsamt());
				tbl7sec17getSummEntity.setActive(true);
				tbl7sec17getSummEntity.setCreatedBy(userName);
				tbl7sec17getSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl7GetSummaryEntities.add(tbl7sec17getSummEntity);
			}
			if (gstr9GetReqDto.getTable7ReqDto()
					.getGstr9Table7RevsTrans1ReqDto() != null) {
				Gstr9GetSummaryEntity tbl7revtrans1getSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl7revtrans1getSummEntity.setChkSum(
						gstr9GetReqDto.getTable7ReqDto().getChksum());
				tbl7revtrans1getSummEntity.setSection("7");
				tbl7revtrans1getSummEntity.setSubSection("7F");
				tbl7revtrans1getSummEntity.setSubSectionName("REVS1_TRAN1");
				tbl7revtrans1getSummEntity
						.setSamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7RevsTrans1ReqDto().getSamt());
				tbl7revtrans1getSummEntity
						.setCamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7RevsTrans1ReqDto().getCamt());
				tbl7revtrans1getSummEntity.setActive(true);
				tbl7revtrans1getSummEntity.setCreatedBy(userName);
				tbl7revtrans1getSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl7GetSummaryEntities.add(tbl7revtrans1getSummEntity);
			}
			if (gstr9GetReqDto.getTable7ReqDto()
					.getGstr9Table7RevsTrans2ReqDto() != null) {
				Gstr9GetSummaryEntity tbl7revtrans2getSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl7revtrans2getSummEntity.setChkSum(
						gstr9GetReqDto.getTable7ReqDto().getChksum());
				tbl7revtrans2getSummEntity.setSection("7");
				tbl7revtrans2getSummEntity.setSubSection("7G");
				tbl7revtrans2getSummEntity.setSubSectionName("REVS1_TRAN2");
				tbl7revtrans2getSummEntity
						.setSamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7RevsTrans2ReqDto().getSamt());
				tbl7revtrans2getSummEntity
						.setCamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7RevsTrans2ReqDto().getCamt());
				tbl7revtrans2getSummEntity.setActive(true);
				tbl7revtrans2getSummEntity.setCreatedBy(userName);
				tbl7revtrans2getSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl7GetSummaryEntities.add(tbl7revtrans2getSummEntity);
			}

			if (gstr9GetReqDto.getTable7ReqDto()
					.getGstr9Table7OtherReqDto() != null
					&& !gstr9GetReqDto.getTable7ReqDto()
							.getGstr9Table7OtherReqDto().isEmpty()) {
				for (int i = 0; i < gstr9GetReqDto.getTable7ReqDto()
						.getGstr9Table7OtherReqDto().size(); i++) {
					Gstr9GetSummaryEntity tbl7OthergetSummEntity = new Gstr9GetSummaryEntity(
							gstin, fy, taxPeriod, derviedTaxPeriod);
					tbl7OthergetSummEntity.setSection("7");
					tbl7OthergetSummEntity.setSubSection("7H");
					tbl7OthergetSummEntity.setSubSectionName("OTHERS");
					tbl7OthergetSummEntity.setIamt(gstr9GetReqDto
							.getTable7ReqDto().getGstr9Table7OtherReqDto()
							.get(i).getIamt());
					tbl7OthergetSummEntity.setSamt(gstr9GetReqDto
							.getTable7ReqDto().getGstr9Table7OtherReqDto()
							.get(i).getSamt());
					tbl7OthergetSummEntity.setCamt(gstr9GetReqDto
							.getTable7ReqDto().getGstr9Table7OtherReqDto()
							.get(i).getCamt());
					tbl7OthergetSummEntity.setCsamt(gstr9GetReqDto
							.getTable7ReqDto().getGstr9Table7OtherReqDto()
							.get(i).getCsamt());
					tbl7OthergetSummEntity.setDesc(gstr9GetReqDto
							.getTable7ReqDto().getGstr9Table7OtherReqDto()
							.get(i).getDesc());
					tbl7OthergetSummEntity.setActive(true);
					tbl7OthergetSummEntity.setCreatedBy(userName);
					tbl7OthergetSummEntity.setCreatedOn(LocalDateTime.now());
					listOfTbl7GetSummaryEntities.add(tbl7OthergetSummEntity);
				}
			}
			if (gstr9GetReqDto.getTable7ReqDto()
					.getGstr9Table7TotItcRevdReqDto() != null) {
				Gstr9GetSummaryEntity tbl7TtlItcRevdgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl7TtlItcRevdgetSummEntity.setChkSum(
						gstr9GetReqDto.getTable7ReqDto().getChksum());
				tbl7TtlItcRevdgetSummEntity.setSection("7");
				tbl7TtlItcRevdgetSummEntity.setSubSection("7I");
				tbl7TtlItcRevdgetSummEntity.setSubSectionName("TOTAL_ITC_REVD");
				tbl7TtlItcRevdgetSummEntity
						.setIamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7TotItcRevdReqDto().getIamt());
				tbl7TtlItcRevdgetSummEntity
						.setSamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7TotItcRevdReqDto().getSamt());
				tbl7TtlItcRevdgetSummEntity
						.setCamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7TotItcRevdReqDto().getCamt());
				tbl7TtlItcRevdgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7TotItcRevdReqDto().getCsamt());
				tbl7TtlItcRevdgetSummEntity.setActive(true);
				tbl7TtlItcRevdgetSummEntity.setCreatedBy(userName);
				tbl7TtlItcRevdgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl7GetSummaryEntities.add(tbl7TtlItcRevdgetSummEntity);
			}
			if (gstr9GetReqDto.getTable7ReqDto()
					.getGstr9Table7NetItcAvalReqDto() != null) {
				Gstr9GetSummaryEntity tbl7NetItcAvalgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl7NetItcAvalgetSummEntity.setChkSum(
						gstr9GetReqDto.getTable7ReqDto().getChksum());
				tbl7NetItcAvalgetSummEntity.setSection("7");
				tbl7NetItcAvalgetSummEntity.setSubSection("7J");
				tbl7NetItcAvalgetSummEntity.setSubSectionName("NET_ITC_AVAL");
				tbl7NetItcAvalgetSummEntity
						.setIamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7NetItcAvalReqDto().getIamt());
				tbl7NetItcAvalgetSummEntity
						.setSamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7NetItcAvalReqDto().getSamt());
				tbl7NetItcAvalgetSummEntity
						.setCamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7NetItcAvalReqDto().getCamt());
				tbl7NetItcAvalgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable7ReqDto()
								.getGstr9Table7NetItcAvalReqDto().getCsamt());
				tbl7NetItcAvalgetSummEntity.setActive(true);
				tbl7NetItcAvalgetSummEntity.setCreatedBy(userName);
				tbl7NetItcAvalgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl7GetSummaryEntities.add(tbl7NetItcAvalgetSummEntity);
			}
		}
		return listOfTbl7GetSummaryEntities;
	}

	public List<Gstr9GetSummaryEntity> populateTbl8Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9GetSummaryEntity> listOfTbl8GetSummaryEntities,
			String userName) {
		if (gstr9GetReqDto.getTable8ReqDto() != null) {
			String chckSum = gstr9GetReqDto.getTable8ReqDto().getChksum();
			if (gstr9GetReqDto.getTable8ReqDto()
					.getGstr9Table8Itc2AReqDto() != null) {
				Gstr9GetSummaryEntity tbl8Itc2agetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl8Itc2agetSummEntity.setChkSum(chckSum);
				tbl8Itc2agetSummEntity.setSection("8");
				tbl8Itc2agetSummEntity.setSubSection("8A");
				tbl8Itc2agetSummEntity.setSubSectionName("ITC_2A");
				tbl8Itc2agetSummEntity.setIamt(gstr9GetReqDto.getTable8ReqDto()
						.getGstr9Table8Itc2AReqDto().getIamt());
				tbl8Itc2agetSummEntity.setSamt(gstr9GetReqDto.getTable8ReqDto()
						.getGstr9Table8Itc2AReqDto().getSamt());
				tbl8Itc2agetSummEntity.setCamt(gstr9GetReqDto.getTable8ReqDto()
						.getGstr9Table8Itc2AReqDto().getCamt());
				tbl8Itc2agetSummEntity.setCsamt(gstr9GetReqDto.getTable8ReqDto()
						.getGstr9Table8Itc2AReqDto().getCsamt());
				tbl8Itc2agetSummEntity.setActive(true);
				tbl8Itc2agetSummEntity.setCreatedBy(userName);
				tbl8Itc2agetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl8GetSummaryEntities.add(tbl8Itc2agetSummEntity);
			}
			if (gstr9GetReqDto.getTable8ReqDto()
					.getGstr9Table8ItcTotReqDto() != null) {
				Gstr9GetSummaryEntity tbl8ItcTotgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl8ItcTotgetSummEntity.setChkSum(chckSum);
				tbl8ItcTotgetSummEntity.setSection("8");
				tbl8ItcTotgetSummEntity.setSubSection("8B");
				tbl8ItcTotgetSummEntity.setSubSectionName("ITC_TOT");
				tbl8ItcTotgetSummEntity.setIamt(gstr9GetReqDto.getTable8ReqDto()
						.getGstr9Table8ItcTotReqDto().getIamt());
				tbl8ItcTotgetSummEntity.setSamt(gstr9GetReqDto.getTable8ReqDto()
						.getGstr9Table8ItcTotReqDto().getSamt());
				tbl8ItcTotgetSummEntity.setCamt(gstr9GetReqDto.getTable8ReqDto()
						.getGstr9Table8ItcTotReqDto().getCamt());
				tbl8ItcTotgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8ItcTotReqDto().getCsamt());
				tbl8ItcTotgetSummEntity.setActive(true);
				tbl8ItcTotgetSummEntity.setCreatedBy(userName);
				tbl8ItcTotgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl8GetSummaryEntities.add(tbl8ItcTotgetSummEntity);
			}
			if (gstr9GetReqDto.getTable8ReqDto()
					.getGstr9Table8ItcInwdSuppReqDto() != null) {
				Gstr9GetSummaryEntity tbl8ItcInwdSuppgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl8ItcInwdSuppgetSummEntity.setChkSum(chckSum);
				tbl8ItcInwdSuppgetSummEntity.setSection("8");
				tbl8ItcInwdSuppgetSummEntity.setSubSection("8C");
				tbl8ItcInwdSuppgetSummEntity.setSubSectionName("ITC_INWD_SUPP");
				tbl8ItcInwdSuppgetSummEntity
						.setIamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8ItcInwdSuppReqDto().getIamt());
				tbl8ItcInwdSuppgetSummEntity
						.setSamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8ItcInwdSuppReqDto().getSamt());
				tbl8ItcInwdSuppgetSummEntity
						.setCamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8ItcInwdSuppReqDto().getCamt());
				tbl8ItcInwdSuppgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8ItcInwdSuppReqDto().getCsamt());
				tbl8ItcInwdSuppgetSummEntity.setActive(true);
				tbl8ItcInwdSuppgetSummEntity.setCreatedBy(userName);
				tbl8ItcInwdSuppgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl8GetSummaryEntities.add(tbl8ItcInwdSuppgetSummEntity);
			}
			if (gstr9GetReqDto.getTable8ReqDto()
					.getGstr9Table8DifferenceABCReqDto() != null) {
				Gstr9GetSummaryEntity tbl8DifferencegetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl8DifferencegetSummEntity.setChkSum(chckSum);
				tbl8DifferencegetSummEntity.setSection("8");
				tbl8DifferencegetSummEntity.setSubSection("8D");
				tbl8DifferencegetSummEntity.setSubSectionName("DIFFERENCEABC");
				tbl8DifferencegetSummEntity
						.setIamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8DifferenceABCReqDto().getIamt());
				tbl8DifferencegetSummEntity
						.setSamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8DifferenceABCReqDto().getSamt());
				tbl8DifferencegetSummEntity
						.setCamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8DifferenceABCReqDto().getCamt());
				tbl8DifferencegetSummEntity.setCsamt(gstr9GetReqDto
						.getTable8ReqDto().getGstr9Table8DifferenceABCReqDto()
						.getCsamt());
				tbl8DifferencegetSummEntity.setActive(true);
				tbl8DifferencegetSummEntity.setCreatedBy(userName);
				tbl8DifferencegetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl8GetSummaryEntities.add(tbl8DifferencegetSummEntity);
			}
			if (gstr9GetReqDto.getTable8ReqDto()
					.getGstr9Table8IogItcNtAvaildReqDto() != null) {
				Gstr9GetSummaryEntity tbl8ItcNtAvailgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl8ItcNtAvailgetSummEntity.setChkSum(chckSum);
				tbl8ItcNtAvailgetSummEntity.setSection("8");
				tbl8ItcNtAvailgetSummEntity.setSubSection("8E");
				tbl8ItcNtAvailgetSummEntity
						.setSubSectionName("ITC_NOT_AVAILED");
				tbl8ItcNtAvailgetSummEntity.setIamt(gstr9GetReqDto
						.getTable8ReqDto().getGstr9Table8IogItcNtAvaildReqDto()
						.getIamt());
				tbl8ItcNtAvailgetSummEntity.setSamt(gstr9GetReqDto
						.getTable8ReqDto().getGstr9Table8IogItcNtAvaildReqDto()
						.getSamt());
				tbl8ItcNtAvailgetSummEntity.setCamt(gstr9GetReqDto
						.getTable8ReqDto().getGstr9Table8IogItcNtAvaildReqDto()
						.getCamt());
				tbl8ItcNtAvailgetSummEntity.setCsamt(gstr9GetReqDto
						.getTable8ReqDto().getGstr9Table8IogItcNtAvaildReqDto()
						.getCsamt());
				tbl8ItcNtAvailgetSummEntity.setActive(true);
				tbl8ItcNtAvailgetSummEntity.setCreatedBy(userName);
				tbl8ItcNtAvailgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl8GetSummaryEntities.add(tbl8ItcNtAvailgetSummEntity);
			}
			if (gstr9GetReqDto.getTable8ReqDto()
					.getGstr9Table8ItcNtElegReqDto() != null) {
				Gstr9GetSummaryEntity tbl8ItcNtEleggetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl8ItcNtEleggetSummEntity.setChkSum(chckSum);
				tbl8ItcNtEleggetSummEntity.setSection("8");
				tbl8ItcNtEleggetSummEntity.setSubSection("8F");
				tbl8ItcNtEleggetSummEntity.setSubSectionName("ITC_NOT_ELEG");
				tbl8ItcNtEleggetSummEntity
						.setIamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8ItcNtElegReqDto().getIamt());
				tbl8ItcNtEleggetSummEntity
						.setSamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8ItcNtElegReqDto().getSamt());
				tbl8ItcNtEleggetSummEntity
						.setCamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8ItcNtElegReqDto().getCamt());
				tbl8ItcNtEleggetSummEntity
						.setCsamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8ItcNtElegReqDto().getCsamt());
				tbl8ItcNtEleggetSummEntity.setActive(true);
				tbl8ItcNtEleggetSummEntity.setCreatedBy(userName);
				tbl8ItcNtEleggetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl8GetSummaryEntities.add(tbl8ItcNtEleggetSummEntity);
			}
			if (gstr9GetReqDto.getTable8ReqDto()
					.getGstr9Table8IogTaxPaidReqDto() != null) {
				Gstr9GetSummaryEntity tbl8IogTxPaidgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl8IogTxPaidgetSummEntity.setChkSum(chckSum);
				tbl8IogTxPaidgetSummEntity.setSection("8");
				tbl8IogTxPaidgetSummEntity.setSubSection("8G");
				tbl8IogTxPaidgetSummEntity.setSubSectionName("IOG_TX_PAID");
				tbl8IogTxPaidgetSummEntity
						.setIamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8IogTaxPaidReqDto().getIamt());
				tbl8IogTxPaidgetSummEntity
						.setSamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8IogTaxPaidReqDto().getSamt());
				tbl8IogTxPaidgetSummEntity
						.setCamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8IogTaxPaidReqDto().getCamt());
				tbl8IogTxPaidgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8IogTaxPaidReqDto().getCsamt());
				tbl8IogTxPaidgetSummEntity.setActive(true);
				tbl8IogTxPaidgetSummEntity.setCreatedBy(userName);
				tbl8IogTxPaidgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl8GetSummaryEntities.add(tbl8IogTxPaidgetSummEntity);
			}
			if (gstr9GetReqDto.getTable8ReqDto()
					.getGstr9Table8IogItcAvaildReqDto() != null) {
				Gstr9GetSummaryEntity tbl8IogItcAvagetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl8IogItcAvagetSummEntity.setChkSum(chckSum);
				tbl8IogItcAvagetSummEntity.setSection("8");
				tbl8IogItcAvagetSummEntity.setSubSection("8H");
				tbl8IogItcAvagetSummEntity.setSubSectionName("IOG_ITC_AVAILD");
				tbl8IogItcAvagetSummEntity
						.setIamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8IogItcAvaildReqDto().getIamt());
				tbl8IogItcAvagetSummEntity
						.setSamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8IogItcAvaildReqDto().getSamt());
				tbl8IogItcAvagetSummEntity
						.setCamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8IogItcAvaildReqDto().getCamt());
				tbl8IogItcAvagetSummEntity
						.setCsamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8IogItcAvaildReqDto().getCsamt());
				tbl8IogItcAvagetSummEntity.setActive(true);
				tbl8IogItcAvagetSummEntity.setCreatedBy(userName);
				tbl8IogItcAvagetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl8GetSummaryEntities.add(tbl8IogItcAvagetSummEntity);
			}
			if (gstr9GetReqDto.getTable8ReqDto()
					.getGstr9Table8DifferenceGhReqDto() != null) {
				Gstr9GetSummaryEntity tbl8DiffeGhgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl8DiffeGhgetSummEntity.setChkSum(chckSum);
				tbl8DiffeGhgetSummEntity.setSection("8");
				tbl8DiffeGhgetSummEntity.setSubSection("8I");
				tbl8DiffeGhgetSummEntity.setSubSectionName("DIFFERENCE_GH");
				tbl8DiffeGhgetSummEntity
						.setIamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8DifferenceGhReqDto().getIamt());
				tbl8DiffeGhgetSummEntity
						.setSamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8DifferenceGhReqDto().getSamt());
				tbl8DiffeGhgetSummEntity
						.setCamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8DifferenceGhReqDto().getCamt());
				tbl8DiffeGhgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8DifferenceGhReqDto().getCsamt());
				tbl8DiffeGhgetSummEntity.setActive(true);
				tbl8DiffeGhgetSummEntity.setCreatedBy(userName);
				tbl8DiffeGhgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl8GetSummaryEntities.add(tbl8DiffeGhgetSummEntity);
			}
			if (gstr9GetReqDto.getTable8ReqDto()
					.getGstr9Table8IogItcNtAvaildReqDto() != null) {
				Gstr9GetSummaryEntity tbl8IogItcNtAvagetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl8IogItcNtAvagetSummEntity.setChkSum(chckSum);
				tbl8IogItcNtAvagetSummEntity.setSection("8");
				tbl8IogItcNtAvagetSummEntity.setSubSection("8J");
				tbl8IogItcNtAvagetSummEntity
						.setSubSectionName("IOG_ITC_NT_AVAILD");
				tbl8IogItcNtAvagetSummEntity.setIamt(gstr9GetReqDto
						.getTable8ReqDto().getGstr9Table8IogItcNtAvaildReqDto()
						.getIamt());
				tbl8IogItcNtAvagetSummEntity.setSamt(gstr9GetReqDto
						.getTable8ReqDto().getGstr9Table8IogItcNtAvaildReqDto()
						.getSamt());
				tbl8IogItcNtAvagetSummEntity.setCamt(gstr9GetReqDto
						.getTable8ReqDto().getGstr9Table8IogItcNtAvaildReqDto()
						.getCamt());
				tbl8IogItcNtAvagetSummEntity.setCsamt(gstr9GetReqDto
						.getTable8ReqDto().getGstr9Table8IogItcNtAvaildReqDto()
						.getCsamt());
				tbl8IogItcNtAvagetSummEntity.setActive(true);
				tbl8IogItcNtAvagetSummEntity.setCreatedBy(userName);
				tbl8IogItcNtAvagetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl8GetSummaryEntities.add(tbl8IogItcNtAvagetSummEntity);
			}
			if (gstr9GetReqDto.getTable8ReqDto()
					.getGstr9Table8TotItcLapsedReqDto() != null) {
				Gstr9GetSummaryEntity tbl8TotItcLapgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl8TotItcLapgetSummEntity.setChkSum(chckSum);
				tbl8TotItcLapgetSummEntity.setSection("8");
				tbl8TotItcLapgetSummEntity.setSubSection("8K");
				tbl8TotItcLapgetSummEntity.setSubSectionName("TOT_ITC_LAPSED");
				tbl8TotItcLapgetSummEntity
						.setIamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8TotItcLapsedReqDto().getIamt());
				tbl8TotItcLapgetSummEntity
						.setSamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8TotItcLapsedReqDto().getSamt());
				tbl8TotItcLapgetSummEntity
						.setCamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8TotItcLapsedReqDto().getCamt());
				tbl8TotItcLapgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable8ReqDto()
								.getGstr9Table8TotItcLapsedReqDto().getCsamt());
				tbl8TotItcLapgetSummEntity.setActive(true);
				tbl8TotItcLapgetSummEntity.setCreatedBy(userName);
				tbl8TotItcLapgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl8GetSummaryEntities.add(tbl8TotItcLapgetSummEntity);
			}
		}
		return listOfTbl8GetSummaryEntities;
	}

	public List<Gstr9GetSummaryEntity> populateTbl9Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9GetSummaryEntity> listOfTbl9GetSummaryEntities,
			String userName) {
		if (gstr9GetReqDto.getTable9ReqDto() != null) {
			String chckSum = gstr9GetReqDto.getTable9ReqDto().getChksum();
			if (gstr9GetReqDto.getTable9ReqDto()
					.getGstr9Table9IamtReqDto() != null) {
				Gstr9GetSummaryEntity tbl9IamtgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9IamtgetSummEntity.setChkSum(chckSum);
				tbl9IamtgetSummEntity.setSection("9");
				tbl9IamtgetSummEntity.setSubSection("9A");
				tbl9IamtgetSummEntity.setSubSectionName("INTEGRATED_Tax");
				tbl9IamtgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9IamtReqDto().getTxpyble());
				tbl9IamtgetSummEntity
						.setTxpaidCash(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9IamtReqDto().getTxpaidCash());
				tbl9IamtgetSummEntity.setTaxPaidItcIamt(gstr9GetReqDto
						.getTable9ReqDto().getGstr9Table9IamtReqDto()
						.getTaxPaidItcIamt());
				tbl9IamtgetSummEntity.setTaxPaidItcCamt(gstr9GetReqDto
						.getTable9ReqDto().getGstr9Table9IamtReqDto()
						.getTaxPaidItcCamt());
				tbl9IamtgetSummEntity.setTaxPaidItcSamt(gstr9GetReqDto
						.getTable9ReqDto().getGstr9Table9IamtReqDto()
						.getTaxPaidItcSamt());
				tbl9IamtgetSummEntity.setActive(true);
				tbl9IamtgetSummEntity.setCreatedBy(userName);
				tbl9IamtgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl9GetSummaryEntities.add(tbl9IamtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable9ReqDto()
					.getGstr9Table9CamtReqDto() != null) {
				Gstr9GetSummaryEntity tbl9CamtgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9CamtgetSummEntity.setChkSum(chckSum);
				tbl9CamtgetSummEntity.setSection("9");
				tbl9CamtgetSummEntity.setSubSection("9B");
				tbl9CamtgetSummEntity.setSubSectionName("CENTRAL_TAX");
				tbl9CamtgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9CamtReqDto().getTxpyble());
				tbl9CamtgetSummEntity
						.setTxpaidCash(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9CamtReqDto().getTxpaidCash());
				tbl9CamtgetSummEntity.setTaxPaidItcIamt(gstr9GetReqDto
						.getTable9ReqDto().getGstr9Table9CamtReqDto()
						.getTaxPaidItcIamt());
				tbl9CamtgetSummEntity.setTaxPaidItcCamt(gstr9GetReqDto
						.getTable9ReqDto().getGstr9Table9CamtReqDto()
						.getTaxPaidItcCamt());
				tbl9CamtgetSummEntity.setActive(true);
				tbl9CamtgetSummEntity.setCreatedBy(userName);
				tbl9CamtgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl9GetSummaryEntities.add(tbl9CamtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable9ReqDto()
					.getGstr9Table9SamtReqDto() != null) {
				Gstr9GetSummaryEntity tbl9SamtgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9SamtgetSummEntity.setChkSum(chckSum);
				tbl9SamtgetSummEntity.setSection("9");
				tbl9SamtgetSummEntity.setSubSection("9C");
				tbl9SamtgetSummEntity.setSubSectionName("STATE_TAX");
				tbl9SamtgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9SamtReqDto().getTxpyble());
				tbl9SamtgetSummEntity
						.setTxpaidCash(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9SamtReqDto().getTxpaidCash());
				tbl9SamtgetSummEntity.setTaxPaidItcIamt(gstr9GetReqDto
						.getTable9ReqDto().getGstr9Table9SamtReqDto()
						.getTaxPaidItcIamt());
				tbl9SamtgetSummEntity.setTaxPaidItcCamt(gstr9GetReqDto
						.getTable9ReqDto().getGstr9Table9CamtReqDto()
						.getTaxPaidItcCamt());
				tbl9SamtgetSummEntity.setActive(true);
				tbl9SamtgetSummEntity.setCreatedBy(userName);
				tbl9SamtgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl9GetSummaryEntities.add(tbl9SamtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable9ReqDto()
					.getGstr9Table9CsamtReqDto() != null) {
				Gstr9GetSummaryEntity tbl9CessAmtgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9CessAmtgetSummEntity.setChkSum(chckSum);
				tbl9CessAmtgetSummEntity.setSection("9");
				tbl9CessAmtgetSummEntity.setSubSection("9D");
				tbl9CessAmtgetSummEntity.setSubSectionName("CESS");
				tbl9CessAmtgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9CsamtReqDto().getTxpyble());
				tbl9CessAmtgetSummEntity
						.setTxpaidCash(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9CsamtReqDto().getTxpaidCash());
				tbl9CessAmtgetSummEntity.setTaxPaidItcCSamt(gstr9GetReqDto
						.getTable9ReqDto().getGstr9Table9CsamtReqDto()
						.getTaxPaidItcCsamt());
				tbl9CessAmtgetSummEntity.setActive(true);
				tbl9CessAmtgetSummEntity.setCreatedBy(userName);
				tbl9CessAmtgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl9GetSummaryEntities.add(tbl9CessAmtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable9ReqDto()
					.getGstr9Table9IntrReqDto() != null) {
				Gstr9GetSummaryEntity tbl9IntrgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9IntrgetSummEntity.setChkSum(chckSum);
				tbl9IntrgetSummEntity.setSection("9");
				tbl9IntrgetSummEntity.setSubSection("9E");
				tbl9IntrgetSummEntity.setSubSectionName("INTEREST");
				tbl9IntrgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9IntrReqDto().getTxpyble());
				tbl9IntrgetSummEntity
						.setTxpaidCash(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9IntrReqDto().getTxpaidCash());
				tbl9IntrgetSummEntity.setActive(true);
				tbl9IntrgetSummEntity.setCreatedBy(userName);
				tbl9IntrgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl9GetSummaryEntities.add(tbl9IntrgetSummEntity);
			}
			if (gstr9GetReqDto.getTable9ReqDto()
					.getGstr9Table9TeeReqDto() != null) {
				Gstr9GetSummaryEntity tbl9LateFeegetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9LateFeegetSummEntity.setChkSum(chckSum);
				tbl9LateFeegetSummEntity.setSection("9");
				tbl9LateFeegetSummEntity.setSubSection("9F");
				tbl9LateFeegetSummEntity.setSubSectionName("LATE_FEE");
				tbl9LateFeegetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9TeeReqDto().getTxpyble());
				tbl9LateFeegetSummEntity
						.setTxpaidCash(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9TeeReqDto().getTxpaidCash());
				tbl9LateFeegetSummEntity.setActive(true);
				tbl9LateFeegetSummEntity.setCreatedBy(userName);
				tbl9LateFeegetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl9GetSummaryEntities.add(tbl9LateFeegetSummEntity);
			}
			if (gstr9GetReqDto.getTable9ReqDto()
					.getGstr9Table9PenReqDto() != null) {
				Gstr9GetSummaryEntity tbl9PengetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9PengetSummEntity.setChkSum(chckSum);
				tbl9PengetSummEntity.setSection("9");
				tbl9PengetSummEntity.setSubSection("9G");
				tbl9PengetSummEntity.setSubSectionName("PEN");
				tbl9PengetSummEntity.setTxPyble(gstr9GetReqDto.getTable9ReqDto()
						.getGstr9Table9PenReqDto().getTxpyble());
				tbl9PengetSummEntity
						.setTxpaidCash(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9PenReqDto().getTxpaidCash());
				tbl9PengetSummEntity.setActive(true);
				tbl9PengetSummEntity.setCreatedBy(userName);
				tbl9PengetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl9GetSummaryEntities.add(tbl9PengetSummEntity);
			}
			if (gstr9GetReqDto.getTable9ReqDto()
					.getGstr9Table9OtherReqDto() != null) {
				Gstr9GetSummaryEntity tbl9OthergetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9OthergetSummEntity.setChkSum(chckSum);
				tbl9OthergetSummEntity.setSection("9");
				tbl9OthergetSummEntity.setSubSection("9H");
				tbl9OthergetSummEntity.setSubSectionName("OTHER");
				tbl9OthergetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9OtherReqDto().getTxpyble());
				tbl9OthergetSummEntity
						.setTxpaidCash(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9OtherReqDto().getTxpaidCash());
				tbl9OthergetSummEntity.setActive(true);
				tbl9OthergetSummEntity.setCreatedBy(userName);
				tbl9OthergetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl9GetSummaryEntities.add(tbl9OthergetSummEntity);
			}
		}
		return listOfTbl9GetSummaryEntities;
	}

	public List<Gstr9GetSummaryEntity> populateTbl10Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9GetSummaryEntity> listOfTbl10GetSummaryEntities,
			String userName) {
		if (gstr9GetReqDto.getTable10ReqDto() != null) {
			String chckSum = gstr9GetReqDto.getTable10ReqDto().getChksum();
			if (gstr9GetReqDto.getTable10ReqDto().getDbnAmdReqDto() != null) {
				Gstr9GetSummaryEntity tbl10DbtAmtgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl10DbtAmtgetSummEntity.setChkSum(chckSum);
				tbl10DbtAmtgetSummEntity.setSection("10");
				tbl10DbtAmtgetSummEntity.setSubSection("10");
				tbl10DbtAmtgetSummEntity.setSubSectionName("DEBIT_AMENDMENTS");
				tbl10DbtAmtgetSummEntity.setTxVal(gstr9GetReqDto
						.getTable10ReqDto().getDbnAmdReqDto().getTxval());
				tbl10DbtAmtgetSummEntity.setIamt(gstr9GetReqDto
						.getTable10ReqDto().getDbnAmdReqDto().getIamt());
				tbl10DbtAmtgetSummEntity.setCamt(gstr9GetReqDto
						.getTable10ReqDto().getDbnAmdReqDto().getCamt());
				tbl10DbtAmtgetSummEntity.setSamt(gstr9GetReqDto
						.getTable10ReqDto().getDbnAmdReqDto().getSamt());
				tbl10DbtAmtgetSummEntity.setCsamt(gstr9GetReqDto
						.getTable10ReqDto().getDbnAmdReqDto().getCsamt());
				tbl10DbtAmtgetSummEntity.setActive(true);
				tbl10DbtAmtgetSummEntity.setCreatedBy(userName);
				tbl10DbtAmtgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl10GetSummaryEntities.add(tbl10DbtAmtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable10ReqDto().getCdnAmdReqDto() != null) {
				Gstr9GetSummaryEntity tbl10CdnAmtgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl10CdnAmtgetSummEntity.setChkSum(chckSum);
				tbl10CdnAmtgetSummEntity.setSection("11");
				tbl10CdnAmtgetSummEntity.setSubSection("11");
				tbl10CdnAmtgetSummEntity.setSubSectionName("CREDIT_AMENDMENTS");
				tbl10CdnAmtgetSummEntity.setTxVal(gstr9GetReqDto
						.getTable10ReqDto().getCdnAmdReqDto().getTxval());
				tbl10CdnAmtgetSummEntity.setIamt(gstr9GetReqDto
						.getTable10ReqDto().getCdnAmdReqDto().getIamt());
				tbl10CdnAmtgetSummEntity.setCamt(gstr9GetReqDto
						.getTable10ReqDto().getCdnAmdReqDto().getCamt());
				tbl10CdnAmtgetSummEntity.setSamt(gstr9GetReqDto
						.getTable10ReqDto().getCdnAmdReqDto().getSamt());
				tbl10CdnAmtgetSummEntity.setCsamt(gstr9GetReqDto
						.getTable10ReqDto().getCdnAmdReqDto().getCsamt());
				tbl10CdnAmtgetSummEntity.setActive(true);
				tbl10CdnAmtgetSummEntity.setCreatedBy(userName);
				tbl10CdnAmtgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl10GetSummaryEntities.add(tbl10CdnAmtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable10ReqDto()
					.getGstr9Table10ItcRvslReqDto() != null) {
				Gstr9GetSummaryEntity tbl10ItcRvslgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl10ItcRvslgetSummEntity.setChkSum(chckSum);
				tbl10ItcRvslgetSummEntity.setSection("12");
				tbl10ItcRvslgetSummEntity.setSubSection("12");
				tbl10ItcRvslgetSummEntity.setSubSectionName("ITC_RVSL");
				tbl10ItcRvslgetSummEntity
						.setTxVal(gstr9GetReqDto.getTable10ReqDto()
								.getGstr9Table10ItcRvslReqDto().getTxval());
				tbl10ItcRvslgetSummEntity
						.setIamt(gstr9GetReqDto.getTable10ReqDto()
								.getGstr9Table10ItcRvslReqDto().getIamt());
				tbl10ItcRvslgetSummEntity
						.setCamt(gstr9GetReqDto.getTable10ReqDto()
								.getGstr9Table10ItcRvslReqDto().getCamt());
				tbl10ItcRvslgetSummEntity
						.setSamt(gstr9GetReqDto.getTable10ReqDto()
								.getGstr9Table10ItcRvslReqDto().getSamt());
				tbl10ItcRvslgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable10ReqDto()
								.getGstr9Table10ItcRvslReqDto().getCsamt());
				tbl10ItcRvslgetSummEntity.setActive(true);
				tbl10ItcRvslgetSummEntity.setCreatedBy(userName);
				tbl10ItcRvslgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl10GetSummaryEntities.add(tbl10ItcRvslgetSummEntity);
			}
			if (gstr9GetReqDto.getTable10ReqDto()
					.getGstr9Table10ItcAvaildReqDto() != null) {
				Gstr9GetSummaryEntity tbl10ItcAvailgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl10ItcAvailgetSummEntity.setChkSum(chckSum);
				tbl10ItcAvailgetSummEntity.setSection("13");
				tbl10ItcAvailgetSummEntity.setSubSection("13");
				tbl10ItcAvailgetSummEntity.setSubSectionName("ITC_AVAILD");
				tbl10ItcAvailgetSummEntity
						.setTxVal(gstr9GetReqDto.getTable10ReqDto()
								.getGstr9Table10ItcAvaildReqDto().getTxval());
				tbl10ItcAvailgetSummEntity
						.setIamt(gstr9GetReqDto.getTable10ReqDto()
								.getGstr9Table10ItcAvaildReqDto().getIamt());
				tbl10ItcAvailgetSummEntity
						.setCamt(gstr9GetReqDto.getTable10ReqDto()
								.getGstr9Table10ItcAvaildReqDto().getCamt());
				tbl10ItcAvailgetSummEntity
						.setSamt(gstr9GetReqDto.getTable10ReqDto()
								.getGstr9Table10ItcAvaildReqDto().getSamt());
				tbl10ItcAvailgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable10ReqDto()
								.getGstr9Table10ItcAvaildReqDto().getCsamt());
				tbl10ItcAvailgetSummEntity.setActive(true);
				tbl10ItcAvailgetSummEntity.setCreatedBy(userName);
				tbl10ItcAvailgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl10GetSummaryEntities.add(tbl10ItcAvailgetSummEntity);
			}

			// Doubt
			// if (gstr9GetReqDto.getTable10ReqDto()
			// .getGstr9Table10TotalTurnOverReqDto() != null) {
			// Gstr9GetSummaryEntity tbl10TtlTurOvgetSummEntity = new
			// Gstr9GetSummaryEntity(
			// gstin, fy, taxPeriod, derviedTaxPeriod);
			// tbl10TtlTurOvgetSummEntity.setChkSum(chckSum);
			// tbl10TtlTurOvgetSummEntity.setSection("10");
			// tbl10TtlTurOvgetSummEntity.setSubSection("13");
			// tbl10TtlTurOvgetSummEntity.setSubSectionName("TOTAL_TURNOVER");
			// tbl10TtlTurOvgetSummEntity.setTxVal(gstr9GetReqDto
			// .getTable10ReqDto().getGstr9Table10TotalTurnOverReqDto()
			// .getTxval());
			// tbl10TtlTurOvgetSummEntity.setIamt(gstr9GetReqDto
			// .getTable10ReqDto().getGstr9Table10TotalTurnOverReqDto()
			// .getIamt());
			// tbl10TtlTurOvgetSummEntity.setCamt(gstr9GetReqDto
			// .getTable10ReqDto().getGstr9Table10TotalTurnOverReqDto()
			// .getCamt());
			// tbl10TtlTurOvgetSummEntity.setSamt(gstr9GetReqDto
			// .getTable10ReqDto().getGstr9Table10TotalTurnOverReqDto()
			// .getSamt());
			// tbl10TtlTurOvgetSummEntity.setCsamt(gstr9GetReqDto
			// .getTable10ReqDto().getGstr9Table10TotalTurnOverReqDto()
			// .getCsamt());
			// tbl10TtlTurOvgetSummEntity.setActive(true);
			// tbl10TtlTurOvgetSummEntity.setCreatedBy(userName);
			// tbl10TtlTurOvgetSummEntity.setCreatedOn(LocalDateTime.now());
			// listOfTbl10GetSummaryEntities.add(tbl10TtlTurOvgetSummEntity);
			// }
		}
		return listOfTbl10GetSummaryEntities;
	}

	public List<Gstr9GetSummaryEntity> populateTbl14Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9GetSummaryEntity> listOfTbl14GetSummaryEntities,
			String userName) {
		if (gstr9GetReqDto.getTable14ReqDto() != null) {
			String chckSum = gstr9GetReqDto.getTable14ReqDto().getChksum();
			if (gstr9GetReqDto.getTable14ReqDto()
					.getGstr9Table14IamtReqDto() != null) {
				Gstr9GetSummaryEntity tbl9IamtgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9IamtgetSummEntity.setChkSum(chckSum);
				tbl9IamtgetSummEntity.setSection("14");
				tbl9IamtgetSummEntity.setSubSection("14A");
				tbl9IamtgetSummEntity.setSubSectionName("INTEGRATED_TAX");
				tbl9IamtgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable14ReqDto()
								.getGstr9Table14IamtReqDto().getTxpyble());
				tbl9IamtgetSummEntity
						.setTxPaid(gstr9GetReqDto.getTable14ReqDto()
								.getGstr9Table14IamtReqDto().getTxpaid());
				tbl9IamtgetSummEntity.setActive(true);
				tbl9IamtgetSummEntity.setCreatedBy(userName);
				tbl9IamtgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl14GetSummaryEntities.add(tbl9IamtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable14ReqDto()
					.getGstr9Table14CamtReqDto() != null) {
				Gstr9GetSummaryEntity tbl14CamtgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl14CamtgetSummEntity.setChkSum(chckSum);
				tbl14CamtgetSummEntity.setSection("14");
				tbl14CamtgetSummEntity.setSubSection("14B");
				tbl14CamtgetSummEntity.setSubSectionName("CENTRAL_TAX");
				tbl14CamtgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable14ReqDto()
								.getGstr9Table14CamtReqDto().getTxpyble());
				tbl14CamtgetSummEntity
						.setTxPaid(gstr9GetReqDto.getTable14ReqDto()
								.getGstr9Table14CamtReqDto().getTxpaid());
				tbl14CamtgetSummEntity.setActive(true);
				tbl14CamtgetSummEntity.setCreatedBy(userName);
				tbl14CamtgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl14GetSummaryEntities.add(tbl14CamtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable14ReqDto()
					.getGstr9Table14SamtReqDto() != null) {
				Gstr9GetSummaryEntity tbl14SamtgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl14SamtgetSummEntity.setChkSum(chckSum);
				tbl14SamtgetSummEntity.setSection("14");
				tbl14SamtgetSummEntity.setSubSection("14C");
				tbl14SamtgetSummEntity.setSubSectionName("STATE_TAX");
				tbl14SamtgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable14ReqDto()
								.getGstr9Table14SamtReqDto().getTxpyble());
				tbl14SamtgetSummEntity
						.setTxPaid(gstr9GetReqDto.getTable14ReqDto()
								.getGstr9Table14SamtReqDto().getTxpaid());
				tbl14SamtgetSummEntity.setActive(true);
				tbl14SamtgetSummEntity.setCreatedBy(userName);
				tbl14SamtgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl14GetSummaryEntities.add(tbl14SamtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable14ReqDto()
					.getGstr9Table14CSamtReqDto() != null) {
				Gstr9GetSummaryEntity tbl14CessAmtgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl14CessAmtgetSummEntity.setChkSum(chckSum);
				tbl14CessAmtgetSummEntity.setSection("14");
				tbl14CessAmtgetSummEntity.setSubSection("14D");
				tbl14CessAmtgetSummEntity.setSubSectionName("CESS");
				tbl14CessAmtgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable14ReqDto()
								.getGstr9Table14CSamtReqDto().getTxpyble());
				tbl14CessAmtgetSummEntity
						.setTxPaid(gstr9GetReqDto.getTable14ReqDto()
								.getGstr9Table14CSamtReqDto().getTxpaid());
				tbl14CessAmtgetSummEntity.setActive(true);
				tbl14CessAmtgetSummEntity.setCreatedBy(userName);
				tbl14CessAmtgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl14GetSummaryEntities.add(tbl14CessAmtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable14ReqDto()
					.getGstr9Table14IntrReqDto() != null) {
				Gstr9GetSummaryEntity tbl14IntrgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl14IntrgetSummEntity.setChkSum(chckSum);
				tbl14IntrgetSummEntity.setSection("14");
				tbl14IntrgetSummEntity.setSubSection("14E");
				tbl14IntrgetSummEntity.setSubSectionName("INTEREST");
				tbl14IntrgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable14ReqDto()
								.getGstr9Table14IntrReqDto().getTxpyble());
				tbl14IntrgetSummEntity
						.setTxPaid(gstr9GetReqDto.getTable14ReqDto()
								.getGstr9Table14IntrReqDto().getTxpaid());
				tbl14IntrgetSummEntity.setActive(true);
				tbl14IntrgetSummEntity.setCreatedBy(userName);
				tbl14IntrgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl14GetSummaryEntities.add(tbl14IntrgetSummEntity);
			}
		}
		return listOfTbl14GetSummaryEntities;
	}

	public List<Gstr9GetSummaryEntity> populateTbl15Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9GetSummaryEntity> listOfTbl15GetSummaryEntities,
			String userName) {
		if (gstr9GetReqDto.getTable15ReqDto() != null) {
			String chckSum = gstr9GetReqDto.getTable15ReqDto().getChksum();
			if (gstr9GetReqDto.getTable15ReqDto()
					.getGstr9Table15RfdClmdReqDto() != null) {
				Gstr9GetSummaryEntity tbl15RfdClmdIamtgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl15RfdClmdIamtgetSummEntity.setChkSum(chckSum);
				tbl15RfdClmdIamtgetSummEntity.setSection("15");
				tbl15RfdClmdIamtgetSummEntity.setSubSection("15A");
				tbl15RfdClmdIamtgetSummEntity
						.setSubSectionName("TOTAL_REFUND_CLAIMED");
				tbl15RfdClmdIamtgetSummEntity
						.setIamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdClmdReqDto().getIamt());
				tbl15RfdClmdIamtgetSummEntity
						.setCamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdClmdReqDto().getCamt());
				tbl15RfdClmdIamtgetSummEntity
						.setSamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdClmdReqDto().getSamt());
				tbl15RfdClmdIamtgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdClmdReqDto().getCsamt());
				tbl15RfdClmdIamtgetSummEntity.setActive(true);
				tbl15RfdClmdIamtgetSummEntity.setCreatedBy(userName);
				tbl15RfdClmdIamtgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl15GetSummaryEntities
						.add(tbl15RfdClmdIamtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable15ReqDto()
					.getGstr9Table15RfdSancReqDto() != null) {
				Gstr9GetSummaryEntity tbl15RfdSancgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl15RfdSancgetSummEntity.setChkSum(chckSum);
				tbl15RfdSancgetSummEntity.setSection("15");
				tbl15RfdSancgetSummEntity.setSubSection("15B");
				tbl15RfdSancgetSummEntity
						.setSubSectionName("TOTAL_REFUND_SANC");
				tbl15RfdSancgetSummEntity
						.setIamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdSancReqDto().getIamt());
				tbl15RfdSancgetSummEntity
						.setCamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdSancReqDto().getCamt());
				tbl15RfdSancgetSummEntity
						.setSamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdSancReqDto().getSamt());
				tbl15RfdSancgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdSancReqDto().getCsamt());
				tbl15RfdSancgetSummEntity.setActive(true);
				tbl15RfdSancgetSummEntity.setCreatedBy(userName);
				tbl15RfdSancgetSummEntity.setCreatedOn(LocalDateTime.now());

				listOfTbl15GetSummaryEntities.add(tbl15RfdSancgetSummEntity);
			}
			if (gstr9GetReqDto.getTable15ReqDto()
					.getGstr9Table15RfdRejtReqDto() != null) {
				Gstr9GetSummaryEntity tbl15RfdRejgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl15RfdRejgetSummEntity.setChkSum(chckSum);
				tbl15RfdRejgetSummEntity.setSection("15");
				tbl15RfdRejgetSummEntity.setSubSection("15C");
				tbl15RfdRejgetSummEntity.setSubSectionName("TOTAL_REFUND_REJ");
				tbl15RfdRejgetSummEntity
						.setIamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdRejtReqDto().getIamt());
				tbl15RfdRejgetSummEntity
						.setCamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdRejtReqDto().getCamt());
				tbl15RfdRejgetSummEntity
						.setSamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdRejtReqDto().getSamt());
				tbl15RfdRejgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdRejtReqDto().getCsamt());
				tbl15RfdRejgetSummEntity.setActive(true);
				tbl15RfdRejgetSummEntity.setCreatedBy(userName);
				tbl15RfdRejgetSummEntity.setCreatedOn(LocalDateTime.now());

				listOfTbl15GetSummaryEntities.add(tbl15RfdRejgetSummEntity);
			}
			if (gstr9GetReqDto.getTable15ReqDto()
					.getGstr9Table15RfdPendReqDto() != null) {
				Gstr9GetSummaryEntity tbl15RfdPengetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl15RfdPengetSummEntity.setChkSum(chckSum);
				tbl15RfdPengetSummEntity.setSection("15");
				tbl15RfdPengetSummEntity.setSubSection("15D");
				tbl15RfdPengetSummEntity.setSubSectionName("TOTAL_REFUND_PEN");
				tbl15RfdPengetSummEntity
						.setIamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdPendReqDto().getIamt());
				tbl15RfdPengetSummEntity
						.setCamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdPendReqDto().getCamt());
				tbl15RfdPengetSummEntity
						.setSamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdPendReqDto().getSamt());
				tbl15RfdPengetSummEntity
						.setCsamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15RfdPendReqDto().getCsamt());
				tbl15RfdPengetSummEntity.setActive(true);
				tbl15RfdPengetSummEntity.setCreatedBy(userName);
				tbl15RfdPengetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl15GetSummaryEntities.add(tbl15RfdPengetSummEntity);
			}
			if (gstr9GetReqDto.getTable15ReqDto()
					.getGstr9Table15TaxDmndtReqDto() != null) {
				Gstr9GetSummaryEntity tbl15TaxDmndgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl15TaxDmndgetSummEntity.setChkSum(chckSum);
				tbl15TaxDmndgetSummEntity.setSection("15");
				tbl15TaxDmndgetSummEntity.setSubSection("15E");
				tbl15TaxDmndgetSummEntity
						.setSubSectionName("TOTALTAX_DEMANDED");
				tbl15TaxDmndgetSummEntity
						.setIamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TaxDmndtReqDto().getIamt());
				tbl15TaxDmndgetSummEntity
						.setCamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TaxDmndtReqDto().getCamt());
				tbl15TaxDmndgetSummEntity
						.setSamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TaxDmndtReqDto().getSamt());
				tbl15TaxDmndgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TaxDmndtReqDto().getCsamt());
				tbl15TaxDmndgetSummEntity
						.setIntr(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TaxDmndtReqDto().getIntr());
				tbl15TaxDmndgetSummEntity
						.setFee(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TaxDmndtReqDto().getFee());
				tbl15TaxDmndgetSummEntity
						.setPen(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TaxDmndtReqDto().getPen());

				tbl15TaxDmndgetSummEntity.setActive(true);
				tbl15TaxDmndgetSummEntity.setCreatedBy(userName);
				tbl15TaxDmndgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl15GetSummaryEntities.add(tbl15TaxDmndgetSummEntity);
			}
			if (gstr9GetReqDto.getTable15ReqDto()
					.getGstr9Table15TaxPaidReqDto() != null) {
				Gstr9GetSummaryEntity tbl15TaxPadgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl15TaxPadgetSummEntity.setChkSum(chckSum);
				tbl15TaxPadgetSummEntity.setSection("15");
				tbl15TaxPadgetSummEntity.setSubSection("15F");
				tbl15TaxPadgetSummEntity.setSubSectionName("TAX_PAID");
				tbl15TaxPadgetSummEntity
						.setIamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TaxPaidReqDto().getIamt());
				tbl15TaxPadgetSummEntity
						.setCamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TaxPaidReqDto().getCamt());
				tbl15TaxPadgetSummEntity
						.setSamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TaxPaidReqDto().getSamt());
				tbl15TaxPadgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TaxPaidReqDto().getCsamt());
				tbl15TaxPadgetSummEntity
						.setIntr(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TaxPaidReqDto().getIntr());
				tbl15TaxPadgetSummEntity
						.setPen(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TaxPaidReqDto().getPen());
				tbl15TaxPadgetSummEntity
						.setFee(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TaxPaidReqDto().getFee());
				tbl15TaxPadgetSummEntity.setActive(true);
				tbl15TaxPadgetSummEntity.setCreatedBy(userName);
				tbl15TaxPadgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl15GetSummaryEntities.add(tbl15TaxPadgetSummEntity);
			}
			if (gstr9GetReqDto.getTable15ReqDto()
					.getGstr9Table15TotalDmndPendReqDto() != null) {
				Gstr9GetSummaryEntity tbl15TaxDmndgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl15TaxDmndgetSummEntity.setChkSum(chckSum);
				tbl15TaxDmndgetSummEntity.setSection("15");
				tbl15TaxDmndgetSummEntity.setSubSection("15G");
				tbl15TaxDmndgetSummEntity.setSubSectionName("TOTAL_DMND");
				tbl15TaxDmndgetSummEntity.setIamt(gstr9GetReqDto
						.getTable15ReqDto().getGstr9Table15TotalDmndPendReqDto()
						.getIamt());
				tbl15TaxDmndgetSummEntity.setCamt(gstr9GetReqDto
						.getTable15ReqDto().getGstr9Table15TotalDmndPendReqDto()
						.getCamt());
				tbl15TaxDmndgetSummEntity.setSamt(gstr9GetReqDto
						.getTable15ReqDto().getGstr9Table15TotalDmndPendReqDto()
						.getSamt());
				tbl15TaxDmndgetSummEntity.setCsamt(gstr9GetReqDto
						.getTable15ReqDto().getGstr9Table15TotalDmndPendReqDto()
						.getCsamt());
				tbl15TaxDmndgetSummEntity.setIntr(gstr9GetReqDto
						.getTable15ReqDto().getGstr9Table15TotalDmndPendReqDto()
						.getIntr());
				tbl15TaxDmndgetSummEntity
						.setPen(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TotalDmndPendReqDto().getPen());
				tbl15TaxDmndgetSummEntity
						.setFee(gstr9GetReqDto.getTable15ReqDto()
								.getGstr9Table15TotalDmndPendReqDto().getFee());
				tbl15TaxDmndgetSummEntity.setActive(true);
				tbl15TaxDmndgetSummEntity.setCreatedBy(userName);
				tbl15TaxDmndgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl15GetSummaryEntities.add(tbl15TaxDmndgetSummEntity);
			}
		}
		return listOfTbl15GetSummaryEntities;
	}

	public List<Gstr9GetSummaryEntity> populateTbl16Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9GetSummaryEntity> listOfTbl16GetSummaryEntities,
			String userName) {
		if (gstr9GetReqDto.getTable16ReqDto() != null) {
			String chckSum = gstr9GetReqDto.getTable16ReqDto().getChksum();
			if (gstr9GetReqDto.getTable16ReqDto()
					.getGstr9Table16CompSuppReqDto() != null) {
				Gstr9GetSummaryEntity tbl16CompSuppgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl16CompSuppgetSummEntity.setChkSum(chckSum);
				tbl16CompSuppgetSummEntity.setSection("16");
				tbl16CompSuppgetSummEntity.setSubSection("16A");
				tbl16CompSuppgetSummEntity.setSubSectionName("COMP_SUPP");
				tbl16CompSuppgetSummEntity
						.setTxVal(gstr9GetReqDto.getTable16ReqDto()
								.getGstr9Table16CompSuppReqDto().getTxval());
				tbl16CompSuppgetSummEntity.setActive(true);
				tbl16CompSuppgetSummEntity.setCreatedBy(userName);
				tbl16CompSuppgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl16GetSummaryEntities.add(tbl16CompSuppgetSummEntity);
			}
			if (gstr9GetReqDto.getTable16ReqDto()
					.getGstr9Table16DeemedSuppReqDto() != null) {
				Gstr9GetSummaryEntity tbl16CompSuppgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl16CompSuppgetSummEntity.setChkSum(chckSum);
				tbl16CompSuppgetSummEntity.setSection("16");
				tbl16CompSuppgetSummEntity.setSubSection("16B");
				tbl16CompSuppgetSummEntity.setSubSectionName("DEEMED_SUPP");
				tbl16CompSuppgetSummEntity
						.setTxVal(gstr9GetReqDto.getTable16ReqDto()
								.getGstr9Table16DeemedSuppReqDto().getTxval());
				tbl16CompSuppgetSummEntity
						.setIamt(gstr9GetReqDto.getTable16ReqDto()
								.getGstr9Table16DeemedSuppReqDto().getIamt());
				tbl16CompSuppgetSummEntity
						.setCamt(gstr9GetReqDto.getTable16ReqDto()
								.getGstr9Table16DeemedSuppReqDto().getCamt());
				tbl16CompSuppgetSummEntity
						.setSamt(gstr9GetReqDto.getTable16ReqDto()
								.getGstr9Table16DeemedSuppReqDto().getSamt());
				tbl16CompSuppgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable16ReqDto()
								.getGstr9Table16DeemedSuppReqDto().getCsamt());
				tbl16CompSuppgetSummEntity.setActive(true);
				tbl16CompSuppgetSummEntity.setCreatedBy(userName);
				tbl16CompSuppgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl16GetSummaryEntities.add(tbl16CompSuppgetSummEntity);
			}
			if (gstr9GetReqDto.getTable16ReqDto()
					.getGstr9Table16NotReturnedReqDto() != null) {
				Gstr9GetSummaryEntity tbl16NtRtnReqgetSummEntity = new Gstr9GetSummaryEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl16NtRtnReqgetSummEntity.setChkSum(chckSum);
				tbl16NtRtnReqgetSummEntity.setSection("16");
				tbl16NtRtnReqgetSummEntity.setSubSection("16C");
				tbl16NtRtnReqgetSummEntity.setSubSectionName("NOT_RETURNED");
				tbl16NtRtnReqgetSummEntity
						.setTxVal(gstr9GetReqDto.getTable16ReqDto()
								.getGstr9Table16NotReturnedReqDto().getTxval());
				tbl16NtRtnReqgetSummEntity
						.setIamt(gstr9GetReqDto.getTable16ReqDto()
								.getGstr9Table16NotReturnedReqDto().getIamt());
				tbl16NtRtnReqgetSummEntity
						.setCamt(gstr9GetReqDto.getTable16ReqDto()
								.getGstr9Table16NotReturnedReqDto().getCamt());
				tbl16NtRtnReqgetSummEntity
						.setSamt(gstr9GetReqDto.getTable16ReqDto()
								.getGstr9Table16NotReturnedReqDto().getSamt());
				tbl16NtRtnReqgetSummEntity
						.setCsamt(gstr9GetReqDto.getTable16ReqDto()
								.getGstr9Table16NotReturnedReqDto().getCsamt());
				tbl16NtRtnReqgetSummEntity.setActive(true);
				tbl16NtRtnReqgetSummEntity.setCreatedBy(userName);
				tbl16NtRtnReqgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl16GetSummaryEntities.add(tbl16NtRtnReqgetSummEntity);
			}
		}
		return listOfTbl16GetSummaryEntities;
	}

	public List<Gstr9GetSummaryEntity> populateTbl17Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9GetSummaryEntity> listOfTbl17GetSummaryEntities,
			String userName) {
		try {
			if (gstr9GetReqDto.getTable17ReqDto() != null) {
				if (gstr9GetReqDto.getTable17ReqDto()
						.getGstr9Table17ItemsReqDtos() != null
						&& !gstr9GetReqDto.getTable17ReqDto()
								.getGstr9Table17ItemsReqDtos().isEmpty()) {
					for (int i = 0; i < gstr9GetReqDto.getTable17ReqDto()
							.getGstr9Table17ItemsReqDtos().size(); i++) {
						Gstr9GetSummaryEntity tbl17ItemsgetSummEntity = new Gstr9GetSummaryEntity(
								gstin, fy, taxPeriod, derviedTaxPeriod);
						tbl17ItemsgetSummEntity.setSection("17");
						tbl17ItemsgetSummEntity.setSubSectionName("HSN");
						tbl17ItemsgetSummEntity
								.setIamt(gstr9GetReqDto.getTable17ReqDto()
										.getGstr9Table17ItemsReqDtos().get(i)
										.getIamt());
						tbl17ItemsgetSummEntity
								.setSamt(gstr9GetReqDto.getTable17ReqDto()
										.getGstr9Table17ItemsReqDtos().get(i)
										.getSamt());
						tbl17ItemsgetSummEntity
								.setCamt(gstr9GetReqDto.getTable17ReqDto()
										.getGstr9Table17ItemsReqDtos().get(i)
										.getCamt());
						tbl17ItemsgetSummEntity
								.setCsamt(gstr9GetReqDto.getTable17ReqDto()
										.getGstr9Table17ItemsReqDtos().get(i)
										.getCsamt());
						tbl17ItemsgetSummEntity
								.setHsnSc(gstr9GetReqDto.getTable17ReqDto()
										.getGstr9Table17ItemsReqDtos().get(i)
										.getHsnSc());
						tbl17ItemsgetSummEntity.setUqc(gstr9GetReqDto
								.getTable17ReqDto()
								.getGstr9Table17ItemsReqDtos().get(i).getUqc());
						tbl17ItemsgetSummEntity
								.setDesc(gstr9GetReqDto.getTable17ReqDto()
										.getGstr9Table17ItemsReqDtos().get(i)
										.getDesc());
						tbl17ItemsgetSummEntity.setIsConcesstional(
								gstr9GetReqDto.getTable17ReqDto()
										.getGstr9Table17ItemsReqDtos().get(i)
										.getIsconcesstional());
						tbl17ItemsgetSummEntity.setQty(gstr9GetReqDto
								.getTable17ReqDto()
								.getGstr9Table17ItemsReqDtos().get(i).getQty());
						tbl17ItemsgetSummEntity
								.setTxVal(gstr9GetReqDto.getTable17ReqDto()
										.getGstr9Table17ItemsReqDtos().get(i)
										.getTxval());
						tbl17ItemsgetSummEntity.setRt(gstr9GetReqDto
								.getTable17ReqDto()
								.getGstr9Table17ItemsReqDtos().get(i).getRt());
						tbl17ItemsgetSummEntity.setActive(true);
						tbl17ItemsgetSummEntity.setCreatedBy(userName);
						tbl17ItemsgetSummEntity
								.setCreatedOn(LocalDateTime.now());
						listOfTbl17GetSummaryEntities
								.add(tbl17ItemsgetSummEntity);

					}
				}
			}
			return listOfTbl17GetSummaryEntities;
		} catch (Exception e) {
			String msg = String
					.format("Exception occured in Gstr9, while Populate the Data of Table 17 the "
							+ "for Gstin %s and Fy %s", gstin, fy);
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		}
	}

	public List<Gstr9GetSummaryEntity> populateTbl18Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9GetSummaryEntity> listOfTbl18GetSummaryEntities,
			String userName) {
		try {
			if (gstr9GetReqDto.getTable18ReqDto() != null) {
				if (gstr9GetReqDto.getTable18ReqDto()
						.getGstr9Table18ItemsReqDtos() != null
						&& !gstr9GetReqDto.getTable18ReqDto()
								.getGstr9Table18ItemsReqDtos().isEmpty()) {
					for (int i = 0; i < gstr9GetReqDto.getTable18ReqDto()
							.getGstr9Table18ItemsReqDtos().size(); i++) {
						Gstr9GetSummaryEntity tbl18ItemsgetSummEntity = new Gstr9GetSummaryEntity(
								gstin, fy, taxPeriod, derviedTaxPeriod);
						tbl18ItemsgetSummEntity.setSection("18");
						tbl18ItemsgetSummEntity.setSubSectionName("HSN_INWARD");
						tbl18ItemsgetSummEntity
								.setIamt(gstr9GetReqDto.getTable18ReqDto()
										.getGstr9Table18ItemsReqDtos().get(i)
										.getIamt());
						tbl18ItemsgetSummEntity
								.setSamt(gstr9GetReqDto.getTable18ReqDto()
										.getGstr9Table18ItemsReqDtos().get(i)
										.getSamt());
						tbl18ItemsgetSummEntity
								.setCamt(gstr9GetReqDto.getTable18ReqDto()
										.getGstr9Table18ItemsReqDtos().get(i)
										.getCamt());
						tbl18ItemsgetSummEntity
								.setCsamt(gstr9GetReqDto.getTable18ReqDto()
										.getGstr9Table18ItemsReqDtos().get(i)
										.getCsamt());
						tbl18ItemsgetSummEntity
								.setHsnSc(gstr9GetReqDto.getTable18ReqDto()
										.getGstr9Table18ItemsReqDtos().get(i)
										.getHsnSc());
						tbl18ItemsgetSummEntity.setUqc(gstr9GetReqDto
								.getTable18ReqDto()
								.getGstr9Table18ItemsReqDtos().get(i).getUqc());
						tbl18ItemsgetSummEntity
								.setDesc(gstr9GetReqDto.getTable18ReqDto()
										.getGstr9Table18ItemsReqDtos().get(i)
										.getDesc());
						tbl18ItemsgetSummEntity.setIsConcesstional(
								gstr9GetReqDto.getTable18ReqDto()
										.getGstr9Table18ItemsReqDtos().get(i)
										.getIsconcesstional());
						tbl18ItemsgetSummEntity.setQty(gstr9GetReqDto
								.getTable18ReqDto()
								.getGstr9Table18ItemsReqDtos().get(i).getQty());
						tbl18ItemsgetSummEntity
								.setTxVal(gstr9GetReqDto.getTable18ReqDto()
										.getGstr9Table18ItemsReqDtos().get(i)
										.getTxval());
						tbl18ItemsgetSummEntity.setRt(gstr9GetReqDto
								.getTable18ReqDto()
								.getGstr9Table18ItemsReqDtos().get(i).getRt());
						tbl18ItemsgetSummEntity.setActive(true);
						tbl18ItemsgetSummEntity.setCreatedBy(userName);
						tbl18ItemsgetSummEntity
								.setCreatedOn(LocalDateTime.now());
						listOfTbl18GetSummaryEntities
								.add(tbl18ItemsgetSummEntity);

					}
				}
			}
			return listOfTbl18GetSummaryEntities;
		} catch (Exception e) {
			String msg = String
					.format("Exception occured in Gstr9, while Populate the Data of Table 18 the "
							+ "for Gstin %s and Fy %s", gstin, fy);
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	// public List<Gstr9GetSummaryEntity> populateTaxPayData(
	// GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
	// String taxPeriod, Integer derviedTaxPeriod,
	// List<Gstr9GetSummaryEntity> listOfTaxPayGetSummaryEntities,
	// String userName) {
	// if (gstr9GetReqDto.getTaxpayReqDto() != null
	// && !gstr9GetReqDto.getTaxpayReqDto().isEmpty()) {
	// for (int i = 0; i < gstr9GetReqDto.getTaxpayReqDto().size(); i++) {
	// Gstr9GetSummaryEntity taxPaygetSummEntity = new Gstr9GetSummaryEntity(
	// gstin, fy, taxPeriod, derviedTaxPeriod);
	// taxPaygetSummEntity.setLiabId(
	// gstr9GetReqDto.getTaxpayReqDto().get(i).getLiabId());
	// taxPaygetSummEntity.setTransCode(
	// gstr9GetReqDto.getTaxpayReqDto().get(i).getTrancd());
	// //
	// taxPaygetSummEntity.setTransDate(EYDateUtil.gstr9GetReqDto.getTaxpayReqDto().get(i).getTrandate());
	// taxPaygetSummEntity.setSection("TAX_PAY");
	// if (gstr9GetReqDto.getTaxpayReqDto().get(i)
	// .getGstr9TaxPaySgstReqDto() != null) {
	// taxPaygetSummEntity.setSubSectionName("SGST");
	// taxPaygetSummEntity.setIntr(gstr9GetReqDto.getTaxpayReqDto()
	// .get(i).getGstr9TaxPaySgstReqDto().getIntr());
	// taxPaygetSummEntity.setPen(gstr9GetReqDto.getTaxpayReqDto()
	// .get(i).getGstr9TaxPaySgstReqDto().getPen());
	// taxPaygetSummEntity.setFee(gstr9GetReqDto.getTaxpayReqDto()
	// .get(i).getGstr9TaxPaySgstReqDto().getFee());
	// taxPaygetSummEntity.setOth(gstr9GetReqDto.getTaxpayReqDto()
	// .get(i).getGstr9TaxPaySgstReqDto().getOth());
	// taxPaygetSummEntity.setTot(gstr9GetReqDto.getTaxpayReqDto()
	// .get(i).getGstr9TaxPaySgstReqDto().getTot());
	// taxPaygetSummEntity
	// .setTxVal(gstr9GetReqDto.getTaxpayReqDto().get(i)
	// .getGstr9TaxPaySgstReqDto().getTx());
	// taxPaygetSummEntity.setActive(true);
	// taxPaygetSummEntity.setCreatedBy(userName);
	// taxPaygetSummEntity.setCreatedOn(LocalDateTime.now());
	// listOfTaxPayGetSummaryEntities.add(taxPaygetSummEntity);
	// }
	//
	// if (gstr9GetReqDto.getTaxpayReqDto().get(i)
	// .getGstr9TaxPayCgstReqDto() != null) {
	// taxPaygetSummEntity.setSubSectionName("CGST");
	// taxPaygetSummEntity.setIntr(gstr9GetReqDto.getTaxpayReqDto()
	// .get(i).getGstr9TaxPayCgstReqDto().getIntr());
	// taxPaygetSummEntity.setPen(gstr9GetReqDto.getTaxpayReqDto()
	// .get(i).getGstr9TaxPayCgstReqDto().getPen());
	// taxPaygetSummEntity.setFee(gstr9GetReqDto.getTaxpayReqDto()
	// .get(i).getGstr9TaxPayCgstReqDto().getFee());
	// taxPaygetSummEntity.setOth(gstr9GetReqDto.getTaxpayReqDto()
	// .get(i).getGstr9TaxPayCgstReqDto().getOth());
	// taxPaygetSummEntity.setTot(gstr9GetReqDto.getTaxpayReqDto()
	// .get(i).getGstr9TaxPayCgstReqDto().getTot());
	// taxPaygetSummEntity
	// .setTxVal(gstr9GetReqDto.getTaxpayReqDto().get(i)
	// .getGstr9TaxPayCgstReqDto().getTx());
	// taxPaygetSummEntity.setActive(true);
	// taxPaygetSummEntity.setCreatedBy(userName);
	// taxPaygetSummEntity.setCreatedOn(LocalDateTime.now());
	// listOfTaxPayGetSummaryEntities.add(taxPaygetSummEntity);
	// }
	// }
	// }
	// return listOfTaxPayGetSummaryEntities;
	// }

	// public List<Gstr9GetSummaryEntity> populateTaxPaidData(
	// GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
	// String taxPeriod, Integer derviedTaxPeriod,
	// List<Gstr9GetSummaryEntity> listOfTaxPaidSummaryEntities,
	// String userName) {
	// if (gstr9GetReqDto.getTaxPaidReqDto() != null) {
	//
	// if (gstr9GetReqDto.getTaxPaidReqDto().getPdByCash() != null
	// && !gstr9GetReqDto.getTaxPaidReqDto().getPdByCash()
	// .isEmpty()) {
	// for (int i = 0; i < gstr9GetReqDto.getTaxPaidReqDto()
	// .getPdByCash().size(); i++) {
	// Gstr9GetSummaryEntity taxPaygetSummEntity = new Gstr9GetSummaryEntity(
	// gstin, fy, taxPeriod, derviedTaxPeriod);
	// taxPaygetSummEntity
	// .setLiabId(gstr9GetReqDto.getTaxPaidReqDto()
	// .getPdByCash().get(i).getLiabId());
	// taxPaygetSummEntity
	// .setTransCode(gstr9GetReqDto.getTaxPaidReqDto()
	// .getPdByCash().get(i).getTrancd());
	// taxPaygetSummEntity
	// .setDebitId(gstr9GetReqDto.getTaxPaidReqDto()
	// .getPdByCash().get(i).getDebitId());
	// //
	// taxPaygetSummEntity.setTransDate(EYDateUtil.gstr9GetReqDto.getTaxpayReqDto().get(i).getTrandate());
	// taxPaygetSummEntity.setSection("TAX_PAID");
	// if (gstr9GetReqDto.getTaxPaidReqDto().getPdByCash().get(i)
	// .getGstr9TaxPaidPdByCashSgstReqDto() != null) {
	// taxPaygetSummEntity.setSubSectionName("SGST");
	// taxPaygetSummEntity.setIntr(gstr9GetReqDto
	// .getTaxPaidReqDto().getPdByCash().get(i)
	// .getGstr9TaxPaidPdByCashSgstReqDto().getIntr());
	// taxPaygetSummEntity.setPen(gstr9GetReqDto
	// .getTaxPaidReqDto().getPdByCash().get(i)
	// .getGstr9TaxPaidPdByCashSgstReqDto().getPen());
	// taxPaygetSummEntity.setFee(gstr9GetReqDto
	// .getTaxPaidReqDto().getPdByCash().get(i)
	// .getGstr9TaxPaidPdByCashSgstReqDto().getFee());
	// taxPaygetSummEntity.setOth(gstr9GetReqDto
	// .getTaxPaidReqDto().getPdByCash().get(i)
	// .getGstr9TaxPaidPdByCashSgstReqDto().getOth());
	// taxPaygetSummEntity.setTot(gstr9GetReqDto
	// .getTaxPaidReqDto().getPdByCash().get(i)
	// .getGstr9TaxPaidPdByCashSgstReqDto().getTot());
	// taxPaygetSummEntity.setTxVal(gstr9GetReqDto
	// .getTaxPaidReqDto().getPdByCash().get(i)
	// .getGstr9TaxPaidPdByCashSgstReqDto().getTx());
	// taxPaygetSummEntity.setActive(true);
	// taxPaygetSummEntity.setCreatedBy(userName);
	// taxPaygetSummEntity.setCreatedOn(LocalDateTime.now());
	// listOfTaxPaidSummaryEntities.add(taxPaygetSummEntity);
	// }
	//
	// if (gstr9GetReqDto.getTaxPaidReqDto().getPdByCash().get(i)
	// .getGstr9TaxPaidPdByCashSgstReqDto() != null) {
	// taxPaygetSummEntity.setSubSectionName("CGST");
	// taxPaygetSummEntity.setIntr(gstr9GetReqDto
	// .getTaxPaidReqDto().getPdByCash().get(i)
	// .getGstr9TaxPaidPdByCashCgstReqDto().getIntr());
	// taxPaygetSummEntity.setPen(gstr9GetReqDto
	// .getTaxPaidReqDto().getPdByCash().get(i)
	// .getGstr9TaxPaidPdByCashCgstReqDto().getPen());
	// taxPaygetSummEntity.setFee(gstr9GetReqDto
	// .getTaxPaidReqDto().getPdByCash().get(i)
	// .getGstr9TaxPaidPdByCashCgstReqDto().getFee());
	// taxPaygetSummEntity.setOth(gstr9GetReqDto
	// .getTaxPaidReqDto().getPdByCash().get(i)
	// .getGstr9TaxPaidPdByCashCgstReqDto().getOth());
	// taxPaygetSummEntity.setTot(gstr9GetReqDto
	// .getTaxPaidReqDto().getPdByCash().get(i)
	// .getGstr9TaxPaidPdByCashCgstReqDto().getTot());
	// taxPaygetSummEntity.setTxVal(gstr9GetReqDto
	// .getTaxPaidReqDto().getPdByCash().get(i)
	// .getGstr9TaxPaidPdByCashCgstReqDto().getTx());
	// taxPaygetSummEntity.setActive(true);
	// taxPaygetSummEntity.setCreatedBy(userName);
	// taxPaygetSummEntity.setCreatedOn(LocalDateTime.now());
	// listOfTaxPaidSummaryEntities.add(taxPaygetSummEntity);
	// }
	// }
	// }
	// }
	// return listOfTaxPaidSummaryEntities;
	// }

}
