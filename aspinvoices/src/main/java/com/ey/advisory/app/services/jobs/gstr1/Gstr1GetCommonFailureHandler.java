/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr1;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Gstr1GetAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetAtaGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetB2bGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetB2baGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetB2clGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetB2claGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetB2csGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetB2csaGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetCdnrGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetCdnraGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetCdnurGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetCdnuraGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetDocGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEInvoiceB2bGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEInvoiceCdnGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEInvoiceCdnurGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEInvoiceExpGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEcomAmdGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEcomGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetExpGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetExpaGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetHSNOrSacGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetNilRatedGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetSupEcomAmdGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetSupEcomGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetTxpGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetTxpaGstnRepository;
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
@Service("Gstr1GetCommonFailureHandler")
@Slf4j
public class Gstr1GetCommonFailureHandler implements FailureHandler {

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("Gstr1GetB2bGstnRepository")
	private Gstr1GetB2bGstnRepository gstr1GetB2bGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetB2baGstnRepository")
	private Gstr1GetB2baGstnRepository gstr1GetB2baGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetAtGstnRepository")
	private Gstr1GetAtGstnRepository gstr1GetAtGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetAtaGstnRepository")
	private Gstr1GetAtaGstnRepository gstr1GetAtaGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetB2claGstnRepository")
	private Gstr1GetB2claGstnRepository gstr1GetB2claGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetB2clGstnRepository")
	private Gstr1GetB2clGstnRepository gstr1GetB2clGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetB2csaGstnRepository")
	private Gstr1GetB2csaGstnRepository gstr1GetB2csaGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetB2csGstnRepository")
	private Gstr1GetB2csGstnRepository gstr1GetB2csGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetCdnrGstnRepository")
	private Gstr1GetCdnrGstnRepository gstr1GetCdnrGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetCdnraGstnRepository")
	private Gstr1GetCdnraGstnRepository gstr1GetCdnraGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetCdnurGstnRepository")
	private Gstr1GetCdnurGstnRepository gstr1GetCdnurGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetCdnuraGstnRepository")
	private Gstr1GetCdnuraGstnRepository gstr1GetCdnuraGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetDocGstnRepository")
	private Gstr1GetDocGstnRepository gstr1GetDocGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetExpGstnRepository")
	private Gstr1GetExpGstnRepository gstr1GetExpGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetExpaGstnRepository")
	private Gstr1GetExpaGstnRepository gstr1GetExpaGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetHSNOrSacGstnRepository")
	private Gstr1GetHSNOrSacGstnRepository gstr1GetHSNOrSacGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetNilRatedGstnRepository")
	private Gstr1GetNilRatedGstnRepository gstr1GetNilRatedGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetTxpGstnRepository")
	private Gstr1GetTxpGstnRepository gstr1GetTxpGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetTxpaGstnRepository")
	private Gstr1GetTxpaGstnRepository gstr1GetTxpaGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetEInvoiceB2bGstnRepository")
	private Gstr1GetEInvoiceB2bGstnRepository gstr1GetEInvoiceB2bGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetEInvoiceCdnGstnRepository")
	private Gstr1GetEInvoiceCdnGstnRepository gstr1GetEInvoiceCdnGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetEInvoiceCdnurGstnRepository")
	private Gstr1GetEInvoiceCdnurGstnRepository gstr1GetEInvoiceCdnurGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetEInvoiceExpGstnRepository")
	private Gstr1GetEInvoiceExpGstnRepository gstr1GetEInvoiceExpGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetSupEcomGstnRepository")
	private Gstr1GetSupEcomGstnRepository gstr1GetSupEcomGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetSupEcomAmdGstnRepository")
	private Gstr1GetSupEcomAmdGstnRepository gstr1GetSupEcomAmdGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetEcomGstnRepository")
	private Gstr1GetEcomGstnRepository gstr1GetEcomGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetEcomAmdGstnRepository")
	private Gstr1GetEcomAmdGstnRepository gstr1GetEcomAmdGstnRepository;

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
				if (APIConstants.GSTR1.equalsIgnoreCase(dto.getApiSection())) {
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
					

					if (APIConstants.SUPECO.equalsIgnoreCase(dto.getType())) {
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
					if (APIConstants.SUPECOAMD.equalsIgnoreCase(dto.getType())) {
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
					if (APIConstants.ECOM.equalsIgnoreCase(dto.getType())) {
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
					if (APIConstants.ECOMAMD.equalsIgnoreCase(dto.getType())) {
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
				} else if (APIConstants.EINV_B2B
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
				}
				batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
						APIConstants.SUCCESS_WITH_NO_DATA, errorCode, errorDesc,
						false);
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
