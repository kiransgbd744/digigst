/**
 * 
 */
package com.ey.advisory.app.services.compute;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Annexure2ComputeRepository;
import com.ey.advisory.app.dto.compute.Annexure2ComputeDto;
import com.ey.advisory.app.services.common.Annexure2ComputeConstants;
import com.ey.advisory.common.AppException;
import com.google.common.base.Strings;

/**
 * @author Khalid1.Khan
 *
 */
@Component("Annexure2ReconComputeService")
public class Annexure2ReconComputeServiceImpl implements 
Annexure2ReconComputeServiceFacade{

	@Autowired
	@Qualifier("Annexure2ComputeRepository")
	private Annexure2ComputeRepository annexure2ComputeRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Annexure2ReconComputeServiceImpl.class);

	private List<Annexure2ComputeDto> claimItcAsPerA2List;
	private List<Annexure2ComputeDto> claimItcAsPerPrList;
	private List<Annexure2ComputeDto> pendingList;
	private List<Annexure2ComputeDto> rejectA2AndPrList;
	private List<Annexure2ComputeDto> rejectA2List;
	private List<String> claimItcAsPerA2InvoiceList;
	private List<String> claimItcAsPerPrInvoiceList;
	private List<String> pendingInvoiceList;
	private List<String> rejectA2AndPrInvoiceList;
	private List<String> rejectA2InvoiceList;

	public void reconComputeAction(List<Annexure2ComputeDto> reconComputeList) {

		claimItcAsPerA2List = new ArrayList<>();
		claimItcAsPerPrList = new ArrayList<>();
		pendingList = new ArrayList<>();
		rejectA2AndPrList = new ArrayList<>();
		rejectA2List = new ArrayList<>();
		claimItcAsPerA2InvoiceList = new ArrayList<>();
		claimItcAsPerPrInvoiceList = new ArrayList<>();
		pendingInvoiceList = new ArrayList<>();
		rejectA2AndPrInvoiceList = new ArrayList<>();
		rejectA2InvoiceList = new ArrayList<>();
		try {
			for (Annexure2ComputeDto reconComputeDto : reconComputeList) {
				if (reconComputeDto.getAction().equals(
						Annexure2ComputeConstants.
						COMPUTE_ACTION_CLAIM_ITC_AS_PER_A2)) {
					claimItcAsPerA2List.add(reconComputeDto);
					if (!Strings
							.isNullOrEmpty(reconComputeDto.getA2InvoiceKey()))
						claimItcAsPerA2InvoiceList
								.add(reconComputeDto.getA2InvoiceKey());

				} else if (reconComputeDto.getAction().equals(
						Annexure2ComputeConstants.
						COMPUTE_ACTION_CLAIM_ITC_AS_PER_PR)) {
					claimItcAsPerPrList.add(reconComputeDto);
					if (!Strings
							.isNullOrEmpty(reconComputeDto.getA2InvoiceKey()))
						claimItcAsPerPrInvoiceList
								.add(reconComputeDto.getA2InvoiceKey());
				} else if (reconComputeDto.getAction().equals(
						Annexure2ComputeConstants.COMPUTE_ACTION_PENDING_A2)) {
					pendingList.add(reconComputeDto);
					if (!Strings
							.isNullOrEmpty(reconComputeDto.getA2InvoiceKey()))
						pendingInvoiceList
								.add(reconComputeDto.getA2InvoiceKey());
				} else if (reconComputeDto.getAction().equals(
						Annexure2ComputeConstants.COMPUTE_ACTION_REJECT_A2)) {
					rejectA2List.add(reconComputeDto);
					if (!Strings
							.isNullOrEmpty(reconComputeDto.getA2InvoiceKey()))
						rejectA2InvoiceList
								.add(reconComputeDto.getA2InvoiceKey());
				} else if (reconComputeDto.getAction().equals(
						Annexure2ComputeConstants.
						COMPUTE_ACTION_REJECT_A2_AND_PR)) {
					rejectA2AndPrList.add(reconComputeDto);
					if (!Strings
							.isNullOrEmpty(reconComputeDto.getA2InvoiceKey()))
						rejectA2AndPrInvoiceList
								.add(reconComputeDto.getA2InvoiceKey());
				}
			}
			if (!claimItcAsPerA2List.isEmpty()) {
				claimItcAsPerA2ComputeAction(claimItcAsPerA2List,
						claimItcAsPerA2InvoiceList);
			}
			if (!claimItcAsPerPrList.isEmpty()) {
				claimItcAsPerPrComputeAction(claimItcAsPerPrList,
						claimItcAsPerPrInvoiceList);
			}
			if (!pendingList.isEmpty()) {
				pendingA2ComputeAction(pendingList, pendingInvoiceList);
			}
			if (!rejectA2List.isEmpty()) {
				rejectA2ComputeAction(rejectA2List, rejectA2InvoiceList);
			}
			if (!rejectA2AndPrList.isEmpty()) {
				rejectA2AndPrComputeAction(rejectA2AndPrList,
						rejectA2AndPrInvoiceList);
			}
		} catch (NullPointerException | DataAccessException | AppException e) {
			LOGGER.error(
					"Exception in "
					+ "Annexure2ReconComputeService.reconComputeAction"
							+ e.getMessage());
			throw new AppException(e);
		} catch (Exception e) {
			LOGGER.error(
					"Exception in "
					+ "Annexure2ReconComputeService.reconComputeAction"
							+ e.getMessage());
			throw new AppException(e);
		}
	}

	private void claimItcAsPerA2ComputeAction(
			List<Annexure2ComputeDto> claimItcAsPerA2List,
			List<String> claimItcAsPerA2InvoiceList2)
			throws DataAccessException {
		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("INSIDE Annexure2ReconComputeService."
				+ "claimItcAsPerA2ComputeAction LIST VALUE = "
				+ claimItcAsPerA2InvoiceList2);
		}
		annexure2ComputeRepository.updateActionStatus(
				Annexure2ComputeConstants.A2_ACTION_TAKEN_ACCEPT,
				claimItcAsPerA2InvoiceList2);

	}

	private void claimItcAsPerPrComputeAction(
			List<Annexure2ComputeDto> claimItcAsPerPrList,
			List<String> claimItcAsPerPrInvoiceList2)
			throws DataAccessException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("INSIDE Annexure2ReconComputeService."
					+ "claimItcAsPerPrComputeAction LIST VALUE = "
					+ claimItcAsPerPrInvoiceList2);
		}
		annexure2ComputeRepository.updateActionStatus(
				Annexure2ComputeConstants.A2_ACTION_TAKEN_ACCEPT,
				claimItcAsPerPrInvoiceList2);

	}

	private void pendingA2ComputeAction(List<Annexure2ComputeDto> pendingList,
			List<String> pendingInvoiceList2) throws DataAccessException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("INSIDE Annexure2ReconComputeService."
					+ "pendingA2ComputeAction LIST VALUE = "
					+ pendingInvoiceList2);
		}
		annexure2ComputeRepository.updateActionStatus(
				Annexure2ComputeConstants.A2_ACTION_TAKEN_PENDING,
				pendingInvoiceList2);

	}

	private void rejectA2ComputeAction(List<Annexure2ComputeDto> rejectA2List,
			List<String> rejectA2InvoiceList2) throws DataAccessException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("INSIDE Annexure2ReconComputeService."
					+ "rejectA2ComputeAction LIST VALUE = "
					+ rejectA2InvoiceList2);
		}
		annexure2ComputeRepository.updateActionStatus(
				Annexure2ComputeConstants.A2_ACTION_TAKEN_REJECT,
				rejectA2InvoiceList2);

	}

	private void rejectA2AndPrComputeAction(
			List<Annexure2ComputeDto> rejectA2AndPrList,
			List<String> rejectA2AndPrInvoiceList2) throws DataAccessException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("INSIDE Annexure2ReconComputeService."
					+ "rejectA2AndPrComputeAction LIST VALUE = "
					+ rejectA2AndPrInvoiceList2);
		}
		annexure2ComputeRepository.updateActionStatus(
				Annexure2ComputeConstants.A2_ACTION_TAKEN_REJECT,
				rejectA2AndPrInvoiceList2);

	}

}
