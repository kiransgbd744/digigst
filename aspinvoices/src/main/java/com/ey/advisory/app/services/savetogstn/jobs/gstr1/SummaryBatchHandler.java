package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.BifurcationConstants;
import com.ey.advisory.app.data.entities.client.Gstr1NilNonExmptSummaryEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.DocRepositoryGstr1A;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AARRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AATARepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AAtAtaProcRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AB2CSRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AB2csProcRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AHsnProcRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AInvoiceRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ANilNonExtSummaryRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ANilProcRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ANilRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ATxpTxpaProcRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AUserInputHsnSacRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AUserInputNilExtnOnRepository;
import com.ey.advisory.app.data.repositories.client.Anx1B2cProcRepository;
import com.ey.advisory.app.data.repositories.client.Anx1EcomProcRepository;
import com.ey.advisory.app.data.repositories.client.Anx1ImpsProcRepository;
import com.ey.advisory.app.data.repositories.client.Anx1RevProcRepository;
import com.ey.advisory.app.data.repositories.client.AtAtaProcRepository;
import com.ey.advisory.app.data.repositories.client.B2csProcRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ARRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ATARepository;
import com.ey.advisory.app.data.repositories.client.Gstr1B2CSRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1InvoiceRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1NilNonExtSummaryRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1NilRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1UserInputHsnSacRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1UserInputNilExtnOnRepository;
import com.ey.advisory.app.data.repositories.client.HsnProcRepository;
import com.ey.advisory.app.data.repositories.client.InwardTable3HRepository;
import com.ey.advisory.app.data.repositories.client.NilProcRepository;
import com.ey.advisory.app.data.repositories.client.OutwardB2cRepository;
import com.ey.advisory.app.data.repositories.client.OutwardTable4Repository;
import com.ey.advisory.app.data.repositories.client.TxpTxpaProcRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.data.repositories.client.gstr7.Gstr7ProcessedRepository;
import com.ey.advisory.app.data.repositories.client.gstr7.Gstr7TdsSaveRepository;
import com.ey.advisory.app.data.repositories.client.gstr7trans.Gstr7TransDocHeaderRepository;
import com.ey.advisory.app.data.repositories.client.gstr8.Gstr8SaveProcRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */

@Service("SummaryBatchHandler")
@Slf4j
public class SummaryBatchHandler {

	@Autowired
	@Qualifier("B2csProcRepository")
	private B2csProcRepository gstr1B2csProcRepo;

	@Autowired
	@Qualifier("HsnProcRepository")
	private HsnProcRepository gstr1HsnProcRepo;

	@Autowired
	@Qualifier("AtAtaProcRepository")
	private AtAtaProcRepository gstr1AtProcRepo;

	@Autowired
	@Qualifier("TxpTxpaProcRepository")
	private TxpTxpaProcRepository gstr1TxpProcRepo;

	@Autowired
	@Qualifier("NilProcRepository")
	private NilProcRepository gstr1NilProcRepo;

	@Autowired
	@Qualifier("Gstr1InvoiceRepository")
	private Gstr1InvoiceRepository gstr1DocIssueProcRepo;

	@Autowired
	private Anx1B2cProcRepository anx1B2cProcRepo;

	@Autowired
	private Anx1ImpsProcRepository anx1ImpsProcRepo;

	@Autowired
	private Anx1RevProcRepository anx1RevProcRepo;

	@Autowired
	private Anx1EcomProcRepository anx1EcomProcRepo;

	@Autowired
	private Gstr8SaveProcRepository gstr8SaveSummRepo;
	
	@Autowired
	private Gstr7TransDocHeaderRepository gstr7TransDocRepo;

	// Update Header and vertical tables with Original active
	// Batch_ID in place of temp Batch_ID zero.

	@Autowired
	private DocRepository docRepo;
	
	@Autowired
	private DocRepositoryGstr1A gstr1AdocRepo;

	@Autowired
	private InwardTransDocRepository inDocRepo;

