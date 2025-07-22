/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.EwbEntity;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.EwbStatus;
import com.ey.advisory.ewb.client.domain.EWBHeader;
import com.ey.advisory.ewb.client.domain.EwbLifecycleEntity;
import com.ey.advisory.ewb.client.repositories.EwbLifecycleRepository;

/**
 * @author Arun.KA
 *
 */

@Component("EwbSaveCounterPartyDetailsImpl")
public class EwbSaveCounterPartyDetailsImpl
		implements EwbSaveCounterPartyDetails {

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepository;

	@Autowired
	@Qualifier("EwbLifecycleRepository")
	private EwbLifecycleRepository ewbLCeRepo;

	@Override
	public void SaveEwbDetails(EWBHeader hdr) {

		EwbLifecycleEntity ewbLcEntity = new EwbLifecycleEntity();
		if (hdr.getValidUpto() != null) {
			ewbLcEntity.setEwbNum(hdr.getEwbNo());
			// ewbLcEntity.setVehicleNum(nicReq.getVehicleNo());
			ewbLcEntity.setVehicleType(hdr.getVehicleType());
			// ewbLcEntity.setTransMode(nicReq.getTransMode());
			// ewbLcEntity.setTransDocNo(nicReq.getTransDocNo());
			// ewbLcEntity.setTransDocDate(nicReq.getTransDocDate());
			ewbLcEntity.setFromPlace(hdr.getFromPlace());
			ewbLcEntity.setFromState(hdr.getFromStateCode());
			ewbLcEntity.setVehicleUpdateDate(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			ewbLcEntity.setValidUpto(hdr.getValidUpto());
			ewbLcEntity.setModifiedOn(hdr.getEwbBillDate());
			ewbLcEntity.setAspDistance(hdr.getActualDist());
			ewbLcEntity.setActive(true);
			ewbLcEntity.setEwbOrigin(2);
			// ewbLcEntity.setFunction(APIIdentifiers.UPDATE_VEHICLE_DETAILS);
			ewbLcEntity = ewbLCeRepo.save(ewbLcEntity);
		}

		saveEwbEntity(hdr, ewbLcEntity.getId());

	}

	private void saveEwbEntity(EWBHeader hdr, Long lifeCycleId) {
			EwbEntity ewbEntity = new EwbEntity();
			//ewbEntity.setAlert(resp.getAlert());
			ewbEntity.setCreatedBy("SYSTEM");
			ewbEntity.setCreatedOn(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			//ewbEntity.setDocHeaderId(id);
			ewbEntity.setEwbDate(hdr.getEwbBillDate());
			ewbEntity.setEwbNum(hdr.getEwbNo());
			ewbEntity.setModifiedBy("SYSTEM");
			ewbEntity.setModifiedOn(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			ewbEntity.setValidUpto(hdr.getValidUpto());
			ewbEntity.setFromPlace(hdr.getFromPlace());
			ewbEntity.setFromPincode(hdr.getFromPincode());
			ewbEntity.setFromState(hdr.getFromStateCode());
			ewbEntity.setLifeCycleId(lifeCycleId);
			//ewbEntity.setRemDistance(nicReq.getTransDistance());
			ewbEntity.setAspDistance(hdr.getActualDist());
			ewbEntity.setStatus(hdr.getValidUpto() != null
					? EwbStatus.EWB_ACTIVE.getEwbStatusCode()
					: EwbStatus.PARTA_GENERATED.getEwbStatusCode());
			//ewbEntity.setTransDocDate(nicReq.getTransDocDate());
			//ewbEntity.setTransDocNum(nicReq.getTransDocNo());
			//ewbEntity.setTransMode(nicReq.getTransMode());
			ewbEntity.setTransporterId(hdr.getTranspoterId());
			//ewbEntity.setVehicleType(nicReq.getVehicleType());
			//ewbEntity.setVehicleNum(nicReq.getVehicleNo());
			ewbEntity.setEwbOrigin(2);
			ewbEntity.setTransType(hdr.getTransactionType());
			ewbRepository.save(ewbEntity);

		
	}

}
