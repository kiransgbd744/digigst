/**
 * 
 */
package com.ey.advisory.app.data.services.pdf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Balakrishna.S
 *
 */
@Slf4j
@Component("Gstr6MultiplePdfZipReportService")
public class Gstr6MultiplePdfZipReportService {

	private static final String ISD = "ISD";
	@Autowired
	@Qualifier("BasicGstr6SecCommonParam")
	BasicGstr6SecCommonParam basicGstr6SecCommonParam;

	@Autowired
	@Qualifier("Gstr6AspSummaryPDFGenerationReportImpl")
	private Gstr6AspSummaryPDFGenerationReportImpl gstr6ReportImpl;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	public String generateGstr6PdfZip(File tempDir,
			Gstr2AProcessedRecordsReqDto reqDto) throws Exception {

		Gstr2AProcessedRecordsReqDto request = setDataSecuritySearchParams(
				reqDto);

		// String sgstin = null;
		List<String> gstinList = null;

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		String retunPeriod = reqDto.getRetunPeriod();
		boolean isDigigst = reqDto.getIsDigigst();

		for (String gstn : gstinList) {
			String fileName = null;
			
			if(isDigigst){
				 fileName=String.format("GSTR6_%s_%s_DigiGST", gstn, retunPeriod);
			 }
			 else{
				 fileName=String.format("GSTR6_%s_%s_GSTN", gstn, retunPeriod);
			 }
			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ fileName + ".pdf";
			try (OutputStream outStream = new BufferedOutputStream(
					new FileOutputStream(fullPath), 8192)) {

				JasperPrint jasperPrint = gstr6ReportImpl
						.generatePdfGstr6Report(request, gstn);

				JasperExportManager.exportReportToPdfStream(jasperPrint,
						outStream);

				outStream.flush();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String zipFileName = zipEinvoicePdfFiles(tempDir);
		return zipFileName;
	}

	private String zipEinvoicePdfFiles(File tempDir) throws Exception {

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);
		String fileName = "Gstr6PdfReports";
		String compressedFileName = fileName;

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("compressedFileName : %s",
					compressedFileName);
			LOGGER.debug(msg);
		}

		CombineAndZipXlsxFiles.compressFiles(tempDir.getAbsolutePath(),
				compressedFileName + ".zip", filesToZip);
		String zipFileName = compressedFileName + ".zip";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("zipFileName : %s", zipFileName);
			LOGGER.debug(msg);
		}
		return zipFileName;

	}

	private static List<String> getAllFilesToBeZipped(File tmpDir)
			throws Exception {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Temporary directory containing files to "
							+ "be zipped: %s", tmpDir.getAbsolutePath());
			LOGGER.debug(msg);
		}
		FilenameFilter pdfFilter = new FilenameFilter() {
			public boolean accept(File tmpDir, String name) {
				return name.toLowerCase().endsWith(".pdf");
			}
		};
		File[] files = tmpDir.listFiles(pdfFilter);
		List<String> retFileNames = Arrays.stream(files)
				.map(f -> f.getAbsolutePath())
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("List of files to be zipped %s",
					retFileNames);
			LOGGER.debug(msg);
		}
		// Return the list of files.
		return retFileNames;
	}

	// ----------------------

	public Gstr2AProcessedRecordsReqDto setDataSecuritySearchParams(
			Gstr2AProcessedRecordsReqDto searchParams) {

		List<Long> entityIds = searchParams.getEntityId();
		Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();

		Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		dataSecurityAttrMap = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						inwardSecurityAttributeMap);
		
		List<String> ttlGstinList = dataSecurityAttrMap.get(OnboardingConstant.GSTIN);
		List<String> regTypeList = Arrays.asList(ISD);
		ttlGstinList = gstNDetailRepository
				.filterGstinBasedByRegType(ttlGstinList, regTypeList);
		dataSecurityAttrMap.put(OnboardingConstant.GSTIN, ttlGstinList);

		if (searchParams.getDataSecAttrs() == null
				|| searchParams.getDataSecAttrs().isEmpty()) {
			searchParams.setDataSecAttrs(dataSecurityAttrMap);
		} else {
			Map<String, List<String>> dataSecReqMap = searchParams
					.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);
			List<String> divList = dataSecReqMap
					.get(OnboardingConstant.DIVISION);
			List<String> poList = dataSecReqMap.get(OnboardingConstant.PO);
			List<String> locList = dataSecReqMap
					.get(OnboardingConstant.LOCATION);
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
			if ((gstinList == null || gstinList.isEmpty())
					&& (pcList == null || pcList.isEmpty())
					&& (plantList == null || plantList.isEmpty())
					&& (divList == null || divList.isEmpty())
					&& (poList == null || poList.isEmpty())
					&& (locList == null || locList.isEmpty())
					&& (ud1List == null || ud1List.isEmpty())
					&& (ud2List == null || ud2List.isEmpty())
					&& (ud3List == null || ud3List.isEmpty())
					&& (ud4List == null || ud4List.isEmpty())
					&& (ud5List == null || ud5List.isEmpty())
					&& (ud6List == null || ud6List.isEmpty())) {
				searchParams.setDataSecAttrs(dataSecurityAttrMap);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.GSTIN,
							gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.PLANT,
							plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.DIVISION,
							divList);
				}

				if ((poList != null && !poList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.PO, poList);
				}

				if ((locList != null && !locList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.LOCATION,
							locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD6, ud6List);
				}

				searchParams.setDataSecAttrs(dataSecurityAttrMap);
			}
		}
		return searchParams;
	}

}
