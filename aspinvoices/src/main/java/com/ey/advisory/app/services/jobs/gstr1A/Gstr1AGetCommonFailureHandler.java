/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr1A;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetAtGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetAtaGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetB2bGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetB2baGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetB2clGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetB2claGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetB2csGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetB2csaGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetCdnrGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetCdnraGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetCdnurGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetCdnuraGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetDocGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetEcomAmdGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetEcomGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetExpGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetExpaGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetHSNOrSacGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetNilRatedGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetSupEcomAmdGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetSupEcomGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetTxpGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetTxpaGstnRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.FailureHandler;
import com.ey.advisory.gstnapi.FailureResult;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Gstr1AGetCommonFailureHandler")
@Slf4j
public class Gstr1AGetCommonFailureHandler implements FailureHandler {

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("Gstr1AGetB2bGstnRepository")
	private Gstr1AGetB2bGstnRepository gstr1GetB2bGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetB2baGstnRepository")
	private Gstr1AGetB2baGstnRepository gstr1GetB2baGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetAtGstnRepository")
	private Gstr1AGetAtGstnRepository gstr1GetAtGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetAtaGstnRepository")
	private Gstr1AGetAtaGstnRepository gstr1GetAtaGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetB2claGstnRepository")
	private Gstr1AGetB2claGstnRepository gstr1GetB2claGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetB2clGstnRepository")
	private Gstr1AGetB2clGstnRepository gstr1GetB2clGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetB2csaGstnRepository")
	private Gstr1AGetB2csaGstnRepository gstr1GetB2csaGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetB2csGstnRepository")
	private Gstr1AGetB2csGstnRepository gstr1GetB2csGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetCdnrGstnRepository")
	private Gstr1AGetCdnrGstnRepository gstr1GetCdnrGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetCdnraGstnRepository")
	private Gstr1AGetCdnraGstnRepository gstr1GetCdnraGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetCdnurGstnRepository")
	private Gstr1AGetCdnurGstnRepository gstr1GetCdnurGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetCdnuraGstnRepository")
	private Gstr1AGetCdnuraGstnRepository gstr1GetCdnuraGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetDocGstnRepository")
	private Gstr1AGetDocGstnRepository gstr1GetDocGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetExpGstnRepository")
	private Gstr1AGetExpGstnRepository gstr1GetExpGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetExpaGstnRepository")
	private Gstr1AGetExpaGstnRepository gstr1GetExpaGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetHSNOrSacGstnRepository")
	private Gstr1AGetHSNOrSacGstnRepository gstr1GetHSNOrSacGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetNilRatedGstnRepository")
	private Gstr1AGetNilRatedGstnRepository gstr1GetNilRatedGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetTxpGstnRepository")
	private Gstr1AGetTxpGstnRepository gstr1GetTxpGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetTxpaGstnRepository")
	private Gstr1AGetTxpaGstnRepository gstr1GetTxpaGstnRepository;

	/*@Autowired
	@Qualifier("Gstr1AGetEInvoiceB2bGstnRepository")
	private Gstr1AGetEInvoiceB2bGstnRepository gstr1GetEInvoiceB2bGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetEInvoiceCdnGstnRepository")
	private Gstr1AGetEInvoiceCdnGstnRepository gstr1GetEInvoiceCdnGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetEInvoiceCdnurGstnRepository")
	private Gstr1AGetEInvoiceCdnurGstnRepository gstr1GetEInvoiceCdnurGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetEInvoiceExpGstnRepository")
	private Gstr1AGetEInvoiceExpGstnRepository gstr1GetEInvoiceExpGstnRepository;*/

	@Autowired
	@Qualifier("Gstr1AGetSupEcomGstnRepository")
	private Gstr1AGetSupEcomGstnRepository gstr1GetSupEcomGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetSupEcomAmdGstnRepository")
	private Gstr1AGetSupEcomAmdGstnRepository gstr1GetSupEcomAmdGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetEcomGstnRepository")
	private Gstr1AGetEcomGstnRepository gstr1GetEcomGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetEcomAmdGstnRepository")
	private Gstr1AGetEcomAmdGstnRepository gstr1GetEcomAmdGstnRepository;

	private static final List<String> ERROR_CODES = ImmutableList.of("RET13508",
			"RET13509", "RET13510", "RET11416", "RET11417","RETWEB_04");

