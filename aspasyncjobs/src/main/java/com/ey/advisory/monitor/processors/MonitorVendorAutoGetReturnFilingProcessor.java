package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.asprecon.VendorMasterApiEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterApiEntityRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("MonitorVendorAutoGetReturnFilingProcessor")
public class MonitorVendorAutoGetReturnFilingProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	private GstnReturnFilingStatus gstnReturnFiling;

	@Autowired
	@Qualifier("VendorMasterApiEntityRepository")
	private VendorMasterApiEntityRepository vendorMasterApiRepo;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		String groupCode = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Group Code:{}", groupCode);
		}
		try {
			List<Long> entityIds = entityInfoRepository
					.findEntityIdsByGroupCode(groupCode);
			List<Long> optedEntities = entityConfigPemtRepo
					.getAllEntitiesOpted2B(entityIds, "G26");
			for (Long entityId : optedEntities) {

				List<String> recipientPanList = entityInfoRepository
						.findPanByEntityId(entityId);
				List<String> vendorGstinList = new ArrayList<>();

				// Entities from Vendor Master
				List<VendorMasterUploadEntity> masterEntities = vendorMasterUploadEntityRepository
						.findByIsDeleteFalseAndRecipientPANIn(recipientPanList);
				vendorGstinList.addAll(masterEntities.stream()
						.map(VendorMasterUploadEntity::getVendorGstin)
						.distinct().collect(Collectors.toList()));
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Vendor Master Gstins Size"
									+ " %d and for GroupCode %s",
							masterEntities.size(), message.getGroupCode());
					LOGGER.debug(msg);
				}

				// Entities from Vendor Async Api Push
				List<VendorMasterApiEntity> apiEntities = vendorMasterApiRepo
						.findByIsDeleteFalseAndRecipientPANIn(recipientPanList);
				vendorGstinList.addAll(apiEntities.stream()
						.map(VendorMasterApiEntity::getVendorGstin).distinct()
						.collect(Collectors.toList()));
				LocalDate oneMonthPreviousDate = LocalDate.now()
						.minusMonths(1L);
				String fy = GenUtil.getFinYear(oneMonthPreviousDate);
				String finYear = fy.substring(0, 4) + "-" + fy.substring(4);
				try {
					List<ReturnFilingGstnResponseDto> retFilingList = gstnReturnFiling
							.callGstnApi(vendorGstinList, finYear, true);
					gstnReturnFiling.persistReturnFillingStatus(retFilingList,
							true);
				} catch (Exception e) {

				}

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Async Api Push Size" + " %d and for GroupCode %s",
							apiEntities.size(), message.getGroupCode());
					LOGGER.debug(msg);
				}

			}

		} catch (Exception e) {
			LOGGER.error(
					"Exception while processing the vendor Filing Frequency:",
					e);
		}
	}
}
