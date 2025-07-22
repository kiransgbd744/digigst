/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.io.File;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.gstr3b.dto.Gstr3BInvoice;
import com.ey.advisory.app.gstr3b.dto.InterSupDetailsInvoiceGstr3B;
import com.ey.advisory.app.gstr3b.dto.InterestLateFeeDetailsInvoice;
import com.ey.advisory.app.gstr3b.dto.InterestandLatefeeDetails;
import com.ey.advisory.app.gstr3b.dto.InwardSupDetailsGstr3B;
import com.ey.advisory.app.gstr3b.dto.InwardSupDetailsInvoiceGstr3B;
import com.ey.advisory.app.gstr3b.dto.ItcDetailsGstr3B;
import com.ey.advisory.app.gstr3b.dto.ItcEligibleInvoiceGstr3B;
import com.ey.advisory.app.gstr3b.dto.PaidCashDetails;
import com.ey.advisory.app.gstr3b.dto.PaidItcDetails;
import com.ey.advisory.app.gstr3b.dto.SupDetailsGstr3B;
import com.ey.advisory.app.gstr3b.dto.SupDetailsInvoiceGstr3B;
import com.ey.advisory.app.gstr3b.dto.TaxPayableDetails;
import com.ey.advisory.app.gstr3b.dto.TaxPaymentDetailsInvoice;
import com.ey.advisory.app.gstr3b.dto.UnregCompUinDetailsGstr3B;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.CompressAndZipXlsxFiles;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 */
@Slf4j
@Component("Gstr3bSaveSubmitReportServiceImpl")
public class Gstr3bSaveSubmitReportServiceImpl
		implements Gstr3bSaveSubmitReportService {

	@Autowired
	Gstr3bUtil gstr3bUtil;

	@Autowired
	CommonUtility commonUtility;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	CompressAndZipXlsxFiles compressAndZipXlsxFiles;

	final static List<String> sectionList = ImmutableList.of(
			Gstr3BConstants.Table3_1, Gstr3BConstants.Table3_2,
			Gstr3BConstants.Table4, Gstr3BConstants.Table4A,
			Gstr3BConstants.Table4B, Gstr3BConstants.Table4D,
			Gstr3BConstants.Table5, Gstr3BConstants.Table5_1,
			Gstr3BConstants.Table5A, Gstr3BConstants.Table5B);

	@Override
	// public Workbook getGstr3bSaveReportData(String taxPeriod, String gstin) {
	public void generateSaveSubmitReports(Long id) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
				.findById(id);

		FileStatusDownloadReportEntity entity = optEntity.get();

		String gstins = GenUtil.convertClobtoString(entity.getGstins());
		List<String> gstinList = Arrays
				.asList(gstins.replaceAll("'", "").split(","));
		String taxPeriod = entity.getTaxPeriod();

		File tempDir = null;
		try {
			tempDir = Files.createTempDirectory("DownloadReports").toFile();

			String fullPath = tempDir.getAbsolutePath() + File.separator;

			for (String gstin : gstinList) {
				Workbook workbook = null;

				GstnUserRequestEntity gstnUserRequestEntity = new GstnUserRequestEntity();
				gstnUserRequestEntity = gstnUserRequestRepo
						.findByGstinAndTaxPeriodAndReturnType(gstin, taxPeriod,
								"GSTR3B");
				if (gstnUserRequestEntity == null) {
					continue;
				}
				Clob clob = gstnUserRequestEntity.getGetResponsePayload();

				if (clob != null) {
					StringBuffer buffer = new StringBuffer();
					Reader r;
					try {
						r = clob.getCharacterStream();
						int ch;
						while ((ch = r.read()) != -1) {
							buffer.append("" + (char) ch);
						}

						List<Gstr3bGstinLevelReportDto> gstr3bList = new ArrayList<>();
						List<Gstr3bTaxPaymentReportDto> taxPayment = new ArrayList<>();

						Gstr3BInvoice gstr3b = gson.fromJson(buffer.toString(),
								Gstr3BInvoice.class);

						SupDetailsInvoiceGstr3B supDetails = gstr3b
								.getSupDetails();

						if (supDetails != null) {
							if (supDetails.getOsupDet() != null)
								gstr3bList.add(supDetails(
										supDetails.getOsupDet(), "osup_det"));

							if (supDetails.getOsupNilExmp() != null)
								gstr3bList.add(
										supDetails(supDetails.getOsupNilExmp(),
												"osup_nil_exmp"));

							if (supDetails.getOsupZero() != null)
								gstr3bList.add(supDetails(
										supDetails.getOsupZero(), "osup_zero"));

							if (supDetails.getIsupRev() != null)
								gstr3bList.add(supDetails(
										supDetails.getIsupRev(), "isup_rev"));

							if (supDetails.getOsupNongst() != null)
								gstr3bList.add(
										supDetails(supDetails.getOsupNongst(),
												"osup_nongst"));
						}

						InterSupDetailsInvoiceGstr3B interSup = gstr3b
								.getInterSup();

						if (interSup != null) {
							if (interSup.getUnregDetails() != null)
								gstr3bList.addAll(interSupDetails(
										interSup.getUnregDetails(),
										"unreg_details"));

							if (interSup.getUinDetails() != null)
								gstr3bList.addAll(interSupDetails(
										interSup.getUinDetails(),
										"uin_details"));

							if (interSup.getCompDetails() != null)
								gstr3bList.addAll(interSupDetails(
										interSup.getCompDetails(),
										"comp_details"));
						}

						ItcEligibleInvoiceGstr3B itcElg = gstr3b.getItcElg();
						if (itcElg != null) {
							if (itcElg.getItcAvl() != null)
								gstr3bList.addAll(itcEligible(
										itcElg.getItcAvl(), "itc_avl"));

							if (itcElg.getItcRev() != null)
								gstr3bList.addAll(itcEligible(
										itcElg.getItcRev(), "itc_rev"));

							if (itcElg.getItcInelg() != null)
								gstr3bList.addAll(itcEligible(
										itcElg.getItcInelg(), "itc_inelg"));

							if (itcElg.getItcNet() != null)
								gstr3bList.addAll(itcEligible(
										Arrays.asList(itcElg.getItcNet()),
										"itc_net"));
						}

						InwardSupDetailsInvoiceGstr3B inwardSup = gstr3b
								.getInwardSup();
						if (inwardSup != null) {
							if (inwardSup.getIsupDetails() != null)
								gstr3bList.addAll(InwardSupplies(
										inwardSup.getIsupDetails()));
						}

						InterestLateFeeDetailsInvoice intrLtfee = gstr3b
								.getIntrLtfee();

						if (intrLtfee != null) {
							if (intrLtfee.getInterestDetails() != null)
								gstr3bList.add(interestLateFee(
										intrLtfee.getInterestDetails(),
										"intr_details"));

							if (intrLtfee.getLatetfeeDetails() != null)
								gstr3bList.add(interestLateFee(
										intrLtfee.getLatetfeeDetails(),
										"ltfee_details"));
						}

						TaxPaymentDetailsInvoice taxPmt = gstr3b.getTxPmt();
						if (taxPmt != null) {
							taxPayment = TaxPayment(taxPmt.getTaxPayable(),
									taxPmt.getPaidCash(), taxPmt.getPaidItc());
						}

						writeToExcel(gstr3bList, taxPayment, fullPath, gstin);

					} catch (Exception e) {
						fileStatusDownloadReportRepo.updateStatus(id,
								ReportStatusConstants.REPORT_GENERATION_FAILED,
								null, EYDateUtil.toUTCDateTimeFromLocal(
										LocalDateTime.now()));
						String msg = "Exception occured while generating GSTR3B "
								+ "save subit report";
						LOGGER.error(msg);
						throw new AppException(msg, e);
					}
				}
			}

			String zipFileName = "";
			if (tempDir.list().length > 0) {
				zipFileName = compressAndZipXlsxFiles.zipfolder(1L, tempDir);

				File zipFile = new File(tempDir, zipFileName);

				Pair<String, String> uploadedZipName = DocumentUtility
						.uploadFile(zipFile, "Anx1FileStatusReport");
				String uploadedDocName = uploadedZipName.getValue0();
				String docId = uploadedZipName.getValue1();
				if (LOGGER.isDebugEnabled()) {
					String msg = "Sucessfully uploaded zip file and updating the "
							+ "status as 'Report Generated'";
					LOGGER.debug(msg);
				}
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						docId);

			} else {
				LOGGER.error("No Data found for report id : %d", id);
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			}
			deleteTemporaryDirectory(tempDir);

		} catch (Exception ex) {
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			String msg = "Exception occured in GSTR3B Save submit "
					+ "while generating report ";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
		// return workbook;
	}

	private List<Gstr3bGstinLevelReportDto> InwardSupplies(
			List<InwardSupDetailsGstr3B> isupDetails) {

		List<Gstr3bGstinLevelReportDto> list = new ArrayList<>();
		for (InwardSupDetailsGstr3B obj : isupDetails) {
			Gstr3bGstinLevelReportDto dto1 = new Gstr3bGstinLevelReportDto();
			Gstr3bGstinLevelReportDto dto2 = new Gstr3bGstinLevelReportDto();

			if ("GST".equalsIgnoreCase(obj.getTy())) {
				dto1.setTableSection(Gstr3BConstants.Table5A1);
				Pair<String, String> table = CommonUtility
						.getGstr3bHeadingandDesc(Gstr3BConstants.Table5A1);
				dto1.setTableHeading(table.getValue0());
				dto1.setTableDesc(table.getValue1());
				dto1.setTotalTaxableVal(obj.getInter());
				list.add(dto1);

				dto2.setTableSection(Gstr3BConstants.Table5A2);
				Pair<String, String> table1 = CommonUtility
						.getGstr3bHeadingandDesc(Gstr3BConstants.Table5A2);
				dto2.setTableHeading(table1.getValue0());
				dto2.setTableDesc(table1.getValue1());
				dto2.setTotalTaxableVal(obj.getIntra());
				list.add(dto2);

			}
			if ("NONGST".equalsIgnoreCase(obj.getTy())) {
				dto1.setTableSection(Gstr3BConstants.Table5B1);
				Pair<String, String> table = CommonUtility
						.getGstr3bHeadingandDesc(Gstr3BConstants.Table5B1);
				dto1.setTableHeading(table.getValue0());
				dto1.setTableDesc(table.getValue1());
				dto1.setTotalTaxableVal(obj.getInter());
				list.add(dto1);

				dto2.setTableSection(Gstr3BConstants.Table5B2);
				Pair<String, String> table1 = CommonUtility
						.getGstr3bHeadingandDesc(Gstr3BConstants.Table5B2);
				dto2.setTableHeading(table1.getValue0());
				dto2.setTableDesc(table1.getValue1());
				dto2.setTotalTaxableVal(obj.getIntra());
				list.add(dto2);

			}

		}

		return list;
	}

	private List<Gstr3bTaxPaymentReportDto> TaxPayment(
			List<TaxPayableDetails> taxPayable, List<PaidCashDetails> paidCash,
			PaidItcDetails paidItc) {

		List<Gstr3bTaxPaymentReportDto> taxPaymentList = new ArrayList<>();

		Gstr3bTaxPaymentReportDto igstTaxPayment = new Gstr3bTaxPaymentReportDto();
		Gstr3bTaxPaymentReportDto cgstTaxPayment = new Gstr3bTaxPaymentReportDto();
		Gstr3bTaxPaymentReportDto sgstTaxPayment = new Gstr3bTaxPaymentReportDto();
		Gstr3bTaxPaymentReportDto cessTaxPayment = new Gstr3bTaxPaymentReportDto();

		igstTaxPayment.setName("IGST");
		cgstTaxPayment.setName("CGST");
		sgstTaxPayment.setName("SGST");
		cessTaxPayment.setName("Cess");

		for (TaxPayableDetails obj : taxPayable) {
			igstTaxPayment.setTaxPayable(
					igstTaxPayment.getTaxPayable().add(obj.getIgst().getTax()));
			cgstTaxPayment.setTaxPayable(
					cgstTaxPayment.getTaxPayable().add(obj.getCgst().getTax()));
			sgstTaxPayment.setTaxPayable(
					sgstTaxPayment.getTaxPayable().add(obj.getSgst().getTax()));
			cessTaxPayment.setTaxPayable(
					cessTaxPayment.getTaxPayable().add(obj.getCess().getTax()));
		}

		if (paidCash != null) {
			for (PaidCashDetails obj : paidCash) {
				igstTaxPayment.setPaidInCash(obj.getIgstPaid());
				cgstTaxPayment.setPaidInCash(obj.getCgstPaid());
				sgstTaxPayment.setPaidInCash(obj.getSgstPaid());
				cessTaxPayment.setPaidInCash(obj.getCessPaid());

				igstTaxPayment.setInterest(obj.getIgstIntPaid());
				cgstTaxPayment.setInterest(obj.getCgstIntPaid());
				sgstTaxPayment.setInterest(obj.getSgstIntPaid());
				cessTaxPayment.setInterest(obj.getCessIntPaid());

				igstTaxPayment.setLateFee(obj.getIgstLateFeePaid());
				cgstTaxPayment.setLateFee(obj.getCgstLateFeePaid());
				sgstTaxPayment.setLateFee(obj.getSgstLateFeePaid());
				cessTaxPayment.setLateFee(obj.getCessLateFeePaid());

			}
		} else {
			igstTaxPayment.setPaidInCash(BigDecimal.ZERO);
			cgstTaxPayment.setPaidInCash(BigDecimal.ZERO);
			sgstTaxPayment.setPaidInCash(BigDecimal.ZERO);
			cessTaxPayment.setPaidInCash(BigDecimal.ZERO);

			igstTaxPayment.setInterest(BigDecimal.ZERO);
			cgstTaxPayment.setInterest(BigDecimal.ZERO);
			sgstTaxPayment.setInterest(BigDecimal.ZERO);
			cessTaxPayment.setInterest(BigDecimal.ZERO);

			igstTaxPayment.setLateFee(BigDecimal.ZERO);
			cgstTaxPayment.setLateFee(BigDecimal.ZERO);
			sgstTaxPayment.setLateFee(BigDecimal.ZERO);
			cessTaxPayment.setLateFee(BigDecimal.ZERO);

		}

		if (paidItc != null) {
			igstTaxPayment.setIgst(paidItc.getIGSTPaidUsingIGST());
			igstTaxPayment.setCgst(paidItc.getIGSTPaidUsingCGST());
			igstTaxPayment.setSgst((paidItc.getIGSTPaidUsingSGST()));
			cgstTaxPayment.setIgst(paidItc.getCGSTPaidUsingIGST());
			cgstTaxPayment.setCgst((paidItc.getCGSTPaidUsingCGST()));
			sgstTaxPayment.setIgst(paidItc.getSGSTPaidUsingIGST());
			sgstTaxPayment.setSgst(paidItc.getSGSTPaidUsingSGST());
			cessTaxPayment.setCess(paidItc.getCessPaidUsingCess());
		} else {
			igstTaxPayment.setIgst(BigDecimal.ZERO);
			igstTaxPayment.setCgst(BigDecimal.ZERO);
			igstTaxPayment.setSgst(BigDecimal.ZERO);
			cgstTaxPayment.setIgst(BigDecimal.ZERO);
			cgstTaxPayment.setCgst(BigDecimal.ZERO);
			sgstTaxPayment.setIgst(BigDecimal.ZERO);
			sgstTaxPayment.setSgst(BigDecimal.ZERO);
			cessTaxPayment.setCess(BigDecimal.ZERO);
		}

		taxPaymentList.add(igstTaxPayment);
		taxPaymentList.add(cgstTaxPayment);
		taxPaymentList.add(sgstTaxPayment);
		taxPaymentList.add(cessTaxPayment);

		return taxPaymentList;
	}

	private Gstr3bGstinLevelReportDto interestLateFee(
			InterestandLatefeeDetails intDets, String section) {

		Gstr3bGstinLevelReportDto dto = new Gstr3bGstinLevelReportDto();

		if (section.equalsIgnoreCase("intr_details")) {
			dto.setTableSection(Gstr3BConstants.Table5_1A);
			Pair<String, String> table = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table5_1A);
			dto.setTableHeading(table.getValue0());
			dto.setTableDesc(table.getValue1());

		} else if (section.equalsIgnoreCase("ltfee_details")) {
			dto.setTableSection(Gstr3BConstants.Table5_1B);
			Pair<String, String> table = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table5_1B);
			dto.setTableHeading(table.getValue0());
			dto.setTableDesc(table.getValue1());

		}

		dto.setIgst(intDets.getIgstAmount());
		dto.setCgst(intDets.getCgstAmount());
		dto.setSgst(intDets.getSgstAmount());
		dto.setCess(intDets.getCessAmount());

		return dto;
	}

	private List<Gstr3bGstinLevelReportDto> itcEligible(
			List<ItcDetailsGstr3B> itcAvl, String section) {

		List<Gstr3bGstinLevelReportDto> list = new ArrayList<>();

		for (ItcDetailsGstr3B obj : itcAvl) {
			Gstr3bGstinLevelReportDto dto = new Gstr3bGstinLevelReportDto();
			if (section.equalsIgnoreCase("itc_avl")) {
				if ("IMPG".equalsIgnoreCase(obj.getTy())) {
					dto.setTableSection(Gstr3BConstants.Table4A1);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A1);
					dto.setTableHeading(table.getValue0());
					dto.setTableDesc(table.getValue1());

				} else if ("IMPS".equalsIgnoreCase(obj.getTy())) {
					dto.setTableSection(Gstr3BConstants.Table4A2);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A2);
					dto.setTableHeading(table.getValue0());
					dto.setTableDesc(table.getValue1());

				} else if ("ISRC".equalsIgnoreCase(obj.getTy())) {
					dto.setTableSection(Gstr3BConstants.Table4A3);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A3);
					dto.setTableHeading(table.getValue0());
					dto.setTableDesc(table.getValue1());

				} else if ("ISD".equalsIgnoreCase(obj.getTy())) {
					dto.setTableSection(Gstr3BConstants.Table4A4);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A4);
					dto.setTableHeading(table.getValue0());
					dto.setTableDesc(table.getValue1());

				} else if ("OTH".equalsIgnoreCase(obj.getTy())) {
					dto.setTableSection(Gstr3BConstants.Table4A5);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A5);
					dto.setTableHeading(table.getValue0());
					dto.setTableDesc(table.getValue1());

				}
			} else if (section.equalsIgnoreCase("itc_rev")) {
				if ("RUL".equalsIgnoreCase(obj.getTy())) {
					dto.setTableSection(Gstr3BConstants.Table4B1);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4B1);
					dto.setTableHeading(table.getValue0());
					dto.setTableDesc(table.getValue1());

				} else if ("OTH".equalsIgnoreCase(obj.getTy())) {
					dto.setTableSection(Gstr3BConstants.Table4B2);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4B2);
					dto.setTableHeading(table.getValue0());
					dto.setTableDesc(table.getValue1());
				}
			} else if (section.equalsIgnoreCase("itc_net")) {
				dto.setTableSection(Gstr3BConstants.Table4C);
				Pair<String, String> table = CommonUtility
						.getGstr3bHeadingandDesc(Gstr3BConstants.Table4B2);
				dto.setTableHeading(table.getValue0());
				dto.setTableDesc(table.getValue1());
			} else if (section.equalsIgnoreCase("itc_inelg")) {
				if ("RUL".equalsIgnoreCase(obj.getTy())) {
					dto.setTableSection(Gstr3BConstants.Table4D1);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4D1);
					dto.setTableHeading(table.getValue0());
					dto.setTableDesc(table.getValue1());

				} else if ("OTH".equalsIgnoreCase(obj.getTy())) {
					dto.setTableSection(Gstr3BConstants.Table4D2);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4D2);
					dto.setTableHeading(table.getValue0());
					dto.setTableDesc(table.getValue1());
				}

			}
			dto.setTotalTaxableVal(BigDecimal.ZERO);
			dto.setIgst(dto.getIgst().add(obj.getIgstAmount()));
			dto.setCgst(dto.getCgst().add(obj.getCgstAmount()));
			dto.setSgst(dto.getSgst().add(obj.getSgstAmount()));
			dto.setCess(dto.getCess().add(obj.getCessAmount()));
			list.add(dto);
		}

		return list;

	}

	private List<Gstr3bGstinLevelReportDto> interSupDetails(
			List<UnregCompUinDetailsGstr3B> interDetails, String section) {

		List<Gstr3bGstinLevelReportDto> list = new ArrayList<>();
		List<Gstr3bGstinLevelReportDto> OrderedList = new ArrayList<>();
		if (interDetails != null) {

			for (UnregCompUinDetailsGstr3B obj : interDetails) {
				Gstr3bGstinLevelReportDto gstinDto = new Gstr3bGstinLevelReportDto();
				if (section.equalsIgnoreCase("unreg_details")) {
					gstinDto.setTableSection(Gstr3BConstants.Table3_2_A);
				} else if (section.equalsIgnoreCase("comp_details")) {
					gstinDto.setTableSection(Gstr3BConstants.Table3_2_B);
				} else if (section.equalsIgnoreCase("uin_details")) {
					gstinDto.setTableSection(Gstr3BConstants.Table3_2_C);
				}
				gstinDto.setTableDesc("POS" + "-" + obj.getPos());
				gstinDto.setTotalTaxableVal(gstinDto.getTotalTaxableVal()
						.add(obj.getTaxableValue()));
				gstinDto.setIgst(gstinDto.getIgst().add(obj.getIgstAmount()));
				gstinDto.setCgst(BigDecimal.ZERO);
				gstinDto.setSgst(BigDecimal.ZERO);
				gstinDto.setCess(BigDecimal.ZERO);
				list.add(gstinDto);
			}
			Gstr3bGstinLevelReportDto dto = new Gstr3bGstinLevelReportDto();

			if (section.equalsIgnoreCase("unreg_details")) {
				dto.setTableSection(Gstr3BConstants.Table3_2_A);
				Pair<String, String> table = CommonUtility
						.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_2_A);
				dto.setTableHeading(table.getValue0());
				dto.setTableDesc(table.getValue1());
				for (Gstr3bGstinLevelReportDto obj : list) {
					dto.setTotalTaxableVal(dto.getTotalTaxableVal()
							.add(obj.getTotalTaxableVal()));
					dto.setIgst(dto.getIgst().add(obj.getIgst()));
				}
				dto.setCgst(BigDecimal.ZERO);
				dto.setSgst(BigDecimal.ZERO);
				dto.setCess(BigDecimal.ZERO);
				OrderedList.add(dto);

			} else if (section.equalsIgnoreCase("comp_details")) {
				dto.setTableSection(Gstr3BConstants.Table3_2_B);
				Pair<String, String> table = CommonUtility
						.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_2_B);
				dto.setTableHeading(table.getValue0());
				dto.setTableDesc(table.getValue1());
				for (Gstr3bGstinLevelReportDto obj : list) {
					dto.setTotalTaxableVal(dto.getTotalTaxableVal()
							.add(obj.getTotalTaxableVal()));
					dto.setIgst(dto.getIgst().add(obj.getIgst()));
				}
				dto.setCgst(BigDecimal.ZERO);
				dto.setSgst(BigDecimal.ZERO);
				dto.setCess(BigDecimal.ZERO);
				OrderedList.add(dto);

			} else if (section.equalsIgnoreCase("uin_details")) {
				dto.setTableSection(Gstr3BConstants.Table3_2_C);
				Pair<String, String> table = CommonUtility
						.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_2_C);
				dto.setTableHeading(table.getValue0());
				dto.setTableDesc(table.getValue1());
				for (Gstr3bGstinLevelReportDto obj : list) {
					dto.setTotalTaxableVal(dto.getTotalTaxableVal()
							.add(obj.getTotalTaxableVal()));
					dto.setIgst(dto.getIgst().add(obj.getIgst()));
				}
				dto.setCgst(BigDecimal.ZERO);
				dto.setSgst(BigDecimal.ZERO);
				dto.setCess(BigDecimal.ZERO);
				OrderedList.add(dto);

			}
		}
		OrderedList.addAll(list);

		return OrderedList;
	}

	private Gstr3bGstinLevelReportDto supDetails(SupDetailsGstr3B supDet,
			String section) {

		Gstr3bGstinLevelReportDto dto = new Gstr3bGstinLevelReportDto();

		if (section.equalsIgnoreCase("osup_det")) {
			dto.setTableSection(Gstr3BConstants.Table3_1_A);
			Pair<String, String> table = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_1_A);
			dto.setTableHeading(table.getValue0());
			dto.setTableDesc(table.getValue1());

		} else if (section.equalsIgnoreCase("osup_zero")) {
			dto.setTableSection(Gstr3BConstants.Table3_1_B);
			Pair<String, String> table = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_1_B);
			dto.setTableHeading(table.getValue0());
			dto.setTableDesc(table.getValue1());

		} else if (section.equalsIgnoreCase("osup_nil_exmp")) {
			dto.setTableSection(Gstr3BConstants.Table3_1_C);
			Pair<String, String> table = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_1_C);
			dto.setTableHeading(table.getValue0());
			dto.setTableDesc(table.getValue1());

		} else if (section.equalsIgnoreCase("isup_rev")) {
			dto.setTableSection(Gstr3BConstants.Table3_1_D);
			Pair<String, String> table = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_1_D);
			dto.setTableHeading(table.getValue0());
			dto.setTableDesc(table.getValue1());

		} else if (section.equalsIgnoreCase("osup_nongst")) {
			dto.setTableSection(Gstr3BConstants.Table3_1_E);
			Pair<String, String> table = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_1_E);
			dto.setTableHeading(table.getValue0());
			dto.setTableDesc(table.getValue1());

		}
		dto.setTotalTaxableVal(supDet.getTaxableValue());
		dto.setIgst(supDet.getIgstAmount());
		dto.setCgst(supDet.getCgstAmount());
		dto.setSgst(supDet.getSgstAmount());
		dto.setCess(supDet.getCessAmount());

		return dto;
	}

	private void writeToExcel(List<Gstr3bGstinLevelReportDto> gstr3bRespList,
			List<Gstr3bTaxPaymentReportDto> taxPayment, String fullPath,
			String gstin) {
		Workbook workbook = null;
		int startRow = 5;
		int startcolumn = 2;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Number of records for gstr3b save submit  "
					+ gstr3bRespList.size();
			LOGGER.debug(msg);
		}
		try {

			if (gstr3bRespList != null && !gstr3bRespList.isEmpty()) {

				addMissingDto(gstr3bRespList);

				String[] invoiceHeaders = commonUtility
						.getProp("gstr3b.gstin.report.header").split(",");

				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "Gstr3bGstinReport.xlsx");
				if (LOGGER.isDebugEnabled()) {
					String msg = "workbook created writing data to the workbook";
					LOGGER.debug(msg);
				}
				DateTimeFormatter dtf = DateTimeFormatter
						.ofPattern("yyyyMMddHHmmss");
				String timeMilli = dtf.format(LocalDateTime.now());
				// workbook.setFileName("Gstr3bSaveSubmit_"+timeMilli);
				Cells reportCells = workbook.getWorksheets().get(0).getCells();

				List<Gstr3bGstinLevelReportDto> sortList = gstr3bRespList
						.stream()
						.sorted((o1, o2) -> o1.getTableSection()
								.compareTo(o2.getTableSection()))
						.collect(Collectors.toList());

				reportCells.importCustomObjects(sortList, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						sortList.size(), true, "yyyy-mm-dd", false);

				if (LOGGER.isDebugEnabled()) {
					String msg = "Data written to GSTR3B gstin details";
					LOGGER.debug(msg);
				}

				Cells taxReportCells = workbook.getWorksheets().get(1)
						.getCells();

				String[] InvoiceHeadersTax = commonUtility
						.getProp("gstr3b.gstin.taxpayment.report.header")
						.split(",");

				taxReportCells.importCustomObjects(taxPayment,
						InvoiceHeadersTax, isHeaderRequired, 2, 0,
						taxPayment.size(), true, "yyyy-mm-dd", false);
				if (LOGGER.isDebugEnabled()) {
					String msg = "Data written to GSTR3B Tax Payment details";
					LOGGER.debug(msg);
				}

				if (LOGGER.isDebugEnabled()) {
					String msg = "About to save GSTR3B save submit report ";
					LOGGER.debug(msg);
				}
				workbook.save(
						fullPath + ConfigConstants.GSTR3B_SAVE_SUBMIT_REPORT
								+ "_" + gstin + "_" + timeMilli + ".xlsx",
						SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " in the directory : %s",
							workbook.getAbsolutePath());
				}

			} else {
				throw new AppException(
						"No records found, cannot generate report");
			}
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while "
							+ "saving excel sheet into folder, %s ",
					e.getMessage());
			LOGGER.error(msg);
			throw new AppException(e.getMessage(), e);
		}
	}

	private void deleteTemporaryDirectory(File tempFile) {

		if (tempFile != null && tempFile.exists()) {
			try {
				FileUtils.deleteDirectory(tempFile);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Deleted the Temp directory/Folder '%s'",
							tempFile.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format(
						"Failed to remove the temp "
								+ "directory created for zip: '%s'. This will "
								+ "lead to clogging of disk space.",
						tempFile.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}

	}

	private void addMissingDto(List<Gstr3bGstinLevelReportDto> v) {
		Set<String> tableSectionSet = commonUtility.Gstr3bTableHeadingAndDescription
				.keySet();
		List<String> respTableSecList = v.stream().map(o -> o.getTableSection())
				.collect(Collectors.toList());
		tableSectionSet.forEach(o -> {
			if (!respTableSecList.contains(o)) {
				v.add(getTable3MandatoryData(o));
			}
		});
	}

	private Gstr3bGstinLevelReportDto getTable3MandatoryData(String tableSec) {

		Gstr3bGstinLevelReportDto obj = new Gstr3bGstinLevelReportDto();
		obj.setTableSection(tableSec);
		Pair<String, String> table = CommonUtility
				.getGstr3bHeadingandDesc(tableSec);
		obj.setTableHeading(table.getValue0());
		obj.setTableDesc(table.getValue1());
		obj.setIgst(BigDecimal.ZERO);
		obj.setCgst(BigDecimal.ZERO);
		obj.setSgst(BigDecimal.ZERO);
		obj.setCess(BigDecimal.ZERO);
		obj.setTotalTaxableVal(BigDecimal.ZERO);

		return obj;

	}

	private BigDecimal checkForNull(BigDecimal val) {
		if (val == null)
			val = BigDecimal.ZERO;

		return val;

	}

}
