/**
 * 
 */
package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.VendorGstinFilingTypeEntity;
import com.ey.advisory.app.data.repositories.client.VendorGstinFilingTypeRepository;
import com.ey.advisory.app.docs.dto.GstinValidatorAndReturnFilingReportDto;
import com.ey.advisory.app.docs.dto.erp.GetFilingFrequencyDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ashutosh.kar
 *
 */
@Slf4j
@Component("ReturnFilingFileArrivalHandler")
public class ReturnFilingFileArrivalHandler {

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	GstnReturnFilingStatus gstnReturnFiling;

	@Autowired
	ReturnFilingReportDetails returnFilingReportDetails;

	@Autowired
	@Qualifier("taxPayerDetailsServiceImpl")
	private TaxPayerDetailsService taxPayerService;
	
	@Autowired
	@Qualifier("GetFilingFrequencyVendorCommunicationServiceImpl")
	private GetFilingFrequencyVendorCommunicationServiceImpl getFreqservice;
	
	@Autowired
	@Qualifier("VendorGstinFilingTypeRepository")
	private VendorGstinFilingTypeRepository vendorGstinFilingTypeRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gstinDetailRepository;
	
	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;
	
	private static final Map<String, MonthDay> dueDates = new HashMap<>();

	static {

		// GSTR1 Monthly due dates
		dueDates.put("Q1", MonthDay.of(4, 30));
		dueDates.put("Q2", MonthDay.of(7, 31));
		dueDates.put("Q3", MonthDay.of(10, 31));
		dueDates.put("Q4", MonthDay.of(1, 31));
	}

