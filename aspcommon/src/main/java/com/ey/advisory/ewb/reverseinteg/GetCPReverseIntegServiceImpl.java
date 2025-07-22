/**
 * 
 */
package com.ey.advisory.ewb.reverseinteg;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.clientBusiness.BCApiErpScenarioPermissionRepositoryBusiness;
import com.ey.advisory.app.data.repositories.clientBusiness.CounterPartyEwbBillRepository;
import com.ey.advisory.app.data.repositories.clientBusiness.CounterPartyInvocControlRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.ewb.client.domain.EWBHeader;
import com.ey.advisory.ewb.client.repositories.EWBHeaderRepository;
import com.ey.advisory.ewb.common.DestinationConnectivityBusiness;
import com.ey.advisory.ewb.common.EwbConstants;
import com.ey.advisory.ewb.data.entities.clientBusiness.BCApiErpScenarioPermissionEntity;
import com.ey.advisory.ewb.data.entities.clientBusiness.CounterPartyEwbBillEntity;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid.Khan
 *
 */
@Slf4j
@Component("GetCPReverseIntegServiceImpl")
public class GetCPReverseIntegServiceImpl implements GetCPReverseIntegService {

	@Autowired
	private BCApiErpScenarioPermissionRepositoryBusiness scenarioRepo;

	@Autowired
	private EWBHeaderRepository ewbHeaderRepo;

	@Autowired
	private CounterPartyEwbBillRepository cpRepo;

	@Autowired
	private DestinationConnectivityBusiness destinationConn;

	@Autowired
	CounterPartyInvocControlRepository counterPartyInvocControlRepo;

	@Override
	public void pushCPEwbsToErp(ReverseIntegParamsDto req) {

		try {
			BCApiErpScenarioPermissionEntity permissionEntity = scenarioRepo
					.findDestinationByGstin(req.getScenarioId(),
							req.getGstin());

			if (permissionEntity == null) {
				LOGGER.error(
						"No destination name found for GSTIN : {} and "
								+ "groupcode : {}",
						req.getGstin(), TenantContext.getTenantId());
				cpRepo.updateRevIntgStatus(req.getGstin(), "NO_DEST_FOUND",
						req.getLocalDate(), LocalDateTime.now());
				return;
			}

			String destination = permissionEntity.getDestName();
			String header = permissionEntity.getStartRootTag();
			String footer = permissionEntity.getEndRootTag();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("destination name :{}, header : {}, footer : {}",
						destination, header, footer);
			}
			Long batchId = counterPartyInvocControlRepo
					.getCntrlId(req.getGstin(), req.getLocalDate());
			// update in controll table status as "initiated" for gstin and date
			counterPartyInvocControlRepo.updateRevIntgStatus(req.getGstin(),
					req.getLocalDate(), EwbConstants.INITIATED);

			List<CounterPartyEwbBillEntity> cpList = cpRepo
					.findCounterPartyForReverseIntegration(req.getGstin(),
							req.getLocalDate());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ewb list : {}", cpList.size()); // renmove
			}

			if (cpList.isEmpty()) {
				LOGGER.error(
						"No Ewb Numbers found for this date {} and gstin {} ",
						req.getLocalDate(), req.getGstin());
				counterPartyInvocControlRepo.updateRevIntgStatus(req.getGstin(),
						req.getLocalDate(), EwbConstants.FAILED);
				return;
			}
			cpList.forEach(o -> o.setRevIntStatus(EwbConstants.INITIATED));
			cpRepo.saveAll(cpList);

			List<String> ewbList = cpList.stream().map(o -> o.getEwbNo())
					.collect(Collectors.toList());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("List of ewbNos : {}", ewbList.size()); // remove
			}

			List<EWBHeader> ewbs = ewbHeaderRepo.findAllByEwbNos(ewbList);

			if (ewbList.isEmpty()) {
				LOGGER.error("No Ewb Numbers found in EWB Header ewbList {} is",
						ewbList);
				counterPartyInvocControlRepo.updateRevIntgStatus(req.getGstin(),
						req.getLocalDate(), EwbConstants.FAILED);
				return;
			}

			boolean isPushed = pushToErp(ewbs, destination, header, footer,
					batchId);

			String status = isPushed ? EwbConstants.SUCCESS
					: EwbConstants.FAILED;

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Push status : {}", status);
			}
			cpList.forEach(o -> {
				LOGGER.debug("DB updates for ewbNo - {}", o.getEwbNo());// remove
				o.setRevIntStatus(status);
				o.setModifiedOn(LocalDateTime.now());
			});
			cpRepo.saveAll(cpList);

			counterPartyInvocControlRepo.updateRevIntgStatus(req.getGstin(),
					req.getLocalDate(), status);
			// update in control table status as "success/failed" for gstin and
			// date

		} catch (Exception e) {
			LOGGER.error("error while reverse integration", e);
			throw new AppException("error while reverse integration", e);
		}

	}

	private boolean pushToErp(List<EWBHeader> ewbs, String destinationName,
			String header, String footer, long cntrlId) {
		boolean isPushed = false;
		try {
			LOGGER.error("inside push to erp");
			EwbHeaderDto headerList = new EwbHeaderDto();
			headerList.setEwbHeaderList(ewbs);
			ByteArrayOutputStream itemOut = new ByteArrayOutputStream();
			JAXBContext itemContext = JAXBContext
					.newInstance(EwbHeaderDto.class);
			Marshaller itemMarshr = itemContext.createMarshaller();
			itemMarshr.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			itemMarshr.marshal(headerList, itemOut);
			String xml = itemOut.toString();
			String reqXml = null;

			if (xml != null && xml.length() > 0) {
				xml = xml.substring(xml.indexOf('\n') + 1);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("xml : {}", xml);
			}
			if (xml != null) {
				reqXml = header + xml + footer;
			}

			if (reqXml != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Posting the  Ewb Details {} to ERP with xml {}",
							ewbs.size(), reqXml);
				}
				Integer respCode = destinationConn.post(destinationName, reqXml,
						cntrlId);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.error("resp code : {}", respCode);
				}
				if (respCode == HttpURLConnection.HTTP_OK) {
					isPushed = true;
					LOGGER.error(
							"Ewb Details {}  has been posted Successfully ",
							ewbs.size());
				} else {
					LOGGER.error(
							"Failed to post Ewb Details with respCode {}, {} ",
							respCode, ewbs.size());
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured while pushing Ewb Details to ERP",
					e);
		}
		return isPushed;
	}

}
