package com.ey.advisory.app.approvalWorkflow;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApprovalWkflwPdfUtility {/*
	public static final String APPR_WKFLWFILES = "ApprWkflwFiles";

	@Autowired
	@Qualifier("Gstr1SummaryPDFGenerationReportImpl")
	Gstr1SummaryPDFGenerationReport gstr1SummaryPDFGenerationReport;

	@Autowired
	@Qualifier("Gstr1SimpleDocGstnSummarySearchService")
	Gstr1SimpleDocGstnSummarySearchService tableSearchService;

	@Autowired
	@Qualifier("Gstr1SummaryMultiPDFService")
	Gstr1SummaryMultiPDFService gstr1SummaryMultiPDFService;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	private ApprovalMakerRequestRepository apprRepo;

	public String uploadGSTR1SummPdf(
			Annexure1SummaryReqDto annexure1SummaryRequest, Long requestId) {

		File tempDir = Files.createTempDir();
		String uploadedDocName =  null;
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));

		LOGGER.debug("GSTN Data Summary Execution BEGIN ");
		SearchResult<Gstr1CompleteSummaryDto> gstnSumryResult = (SearchResult<Gstr1CompleteSummaryDto>) tableSearchService
				.find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);
		LOGGER.debug("OUTWARD GSTN Data Summary Execution END ");

		List<? extends Gstr1CompleteSummaryDto> gstnResult = gstnSumryResult
				.getResult();

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(annexure1SummaryRequest);

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}

		String sgstin = gstinList.get(0);
		String taxPeriod = req.getTaxPeriod();
		String filePath = tempDir.getAbsolutePath() + "/" + "GSTR1" + "_"
				+ sgstin + "_" + taxPeriod + ".pdf";
		
		LOGGER.debug("File Path {} ", filePath); 
		JasperPrint jasperPrint = gstr1SummaryPDFGenerationReport
				.generatePdfGstr1Report(req, gstnResult);
		try {
			JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
			 uploadedDocName = DocumentUtility
					.uploadZipFile(new File(filePath), APPR_WKFLWFILES);
			apprRepo.updateSnapShotPdf(requestId, uploadedDocName);
		} catch (Exception ex) {
			LOGGER.error("Exception while generating Gstr1SummaryReport PDF ",
					ex);
		} finally {
			GenUtil.deleteTempDir(new File(tempDir.getAbsolutePath()));
		}
		return uploadedDocName;
	}

	public File downloadPdf(String filePath) throws Exception {
		File downloadDir = null;
		File tempDir = null;
		File destFile = null;
		Document document = null;
		try {
			tempDir = MergeFilesUtil.createTempDir();
			LOGGER.debug("FileName {}", filePath);
			downloadDir = QRCommonUtility.createDownloadDir(tempDir);
			destFile = new File(filePath);
			LOGGER.debug("DestiFile {}", destFile);
			document = DocumentUtility.downloadDocument(filePath,
					APPR_WKFLWFILES);
			if (document == null) {
				String msg = String.format(
						"Not Able to Download the Document for FileName %s in Folder %s",
						filePath, APPR_WKFLWFILES);
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			FileUtils.copyInputStreamToFile(
					document.getContentStream().getStream(), destFile);
			return destFile;
		} catch (Exception e) {
			String msg = String.format(
					"Error in Populating the PDF for File %s", filePath);
			LOGGER.error(msg, e);
			throw new AppException(msg);
		} finally {
			GenUtil.deleteTempDir(tempDir);
			GenUtil.deleteTempDir(downloadDir);
		}

	}

	public static String uploadFile(MultipartFile inputfile, File tempDir,
			String extension) {
		try {
			String tempFileName = tempDir.getAbsolutePath() + File.separator
					+ inputfile.getOriginalFilename();

			File tempFile = new File(tempFileName);
			inputfile.transferTo(tempFile);

			LOGGER.debug("Transferred Successfully");
			String uploadedDocName = DocumentUtility.uploadZipFile(tempFile,
					APPR_WKFLWFILES);
			return uploadedDocName;
		} catch (Exception e) {
			String msg = String.format(
					"Error While Upload the File to DocRepo %s",
					inputfile.getOriginalFilename());
			LOGGER.error(msg);
			throw new AppException(msg);
		}
	}

*/}