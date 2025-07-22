/**
 * 
 */
package com.ey.advisory.functions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.dto.compute.Annexure2ComputeDto;
import com.ey.advisory.app.services.common.Annexure2ComputeConstants;
import com.ey.advisory.controllers.anexure2.Annexure2ComputeController;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * @author Khalid1.Khan
 *
 */
@Component("Annexure2ComputeHelper")
public class Annexure2ComputeHelper {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Annexure2ComputeController.class);

	public String validateReconComputeList(
			List<Annexure2ComputeDto> reconComputeList, String comment) {
		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Annexure2ComputeHelper.validateReconComputeList Begin");
		}
		final List<String> matchType = ImmutableList.of(
				Annexure2ComputeConstants.COMPUTE_MATCH_TYPE_ABSOLUTE,
				Annexure2ComputeConstants.COMPUTE_MATCH_TYPE_MISMATCH,
				Annexure2ComputeConstants.COMPUTE_MATCH_TYPE_POTENTIAL_MISMATCH,
				Annexure2ComputeConstants.COMPUTE_MATCH_TYPE_ADDITIONAL_A2,
				Annexure2ComputeConstants.COMPUTE_MATCH_TYPE_ADDITIONAL_PR);

		final List<String> actionType = ImmutableList.of(
				Annexure2ComputeConstants.COMPUTE_ACTION_CLAIM_ITC_AS_PER_A2,
				Annexure2ComputeConstants.COMPUTE_ACTION_CLAIM_ITC_AS_PER_PR,
				Annexure2ComputeConstants.COMPUTE_ACTION_PENDING_A2,
				Annexure2ComputeConstants.COMPUTE_ACTION_REJECT_A2,
				Annexure2ComputeConstants.COMPUTE_ACTION_REJECT_A2_AND_PR);

		for (Annexure2ComputeDto reconComputeDto : reconComputeList) {
			if (!matchType.contains(reconComputeDto.getMatchingType())) {
				comment = String.format("Invalid Match Type %s",
						reconComputeDto.getMatchingType());
				break;
			} else if (!actionType.contains(reconComputeDto.getAction())) {
				comment = String.format("Invalid Action Name %s",
						reconComputeDto.getAction());
				break;
			} else if (Strings.isNullOrEmpty(reconComputeDto.getA2InvoiceKey())
					&& !reconComputeDto.getMatchingType().equals(
							Annexure2ComputeConstants.
							COMPUTE_MATCH_TYPE_ADDITIONAL_PR)) {
				comment = String.format("Invalid A2InvoiceKey %s",
						reconComputeDto.getA2InvoiceKey());
				break;
			} else if (Strings.isNullOrEmpty(reconComputeDto.getPrInvoiceKey())
					&& !reconComputeDto.getMatchingType().equals(
							Annexure2ComputeConstants.
							COMPUTE_MATCH_TYPE_ADDITIONAL_A2)) {
				comment = String.format("Invalid PrInvoiceKey %s",
						reconComputeDto.getPrInvoiceKey());
				break;
			}

		}
		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(
				"Annexure2ComputeHelper.validateReconComputeList End "
				+ "validationResult = "
						+ comment);
		}
		return comment;
	}

}