	@Autowired
	private OutwardTable4Repository table4Repo;// OutwardGstnTable4Repository

	@Autowired
	private OutwardB2cRepository b2cRepo;// OutwardGstnB2cRepository

	@Autowired
	private InwardTable3HRepository table3h3iRepo; // InwardTable3H3IGstnRepository

	@Autowired
	private Gstr1B2CSRepository b2csB2csaRepo; // Gstr1B2CSGstnRepository

	@Autowired
	private Gstr1ARRepository atAtaRepo; // Gstr1ATGstnRepository

	@Autowired
	private Gstr1ATARepository txpTxpaRepo; // Gstr1TxpdGstnRepository

	@Autowired
	private Gstr7ProcessedRepository gstr7ProcessedRepository;

	@Autowired
	private Gstr7TdsSaveRepository gstr7TdsSaveRepository;

	@Autowired
	private Gstr1NilNonExtSummaryRepository nilSumryRepo;

	@Autowired
	private Gstr1NilRepository nilVertRepo;

	@Autowired
	@Qualifier("gstr1UserInputNilExtnOnRepository")
	private Gstr1UserInputNilExtnOnRepository nilUserRepo;

	@Autowired
	private Gstr1UserInputHsnSacRepository hsnUserRepo;
	
	@Autowired
	@Qualifier("Gstr1AB2csProcRepository")
	private Gstr1AB2csProcRepository gstr1AB2csProcRepo;

	@Autowired
	@Qualifier("Gstr1AHsnProcRepository")
	private Gstr1AHsnProcRepository gstr1AHsnProcRepo;

	@Autowired
	@Qualifier("Gstr1AAtAtaProcRepository")
	private Gstr1AAtAtaProcRepository gstr1AAtProcRepo;

	@Autowired
	@Qualifier("Gstr1ATxpTxpaProcRepository")
	private Gstr1ATxpTxpaProcRepository gstr1ATxpProcRepo;

	@Autowired
	@Qualifier("Gstr1ANilProcRepository")
	private Gstr1ANilProcRepository gstr1ANilProcRepo;

	@Autowired
	@Qualifier("Gstr1AInvoiceRepository")
	private Gstr1AInvoiceRepository gstr1ADocIssueProcRepo;

	@Autowired
	private Gstr1AB2CSRepository gstr1Ab2csB2csaRepo; // Gstr1B2CSGstnRepository

	@Autowired
	private Gstr1AARRepository gstr1AatAtaRepo; // Gstr1ATGstnRepository

	@Autowired
	private Gstr1AATARepository gstr1AtxpTxpaRepo; // Gstr1TxpdGstnRepository

	@Autowired
	private Gstr1ANilNonExtSummaryRepository gstr1AnilSumryRepo;

	@Autowired
	private Gstr1ANilRepository gstr1AnilVertRepo;
	
	@Autowired
	@Qualifier("Gstr1AUserInputNilExtnOnRepository")
	private Gstr1AUserInputNilExtnOnRepository gstr1AnilUserRepo;

	@Autowired
	private Gstr1AUserInputHsnSacRepository gstr1AhsnUserRepo;

	public void summaryBatchUpdate(String retPeriod, String gstin,
			Long gstnBatchId, String groupCode, List<Long> ids, String section,
			Long hMaxId, Long v1MaxId, Long v2MaxId, Long userMaxId) {

		if (ids != null && gstnBatchId != null && groupCode != null
				&& section != null) {

			TenantContext.setTenantId(groupCode);

			// Update Header and vertical tables with Original active
			// Batch_ID in place of temp Batch_ID zero.

			if (section.equalsIgnoreCase(APIConstants.GSTR1A)) {
				updateOriginTablesGstr1A(gstnBatchId, retPeriod, gstin,
						section.toUpperCase(), ids, hMaxId, v1MaxId, v2MaxId,
						userMaxId);
			} else {
				updateOriginTables(gstnBatchId, retPeriod, gstin,
						section.toUpperCase(), ids, hMaxId, v1MaxId, v2MaxId,
						userMaxId);
			}
			
		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}

	}
	

