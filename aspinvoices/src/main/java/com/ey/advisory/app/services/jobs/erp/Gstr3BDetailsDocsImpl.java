package com.ey.advisory.app.services.jobs.erp;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.erp.Gstr3BDashboardSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3BDetailResponseDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3BPosDetailResponseDto;
import com.ey.advisory.app.docs.dto.erp.Gstrn3BFinalResultDto;
import com.ey.advisory.app.gstr3b.Gstr3BEntityDashboardDto;
import com.ey.advisory.app.gstr3b.Gstr3BEntityDashboardServiceImpl;
import com.google.common.collect.ImmutableList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("Gstr3BDetailsDocsImpl")
public class Gstr3BDetailsDocsImpl implements GSTR3BDetailsDocs {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier(value = "GSTNDetailRepository")
	GSTNDetailRepository gstinDetailsRepo;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository stateInfoRepo;

	@Autowired
	@Qualifier("Gstr3BEntityDashboardServiceImpl")
	private Gstr3BEntityDashboardServiceImpl gstin3BDahBoardService;

	@Override
	public List<Gstr3BPosDetailResponseDto> convertPosDetails(
			List<Object[]> objs, String entityName, String companyCode) {
		// TODO Auto-generated method stub

		List<Gstr3BPosDetailResponseDto> childArry = new ArrayList<Gstr3BPosDetailResponseDto>();
		ImmutableList<String> sections = ImmutableList.of("3.2(a)", "3.2(b)",
				"3.2(c)", "5(a)", "5(b)");
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Got Gstr3B Details Response " + "Except 3.2 and 5 Section "
							+ "as they are Pos so not considering Data "
							+ "Size of Data is : %d",
					objs.size());
			LOGGER.debug(msg);
		}
		objs.forEach(obj -> {
			if (isUserEntryAvailable(obj) && sections.contains(obj[2])) {
				Gstr3BPosDetailResponseDto child = new Gstr3BPosDetailResponseDto();
				childArry.add(child);
				child.setRetPeriod(
						obj[0] != null ? String.valueOf(obj[0]) : null);
				child.setGstinNum(
						obj[1] != null ? String.valueOf(obj[1]) : null);
				child.setTableName(
						obj[2] != null ? String.valueOf(obj[2]) : null);
				child.setTableSubType(
						obj[3] != null ? String.valueOf(obj[3]) : null);

				String entity = child.getGstinNum().substring(2, 12);
				child.setEntity(entity);
				child.setCompanyCode(companyCode);
				child.setAmount1(
						obj[4] != null ? new BigDecimal(String.valueOf(obj[4]))
								: BigDecimal.ZERO);
				child.setAmount2(
						obj[5] != null ? new BigDecimal(String.valueOf(obj[5]))
								: BigDecimal.ZERO);
				String pos = String.valueOf(obj[9]);
				String stateName = stateInfoRepo.findStateNameByCode(pos);
				child.setState(pos + " - " + stateName);
			}
		});

		return childArry;
	}

	@Override
	public List<Gstr3BDetailResponseDto> convertDetailsDocsAsDtos(
			List<Object[]> objs, String entityName, String companyCode) {
		// TODO Auto-generated method stub
		// childArry = new ArrayList<>();
		ImmutableList<String> sections = ImmutableList.of("3.2(a)", "3.2(b)",
				"3.2(c)", "5(a)", "5(b)");

		List<Gstr3BDetailResponseDto> childArry = new ArrayList<Gstr3BDetailResponseDto>();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Got Gstr3B Details Response " + "Exccept 3.2 and 5 Section"
							+ "as they are Pos so not considering Data "
							+ "Size of Data is : %d",
					objs.size());
			LOGGER.debug(msg);
		}

		objs.forEach(obj -> {
			Gstr3BDetailResponseDto child = new Gstr3BDetailResponseDto();
			if (!(sections.contains(obj[2]) || sections.contains(obj[12]))) {

				childArry.add(child);
				if (isUserEntryAvailable(obj) == true) {
					child.setRetPeriod(
							obj[0] != null ? String.valueOf(obj[0]) : null);
					child.setGstinNum(
							obj[1] != null ? String.valueOf(obj[1]) : null);
					child.setTableName(
							obj[2] != null ? String.valueOf(obj[2]) : null);
					child.setTableSubType(
							obj[3] != null ? String.valueOf(obj[3]) : null);
				} else if (isGstnEntryAvailable(obj) == true) {
					child.setRetPeriod(
							obj[10] != null ? String.valueOf(obj[10]) : null);
					child.setGstinNum(
							obj[11] != null ? String.valueOf(obj[11]) : null);
					child.setTableName(
							obj[12] != null ? String.valueOf(obj[12]) : null);
					child.setTableSubType(
							obj[13] != null ? String.valueOf(obj[13]) : null);

				}

				child.setUserComputeTax(
						obj[4] != null ? new BigDecimal(String.valueOf(obj[4]))
								: BigDecimal.ZERO);
				child.setUserComputeIgst(
						obj[5] != null ? new BigDecimal(String.valueOf(obj[5]))
								: BigDecimal.ZERO);
				child.setUserComputeCgst(
						obj[6] != null ? new BigDecimal(String.valueOf(obj[6]))
								: BigDecimal.ZERO);
				child.setUserComputeSgst(
						obj[7] != null ? new BigDecimal(String.valueOf(obj[7]))
								: BigDecimal.ZERO);
				child.setUserComputeCess(
						obj[8] != null ? new BigDecimal(String.valueOf(obj[8]))
								: BigDecimal.ZERO);

				String entity = child.getGstinNum().substring(2, 12);
				child.setEntity(entity);
				child.setEntityName(entityName);
				child.setCompanyCode(companyCode);
				child.setGstinTaxVal(obj[14] != null
						? new BigDecimal(String.valueOf(obj[14]))
						: BigDecimal.ZERO);
				child.setGstinIgst(obj[15] != null
						? new BigDecimal(String.valueOf(obj[15]))
						: BigDecimal.ZERO);
				child.setGstinCgst(obj[16] != null
						? new BigDecimal(String.valueOf(obj[16]))
						: BigDecimal.ZERO);
				child.setGstinSgst(obj[17] != null
						? new BigDecimal(String.valueOf(obj[17]))
						: BigDecimal.ZERO);
				child.setGstinCess(obj[18] != null
						? new BigDecimal(String.valueOf(obj[18]))
						: BigDecimal.ZERO);
			}
		});

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Got Gstr3B Details Response "
							+ "only Gstins Value for 3.2 and 5 Section "
							+ "as they are Pos Data Size of Data is : %d",
					objs.size());
			LOGGER.debug(msg);
		}
		objs.forEach(obj -> {
			if (isGstnEntryAvailable(obj) && sections.contains(obj[12])) {
				Gstr3BDetailResponseDto child = new Gstr3BDetailResponseDto();
				childArry.add(child);
				child.setRetPeriod(
						obj[10] != null ? String.valueOf(obj[10]) : null);
				child.setGstinNum(
						obj[11] != null ? String.valueOf(obj[11]) : null);
				child.setTableName(
						obj[12] != null ? String.valueOf(obj[12]) : null);
				child.setTableSubType(
						obj[13] != null ? String.valueOf(obj[13]) : null);
				String entity = child.getGstinNum().substring(2, 12);
				child.setEntity(entity);
				child.setEntityName(entityName);
				child.setGstinTaxVal(obj[14] != null
						? new BigDecimal(String.valueOf(obj[14]))
						: BigDecimal.ZERO);
				child.setGstinIgst(obj[15] != null
						? new BigDecimal(String.valueOf(obj[15]))
						: BigDecimal.ZERO);
				child.setGstinCgst(obj[16] != null
						? new BigDecimal(String.valueOf(obj[16]))
						: BigDecimal.ZERO);
				child.setGstinSgst(obj[17] != null
						? new BigDecimal(String.valueOf(obj[17]))
						: BigDecimal.ZERO);
				child.setGstinCess(obj[18] != null
						? new BigDecimal(String.valueOf(obj[18]))
						: BigDecimal.ZERO);
			}
		});
		return childArry;
	}

	private boolean isUserEntryAvailable(Object[] arr) {
		if (arr[0] == null && arr[1] == null && arr[3] == null) {
			return false;
		} else {
			return true;
		}
	}

	private boolean isGstnEntryAvailable(Object[] arr) {
		if (arr[12] == null && arr[13] == null && arr[14] == null) {
			return false;
		} else {
			return true;
		}

	}

	@Override
	public List<Gstr3BDashboardSummaryDto> convertSummarisedDocsAsDtos(
			List<Gstr3BEntityDashboardDto> gstinDashBoardDto, String entityName,
			String companyCode) {
		List<Gstr3BDashboardSummaryDto> responseDashBoard = new ArrayList<Gstr3BDashboardSummaryDto>();
		gstinDashBoardDto.forEach(obj -> {
			Gstr3BDashboardSummaryDto child = new Gstr3BDashboardSummaryDto();
			responseDashBoard.add(child);
			child.setGstinNum(obj.getGstin());
			String entity = child.getGstinNum().substring(2, 12);
			child.setEntity(entity);
			child.setEntityName(entityName);
			child.setCompanyCode(companyCode);
			child.setTItc(obj.getTotalItc());
			child.setTLiability(obj.getTotalLiability());
			child.setState(obj.getStateName());
			child.setSaveStatus(obj.getSavedStatus());
			child.setRetPeriod(obj.getTaxPeriod());
			LocalDateTime date = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd-MM-yyyy hh:mm:ss");
			child.setAsOn(date.format(formatter));
		});
		return responseDashBoard;

	}

	@Override
	public List<Gstr3BEntityDashboardDto> get3BSummarisedData(String gstinNum,
			String taxPeriod) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Gstin3BSummarised Data for Gstin :" + "%s", gstinNum);
			LOGGER.debug(msg);
		}
		List<String> gstins = new ArrayList<String>();
		gstins.add(gstinNum);
		List<Gstr3BEntityDashboardDto> responseDetails = gstin3BDahBoardService
				.getEntityDashBoard(null, gstins, null);
		// TODO Auto-generated method stub
		return responseDetails;
	}

	@Override
	public Integer pushToErp(Gstrn3BFinalResultDto dto, String destinationName,
			AnxErpBatchEntity batch) {

		try {
			String xml = null;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JAXBContext context = JAXBContext
					.newInstance(Gstrn3BFinalResultDto.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(dto, out);
			xml = out.toString();

			if (xml != null && xml.length() > 0) {
				xml = xml.substring(xml.indexOf('\n') + 1);
			}

			String header = "<soapenv:Envelope xmlns:soapenv="
					+ "\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn="
					+ "\"urn:sap-com:document:sap:rfc:functions\">"
					+ "<soapenv:Header/>" + "<soapenv:Body>";

			String footer = "</soapenv:Body>" + "</soapenv:Envelope>";

			// final payload using header and footer.
			if (xml != null) {
				xml = header + xml + footer;
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"The Request Payload " + "For Gstr3B is : %s", xml);
				LOGGER.debug(msg);
			}

			if (xml != null && destinationName != null) {
				// return destinationConn.post(destinationName, xml, batch);
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<Object[]> get3BDetailsData(String gstin) {
		String sql = get3BDetailsQuery();
		try {
			Query q = entityManager.createNativeQuery(sql);
			q.setParameter("gstin", gstin);
			return q.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String get3BDetailsQuery() {
		return "SELECT USERVALUE.TAX_PERIOD AS USER_TAXPERIOD,"
				+ "USERVALUE.GSTIN AS USER_GSTIN,"
				+ "USERVALUE.SECTION_NAME AS USER_SECTIONAME,"
				+ "USERVALUE.SUBSECTION_NAME AS USER_SUBSECTION,"
				+ "USERVALUE.TAXABLE_VALUE AS USER_TAXAVALUE,"
				+ "USERVALUE.IGST AS USER_IGST,"
				+ "USERVALUE.CGST AS USER_CGST,"
				+ "USERVALUE.SGST AS USER_SGST,"
				+ "USERVALUE.CESS AS USER_CESS," + "USERVALUE.POS AS USER_POS,"
				+ "GSTVALUE.TAX_PERIOD AS GST_TAXPERIOD,"
				+ "GSTVALUE.GSTIN AS GST_GSTIN,"
				+ "GSTVALUE.SECTION_NAME AS GST_SECTIONAME,"
				+ "GSTVALUE.SUBSECTION_NAME AS GST_SUBSECTION,"
				+ "GSTVALUE.TAXABLE_VALUE AS GST_TAXVALUE,"
				+ "GSTVALUE.IGST AS  GST_IGST" + ",GSTVALUE.CGST AS GST_CGST,"
				+ "GSTVALUE.SGST AS GST_SGST," + "GSTVALUE.CESS AS GST_CESS,"
				+ "GSTVALUE.POS  AS GST_POS"
				+ " FROM (SELECT * FROM GSTR3B_ASP_USER WHERE "
				+ " GSTIN =:gstin AND IS_ACTIVE=TRUE) USERVALUE"
				+ " FULL OUTER JOIN (SELECT * FROM GSTR3B_GSTN WHERE "
				+ " GSTIN =:gstin AND IS_ACTIVE=TRUE) GSTVALUE ON 	USERVALUE."
				+ "TAX_PERIOD = GSTVALUE.TAX_PERIOD AND "
				+ " USERVALUE.SECTION_NAME = GSTVALUE.SECTION_NAME AND"
				+ " USERVALUE.GSTIN = GSTVALUE.GSTIN AND"
				+ " USERVALUE.POS = GSTVALUE.POS";
	}

}