	public Pair<String, String> gstinValidatorOnFile(Long requestId,
			String fileName, String folderName, String docId, String filingFrequency) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("About to start Processing" + " ReturnFillingStatus Upload file Name - %s ",
					fileName);
			LOGGER.debug(msg);
		}
		try {
			/*
			 * Document doc = DocumentUtility.downloadDocument(fileName, folderName);
			 */
			Document doc = DocumentUtility.downloadDocumentByDocId(docId);
			InputStream in = doc.getContentStream().getStream();
			TabularDataSourceTraverser traverser = traverserFactory.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(2);
			// Add a dummy row handler that will keep counting the rows.
			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(in, layout, rowHandler, null);

			List<Object[]> rowDataList = ((FileUploadDocRowHandler<?>) rowHandler).getFileUploadList();

			List<ReturnFilingGstnResponseDto> respList = new ArrayList<>();
			List<GstinValidatorAndReturnFilingReportDto> combinedRespList = new ArrayList<>();
			for (Object[] obj : rowDataList) {
				String gstin = "";
				String fiYear = "";

				if (obj[0] != null && !obj[0].toString().isEmpty()) {
					gstin = obj[0].toString().trim().replaceAll("\\s", "");
				}
				if (obj[1] != null && !obj[1].toString().isEmpty()) {
					fiYear = obj[1].toString().trim();
				}

				// gstinValidator api call
				String groupCode = TenantContext.getTenantId();
				PublicApiContext.setContextMap(PublicApiConstants.SOURCE, PublicApiConstants.GSTIN_UI_UPLOAD);
				TaxPayerDetailsDto taxPayerDetDto = taxPayerService.getTaxPayerDetails(gstin, groupCode);

				if (gstin.isEmpty()) {
					continue;
				}

				// Filing Frequency Api Call and Save
				StringBuilder frequency = new StringBuilder();
				if (!fiYear.isEmpty() && filingFrequency.equalsIgnoreCase("Y")) {
					saveFilingFrequency(Collections.singletonList(gstin), fiYear);
					List<VendorGstinFilingTypeEntity> filingFreqEntities = vendorGstinFilingTypeRepo
							.findByGstinInAndFy(Collections.singletonList(gstin), fiYear);
					for (VendorGstinFilingTypeEntity freq : filingFreqEntities) {
						if (frequency.length() > 0)
							frequency.append(", ");
						frequency.append(freq.getQuarter()).append(" - ").append(freq.getFilingType());
					}
				}

				// Return Filing API Call
				List<ReturnFilingGstnResponseDto> filingResponses = Collections.emptyList();
				if (!fiYear.isEmpty()) {
					PublicApiContext.setContextMap(PublicApiConstants.SOURCE, PublicApiConstants.RET_FILING_UI_UPLOAD);
					filingResponses = gstnReturnFiling.callGstnApi(Collections.singletonList(gstin), fiYear, false);
				}
				if (filingResponses != null && !filingResponses.isEmpty()) {
					respList.addAll(filingResponses);
					for (ReturnFilingGstnResponseDto returnDto : filingResponses) {
						GstinValidatorAndReturnFilingReportDto combinedDto = new GstinValidatorAndReturnFilingReportDto();
						combinedDto.setGstin(returnDto.getGstin());
						combinedDto.setArnNo(returnDto.getArnNo());
						combinedDto.setRetPeriod(returnDto.getRetPeriod());
						combinedDto.setRetType(returnDto.getRetType());
						combinedDto.setFilingDate(returnDto.getFilingDate());
						combinedDto.setStatus(returnDto.getStatus());
						combinedDto.setLegalBussNam(taxPayerDetDto.getLegalBussNam());
						combinedDto.setTradeName(taxPayerDetDto.getTradeName());
						combinedDto.setGstnStatus(taxPayerDetDto.getGstnStatus());
						combinedDto.setDateOfReg(taxPayerDetDto.getDateOfReg());
						combinedDto.setTaxPayType(taxPayerDetDto.getTaxPayType());
						combinedDto.setEinvApplicable(taxPayerDetDto.getEinvApplicable());
						combinedDto.setDateOfCan(taxPayerDetDto.getDateOfCan());
						combinedDto.setErrCode(taxPayerDetDto.getErrorCode());
						combinedDto.setErrMsg(taxPayerDetDto.getErrorMsg());
						combinedDto.setFilingFreq(frequency.toString());
						combinedRespList.add(combinedDto);
					}
				} else {
					GstinValidatorAndReturnFilingReportDto combinedDto = new GstinValidatorAndReturnFilingReportDto();
					combinedDto.setGstin(taxPayerDetDto.getGstin());
					combinedDto.setLegalBussNam(taxPayerDetDto.getLegalBussNam());
					combinedDto.setTradeName(taxPayerDetDto.getTradeName());
					combinedDto.setGstnStatus(taxPayerDetDto.getGstnStatus());
					combinedDto.setDateOfReg(taxPayerDetDto.getDateOfReg());
					combinedDto.setTaxPayType(taxPayerDetDto.getTaxPayType());
					combinedDto.setEinvApplicable(taxPayerDetDto.getEinvApplicable());
					combinedDto.setDateOfCan(taxPayerDetDto.getDateOfCan());
					combinedDto.setErrCode(taxPayerDetDto.getErrorCode());
					combinedDto.setErrMsg(taxPayerDetDto.getErrorMsg());
					combinedDto.setFilingFreq(frequency.toString());
					combinedRespList.add(combinedDto);
				}

			}
			combinedRespList.sort(Comparator.comparing(dto -> dto.getErrCode() != null ? 1 : 0));

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("The Response received for combined Resp is  %s ", combinedRespList.size());
				LOGGER.debug(msg);
			}

			if (!respList.isEmpty()) {
				List<ReturnFilingGstnResponseDto> myGstinsRespList = new ArrayList<>();
				List<ReturnFilingGstnResponseDto> vendorGstinsRespList = new ArrayList<>();
				List<String> gstinList = gstinDetailRepository.findAllGstin();
				for (ReturnFilingGstnResponseDto dto : respList) {
					if (gstinList.contains(dto.getGstin())) {
						myGstinsRespList.add(dto);
					} else {
						vendorGstinsRespList.add(dto);
					}
				}
				if (!myGstinsRespList.isEmpty()) {
					gstnReturnFiling.persistReturnFillingStatus(myGstinsRespList, false);
				}
				if (!vendorGstinsRespList.isEmpty()) {
					gstnReturnFiling.persistReturnFillingStatus(vendorGstinsRespList, true);
				}
			}

			return returnFilingReportDetails.generateAndUploadReturnFillingFile(combinedRespList, requestId);

		} catch (Exception e) {
			LOGGER.error("Exception while Generating and" + " Upload returnFillingStatus Report", e);
			throw new AppException(e);
		}

	}

	private void saveFilingFrequency(List<String> gstinList, String financialYear) {

		Set<String> q1FilingGstinList = new HashSet<>();
		Set<String> q2FilingGstinList = new HashSet<>();
		Set<String> q3FilingGstinList = new HashSet<>();
		Set<String> q4FilingGstinList = new HashSet<>();
		try {
			q1FilingGstinList
					.addAll(vendorGstinFilingTypeRepo.
							findByGstinInAndFyAndQuarter(gstinList, financialYear, "Q1"));
			q2FilingGstinList
					.addAll(vendorGstinFilingTypeRepo.
							findByGstinInAndFyAndQuarter(gstinList, financialYear, "Q2"));
			q3FilingGstinList
					.addAll(vendorGstinFilingTypeRepo.
							findByGstinInAndFyAndQuarter(gstinList, financialYear, "Q3"));
			q4FilingGstinList
					.addAll(vendorGstinFilingTypeRepo.
							findByGstinInAndFyAndQuarter(gstinList, financialYear, "Q4"));
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Get Preference api q1FilingGstinList size {} q2FilingGstinList size {} "
								+ "q3FilingGstinList size {}  q4FilingGstinList {} ",
						q1FilingGstinList.size(), q2FilingGstinList.size(),
						q3FilingGstinList.size(), q4FilingGstinList.size());
			}

			Set<String> requiredGetGstinList = getFreqservice.
					getRequiredGstinList(gstinList, financialYear,
					q1FilingGstinList, q2FilingGstinList, q3FilingGstinList, q4FilingGstinList);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Get Preference api requiredGetGstinList size {} ",
						requiredGetGstinList.size());
			}

			Pair<List<String>, List<GetFilingFrequencyDto>> pair = getFreqservice
					.getFilingResponse(requiredGetGstinList, financialYear);
			List<String> requiredGstinList = pair.getValue0();
			List<GetFilingFrequencyDto> getFilingFreqDtoList = pair.getValue1();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Get Preference api requiredGstinList size {} ",
						requiredGstinList.size());
			}
			List<List<String>> gstinchunks = Lists.partition(requiredGstinList,
					2000);
			Set<String> gstinListQ1 = new HashSet<>();
			Set<String> gstinListQ2 = new HashSet<>();
			Set<String> gstinListQ3 = new HashSet<>();
			Set<String> gstinListQ4 = new HashSet<>();
			for (List<String> chunk : gstinchunks) {
				gstinListQ1.addAll(
						vendorGstinFilingTypeRepo.findByGstinInAndFyAndQuarter(
								chunk, financialYear, "Q1"));
				gstinListQ2.addAll(
						vendorGstinFilingTypeRepo.findByGstinInAndFyAndQuarter(
								chunk, financialYear, "Q2"));
				gstinListQ3.addAll(
						vendorGstinFilingTypeRepo.findByGstinInAndFyAndQuarter(
								chunk, financialYear, "Q3"));
				gstinListQ4.addAll(
						vendorGstinFilingTypeRepo.findByGstinInAndFyAndQuarter(
								chunk, financialYear, "Q4"));
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Get Preference api gstinListQ1 size {} gstinListQ2 size {} "
								+ "gstinListQ3 size {}  gstinListQ4 {} ",
						gstinListQ1.size(), gstinListQ2.size(),
						gstinListQ3.size(), gstinListQ4.size());
			}
			Integer year = Integer.parseInt(financialYear.substring(0, 4));
			LocalDate dueDateQ1 = dueDates.get("Q1").atYear(year);
			LocalDate dueDateQ2 = dueDates.get("Q2").atYear(year);
			LocalDate dueDateQ3 = dueDates.get("Q3").atYear(year);
			LocalDate dueDateQ4 = dueDates.get("Q4").atYear(year + 1);
			LocalDate currentDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			List<VendorGstinFilingTypeEntity> entities = new ArrayList<>();
			for (GetFilingFrequencyDto dto : getFilingFreqDtoList) {
				String gstin = dto.getGstin();
				if (((dto.getQuarter().equalsIgnoreCase("Q1"))
						&& (!gstinListQ1.contains(gstin))
						&& (currentDate.isAfter(dueDateQ1)))
						|| ((dto.getQuarter().equalsIgnoreCase("Q2"))
								&& (!gstinListQ2.contains(gstin))
								&& (currentDate.isAfter(dueDateQ2)))
						|| ((dto.getQuarter().equalsIgnoreCase("Q3"))
								&& (!gstinListQ3.contains(gstin))
								&& (currentDate.isAfter(dueDateQ3)))
						|| ((dto.getQuarter().equalsIgnoreCase("Q4"))
								&& (!gstinListQ4.contains(gstin))
								&& (currentDate.isAfter(dueDateQ4)))) {

					VendorGstinFilingTypeEntity entity = new VendorGstinFilingTypeEntity();
					entity.setGstin(gstin);
					entity.setReturnType("GSTR1");
					entity.setCreatedOn(LocalDateTime.now());
					entity.setQuarter(dto.getQuarter());
					entity.setFilingType(dto.getFilingType());
					entity.setFy(dto.getFinancialYear());
					entities.add(entity);
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Get Preference api entities size {} ",
						entities.size());
			}
			vendorGstinFilingTypeRepo.saveAll(entities);
		} catch (Exception e) {
			LOGGER.error("Exception while processing the vendor filing type request:", e);
			throw new AppException(e);
		}

	}
}