	private void updateOriginTables(Long gstnBatchId, String retPeriod,
			String gstin, String section, List<Long> ids, Long hMaxId,
			Long v1MaxId, Long v2MaxId, Long userMaxId) {

		// ------------------------GSTR1---------------------------//

		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		if (section.equalsIgnoreCase(APIConstants.B2CS)
				|| section.equalsIgnoreCase(APIConstants.B2CSA)) {

			String docType = null;
			if (section.equalsIgnoreCase(APIConstants.B2CS)) {
				docType = GSTConstants.GSTR1_B2CS;
			} else {
				docType = GSTConstants.GSTR1_B2CSA;
			}
			gstr1B2csProcRepo.updateBatchId(gstnBatchId, ids, now);
			docRepo.updateOldReturnsSumryBatchId(gstnBatchId, retPeriod, gstin,
					docType, hMaxId, now);
			b2csB2csaRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin,
					v1MaxId, now);

		} else if (section.equalsIgnoreCase(APIConstants.AT)
				|| section.equalsIgnoreCase(APIConstants.ATA)) {

			// String docType = null;
			// if (section.equalsIgnoreCase(APIConstants.AT)) {
			// docType = BifurcationConstants.TAX_DOC_TYPE_ADV_REC;
			// } else {
			// docType = BifurcationConstants.TAX_DOC_TYPE_ADV_RECA;
			// }

			gstr1AtProcRepo.updateBatchId(gstnBatchId, ids);
			docRepo.updateOldReturnsSumryBatchId(gstnBatchId, retPeriod, gstin,
					section.toUpperCase(), hMaxId, now);
			atAtaRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin,
					v1MaxId);

		} else if (section.equalsIgnoreCase(APIConstants.TXP)
				|| section.equalsIgnoreCase(APIConstants.TXPA)) {

			String docType = null;
			if (section.equalsIgnoreCase(APIConstants.TXP)) {
				docType = BifurcationConstants.TAX_DOC_TYPE_ADV_ADJ;
			} else {
				docType = BifurcationConstants.TAX_DOC_TYPE_ADV_ADJ_A;
			}

			gstr1TxpProcRepo.updateBatchId(gstnBatchId, ids);
			docRepo.updateOldReturnsSumryBatchId(gstnBatchId, retPeriod, gstin,
					section.toUpperCase(), hMaxId, now);
			txpTxpaRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin,
					v1MaxId);

		} else if (section.equalsIgnoreCase(APIConstants.NIL)) {

			String docType = GSTConstants.NIL_EXT_NON;
			gstr1NilProcRepo.updateBatchId(gstnBatchId, ids);
			docRepo.updateOldReturnsSumryBatchId(gstnBatchId, retPeriod, gstin,
					docType, hMaxId, now);
			// Vertical Summary table
			nilSumryRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin,
					v1MaxId);
			// Vertical Processed table
			Optional<Gstr1NilNonExmptSummaryEntity> summryrecord = nilSumryRepo
					.findById(v1MaxId);
			if (summryrecord.isPresent()) {
				nilVertRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin,
						summryrecord.get().getFileId());
			}
			// user input table
			nilUserRepo.updateUserInputBatchId(gstnBatchId, retPeriod, gstin,
					userMaxId);

		} else if (section.equalsIgnoreCase(APIConstants.HSNSUM)) {

			String docType = null;// bifurcation type should come here
			gstr1HsnProcRepo.updateBatchId(gstnBatchId, ids);
			// There is no seperate Bifurcation type for HSN.
			/*
			 * docRepo.updateOldReturnsSumryBatchId(gstnBatchId, retPeriod,
			 * gstin, section.toUpperCase(), hMaxId);
			 */
			// user input table
			hsnUserRepo.updateUserInputBatchId(gstnBatchId, retPeriod, gstin,
					userMaxId);

		} else if (section.equalsIgnoreCase(APIConstants.DOCISS)) {

			String docType = null;// bifurcation type should come here
			gstr1DocIssueProcRepo.updateBatchId(gstnBatchId, ids);

		
		}
	}
	
	// ------------------------GSTR1A---------------------------//
		private void updateOriginTablesGstr1A(Long gstnBatchId, String retPeriod,
				String gstin, String section, List<Long> ids, Long hMaxId,
				Long v1MaxId, Long v2MaxId, Long userMaxId) {


			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (section.equalsIgnoreCase(APIConstants.B2CS)
					|| section.equalsIgnoreCase(APIConstants.B2CSA)) {

				String docType = null;
				if (section.equalsIgnoreCase(APIConstants.B2CS)) {
					docType = GSTConstants.GSTR1_B2CS;
				} else {
					docType = GSTConstants.GSTR1_B2CSA;
				}
				gstr1AB2csProcRepo.updateBatchId(gstnBatchId, ids, now);
				gstr1AdocRepo.updateOldReturnsSumryBatchId(gstnBatchId, retPeriod, gstin,
						docType, hMaxId, now);
				gstr1Ab2csB2csaRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin,
						v1MaxId, now);

			} else if (section.equalsIgnoreCase(APIConstants.AT)
					|| section.equalsIgnoreCase(APIConstants.ATA)) {

				gstr1AAtProcRepo.updateBatchId(gstnBatchId, ids);
				gstr1AdocRepo.updateOldReturnsSumryBatchId(gstnBatchId, retPeriod, gstin,
						section.toUpperCase(), hMaxId, now);
				gstr1AatAtaRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin,
						v1MaxId);

			} else if (section.equalsIgnoreCase(APIConstants.TXP)
					|| section.equalsIgnoreCase(APIConstants.TXPA)) {
				gstr1ATxpProcRepo.updateBatchId(gstnBatchId, ids);
				gstr1AdocRepo.updateOldReturnsSumryBatchId(gstnBatchId, retPeriod, gstin,
						section.toUpperCase(), hMaxId, now);
				gstr1AtxpTxpaRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin,
						v1MaxId);

			} else if (section.equalsIgnoreCase(APIConstants.NIL)) {

				String docType = GSTConstants.NIL_EXT_NON;
				gstr1ANilProcRepo.updateBatchId(gstnBatchId, ids);
				gstr1AdocRepo.updateOldReturnsSumryBatchId(gstnBatchId, retPeriod, gstin,
						docType, hMaxId, now);
				// Vertical Summary table
				gstr1AnilSumryRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin,
						v1MaxId);
				// Vertical Processed table
				Optional<Gstr1NilNonExmptSummaryEntity> summryrecord = nilSumryRepo
						.findById(v1MaxId);
				if (summryrecord.isPresent()) {
					gstr1AnilVertRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin,
							summryrecord.get().getFileId());
				}
				// user input table
				gstr1AnilUserRepo.updateUserInputBatchId(gstnBatchId, retPeriod, gstin,
						userMaxId);

			} else if (section.equalsIgnoreCase(APIConstants.HSNSUM)) {

				gstr1AHsnProcRepo.updateBatchId(gstnBatchId, ids);
				// There is no seperate Bifurcation type for HSN.
				/*
				 * docRepo.updateOldReturnsSumryBatchId(gstnBatchId, retPeriod,
				 * gstin, section.toUpperCase(), hMaxId);
				 */
				// user input table
				gstr1AhsnUserRepo.updateUserInputBatchId(gstnBatchId, retPeriod, gstin,
						userMaxId);

			} else if (section.equalsIgnoreCase(APIConstants.DOCISS)) {

				gstr1ADocIssueProcRepo.updateBatchId(gstnBatchId, ids);

			}

		// ------------------------ANX1---------------------------//

		if (section.equalsIgnoreCase(APIConstants.B2C)) {

			String docType = BifurcationConstants.TAX_DOC_TYPE_B2C;
			anx1B2cProcRepo.updateBatchId(gstnBatchId, ids);
			docRepo.updateNewReturnsSumryBatchId(gstnBatchId, retPeriod, gstin,
					docType, hMaxId, now);
			b2cRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin, v1MaxId);

		} else if (section.equalsIgnoreCase(APIConstants.REV)) {

			String docType = BifurcationConstants.TAX_DOC_TYPE_RCM;
			anx1RevProcRepo.updateBatchId(gstnBatchId, ids);
			inDocRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin, docType,
					hMaxId);
			table3h3iRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin,
					v1MaxId);

		} else if (section.equalsIgnoreCase(APIConstants.IMPS)) {

			String docType = BifurcationConstants.TAX_DOC_TYPE_IMPS;
			anx1ImpsProcRepo.updateBatchId(gstnBatchId, ids);
			inDocRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin, docType,
					hMaxId);
			table3h3iRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin,
					v1MaxId);

		} else if (section.equalsIgnoreCase(APIConstants.ECOM)) {

			String docType = null;// bifurcation type should come here
			anx1EcomProcRepo.updateBatchId(gstnBatchId, ids);
			docRepo.updateNewReturnsSumryBatchId(gstnBatchId, retPeriod, gstin,
					section.toUpperCase(), hMaxId, now);
			b2cRepo.updateSumryBatchId(gstnBatchId, retPeriod, gstin, v1MaxId);
			table4Repo.updateSumryBatchId(gstnBatchId, retPeriod, gstin,
					v2MaxId);
		}

	}

	// -------------------GSTR7----------------------------//

	public void gstr7BatchUpdate(String retPeriod, String gstin,
			Long gstnBatchId, String groupCode, List<Long> ids, String section,
			Long hMaxId, String taxDocType, boolean isTransactional) {

		if (isTransactional) {
			if ("CAN".equalsIgnoreCase(taxDocType)) {
				gstr7TransDocRepo.updateSaveDetailsCan(gstnBatchId, retPeriod,
						gstin, section, hMaxId);
			} else {
				gstr7TransDocRepo.updateSaveDetails(gstnBatchId, retPeriod,
						gstin, section, hMaxId);
			}
		} else {

			if (ids != null && gstnBatchId != null && groupCode != null
					&& section != null) {

				TenantContext.setTenantId(groupCode);

				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());

				if (section.equalsIgnoreCase(APIConstants.TDS)) {
					String docType = "Table-3";
					gstr7TdsSaveRepository.updateBatchId(gstnBatchId, ids);
					if ("CAN".equalsIgnoreCase(taxDocType))
						gstr7ProcessedRepository
								.updateNewReturnsSumryBatchIdForCan(gstnBatchId,
										retPeriod, gstin, docType, hMaxId, now);
					else
						gstr7ProcessedRepository.updateNewReturnsSumryBatchId(
								gstnBatchId, retPeriod, gstin, docType, hMaxId,
								now);
				} else if (section.equalsIgnoreCase(APIConstants.TDSA)) {
					String docType = "Table-4";
					gstr7TdsSaveRepository.updateBatchId(gstnBatchId, ids);
					if ("CAN".equalsIgnoreCase(taxDocType))
						gstr7ProcessedRepository
								.updateNewReturnsSumryBatchIdForCan(gstnBatchId,
										retPeriod, gstin, docType, hMaxId, now);
					else
						gstr7ProcessedRepository.updateNewReturnsSumryBatchId(
								gstnBatchId, retPeriod, gstin, docType, hMaxId,
								now);
				}

			} else {
				LOGGER.debug("Invalid data to DB relationship");
			}
		}

	}

	public void gstr8BatchUpdate(String retPeriod, String gstin,
			Long gstnBatchId, String groupCode, List<Long> ids, String section,
			Long hMaxId, String taxDocType) {

		if (ids != null && gstnBatchId != null && groupCode != null
				&& section != null) {

			TenantContext.setTenantId(groupCode);

			gstr8SaveSummRepo.updateBatchId(gstnBatchId, ids);

		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}

	}

}
