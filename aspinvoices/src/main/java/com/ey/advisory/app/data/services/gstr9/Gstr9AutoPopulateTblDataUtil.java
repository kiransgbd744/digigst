package com.ey.advisory.app.data.services.gstr9;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr9.Gstr9AutoCalculateEntity;
import com.ey.advisory.app.docs.dto.gstr9.GetDetailsForGstr9ReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component
public class Gstr9AutoPopulateTblDataUtil {

	public List<Gstr9AutoCalculateEntity> populateAutoTbl4Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9AutoCalculateEntity> listOfTbl4AutoCalcEntities,
			String userName) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("INSIDE Outward populateAutCalcTbl4Data Method");
		}
		if (gstr9GetReqDto.getTable4ReqDto() != null) {
			if (gstr9GetReqDto.getTable4ReqDto().getTable4B2CReqDto() != null) {
				Gstr9AutoCalculateEntity tbl4b2cgetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl4AutoCalcEntities.add(tbl4b2cgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto().getTable4B2BReqDto() != null) {
				Gstr9AutoCalculateEntity tbl4b2bgetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl4AutoCalcEntities.add(tbl4b2bgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto().getTable4ExpReqDto() != null) {

				Gstr9AutoCalculateEntity tbl4expgetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl4AutoCalcEntities.add(tbl4expgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto().getTable4SezReqDto() != null) {
				Gstr9AutoCalculateEntity tbl4sezgetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl4AutoCalcEntities.add(tbl4sezgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4DeemedReqDto() != null) {
				Gstr9AutoCalculateEntity tbl4deemedgetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl4AutoCalcEntities.add(tbl4deemedgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto().getTable4AtReqDto() != null) {
				Gstr9AutoCalculateEntity tbl4atgetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl4AutoCalcEntities.add(tbl4atgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4RchrgReqDto() != null) {

				Gstr9AutoCalculateEntity tbl4rchrggetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl4AutoCalcEntities.add(tbl4rchrggetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4CrntReqDto() != null) {
				Gstr9AutoCalculateEntity tbl4creditNotegetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl4AutoCalcEntities.add(tbl4creditNotegetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4drntReqDto() != null) {
				Gstr9AutoCalculateEntity tbl4debitNotegetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl4AutoCalcEntities.add(tbl4debitNotegetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4AmdPosReqDto() != null) {
				Gstr9AutoCalculateEntity tbl4AmdPosgetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl4AutoCalcEntities.add(tbl4AmdPosgetSummEntity);
			}

			if (gstr9GetReqDto.getTable4ReqDto()
					.getTable4AmdNegReqDto() != null) {
				Gstr9AutoCalculateEntity tbl4AmdNeggetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl4AutoCalcEntities.add(tbl4AmdNeggetSummEntity);
			}

		}
		return listOfTbl4AutoCalcEntities;
	}

	public List<Gstr9AutoCalculateEntity> populateAutoTbl5Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9AutoCalculateEntity> listOfTbl5AutoCalcEntities,
			String userName) {
		if (gstr9GetReqDto.getTable5ReqDto() != null) {

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5ZeroRtdReqDto() != null) {
				Gstr9AutoCalculateEntity tbl5ZerortdSupgetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl5AutoCalcEntities.add(tbl5ZerortdSupgetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto().getTable5SezReqDto() != null) {
				Gstr9AutoCalculateEntity tbl5SezgetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5SezgetSummEntity.setSection("5");
				tbl5SezgetSummEntity.setSubSection("5B");
				tbl5SezgetSummEntity.setSubSectionName("SEZ");
				tbl5SezgetSummEntity.setTxVal(gstr9GetReqDto.getTable5ReqDto()
						.getTable5SezReqDto().getTxval());
				tbl5SezgetSummEntity.setActive(true);
				tbl5SezgetSummEntity.setCreatedBy(userName);
				tbl5SezgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5AutoCalcEntities.add(tbl5SezgetSummEntity);
			}
			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5RchrgReqDto() != null) {
				Gstr9AutoCalculateEntity tbl5RchrggetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5RchrggetSummEntity.setSection("5");
				tbl5RchrggetSummEntity.setSubSection("5C");
				tbl5RchrggetSummEntity.setSubSectionName("RCHRG");
				tbl5RchrggetSummEntity.setTxVal(gstr9GetReqDto.getTable5ReqDto()
						.getTable5RchrgReqDto().getTxval());
				tbl5RchrggetSummEntity.setActive(true);
				tbl5RchrggetSummEntity.setCreatedBy(userName);
				tbl5RchrggetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5AutoCalcEntities.add(tbl5RchrggetSummEntity);
			}
			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5ExmtReqDto() != null) {
				Gstr9AutoCalculateEntity tbl5exempgetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5exempgetSummEntity.setSection("5");
				tbl5exempgetSummEntity.setSubSection("5D");
				tbl5exempgetSummEntity.setSubSectionName("Exempted");
				tbl5exempgetSummEntity.setTxVal(gstr9GetReqDto.getTable5ReqDto()
						.getTable5ExmtReqDto().getTxval());
				tbl5exempgetSummEntity.setActive(true);
				tbl5exempgetSummEntity.setCreatedBy(userName);
				tbl5exempgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5AutoCalcEntities.add(tbl5exempgetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto().getTable5NilReqDto() != null) {
				Gstr9AutoCalculateEntity tbl5NilRtdgetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5NilRtdgetSummEntity.setSection("5");
				tbl5NilRtdgetSummEntity.setSubSection("5E");
				tbl5NilRtdgetSummEntity.setSubSectionName("Nil Rated");
				tbl5NilRtdgetSummEntity.setTxVal(gstr9GetReqDto
						.getTable5ReqDto().getTable5NilReqDto().getTxval());
				tbl5NilRtdgetSummEntity.setActive(true);
				tbl5NilRtdgetSummEntity.setCreatedBy(userName);
				tbl5NilRtdgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5AutoCalcEntities.add(tbl5NilRtdgetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5NonGstReqDto() != null) {
				Gstr9AutoCalculateEntity tbl5NonGstgetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5NonGstgetSummEntity.setSection("5");
				tbl5NonGstgetSummEntity.setSubSection("5F");
				tbl5NonGstgetSummEntity.setSubSectionName("Non-GST");
				tbl5NonGstgetSummEntity.setTxVal(gstr9GetReqDto
						.getTable5ReqDto().getTable5NonGstReqDto().getTxval());
				tbl5NonGstgetSummEntity.setActive(true);
				tbl5NonGstgetSummEntity.setCreatedBy(userName);
				tbl5NonGstgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5AutoCalcEntities.add(tbl5NonGstgetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5CrNtReqDto() != null) {
				Gstr9AutoCalculateEntity tbl5creditNotegetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5creditNotegetSummEntity.setSection("5");
				tbl5creditNotegetSummEntity.setSubSection("5H");
				tbl5creditNotegetSummEntity.setSubSectionName("CR_NT");
				tbl5creditNotegetSummEntity.setTxVal(gstr9GetReqDto
						.getTable5ReqDto().getTable5CrNtReqDto().getTxval());
				tbl5creditNotegetSummEntity.setActive(true);
				tbl5creditNotegetSummEntity.setCreatedBy(userName);
				tbl5creditNotegetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5AutoCalcEntities.add(tbl5creditNotegetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5DbNtReqDto() != null) {
				Gstr9AutoCalculateEntity tbl5debitNotegetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5debitNotegetSummEntity.setSection("5");
				tbl5debitNotegetSummEntity.setSubSection("5I");
				tbl5debitNotegetSummEntity.setSubSectionName("DR_NT");
				tbl5debitNotegetSummEntity.setTxVal(gstr9GetReqDto
						.getTable5ReqDto().getTable5DbNtReqDto().getTxval());
				tbl5debitNotegetSummEntity.setActive(true);
				tbl5debitNotegetSummEntity.setCreatedBy(userName);
				tbl5debitNotegetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5AutoCalcEntities.add(tbl5debitNotegetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5AmdPosReqDto() != null) {
				Gstr9AutoCalculateEntity tbl5AmdPosgetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5AmdPosgetSummEntity.setSection("5");
				tbl5AmdPosgetSummEntity.setSubSection("5J");
				tbl5AmdPosgetSummEntity.setSubSectionName("AMD_POS");
				tbl5AmdPosgetSummEntity.setTxVal(gstr9GetReqDto
						.getTable5ReqDto().getTable5AmdPosReqDto().getTxval());
				tbl5AmdPosgetSummEntity.setActive(true);
				tbl5AmdPosgetSummEntity.setCreatedBy(userName);
				tbl5AmdPosgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5AutoCalcEntities.add(tbl5AmdPosgetSummEntity);
			}

			if (gstr9GetReqDto.getTable5ReqDto()
					.getTable5AmdNegReqDto() != null) {
				Gstr9AutoCalculateEntity tbl5AmdNeggetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl5AmdNeggetSummEntity.setSection("5");
				tbl5AmdNeggetSummEntity.setSubSection("5K");
				tbl5AmdNeggetSummEntity.setSubSectionName("AMD_NEG");
				tbl5AmdNeggetSummEntity.setTxVal(gstr9GetReqDto
						.getTable5ReqDto().getTable5AmdNegReqDto().getTxval());
				tbl5AmdNeggetSummEntity.setActive(true);
				tbl5AmdNeggetSummEntity.setCreatedBy(userName);
				tbl5AmdNeggetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl5AutoCalcEntities.add(tbl5AmdNeggetSummEntity);
			}
		}
		return listOfTbl5AutoCalcEntities;
	}

	public List<Gstr9AutoCalculateEntity> populateAutoTbl6Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9AutoCalculateEntity> listOfTbl6AutoCalcEntities,
			String userName) {
		if (gstr9GetReqDto.getTable6ReqDto() != null) {
			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6Itc3bReqDto() != null) {
				Gstr9AutoCalculateEntity tbl6Itc3bgetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl6AutoCalcEntities.add(tbl6Itc3bgetSummEntity);
			}

			if (gstr9GetReqDto.getTable6ReqDto().getTable6IsdReqDto() != null) {
				Gstr9AutoCalculateEntity tbl6IsdgetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl6AutoCalcEntities.add(tbl6IsdgetSummEntity);
			}
			
			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6Trans1ReqDto() != null) {
				Gstr9AutoCalculateEntity tbl6Trans1getSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl6AutoCalcEntities.add(tbl6Trans1getSummEntity);
			}
			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6Trans2ReqDto() != null) {
				Gstr9AutoCalculateEntity tbl6Trans2getSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl6AutoCalcEntities.add(tbl6Trans2getSummEntity);
			}
			if (gstr9GetReqDto.getTable6ReqDto()
					.getTable6OtherReqDto() != null) {
				Gstr9AutoCalculateEntity tbl6OthergetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl6AutoCalcEntities.add(tbl6OthergetSummEntity);
			}
		}
		return listOfTbl6AutoCalcEntities;
	}

	public List<Gstr9AutoCalculateEntity> populateAutoTbl8Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9AutoCalculateEntity> listOfTbl8AutoCalcEntities,
			String userName) {
		if (gstr9GetReqDto.getTable8ReqDto() != null) {
			String chckSum = gstr9GetReqDto.getTable8ReqDto().getChksum();
			if (gstr9GetReqDto.getTable8ReqDto()
					.getGstr9Table8Itc2AReqDto() != null) {
				Gstr9AutoCalculateEntity tbl8Itc2agetSummEntity = new Gstr9AutoCalculateEntity(
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
				listOfTbl8AutoCalcEntities.add(tbl8Itc2agetSummEntity);
			}
			
		}
		return listOfTbl8AutoCalcEntities;
	}

	public List<Gstr9AutoCalculateEntity> populateAutoTbl9Data(
			GetDetailsForGstr9ReqDto gstr9GetReqDto, String gstin, String fy,
			String taxPeriod, Integer derviedTaxPeriod,
			List<Gstr9AutoCalculateEntity> listOfTbl9AutoCalcEntities,
			String userName) {
		if (gstr9GetReqDto.getTable9ReqDto() != null) {
			String chckSum = gstr9GetReqDto.getTable9ReqDto().getChksum();
			if (gstr9GetReqDto.getTable9ReqDto()
					.getGstr9Table9IamtReqDto() != null) {
				Gstr9AutoCalculateEntity tbl9IamtgetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9IamtgetSummEntity.setChkSum(chckSum);
				tbl9IamtgetSummEntity.setSection("9");
				tbl9IamtgetSummEntity.setSubSection("9A");
				tbl9IamtgetSummEntity.setSubSectionName("INTEGRATED_Tax");
				tbl9IamtgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9IamtReqDto().getTxpyble());
				tbl9IamtgetSummEntity
						.setTxPaidCash(gstr9GetReqDto.getTable9ReqDto()
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
				listOfTbl9AutoCalcEntities.add(tbl9IamtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable9ReqDto()
					.getGstr9Table9CamtReqDto() != null) {
				Gstr9AutoCalculateEntity tbl9CamtgetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9CamtgetSummEntity.setChkSum(chckSum);
				tbl9CamtgetSummEntity.setSection("9");
				tbl9CamtgetSummEntity.setSubSection("9B");
				tbl9CamtgetSummEntity.setSubSectionName("CENTRAL_TAX");
				tbl9CamtgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9CamtReqDto().getTxpyble());
				tbl9CamtgetSummEntity
						.setTxPaidCash(gstr9GetReqDto.getTable9ReqDto()
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
				listOfTbl9AutoCalcEntities.add(tbl9CamtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable9ReqDto()
					.getGstr9Table9SamtReqDto() != null) {
				Gstr9AutoCalculateEntity tbl9SamtgetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9SamtgetSummEntity.setChkSum(chckSum);
				tbl9SamtgetSummEntity.setSection("9");
				tbl9SamtgetSummEntity.setSubSection("9C");
				tbl9SamtgetSummEntity.setSubSectionName("STATE_TAX");
				tbl9SamtgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9SamtReqDto().getTxpyble());
				tbl9SamtgetSummEntity
						.setTxPaidCash(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9SamtReqDto().getTxpaidCash());
				tbl9SamtgetSummEntity.setTaxPaidItcIamt(gstr9GetReqDto
						.getTable9ReqDto().getGstr9Table9SamtReqDto()
						.getTaxPaidItcIamt());
				tbl9SamtgetSummEntity.setTaxPaidItcSamt(gstr9GetReqDto
						.getTable9ReqDto().getGstr9Table9SamtReqDto()
						.getTaxPaidItcSamt());
				tbl9SamtgetSummEntity.setActive(true);
				tbl9SamtgetSummEntity.setCreatedBy(userName);
				tbl9SamtgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl9AutoCalcEntities.add(tbl9SamtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable9ReqDto()
					.getGstr9Table9CsamtReqDto() != null) {
				Gstr9AutoCalculateEntity tbl9CessAmtgetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9CessAmtgetSummEntity.setChkSum(chckSum);
				tbl9CessAmtgetSummEntity.setSection("9");
				tbl9CessAmtgetSummEntity.setSubSection("9D");
				tbl9CessAmtgetSummEntity.setSubSectionName("CESS");
				tbl9CessAmtgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9CsamtReqDto().getTxpyble());
				tbl9CessAmtgetSummEntity
						.setTxPaidCash(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9CsamtReqDto().getTxpaidCash());
				tbl9CessAmtgetSummEntity.setTaxPaidItcCSamt(gstr9GetReqDto
						.getTable9ReqDto().getGstr9Table9CsamtReqDto()
						.getTaxPaidItcCsamt());
				tbl9CessAmtgetSummEntity.setActive(true);
				tbl9CessAmtgetSummEntity.setCreatedBy(userName);
				tbl9CessAmtgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl9AutoCalcEntities.add(tbl9CessAmtgetSummEntity);
			}
			if (gstr9GetReqDto.getTable9ReqDto()
					.getGstr9Table9IntrReqDto() != null) {
				Gstr9AutoCalculateEntity tbl9IntrgetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9IntrgetSummEntity.setChkSum(chckSum);
				tbl9IntrgetSummEntity.setSection("9");
				tbl9IntrgetSummEntity.setSubSection("9E");
				tbl9IntrgetSummEntity.setSubSectionName("INTEREST");
				tbl9IntrgetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9IntrReqDto().getTxpyble());
				tbl9IntrgetSummEntity
						.setTxPaidCash(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9IntrReqDto().getTxpaidCash());
				tbl9IntrgetSummEntity.setActive(true);
				tbl9IntrgetSummEntity.setCreatedBy(userName);
				tbl9IntrgetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl9AutoCalcEntities.add(tbl9IntrgetSummEntity);
			}
			if (gstr9GetReqDto.getTable9ReqDto()
					.getGstr9Table9TeeReqDto() != null) {
				Gstr9AutoCalculateEntity tbl9LateFeegetSummEntity = new Gstr9AutoCalculateEntity(
						gstin, fy, taxPeriod, derviedTaxPeriod);
				tbl9LateFeegetSummEntity.setChkSum(chckSum);
				tbl9LateFeegetSummEntity.setSection("9");
				tbl9LateFeegetSummEntity.setSubSection("9F");
				tbl9LateFeegetSummEntity.setSubSectionName("LATE_FEE");
				tbl9LateFeegetSummEntity
						.setTxPyble(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9TeeReqDto().getTxpyble());
				tbl9LateFeegetSummEntity
						.setTxPaidCash(gstr9GetReqDto.getTable9ReqDto()
								.getGstr9Table9TeeReqDto().getTxpaidCash());
				tbl9LateFeegetSummEntity.setActive(true);
				tbl9LateFeegetSummEntity.setCreatedBy(userName);
				tbl9LateFeegetSummEntity.setCreatedOn(LocalDateTime.now());
				listOfTbl9AutoCalcEntities.add(tbl9LateFeegetSummEntity);
			}
		}
		return listOfTbl9AutoCalcEntities;
	}
}
