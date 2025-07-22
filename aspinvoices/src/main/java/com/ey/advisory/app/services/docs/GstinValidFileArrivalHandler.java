package com.ey.advisory.app.services.docs;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.common.GstnValidatorConstants;
import com.ey.advisory.app.data.repositories.client.GstinValidatorConfigRepository;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GstinValidFileArrivalHandler")
public class GstinValidFileArrivalHandler {

	@Autowired
	@Qualifier(value = "GstinValidatorConfigRepository")
	GstinValidatorConfigRepository gstinValidRepo;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("taxPayerDetailsServiceImpl")
	private TaxPayerDetailsService taxPayerService;

	@Autowired
	@Qualifier("GstinValidFinalReportDetails")
	GstinValidFinalReportDetails generateReport;
	
	

	public Pair<String,String> gstinValidatorOnFile(Long requestId, String fileName,
			String folderName, boolean isEinvApplicable, String docId) {
		List<TaxPayerDetailsDto> taxPayerResponses = new ArrayList<>();
		String fName = null;
		Pair<String, String> gstinValidatorDetails=null;
		List<String> gstins = new ArrayList<>();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Starting Processing for file Name : %s in Gstin ",
					fileName);
			LOGGER.debug(msg);
		}
		try {
			String groupCode = TenantContext.getTenantId();
			Document doc = DocumentUtility.downloadDocumentByDocId(docId);
			if (doc == null) {
				String erMsg = String.format(
						"Uploaded File '%s' is not available in Doc Repository Folder '%s'",
						fileName, folderName);
				LOGGER.error(erMsg);
				throw new AppException(erMsg);
			}

			InputStream in = doc.getContentStream().getStream();
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(1);
			// Add a dummy row handler that will keep counting the rows.
			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(in, layout, rowHandler, null);

			List<Object[]> listOfGstins = ((FileUploadDocRowHandler<?>) rowHandler)
					.getFileUploadList();

			for (Object[] obj : listOfGstins) {
				if (obj[0] != null) {
					String gstin = obj[0].toString().replaceAll("[^a-zA-Z0-9]",
							"");
					if (!gstin.isEmpty()) {
						gstins.add(gstin);
					}
				}
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"The NO of Valid Gstins Uploaded By The User Are %d",
						gstins.size());
				LOGGER.debug(msg);
			}

			gstins.parallelStream().forEach(gstin -> {
				TenantContext.setTenantId(groupCode);
				PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
						PublicApiConstants.GSTIN_UI_UPLOAD);
				if (isEinvApplicable) {
					PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
							PublicApiConstants.GSTIN_EINV_UI_UPLOAD);
				}
				TaxPayerDetailsDto taxPayerDetDto = taxPayerService
						.getTaxPayerDetails(gstin, groupCode);
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"The Response received for gstin '%s' is  %s",
							gstin, taxPayerDetDto);
					LOGGER.debug(msg);
				}
				taxPayerResponses.add(taxPayerDetDto);
			});

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("GSTIN's Parallel Stream Completed.");
				LOGGER.debug(msg);
			}

			gstinValidatorDetails = generateReport.getGstinDetailsReport(taxPayerResponses,
					requestId, isEinvApplicable);
			
			
		
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Generated Final Excel File with name"
								+ " %s in GstinValidFileArrival Handler",
								gstinValidatorDetails.getValue0());
				LOGGER.debug(msg);
			}

		} catch (Exception e) {
			LOGGER.error("Exception Occured in GstinValidatorFile :", e);
			String errMsg = e.getMessage();
			gstinValidRepo.updateGstinValidatorDetails(
					GstnValidatorConstants.FAILED_STATUS, null,
					LocalDateTime.now(), requestId,
					errMsg.substring(0, Math.min(errMsg.length(), 100)),null);
		}

		return gstinValidatorDetails;
	}
}