	@Override
	public void handleFailure(FailureResult result, String apiParams) {
		try {
			String errorCode = result.getError().getErrorCode();
			String errorDesc = result.getError().getErrorDesc();
			Gson gson = GsonUtil.newSAPGsonInstance();
			String ctxParams = result.getCtxParams();
			JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Failed for the batchId {} inside "
								+ "Gstr1GetCommonFailureHandler.java",
						ctxParamsObj.get("batchId").getAsLong());
			}
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("dto object: {} ", dto.toString());
				LOGGER.debug("dto type: {} ", dto.getType());
			}
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (ERROR_CODES.contains(errorCode)) {
				if (APIConstants.GSTR1A.equalsIgnoreCase(dto.getApiSection())) {
					if (APIConstants.AT.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetAtGstnRepository
								.softlyDeleteAtHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "AT is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					} else if (APIConstants.ATA
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetAtaGstnRepository
								.softlyDeleteAtaHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "ATA is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					} else if (APIConstants.B2B
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetB2bGstnRepository
								.softlyDeleteB2bHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "B2B is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					} else if (APIConstants.B2BA
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetB2baGstnRepository
								.softlyDeleteB2baHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "B2BA is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					}

					else if (APIConstants.B2CL
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetB2clGstnRepository
								.softlyDeleteB2clHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "B2CL is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					} else if (APIConstants.B2CLA
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetB2claGstnRepository
								.softlyDeleteB2claHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "B2CLA is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					} else if (APIConstants.B2CS
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetB2csGstnRepository
								.softlyDeleteB2csHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "B2CS is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					} else if (APIConstants.B2CSA
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetB2csaGstnRepository
								.softlyDeleteB2csaHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "B2CSA is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					}

					else if (APIConstants.CDNR
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetCdnrGstnRepository
								.softlyDeleteCdnrHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "CDNR is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					} else if (APIConstants.CDNRA
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetCdnraGstnRepository
								.softlyDeleteCdnraHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "CDNRA is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					}

					else if (APIConstants.CDNUR
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetCdnurGstnRepository
								.softlyDeleteCdnurHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "CDNUR is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					} else if (APIConstants.CDNURA
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetCdnuraGstnRepository
								.softlyDeleteCdnuraHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "CDNURA is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					}

					else if (APIConstants.DOC_ISSUE
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetDocGstnRepository
								.softlyDeleteDocHeader(dto.getGstin(),
										dto.getReturnPeriod());
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "DOC_ISSUE is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					}

					else if (APIConstants.EXP.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetExpGstnRepository
								.softlyDeleteExpHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "EXP is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					} else if (APIConstants.EXPA
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetExpaGstnRepository
								.softlyDeleteExpaHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "EXPA is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					} else if (APIConstants.HSN
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetHSNOrSacGstnRepository
								.softlyDeleteHsnHeader(dto.getGstin(),
										dto.getReturnPeriod());
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "HSN is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					}

					else if (APIConstants.NIL.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetNilRatedGstnRepository
								.softlyDeleteNilHeader(dto.getGstin(),
										dto.getReturnPeriod());
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "NIL is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					}

					else if (APIConstants.TXP.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetTxpGstnRepository
								.softlyDeleteTxpHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "TXP is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					} else if (APIConstants.TXPA
							.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetTxpaGstnRepository
								.softlyDeleteTxpaHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "TXPA is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					}
					

					else if (APIConstants.SUPECO.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetSupEcomGstnRepository
								.softlyDeleteSupEcomHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "SUPECO is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					}
					else if (APIConstants.SUPECOAMD.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetSupEcomAmdGstnRepository
								.softlyDeleteSupEcomAHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "SUPECOAMD is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					}
					else if (APIConstants.ECOM.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetEcomGstnRepository
								.softlyDeleteEcomHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "SUPECOAMD is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					}
					else if (APIConstants.ECOMAMD.equalsIgnoreCase(dto.getType())) {
						int rowAffected = gstr1GetEcomAmdGstnRepository
								.softlyDeleteEcomAmdHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "SUPECOAMD is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffected);
						}
					}

					batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
							APIConstants.SUCCESS_WITH_NO_DATA, errorCode, errorDesc,
							false);
					
				} /*else if (APIConstants.EINV_B2B
						.equalsIgnoreCase(dto.getType())) {
					int rowAffected = gstr1GetEInvoiceB2bGstnRepository
							.softlyDeleteEInvoicesHeader(dto.getGstin(),
									dto.getReturnPeriod(), now);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Total Number of soft deleted "
										+ "for gstin {}, tax period {}, section "
										+ "EINV_B2B is {} ",
								dto.getGstin(), dto.getReturnPeriod(),
								rowAffected);
					}
				} else if (APIConstants.EINV_CDNR
						.equalsIgnoreCase(dto.getType())) {
					int rowAffected = gstr1GetEInvoiceCdnGstnRepository
							.softlyDeleteCdnrHeader(dto.getGstin(),
									dto.getReturnPeriod(), now);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Total Number of soft deleted "
										+ "for gstin {}, tax period {}, section "
										+ "EINV_CDNR is {} ",
								dto.getGstin(), dto.getReturnPeriod(),
								rowAffected);
					}
				} else if (APIConstants.EINV_CDNUR
						.equalsIgnoreCase(dto.getType())) {
					int rowAffected = gstr1GetEInvoiceCdnurGstnRepository
							.softlyDeleteCdnurHeader(dto.getGstin(),
									dto.getReturnPeriod(), now);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Total Number of soft deleted "
										+ "for gstin {}, tax period {}, section "
										+ "EINV_CDNUR is {} ",
								dto.getGstin(), dto.getReturnPeriod(),
								rowAffected);
					}
				} else if (APIConstants.EINV_EXP
						.equalsIgnoreCase(dto.getType())) {
					int rowAffected = gstr1GetEInvoiceExpGstnRepository
							.softlyDeleteExpHeader(dto.getGstin(),
									dto.getReturnPeriod(), now);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Total Number of soft deleted "
										+ "for gstin {}, tax period {}, section "
										+ "EINV_EXP is {} ",
								dto.getGstin(), dto.getReturnPeriod(),
								rowAffected);
					}
				}*/				
			} else {
				batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
						APIConstants.FAILED, errorCode, errorDesc, false);
			}
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw new AppException(e);
		}
	}
}
