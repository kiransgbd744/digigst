package com.ey.advisory.controller.gstr2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2APRReportTypeEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRReportTypeEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2APRReportTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRReportTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.core.dto.Gstr2RequesIdWiseDownloadTabDto;
import com.google.common.collect.ImmutableList;

@Component("Gstr2RequestIdWiseSummaryTabServiceImpl")
public class Gstr2RequestIdWiseSummaryTabServiceImpl
		implements Gstr2RequestIdWiseSummaryTabService {

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository recon2APRConfigRepo;

	@Autowired
	@Qualifier("Gstr2Recon2BPRAddlReportsRepository")
	Gstr2Recon2BPRAddlReportsRepository recon2BPRConfigRepo;

	@Autowired
	@Qualifier("Gstr2Recon2BPRReportTypeRepository")
	Gstr2Recon2BPRReportTypeRepository recon2BPRChildConfigRepo;

	@Autowired
	@Qualifier("Gstr2Recon2APRReportTypeRepository")
	Gstr2Recon2APRReportTypeRepository recon2APRChildConfigRepo;

	private static final String NON_AP_M_2APR = "NON_AP_M_2APR";
	private static final String AP_M_2APR = "AP_M_2APR";
	private static final String AUTO_2APR = "AUTO_2APR";
	private int counter = 1;

	List<String> summaryReportsList = ImmutableList.of(
			"Summary_CalendarPeriod_Records", "Summary_TaxPeriod_Record",
			"Supplier_GSTIN_Summary_Records", "Supplier_PAN_Summary_Records",
			"Recipient_GSTIN_Period_Wise_Record",
			"Recipient_GSTIN_Wise_Records", "Vendor_GSTIN_Period_Wise_Records",
			"Vendor_GSTIN_Wise_Records", "Vendor_PAN_Period_Wise_Records",
			"Vendor_PAN_Wise_Records", "CRD-INV_Ref_Reg_GSTR_2B_Records",
			"CRD-INV_Ref_Reg_PR_Records", "GSTR_2B_Time_Stamp_Report",
			"GSTR_2A_Time_Stamp_Report", "CRD-INV_Ref_Reg_GSTR_2A_Records","Recipient_GSTIN_Period_Wise_Record_II");

	@Override
	public List<Gstr2RequesIdWiseDownloadTabDto> getSummaryDownloadData(
			Long configId, String reconType) {

		List<Gstr2RequesIdWiseDownloadTabDto> finalList = new ArrayList<>();

		if (reconType != null && reconType.equalsIgnoreCase("2BPR")) {

			List<Gstr2Recon2BPRAddlReportsEntity> entity = recon2BPRConfigRepo
					.getDataList(configId);

			List<Long> idList = new ArrayList<>();

			for (Gstr2Recon2BPRAddlReportsEntity entity1 : entity) {
				idList.add(entity1.getId());
			}

			List<Gstr2Recon2BPRReportTypeEntity> childList = recon2BPRChildConfigRepo
					.findByReportDwnldIdIn(idList);

			for (Gstr2Recon2BPRAddlReportsEntity entity1 : entity) {
				counter = 1;
				if (!summaryReportsList.contains(entity1.getReportType()))
					continue;
				// for saving at parent level =l1
				Gstr2RequesIdWiseDownloadTabDto respDto = new Gstr2RequesIdWiseDownloadTabDto();

				convert2BPR(entity1, false, null, respDto);

				// finding the child table records
				if (entity1.getFilePath() == null
						|| entity1.getFilePath().toString().isEmpty()) {

					String id = entity1.getId().toString();
					for (Gstr2Recon2BPRReportTypeEntity childEntity1 : childList) {
						if (id.equals(
								childEntity1.getReportDwnldId().toString())) {
							convert2BPRL2(childEntity1, respDto);
						}
					} // if there are no records

				} // 2APR case
				else
					convert2BPR(entity1, true, null, respDto);
				if (counter == 1) {
					convert2BPR(entity1, true, "No Records", respDto);
				}
				finalList.add(respDto);

			}

		} else if (reconType != null && (reconType.equalsIgnoreCase("2APR")
				|| reconType.equalsIgnoreCase(NON_AP_M_2APR)
				|| reconType.equalsIgnoreCase(AP_M_2APR)
				|| reconType.equalsIgnoreCase(AUTO_2APR))) {

			List<Gstr2ReconAddlReportsEntity> entity = recon2APRConfigRepo
					.getDataList(configId);

			List<Long> idList = new ArrayList<>();

			for (Gstr2ReconAddlReportsEntity entity1 : entity) {
				idList.add(entity1.getId());
			}

			List<Gstr2Recon2APRReportTypeEntity> childList = recon2APRChildConfigRepo
					.findByReportDwnldIdIn(idList);

			for (Gstr2ReconAddlReportsEntity entity1 : entity) {

				if (!summaryReportsList.contains(entity1.getReportType()))
					continue;
				Gstr2RequesIdWiseDownloadTabDto respDto = new Gstr2RequesIdWiseDownloadTabDto();

				counter = 1;
				convert2APR(entity1, false, null, respDto);

				if (entity1.getFilePath() == null
						|| entity1.getFilePath().toString().isEmpty()) {

					String id = entity1.getId().toString();
					for (Gstr2Recon2APRReportTypeEntity childEntity1 : childList) {
						if (id.equals(
								childEntity1.getReportDwnldId().toString())) {
							convert2APRL2(childEntity1, respDto);
						}

					}
				} else
					convert2APR(entity1, true, null, respDto);
				if (counter == 1)
					convert2APR(entity1, true, "No Records", respDto);

				finalList.add(respDto);

			}
		}
		changeDisplayNames(finalList);

//		finalList.sort(Comparator
//				.comparing(Gstr2RequesIdWiseDownloadTabDto::getOrder));
		finalList.sort(Comparator.comparing(
			    dto -> dto.getOrder() == null ? Integer.MAX_VALUE : dto.getOrder()
			));
		return finalList;
	}

	private void convert2APR(Gstr2ReconAddlReportsEntity obj, boolean flag,
			String msg, Gstr2RequesIdWiseDownloadTabDto dto) {

		// Gstr2RequesIdWiseDownloadTabDto dto = new
		// Gstr2RequesIdWiseDownloadTabDto();

		dto.setReportName(obj.getReportType());

		if (!flag) {
			dto.setLevel("L1");
			dto.setFlag(false);
		} else {
			counter += 1;
			dto.setLevel("L1");
			dto.setPath(obj.getFilePath());
			dto.setFlag(obj.getIsDownloadable());
			dto.setMessage(msg);
		}

		// return dto;
	}

	private void convert2APRL2(Gstr2Recon2APRReportTypeEntity obj,
			Gstr2RequesIdWiseDownloadTabDto dto) {

		// Gstr2RequesIdWiseDownloadTabDto dto = new
		// Gstr2RequesIdWiseDownloadTabDto();
		counter += 1;
		dto.setReportName(obj.getReportType());
		dto.setFlag(obj.isDownloadable());
		dto.setLevel("L1");
		dto.setPath(obj.getFilePath());

		// return dto;
	}

	private void convert2BPRL2(Gstr2Recon2BPRReportTypeEntity obj,
			Gstr2RequesIdWiseDownloadTabDto dto) {

		counter += 1;

		dto.setReportName(obj.getReportType());
		dto.setFlag(obj.isDownloadable());
		dto.setLevel("L1");
		dto.setPath(obj.getFilePath());

	}

	private void convert2BPR(Gstr2Recon2BPRAddlReportsEntity obj, boolean flag,
			String msg, Gstr2RequesIdWiseDownloadTabDto dto) {

		dto.setReportName(obj.getReportType());
		if (!flag) {
			dto.setLevel("L1");
			dto.setFlag(false);
		} else {
			counter += 1;
			dto.setLevel("L1");
			dto.setPath(obj.getFilePath());
			dto.setFlag(obj.getIsDownloadable());
			dto.setMessage(msg);
		}

	}

	private void changeDisplayNames(
			List<Gstr2RequesIdWiseDownloadTabDto> newRespList) {

		for (Gstr2RequesIdWiseDownloadTabDto payloadObj : newRespList) {

			if (payloadObj.getReportName().contains("Exact Match")) {
				payloadObj.setOrder(1);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Exact Match", "Exact Match"));
			} else if (payloadObj.getReportName()
					.contains("Match With Tolerance")) {
				payloadObj.setOrder(2);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Match With Tolerance", "Match With Tolerance"));
			} else if (payloadObj.getReportName().contains("Value Mismatch")) {
				payloadObj.setOrder(3);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Value Mismatch", "Value Mismatch"));
			} else if (payloadObj.getReportName().contains("POS Mismatch")) {
				payloadObj.setOrder(4);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"POS Mismatch", "POS Mismatch"));
			} else if (payloadObj.getReportName()
					.contains("Doc Date Mismatch")) {
				payloadObj.setOrder(5);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Doc Date Mismatch", "Doc Date Mismatch"));
			} else if (payloadObj.getReportName()
					.contains("Doc Type Mismatch")) {
				payloadObj.setOrder(6);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Doc Type Mismatch", "Doc Type Mismatch"));
			} /*
				 * else if (payloadObj.getReportName()
				 * .contains("Doc No Mismatch I")) { payloadObj.setOrder(7);
				 * payloadObj.setReportName(replaceName(payloadObj.getReportName
				 * (), "Doc No Mismatch I", "Doc No Mismatch I"));
				 * 
				 * }
				 */ else if (payloadObj.getReportName()
					.contains("Multi-Mismatch")) {
				payloadObj.setOrder(8);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Multi-Mismatch", "Multi-Mismatch"));
			} /*
				 * else if (payloadObj.getReportName().contains("Potential-I"))
				 * { payloadObj.setOrder(9);
				 * payloadObj.setReportName(replaceName(payloadObj.getReportName
				 * (), "Potential-I", "Potential-I")); } else if
				 * (payloadObj.getReportName() .contains("Doc No Mismatch II"))
				 * { payloadObj.setOrder(10);
				 * payloadObj.setReportName(replaceName(payloadObj.getReportName
				 * (), "Doc No Mismatch II", "Doc No Mismatch II")); } else if
				 * (payloadObj.getReportName().contains("Potential-II")) {
				 * payloadObj.setOrder(11);
				 * payloadObj.setReportName(replaceName(payloadObj.getReportName
				 * (), "Potential-II", "Potential-II")); }
				 */else if (payloadObj.getReportName().contains("Logical Match")) {
				payloadObj.setOrder(12);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Logical Match", "Logical Match"));
			} else if (payloadObj.getReportName().contains("Addition in PR")) {
				payloadObj.setOrder(13);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Addition in PR", "Addition in PR"));
			} else if (payloadObj.getReportName()
					.contains("Addition in 2A_6A")) {
				payloadObj.setOrder(14);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Addition in 2A_6A", "Addition in 2A/6A"));
			} else if (payloadObj.getReportName().contains("Addition in 2B")) {
				payloadObj.setOrder(14);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Addition in 2B", "Addition in 2B"));

			} else if (payloadObj.getReportName()
					.contains("Force_Match_Records")) {
				payloadObj.setOrder(15);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Force_Match_Records", "Force Match"));

			} else if (payloadObj.getReportName()
					.contains("Consolidated PR 2A_6A Report")) {
				payloadObj.setOrder(16);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Consolidated PR 2A_6A Report",
						"Consolidated PR 2A/6A Report"));
			} else if (payloadObj.getReportName()
					.contains("Consolidated PR 2B Report")) {
				payloadObj.setOrder(16);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Consolidated PR 2B Report",
						"Consolidated PR 2B Report"));
			} else if (payloadObj.getReportName()
					.contains("Consolidated_PR_Register")) {
				payloadObj.setOrder(17);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Consolidated_PR_Register",
						"Consolidated PR Register"));
			} else if (payloadObj.getReportName()
					.contains("Summary_CalendarPeriod_Records")) {
				payloadObj.setOrder(18);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Summary_CalendarPeriod_Records",
						"Summary Report Calendar Period"));
			} else if (payloadObj.getReportName()
					.contains("Summary_TaxPeriod_Record")) {
				payloadObj.setOrder(19);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Summary_TaxPeriod_Record",
						"Summary Report Tax Period"));

			} else if (payloadObj.getReportName()
					.contains("Supplier_GSTIN_Summary_Records")) {
				payloadObj.setOrder(20);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Supplier_GSTIN_Summary_Records",
						"Supplier GSTIN Summary Report"));

			} else if (payloadObj.getReportName()
					.contains("Supplier_PAN_Summary_Records")) {
				payloadObj.setOrder(21);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Supplier_PAN_Summary_Records",
						"Supplier PAN Summary Report"));

			} /*else if (payloadObj.getReportName()
					.equalsIgnoreCase("Recipient_GSTIN_Period_Wise_Record")) {
				payloadObj.setOrder(22);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Recipient_GSTIN_Period_Wise_Record",
						"Recipient GSTIN Tax Period Wise Report"));
			}*/ else if (payloadObj.getReportName()
					.contains("Recipient_GSTIN_Wise_Records")) {
				payloadObj.setOrder(23);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Recipient_GSTIN_Wise_Records",
						"Recipient GSTIN Wise Report"));
			} else if (payloadObj.getReportName()
					.contains("Vendor_GSTIN_Period_Wise_Records")) {
				payloadObj.setOrder(24);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Vendor_GSTIN_Period_Wise_Records",
						"Vendor GSTIN Tax Period Wise Report"));
			} else if (payloadObj.getReportName()
					.contains("Vendor_GSTIN_Wise_Records")) {
				payloadObj.setOrder(25);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Vendor_GSTIN_Wise_Records",
						"Vendor GSTIN Wise Report"));

			} else if (payloadObj.getReportName()
					.contains("Vendor_PAN_Period_Wise_Records")) {
				payloadObj.setOrder(26);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Vendor_PAN_Period_Wise_Records",
						"Vendor PAN Tax Period Wise Report"));
			} else if (payloadObj.getReportName()
					.contains("Vendor_PAN_Wise_Records")) {
				payloadObj.setOrder(27);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Vendor_PAN_Wise_Records", "Vendor PAN Wise Report"));

			} else if (payloadObj.getReportName()
					.contains("Reverse_Charge_Register")) {

				payloadObj.setOrder(28);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Reverse_Charge_Register", "Reverse Charge Register"));
			} else if (payloadObj.getReportName()
					.contains("CRD-INV_Ref_Reg_GSTR_2A_Records")) {

				payloadObj.setOrder(29);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"CRD-INV_Ref_Reg_GSTR_2A_Records",
						"CR/DR-Invoice Reference Register-GSTR 2A/6A"));
			} else if (payloadObj.getReportName()
					.contains("CRD-INV_Ref_Reg_GSTR_2B_Records")) {
				payloadObj.setOrder(29);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"CRD-INV_Ref_Reg_GSTR_2B_Records",
						"CR/DR-Invoice Reference Register- GSTR 2B"));
			} else if (payloadObj.getReportName()
					.contains("CRD-INV_Ref_Reg_PR_Records")) {
				payloadObj.setOrder(30);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"CRD-INV_Ref_Reg_PR_Records",
						"CR/DR-Invoice Reference Register-PR"));
			} else if (payloadObj.getReportName()
					.contains("Vendor_Records_GSTIN")) {
				payloadObj.setOrder(31);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Vendor_Records_GSTIN",
						"Vendor GSTIN Wise Detailed Report"));
			} else if (payloadObj.getReportName()
					.contains("Vendor_Records_PAN")) {

				payloadObj.setOrder(32);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Vendor_Records_PAN",
						"Vendor PAN Wise Detailed Report"));
			} else if (payloadObj.getReportName()
					.contains("GSTR_2A_Time_Stamp_Report")) {
				payloadObj.setOrder(33);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"GSTR_2A_Time_Stamp_Report",
						"GSTR 2A/6A Time Stamp Report"));
			} else if (payloadObj.getReportName()
					.contains("GSTR_2B_Time_Stamp_Report")) {
				payloadObj.setOrder(33);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"GSTR_2B_Time_Stamp_Report",
						"GSTR 2B Time Stamp Report"));
			} else if (payloadObj.getReportName()
					.contains("Locked_CFS_N_Amended_Records")) {
				payloadObj.setOrder(34);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Locked_CFS_N_Amended_Records",
						"Locked CFS N Amended Records"));
			} else if (payloadObj.getReportName()
					.contains("Consolidated IMPG Report")) {
				payloadObj.setOrder(35);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Consolidated IMPG Report",
						"Imports/SEZG Matching Report"));
			} else if (payloadObj.getReportName()
					.contains("Imports SEZG Matching Report")) {
				payloadObj.setOrder(35);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Imports SEZG Matching Report",
						"Imports/SEZG Matching Report"));
			} else if (payloadObj.getReportName()
					.contains("Dropped_PR_Records_Report")) {
				payloadObj.setOrder(36);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Dropped_PR_Records_Report",
						"Dropped PR Records Report"));
			} else if (payloadObj.getReportName()
					.contains("Dropped 2A_6A Records Report")) {
				payloadObj.setOrder(37);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Dropped 2A_6A Records Report",
						"Dropped 2A/6A Records Report"));
			} else if (payloadObj.getReportName()
					.contains("Dropped 2B Records Report")) {
				payloadObj.setOrder(37);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Dropped 2B Records Report",
						"Dropped 2B Records Report"));
			} else if (payloadObj.getReportName()
					.contains("ITC Tracking Report")) {
				payloadObj.setOrder(38);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"ITC Tracking Report", "ITC Tracking Report"));
			} else if (payloadObj.getReportName().contains("ERP_Report")) {
				payloadObj.setOrder(39);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"ERP_Report", "ERP Report"));
			} /*else if (payloadObj.getReportName()
					.equalsIgnoreCase("Recipient_GSTIN_Period_Wise_Record_II")) {
				payloadObj.setOrder(40);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Recipient_GSTIN_Period_Wise_Record_II",
						"Recipient GSTIN Tax Period Wise Report II"));
			}*/
			potentialDisplayNameChange(payloadObj);

		}

	}

	private void potentialDisplayNameChange(
			Gstr2RequesIdWiseDownloadTabDto payloadObj) {
		if (payloadObj.getReportName().contains("Doc No Mismatch I")) {

			if (payloadObj.getReportName().contains("Doc No Mismatch II")) {
				payloadObj.setOrder(10);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Doc No Mismatch II", "Doc No Mismatch II"));
			} else {
				payloadObj.setOrder(7);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Doc No Mismatch I", "Doc No Mismatch I"));
			}
		}

		else if (payloadObj.getReportName().contains("Potential-I")) {
			if (payloadObj.getReportName().contains("Potential-II")) {
				payloadObj.setOrder(11);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Potential-II", "Potential-II"));
			} else {
				payloadObj.setOrder(9);
				payloadObj.setReportName(replaceName(payloadObj.getReportName(),
						"Potential-I", "Potential-I"));
			}
		}
		 else if (payloadObj.getReportName().contains("Recipient_GSTIN_Period_Wise_Record")) {
				if (payloadObj.getReportName().contains("Recipient_GSTIN_Period_Wise_Record_II")) {
					payloadObj.setOrder(22);
					payloadObj.setReportName(replaceName(payloadObj.getReportName(),
							"Recipient_GSTIN_Period_Wise_Record_II", "Recipient GSTIN Tax Period Wise Report II"));
				} else {
					payloadObj.setOrder(22);
					payloadObj.setReportName(replaceName(payloadObj.getReportName(),
							"Recipient_GSTIN_Period_Wise_Record", "Recipient GSTIN Tax Period Wise Report"));
				}
			}

	}

	private String replaceName(String reportNameDb, String custom,
			String reportName) {
		if (reportNameDb.length() > custom.length())
			return (reportName);
		else
			return reportName;
	}
}
