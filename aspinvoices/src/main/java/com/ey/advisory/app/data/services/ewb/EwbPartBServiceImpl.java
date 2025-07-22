/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.EwbEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.app.docs.dto.einvoice.TransportPartBDetailsDto;
import com.ey.advisory.ewb.client.domain.EwbLifecycleEntity;
import com.ey.advisory.ewb.client.repositories.EwbLifecycleRepository;
import com.ey.advisory.ewb.common.EyEwbCommonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Slf4j
@Service("EwbPartBServiceImpl")
public class EwbPartBServiceImpl implements EwbPartBService {

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepository;

	@Autowired
	@Qualifier("EwbLifecycleRepository")
	private EwbLifecycleRepository ewbLcRepository;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Override
	public List<TransportPartBDetailsDto> getPartBDetailsByEwbNo(
			List<String> ewbNos) {

		String ewbNo = null;
		for (String ewbNum : ewbNos) {
			ewbNo = ewbNum;
		}

		List<TransportPartBDetailsDto> partBDetailsList = new ArrayList<>();

		List<EwbEntity> ewbEntityList = ewbRepository.findEwbByEwbNums(ewbNos);

		List<EwbLifecycleEntity> PartbResponseList = ewbLcRepository
				.findPartBDetailsByewbNum(ewbNos);

		Long docHeaderId = ewbRepository.findDocIdByEwbNum(ewbNo);
		List<Object[]> docDetails = docRepository
				.findDocNumGstinById(docHeaderId);
		String docNum = null;
		String sgstin = null;
		for (Object[] arr : docDetails) {
			if (docDetails != null) {
				docNum = arr[0] != null ? String.valueOf(arr[0]) : null;
				sgstin = arr[1] != null ? String.valueOf(arr[1]) : null;
			}
		}
		for (EwbEntity masterTransporter : ewbEntityList) {
			for (EwbLifecycleEntity partb : PartbResponseList) {

				TransportPartBDetailsDto dto = new TransportPartBDetailsDto();

				dto.setEwbNo(ewbNo);
				dto.setGstin(sgstin);
				dto.setDocNum(docNum);
				dto.setTransporterId(
						partb != null && partb.getTransporterId() != null
								? partb.getTransporterId()
								: masterTransporter.getTransporterId());
				dto.setTransMode(EyEwbCommonUtil.getTransModeDesc(
						partb != null && partb.getTransMode() != null
								? partb.getTransMode()
								: masterTransporter.getTransMode()));
				dto.setTransDocNo(partb != null && partb.getTransDocNo() != null
						? partb.getTransDocNo()
						: masterTransporter.getTransDocNum());
				dto.setTransDocDate(
						partb != null && partb.getTransDocDate() != null
								? partb.getTransDocDate()
								: masterTransporter.getTransDocDate());
				dto.setVehicleNo(partb != null && partb.getVehicleNum() != null
						? partb.getVehicleNum()
						: masterTransporter.getVehicleNum());
				dto.setVehicleType(
						partb != null && partb.getVehicleType() != null
								? partb.getVehicleType()
								: masterTransporter.getVehicleType());
				dto.setTransFrom(partb != null && partb.getFromPlace() != null
						? partb.getFromPlace()
						: masterTransporter.getFromPlace());
				dto.setTransFromState(
						partb != null && partb.getFromState() != null
								? partb.getFromState()
								: masterTransporter.getFromState());
				dto.setDistance(partb != null && partb.getAspDistance() != null
						? partb.getAspDistance()
						: masterTransporter.getAspDistance());
				dto.setNicDistance(
						partb != null && partb.getRemDistance() != null
								? partb.getRemDistance()
								: masterTransporter.getRemDistance());
				dto.setUpdatePartBdate(partb.getModifiedOn());
				dto.setVehicleUpdate(partb.getVehicleUpdateDate());
				dto.setErrorCode(partb.getErrorCode());
				dto.setErrorDesc(partb.getErrorDesc());
				dto.setEwbDate(masterTransporter.getEwbDate());

				partBDetailsList.add(dto);
			}
		}
		return partBDetailsList;
	}

}
