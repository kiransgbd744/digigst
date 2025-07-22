/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.entities.client.CewbEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.CewbRepository;
import com.ey.advisory.app.data.repositories.client.OutwardTransDocumentRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author Sujith.Nanga
 *
 */

@Component("CEWBPDFGenerationReportImpl")
@Slf4j
public class CEWBPDFGenerationReportImpl implements CEWBPDFReport {

	private static final String AMOUNT_STRING = "0.00";

	@Autowired
	CEWBQRCodeGenerator cEWBQRCodeGenerator;

	@Autowired
	private OutwardTransDocumentRepository outwardTransDocumentRepo;

	@Autowired
	private CewbRepository cewbRepository;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	/*DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy hh:mm:ss a");*/
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy hh:mm:ss a");

	static DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	private static final String SLASH = "/";

	@Override
	public JasperPrint generateEinvoiceSummaryPdfReport(String cewb) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<>();
		String source = "jasperReports/CEWBPrint.jrxml";
		try {
			List<CewbEntity> consoliDated = cewbRepository
					.findByConsolidatedEWB(cewb);
			if (consoliDated.isEmpty()) {
				String errMsg = String.format(
						"Invoice %s is not available in the system", cewb);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			List<Long> ewbnumlist = new ArrayList();

			for (CewbEntity dto : consoliDated) {
				ewbnumlist.add(Long.parseLong(dto.getEwbNo()));
			}
			CewbEntity cons = consoliDated.get(0);

			List<OutwardTransDocument> findByEwayBillNo = outwardTransDocumentRepo
					.findByEwayBillNo(ewbnumlist);
			if (findByEwayBillNo != null && !findByEwayBillNo.isEmpty()) {
				OutwardTransDocument out = findByEwayBillNo.get(0);

				parameters.put("transporterID", out.getTransporterID() != null
						? out.getTransporterID() : "");

			}
			parameters.put("cewbNo", cons.getConsolidatedEwbNum() != null
					? cons.getConsolidatedEwbNum() : "");
			parameters
					.put("cewbDate",
							cons.getConsolidatedEwbDate() != null
									? formatter2.format(
											EYDateUtil.toISTDateTimeFromUTC(
													cons.getConsolidatedEwbDate()))
									: "");

			parameters.put("vehicleNo",
					cons.getVehicleNo() != null ? cons.getVehicleNo() : "");

			parameters.put("fromPlace",
					cons.getFromPlace() != null ? cons.getFromPlace() : "");

			LocalDate currentDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());

			parameters.put("currentDate",
					currentDate != null ? formatter2.format(currentDate) : "");

			parameters.put("mode",
					cons.getTransMode() != null ? cons.getTransMode() : "");

			List<CEWBItemDetails> cewbItemDetails = new ArrayList<>();
			int slno = 1;
			for (OutwardTransDocument line : findByEwayBillNo) {
				CEWBItemDetails productDetails = new CEWBItemDetails();
				productDetails.setSno(String.valueOf(slno));

				productDetails
						.setEwayBillNoAndDate(
								line.getEwbNoresp() != null
										? line.getEwbNoresp() + "-"
												+ formatter2.format(
														line.getEwbDateResp())
										: "");

				productDetails.setGeneratedBy(
						line.getTransactionType().equalsIgnoreCase("o")
								? line.getSgstin() : line.getCgstin());
				productDetails
						.setDocNoAndDate(
								line.getDocNo() != null
										? line.getDocNo() + "-"
												+ formatter2.format(
														line.getDocDate())
										: "");

				productDetails.setValue(line.getDocAmount() != null
						? String.valueOf(line.getDocAmount()) : "");

				String Shiptostatename = "";
				if (line.getShipToState() != null) {
					Shiptostatename = statecodeRepository
							.findStateNameByCode(line.getShipToState());
				}
				String custstateName = "";
				if (line.getBillToState() != null) {
					custstateName = statecodeRepository
							.findStateNameByCode(line.getBillToState());
				}
				if (Strings.isNullOrEmpty(Shiptostatename)) {
					Shiptostatename = custstateName;
				}

				String shiptoStatepin = (line.getShipToPincode() != null
						? line.getShipToPincode().toString() : "");

				String custstate = (line.getCustomerPincode() != null
						? line.getCustomerPincode().toString() : "");

				if (Strings.isNullOrEmpty(shiptoStatepin)) {
					shiptoStatepin = custstate;
				}

				productDetails
						.setToAddress(Shiptostatename + "-" + shiptoStatepin);

				cewbItemDetails.add(productDetails);
				slno++;
			}
			parameters.put("itemDataSource",
					new JRBeanCollectionDataSource(cewbItemDetails));

			String ewbnum = "";
			ewbnum = cons.getConsolidatedEwbNum() + SLASH + cons.getGstin()
					+ SLASH + FORMATTER.format(EYDateUtil.toISTDateTimeFromUTC(
							cons.getConsolidatedEwbDate()));
			parameters.put("Image", cEWBQRCodeGenerator.getImage(ewbnum));

			File file = ResourceUtils.getFile("classpath:" + source);

			JasperReport jasperReport = JasperCompileManager
					.compileReport(file.toString());
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
					new JREmptyDataSource());
		} catch (Exception ex) {
			LOGGER.error("Exception occured while genearting pdf..", ex);
		}

		return jasperPrint;

	}
}
