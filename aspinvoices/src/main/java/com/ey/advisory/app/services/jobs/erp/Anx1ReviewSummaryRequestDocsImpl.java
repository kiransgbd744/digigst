/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.simplified.Anx1EcomSummarySectionDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.Anx1InwardDetailSummarySectionDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.Anx1OutwardDetailSummarySectionDaoImpl;
import com.ey.advisory.app.docs.dto.erp.Anx1ReviewSummaryRequestDto;
import com.ey.advisory.app.docs.dto.erp.Anx1ReviewSummaryRequestItemDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionEcomDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx1ReviewSummaryRequestDocsImpl")
public class Anx1ReviewSummaryRequestDocsImpl
		implements Anx1ReviewSummaryRequestDocs {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ReviewSummaryRequestDocsImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private List<Anx1ReviewSummaryRequestItemDto> childArry = null;

	private static final Integer MAX = 20;

	@Autowired
	@Qualifier("Anx1OutwardDetailSummarySectionDaoImpl")
	private Anx1OutwardDetailSummarySectionDaoImpl outwardSectionDaoImpl;

	@Autowired
	@Qualifier("Anx1InwardDetailSummarySectionDaoImpl")
	private Anx1InwardDetailSummarySectionDaoImpl inwardSectionDaoImpl;

	@Autowired
	@Qualifier("Anx1EcomSummarySectionDaoImpl")
	private Anx1EcomSummarySectionDaoImpl ecomSectionDaoImpl;

	public Anx1ReviewSummaryRequestDto convertDocsAsDtos(String gstin,
			String entityName, String entityPan, String companyCode,
			String returnPeriod) {

		Anx1ReviewSummaryRequestDto sumReqDto = new Anx1ReviewSummaryRequestDto();
		childArry = new ArrayList<>();
		// Outward
		conversionOutwardDocsToResp(gstin, entityName, entityPan, companyCode,
				returnPeriod);

		// Inward
		convertInwardDocsAsDtos(gstin, entityName, entityPan, companyCode,
				returnPeriod);

		// Table 4
		convertCustomSuppDocsAsDtos(gstin, entityName, entityPan, companyCode,
				returnPeriod);

		sumReqDto.setImItem(childArry);

		return sumReqDto;

	}

	private void conversionOutwardDocsToResp(String gstin, String entityName,
			String entityPan, String companyCode, String returnPeriod) {

		Annexure1SummaryReqDto requestDto = new Annexure1SummaryReqDto();
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstin);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		if (returnPeriod != null) {
			requestDto.setTaxPeriod(returnPeriod);
		}
		List<Annexure1SummarySectionDto> sectionDtos = outwardSectionDaoImpl
				.loadBasicSummarySection(requestDto);

		sectionDtos.forEach(sectionDto -> {
			Anx1ReviewSummaryRequestItemDto child = new Anx1ReviewSummaryRequestItemDto();
			childArry.add(child);
			if (entityName != null) {
				child.setEntity(entityName.toUpperCase());
			}
			child.setEntityPan(entityPan);
			child.setCompanyCode(companyCode);
			child.setDataType("OUTWARD");
			child.setRetPer(sectionDto.getReturnPeriod());
			child.setGstinNum(gstin);
			child.setTaxDoctype(sectionDto.getDocType());
			child.setTaxTable(sectionDto.getTableSection());

			child.setEyTotDoc(sectionDto.getRecords() != null
					? new BigInteger(String.valueOf(sectionDto.getRecords()))
					: BigInteger.ZERO);
			child.setEyInval(sectionDto.getInvValue());

			child.setEyIgstval(sectionDto.getIgst());
			child.setEyCgstval(sectionDto.getCgst());
			child.setEySgstval(sectionDto.getSgst());
			child.setEyTbval(sectionDto.getTaxableValue());
			child.setEyTxval(sectionDto.getTaxPayble());
			child.setEyCessval(sectionDto.getCess());

			if (child.getTaxTable().equalsIgnoreCase("B2C")) {
				child.setMemoTotDoc(child.getEyTotDoc());
				child.setMemoInval(child.getEyInval());
				child.setMemoTbval(child.getEyTbval());
				child.setMemoTxval(child.getEyTxval());
				child.setMemoIgstval(child.getEyIgstval());
				child.setMemoCgstval(child.getEyCgstval());
				child.setMemoSgstval(child.getEySgstval());
				child.setMemoCessval(child.getEyCessval());
			} else {
				child.setMemoTotDoc(child.getEyTotDoc());
				child.setMemoInval(child.getEyInval());
				child.setMemoTbval(child.getEyTbval());

				child.setMemoIgstval(sectionDto.getMemoIgst());
				child.setMemoCgstval(sectionDto.getMemoCgst());
				child.setMemoSgstval(sectionDto.getSgst());
				child.setMemoTxval(sectionDto.getMemoTaxPayable());
				child.setMemoCessval(sectionDto.getMemoCess());

			}

			child.setGstnTotDoc(sectionDto.getGstnCount() != null
					? new BigInteger(String.valueOf(sectionDto.getGstnCount()))
					: BigInteger.ZERO);
			child.setGstnInval(sectionDto.getGstnInvoiceValue());
			child.setGstnIgstval(sectionDto.getGstnIgst());
			child.setGstnCgstval(sectionDto.getGstnCgst());
			child.setGstnSgstval(sectionDto.getGstnSgst());
			child.setGstnTbval(sectionDto.getGstnTaxableValue());
			child.setGstnTxval(sectionDto.getTaxPayble());
			child.setGstnCessval(sectionDto.getGstnCess());

			child.setDiffInval(
					child.getMemoInval().subtract(child.getGstnInval()));

			child.setDiffInval(
					child.getMemoInval().subtract(child.getGstnInval()));
			child.setDiffTbval(
					child.getMemoTbval().subtract(child.getGstnTbval()));
			child.setDiffTxval(
					child.getMemoTxval().subtract(child.getGstnTxval()));

			child.setDiffIgstval(
					child.getMemoIgstval().subtract(child.getGstnIgstval()));
			child.setDiffCgstval(
					child.getMemoCgstval().subtract(child.getGstnCgstval()));
			child.setDiffSgstval(
					child.getMemoSgstval().subtract(child.getGstnSgstval()));
			child.setDiffCessval(
					child.getMemoCessval().subtract(child.getGstnCessval()));
			child.setDiffTotDoc(
					child.getMemoTotDoc().subtract(child.getGstnTotDoc()));
		});

		/*
		 * child.setRetPer(obj[0] != null ? String.valueOf(obj[0]) : null);
		 * child.setGstinNum(obj[1] != null ? String.valueOf(obj[1]) : null);
		 * child.setTaxDoctype(obj[2] != null ? String.valueOf(obj[2]) : null);
		 * child.setTaxTable(obj[3] != null ? String.valueOf(obj[3]) : null);
		 * 
		 * child.setEyTotDoc(obj[4] != null ? new
		 * BigInteger(String.valueOf(obj[4])) : BigInteger.ZERO);
		 * child.setEyInval(obj[5] != null ? new
		 * BigDecimal(String.valueOf(obj[5])) : BigDecimal.ZERO);
		 * 
		 * child.setEyIgstval(obj[6] != null ? new
		 * BigDecimal(String.valueOf(obj[6])) : BigDecimal.ZERO);
		 * child.setEyCgstval(obj[7] != null ? new
		 * BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);
		 * child.setEySgstval(obj[8] != null ? new
		 * BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);
		 * child.setEyTbval(obj[9] != null ? new
		 * BigDecimal(String.valueOf(obj[9])) : BigDecimal.ZERO);
		 * child.setEyTxval(obj[10] != null ? new
		 * BigDecimal(String.valueOf(obj[10])) : BigDecimal.ZERO);
		 * child.setEyCessval(obj[11] != null ? new
		 * BigDecimal(String.valueOf(obj[11])) : BigDecimal.ZERO);
		 */

		/*
		 * int max = 20; String profitCenter = obj[25] != null ?
		 * String.valueOf(obj[25]) : null; if (profitCenter != null) { if
		 * (profitCenter.length() > max) {
		 * child.setProfitCenter(profitCenter.substring(0, max)); } else {
		 * child.setProfitCenter(profitCenter); } } String plantCode = obj[26]
		 * != null ? String.valueOf(obj[26]) : null; if (plantCode != null) { if
		 * (plantCode.length() > max) {
		 * child.setPlantCode(plantCode.substring(0, max));
		 * 
		 * } else { child.setPlantCode(plantCode); } } String location = obj[27]
		 * != null ? String.valueOf(obj[27]) : null; if (location != null) { if
		 * (location.length() > max) { child.setLocation(location.substring(0,
		 * max)); } else { child.setLocation(location); } } String salesOrg =
		 * obj[28] != null ? String.valueOf(obj[28]) : null; if (salesOrg !=
		 * null) { if (salesOrg.length() > max) {
		 * child.setSalesOrganization(salesOrg.substring(0, max)); } else {
		 * child.setSalesOrganization(salesOrg); } } String distChannel =
		 * obj[29] != null ? String.valueOf(obj[29]) : null; if (distChannel !=
		 * null) { if (distChannel.length() > max) {
		 * child.setDistChannel(distChannel.substring(0, max)); } else {
		 * child.setDistChannel(distChannel); } } String division = obj[30] !=
		 * null ? String.valueOf(obj[30]) : null; if (division != null) { if
		 * (division.length() > max) { child.setDivision(division.substring(0,
		 * max)); } else { child.setDivision(division); } } String userAccess1 =
		 * obj[31] != null ? String.valueOf(obj[31]) : null; if (userAccess1 !=
		 * null) { if (userAccess1.length() > max) {
		 * child.setUseraccess1(userAccess1.substring(0, max)); } else {
		 * child.setUseraccess1(userAccess1); } } String userAccess2 = obj[32]
		 * != null ? String.valueOf(obj[32]) : null; if (userAccess2 != null) {
		 * if (userAccess2.length() > max) {
		 * child.setUseraccess2(userAccess2.substring(0, max)); } else {
		 * child.setUseraccess2(userAccess2); } } String userAccess3 = obj[33]
		 * != null ? String.valueOf(obj[33]) : null; if (userAccess3 != null) {
		 * if (userAccess3.length() > max) {
		 * child.setUseraccess3(userAccess3.substring(0, max)); } else {
		 * child.setUseraccess3(userAccess3); } } String userAccess4 = obj[34]
		 * != null ? String.valueOf(obj[34]) : null; if (userAccess4 != null) {
		 * if (userAccess4.length() > max) {
		 * child.setUseraccess4(userAccess4.substring(0, max)); } else {
		 * child.setUseraccess4(userAccess4); } } String userAccess5 = obj[35]
		 * != null ? String.valueOf(obj[35]) : null; if (userAccess5 != null) {
		 * if (userAccess5.length() > max) {
		 * child.setUseraccess5(userAccess5.substring(0, max)); } else {
		 * child.setUseraccess5(userAccess5); } } String userAccess6 = obj[36]
		 * != null ? String.valueOf(obj[36]) : null; if (userAccess6 != null) {
		 * if (userAccess6.length() > max) {
		 * child.setUseraccess6(userAccess6.substring(0, max)); } else {
		 * child.setUseraccess6(userAccess6); } }
		 */

	}

	private void convertInwardDocsAsDtos(String gstin, String entityName,
			String entityPan, String companyCode, String returnPeriod) {

		Annexure1SummaryReqDto requestDto = new Annexure1SummaryReqDto();
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstin);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		requestDto.setDataSecAttrs(dataSecAttrs);
		List<Annexure1SummarySectionDto> inwardSectionDtos = inwardSectionDaoImpl
				.loadBasicSummarySection(requestDto);
		inwardSectionDtos.forEach(inwardSectionDto -> {
			Anx1ReviewSummaryRequestItemDto child = new Anx1ReviewSummaryRequestItemDto();
			childArry.add(child);
			if (entityName != null) {
				child.setEntity(entityName.toUpperCase());
			}
			child.setEntityPan(entityPan);
			child.setCompanyCode(companyCode);
			child.setDataType("INWARD");
			child.setRetPer(inwardSectionDto.getReturnPeriod());
			child.setGstinNum(gstin);
			child.setTaxDoctype(inwardSectionDto.getDocType());
			child.setTaxTable(inwardSectionDto.getTableSection());

			child.setEyTotDoc(inwardSectionDto.getRecords() != null
					? new BigInteger(
							String.valueOf(inwardSectionDto.getRecords()))
					: BigInteger.ZERO);
			child.setEyInval(inwardSectionDto.getInvValue());

			child.setEyIgstval(inwardSectionDto.getIgst());
			child.setEyCgstval(inwardSectionDto.getCgst());
			child.setEySgstval(inwardSectionDto.getSgst());
			child.setEyTbval(inwardSectionDto.getTaxableValue());
			child.setEyTxval(inwardSectionDto.getTaxPayble());
			child.setEyCessval(inwardSectionDto.getCess());
			/*
			 * child.setRetPer(obj[0] != null ? String.valueOf(obj[0]) : null);
			 * child.setGstinNum(obj[1] != null ? String.valueOf(obj[1]) :
			 * null); child.setTaxDoctype(obj[2] != null ?
			 * String.valueOf(obj[2]) : null); child.setTaxTable(obj[3] != null
			 * ? String.valueOf(obj[3]) : null);
			 * 
			 * child.setEyTotDoc(obj[4] != null ? new
			 * BigInteger(String.valueOf(obj[4])) : BigInteger.ZERO);
			 * child.setEyInval(obj[5] != null ? new
			 * BigDecimal(String.valueOf(obj[5])) : BigDecimal.ZERO);
			 * 
			 * child.setEyIgstval(obj[6] != null ? new
			 * BigDecimal(String.valueOf(obj[6])) : BigDecimal.ZERO);
			 * child.setEyCgstval(obj[7] != null ? new
			 * BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);
			 * child.setEySgstval(obj[8] != null ? new
			 * BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);
			 * child.setEyTbval(obj[9] != null ? new
			 * BigDecimal(String.valueOf(obj[9])) : BigDecimal.ZERO);
			 * child.setEyTxval( obj[10] != null ? new
			 * BigDecimal(String.valueOf(obj[10])) : BigDecimal.ZERO);
			 * child.setEyCessval( obj[11] != null ? new
			 * BigDecimal(String.valueOf(obj[11])) : BigDecimal.ZERO);
			 */

			if (("3H").equalsIgnoreCase(child.getTaxTable())
					|| ("3I").equalsIgnoreCase(child.getTaxTable())) {
				child.setMemoTotDoc(child.getEyTotDoc());
				child.setMemoInval(child.getEyInval());
				child.setMemoTbval(child.getEyTbval());
				child.setMemoTxval(child.getEyTxval());
				child.setMemoIgstval(child.getEyIgstval());
				child.setMemoCgstval(child.getEyCgstval());
				child.setMemoSgstval(child.getEySgstval());
				child.setMemoCessval(child.getEyCessval());
			} else {
				child.setMemoTotDoc(child.getEyTotDoc());
				child.setMemoInval(child.getEyInval());
				child.setMemoTbval(child.getEyTbval());
				child.setMemoIgstval(inwardSectionDto.getMemoIgst());
				child.setMemoCgstval(inwardSectionDto.getMemoCgst());
				child.setMemoSgstval(inwardSectionDto.getMemoSgst());
				child.setMemoCessval(inwardSectionDto.getMemoCess());
				child.setMemoTxval(inwardSectionDto.getMemoTaxPayable());

				/*
				 * child.setMemoIgstval(obj[12] != null ? new
				 * BigDecimal(String.valueOf(obj[12])) : BigDecimal.ZERO);
				 * child.setMemoCgstval(obj[13] != null ? new
				 * BigDecimal(String.valueOf(obj[13])) : BigDecimal.ZERO);
				 * child.setMemoSgstval(obj[14] != null ? new
				 * BigDecimal(String.valueOf(obj[14])) : BigDecimal.ZERO);
				 * child.setMemoCessval(obj[16] != null ? new
				 * BigDecimal(String.valueOf(obj[16])) : BigDecimal.ZERO);
				 * child.setMemoTxval(obj[15] != null ? new
				 * BigDecimal(String.valueOf(obj[15])) : BigDecimal.ZERO);
				 */
			}
			child.setGstnTotDoc(inwardSectionDto.getGstnCount() != null
					? new BigInteger(
							String.valueOf(inwardSectionDto.getGstnCount()))
					: BigInteger.ZERO);
			child.setGstnInval(inwardSectionDto.getGstnInvoiceValue());
			child.setGstnIgstval(inwardSectionDto.getGstnIgst());
			child.setGstnCgstval(inwardSectionDto.getGstnCgst());
			child.setGstnSgstval(inwardSectionDto.getGstnSgst());
			child.setGstnTbval(inwardSectionDto.getGstnTaxableValue());
			child.setGstnTxval(inwardSectionDto.getGstnTaxPayble());
			child.setGstnCessval(inwardSectionDto.getGstnCess());
			/*
			 * child.setGstnTotDoc( obj[17] != null ? new
			 * BigInteger(String.valueOf(obj[17])) : BigInteger.ZERO);
			 * child.setGstnInval( obj[18] != null ? new
			 * BigDecimal(String.valueOf(obj[18])) : BigDecimal.ZERO);
			 * child.setGstnIgstval( obj[19] != null ? new
			 * BigDecimal(String.valueOf(obj[19])) : BigDecimal.ZERO);
			 * child.setGstnCgstval( obj[20] != null ? new
			 * BigDecimal(String.valueOf(obj[20])) : BigDecimal.ZERO);
			 * child.setGstnSgstval( obj[21] != null ? new
			 * BigDecimal(String.valueOf(obj[21])) : BigDecimal.ZERO);
			 * child.setGstnTbval( obj[22] != null ? new
			 * BigDecimal(String.valueOf(obj[22])) : BigDecimal.ZERO);
			 * child.setGstnTxval( obj[23] != null ? new
			 * BigDecimal(String.valueOf(obj[23])) : BigDecimal.ZERO);
			 * child.setGstnCessval( obj[24] != null ? new
			 * BigDecimal(String.valueOf(obj[24])) : BigDecimal.ZERO);
			 */

			/*
			 * String profitCenter = obj[25] != null ? String.valueOf(obj[25]) :
			 * null; if (profitCenter != null) { if (profitCenter.length() >
			 * max) { child.setProfitCenter(profitCenter.substring(0, max)); }
			 * else { child.setProfitCenter(profitCenter); } } String plantCode
			 * = obj[26] != null ? String.valueOf(obj[26]) : null; if (plantCode
			 * != null) { if (plantCode.length() > max) {
			 * child.setPlantCode(plantCode.substring(0, max));
			 * 
			 * } else { child.setPlantCode(plantCode); } } String location =
			 * obj[27] != null ? String.valueOf(obj[27]) : null; if (location !=
			 * null) { if (location.length() > max) {
			 * child.setLocation(location.substring(0, max)); } else {
			 * child.setLocation(location); } } String parchaseOrg = obj[28] !=
			 * null ? String.valueOf(obj[28]) : null; if (parchaseOrg != null) {
			 * if (parchaseOrg.length() > max) {
			 * child.setParchOrganization(parchaseOrg.substring(0, max)); } else
			 * { child.setParchOrganization(parchaseOrg); } }
			 * 
			 * String division = obj[29] != null ? String.valueOf(obj[29]) :
			 * null; if (division != null) { if (division.length() > max) {
			 * child.setDivision(division.substring(0, max)); } else {
			 * child.setDivision(division); } } String userAccess1 = obj[30] !=
			 * null ? String.valueOf(obj[30]) : null; if (userAccess1 != null) {
			 * if (userAccess1.length() > max) {
			 * child.setUseraccess1(userAccess1.substring(0, max)); } else {
			 * child.setUseraccess1(userAccess1); } } String userAccess2 =
			 * obj[31] != null ? String.valueOf(obj[31]) : null; if (userAccess2
			 * != null) { if (userAccess2.length() > max) {
			 * child.setUseraccess2(userAccess2.substring(0, max)); } else {
			 * child.setUseraccess2(userAccess2); } } String userAccess3 =
			 * obj[32] != null ? String.valueOf(obj[32]) : null; if (userAccess3
			 * != null) { if (userAccess3.length() > max) {
			 * child.setUseraccess3(userAccess3.substring(0, max)); } else {
			 * child.setUseraccess3(userAccess3); } } String userAccess4 =
			 * obj[33] != null ? String.valueOf(obj[33]) : null; if (userAccess4
			 * != null) { if (userAccess4.length() > max) {
			 * child.setUseraccess4(userAccess4.substring(0, max)); } else {
			 * child.setUseraccess4(userAccess4); } } String userAccess5 =
			 * obj[34] != null ? String.valueOf(obj[34]) : null; if (userAccess5
			 * != null) { if (userAccess5.length() > max) {
			 * child.setUseraccess5(userAccess5.substring(0, max)); } else {
			 * child.setUseraccess5(userAccess5); } } String userAccess6 =
			 * obj[35] != null ? String.valueOf(obj[35]) : null; if (userAccess6
			 * != null) { if (userAccess6.length() > max) {
			 * child.setUseraccess6(userAccess6.substring(0, max)); } else {
			 * child.setUseraccess6(userAccess6); } }
			 */

			child.setDiffInval(
					child.getMemoInval().subtract(child.getGstnInval()));

			child.setDiffInval(
					child.getMemoInval().subtract(child.getGstnInval()));
			child.setDiffTbval(
					child.getMemoTbval().subtract(child.getGstnTbval()));
			child.setDiffTxval(
					child.getMemoTxval().subtract(child.getGstnTxval()));

			child.setDiffIgstval(
					child.getMemoIgstval().subtract(child.getGstnIgstval()));
			child.setDiffCgstval(
					child.getMemoCgstval().subtract(child.getGstnCgstval()));
			child.setDiffSgstval(
					child.getMemoSgstval().subtract(child.getGstnSgstval()));
			child.setDiffCessval(
					child.getMemoCessval().subtract(child.getGstnCessval()));
			child.setDiffTotDoc(
					child.getMemoTotDoc().subtract(child.getGstnTotDoc()));
		});

	}

	private void convertCustomSuppDocsAsDtos(String gstin, String entityName,
			String pan, String companyCode, String returnPeriod) {

		Annexure1SummaryReqDto requestDto = new Annexure1SummaryReqDto();
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstin);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		requestDto.setDataSecAttrs(dataSecAttrs);
		List<Annexure1SummarySectionEcomDto> ecomDtos = ecomSectionDaoImpl
				.loadBasicSummarySection(requestDto);
		ecomDtos.forEach(ecomDto -> {
			Anx1ReviewSummaryRequestItemDto child = new Anx1ReviewSummaryRequestItemDto();
			childArry.add(child);
			if (entityName != null) {
				child.setEntity(entityName.toUpperCase());
			}
			child.setEntityPan(pan);
			child.setCompanyCode(companyCode);
			child.setRetPer(ecomDto.getReturnPeriod());
			child.setGstinNum(gstin);
			child.setDataType("TABLE4");
			child.setTaxTable(ecomDto.getTableSection());
			/*child.setAspSuppmode(ecomDto.getSupplyMade());
			child.setAspSuppreturn(ecomDto.getSupplyReturn());
			child.setAspSuppnet(ecomDto.getNetSupply());*/

			child.setEyIgstval(ecomDto.getIgst());
			child.setEyCgstval(ecomDto.getCgst());
			child.setEySgstval(ecomDto.getSgst());
			child.setEyCessval(ecomDto.getCess());
			child.setEyTxval(ecomDto.getTaxPayble());
			/*child.setMemoSuppmode(child.getAspSuppmode());
			child.setMemoSuppreturn(child.getAspSuppreturn());
			child.setMemoSuppnet(child.getAspSuppnet());
			*/child.setMemoIgstval(ecomDto.getMemoIgst());

			child.setMemoCgstval(ecomDto.getMemoCgst());

			child.setMemoSgstval(ecomDto.getMemoSgst());
			child.setMemoTxval(ecomDto.getMemoTaxPayable());
			/*child.setGstSuppmode(ecomDto.getGstnSupplyMade());
			child.setGstSuppreturn(ecomDto.getSupplyReturn());
			child.setGstSuppnet(ecomDto.getGstnNetSupply());*/
			child.setGstnIgstval(ecomDto.getGstnIgst());
			child.setGstnCgstval(ecomDto.getGstnCgst());
			child.setGstnSgstval(ecomDto.getGstnSgst());
			child.setGstnCessval(ecomDto.getGstnCess());
			child.setGstnTbval(ecomDto.getGstnTaxPayble());

			/*
			 * String profitCenter = obj[23] != null ? String.valueOf(obj[23]) :
			 * null; if (profitCenter != null) { if (profitCenter.length() >
			 * max) { child.setProfitCenter(profitCenter.substring(0, max)); }
			 * else { child.setProfitCenter(profitCenter); } } String plantCode
			 * = obj[24] != null ? String.valueOf(obj[24]) : null; if (plantCode
			 * != null) { if (plantCode.length() > max) {
			 * child.setPlantCode(plantCode.substring(0, max)); } else {
			 * child.setPlantCode(plantCode); } } String location = obj[25] !=
			 * null ? String.valueOf(obj[25]) : null; if (location != null) { if
			 * (location.length() > max) {
			 * child.setLocation(location.substring(0, max)); } else {
			 * child.setLocation(location); } } String salesOrg = obj[26] !=
			 * null ? String.valueOf(obj[26]) : null; if (salesOrg != null) { if
			 * (salesOrg.length() > max) {
			 * child.setSalesOrganization(salesOrg.substring(0, max)); } else {
			 * child.setSalesOrganization(salesOrg); } } String distChannel =
			 * obj[27] != null ? String.valueOf(obj[27]) : null; if (distChannel
			 * != null) { if (distChannel.length() > max) {
			 * child.setDistChannel(distChannel.substring(0, max)); } else {
			 * child.setDistChannel(distChannel); } } String division = obj[28]
			 * != null ? String.valueOf(obj[28]) : null; if (division != null) {
			 * if (division.length() > max) {
			 * child.setDivision(division.substring(0, max)); } else {
			 * child.setDivision(division); } } String userAccess1 = obj[29] !=
			 * null ? String.valueOf(obj[29]) : null; if (userAccess1 != null) {
			 * if (userAccess1.length() > max) {
			 * child.setUseraccess1(userAccess1.substring(0, max)); } else {
			 * child.setUseraccess1(userAccess1); } } String userAccess2 =
			 * obj[30] != null ? String.valueOf(obj[30]) : null; if (userAccess2
			 * != null) { if (userAccess2.length() > max) {
			 * child.setUseraccess2(userAccess2.substring(0, max)); } else {
			 * child.setUseraccess2(userAccess2); } } String userAccess3 =
			 * obj[31] != null ? String.valueOf(obj[31]) : null; if (userAccess3
			 * != null) { if (userAccess3.length() > max) {
			 * child.setUseraccess3(userAccess3.substring(0, max)); } else {
			 * child.setUseraccess3(userAccess3); } } String userAccess4 =
			 * obj[32] != null ? String.valueOf(obj[32]) : null; if (userAccess4
			 * != null) { if (userAccess4.length() > max) {
			 * child.setUseraccess4(userAccess4.substring(0, max)); } else {
			 * child.setUseraccess4(userAccess4); } } String userAccess5 =
			 * obj[33] != null ? String.valueOf(obj[33]) : null; if (userAccess5
			 * != null) { if (userAccess5.length() > max) {
			 * child.setUseraccess5(userAccess5.substring(0, max)); } else {
			 * child.setUseraccess5(userAccess5); } } String userAccess6 =
			 * obj[34] != null ? String.valueOf(obj[34]) : null; if (userAccess6
			 * != null) { if (userAccess6.length() > max) {
			 * child.setUseraccess6(userAccess6.substring(0, max)); } else {
			 * child.setUseraccess6(userAccess6); } }
			 */
			/*child.setDiffSuppmode(
					child.getMemoSuppmode().subtract(child.getAspSuppmode()));
			child.setDiffSuppnet(
					child.getMemoSuppnet().subtract(child.getAspSuppnet()));
			child.setDiffSuppreturn(child.getMemoSuppreturn()
					.subtract(child.getAspSuppreturn()));*/
			child.setDiffIgstval(
					child.getMemoIgstval().subtract(child.getGstnIgstval()));
			child.setDiffCgstval(
					child.getMemoCgstval().subtract(child.getGstnCgstval()));
			child.setDiffSgstval(
					child.getMemoSgstval().subtract(child.getGstnSgstval()));
			child.setDiffCessval(
					child.getMemoCessval().subtract(child.getGstnCessval()));
			child.setDiffTbval(
					child.getMemoTbval().subtract(child.getGstnTbval()));
		});
	}

	/*@Override
	public Integer pushToErp(Anx1ReviewSummaryRequestDto dto,
			String destinationName, AnxErpBatchEntity batch) {

		try {
			ByteArrayOutputStream itemOut = new ByteArrayOutputStream();
			JAXBContext itemContext = JAXBContext
					.newInstance(Anx1ReviewSummaryRequestDto.class);
			Marshaller itemMarshr = itemContext.createMarshaller();
			itemMarshr.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			itemMarshr.marshal(dto, itemOut);
			String xml = itemOut.toString();

			if (xml != null && xml.length() > 0) {
				xml = xml.substring(xml.indexOf('\n') + 1);
			}

			String header = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' "
					+ "xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'><soapenv:Header/>"
					+ "<soapenv:Body><urn:ZupdateAnx1Aspdata>";
			String footer = "</urn:ZupdateAnx1Aspdata></soapenv:Body></soapenv:Envelope>";
			if (xml != null) {
				xml = header + xml + footer;
			}

			if (xml != null && destinationName != null) {
				return destinationConn.post(destinationName, xml, batch);
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}*/
}
