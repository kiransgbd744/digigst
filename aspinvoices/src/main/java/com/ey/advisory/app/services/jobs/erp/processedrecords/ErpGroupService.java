package com.ey.advisory.app.services.jobs.erp.processedrecords;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.ErpInfoEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.docs.dto.anx1.ErpGroupReqDto;
import com.ey.advisory.app.docs.dto.anx1.ErpGroupResDto;
import com.ey.advisory.app.docs.dto.anx1.ErpGroupSaveReqDto;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Component("ErpGroupService")
@Slf4j
public class ErpGroupService {

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("ErpInfoEntityRepository")
	private ErpInfoEntityRepository erpInfoEntityRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	public List<ErpGroupResDto> getErpValues(ErpGroupReqDto req) {
		List<ErpGroupResDto> listErp = new ArrayList<>();
		String gCode = req.getGroupCode();
		List<GSTNDetailEntity> entityInfo = gSTNDetailRepository
				.findAllByGroupCode(gCode);
		List<Long> ids = new ArrayList<>();
		for (GSTNDetailEntity erpData : entityInfo) {
			ErpInfoEntity header = erpData.getHeader();
			if (header != null) {
				Long id = header.getId();
				ids.add(id);
			}
		}
		Iterable<ErpInfoEntity> findAllById = erpInfoEntityRepository
				.findAllById(ids);
		for (ErpInfoEntity erps : findAllById) {
			ErpGroupResDto erp = new ErpGroupResDto();
			erp.setErpId(erps.getId());
			erp.setErpName(erps.getHostName());
			listErp.add(erp);
		}
		return listErp;
	}

	public ResponseEntity<String> saveErpValues(ErpGroupSaveReqDto req) {

		try {
			String groupCode = req.getGroupCode();
			Long entityId = req.getEntityId();
			List<ErpGroupResDto> erpGroupReq = req.getErpGroupReq();

			for (ErpGroupResDto erpDtos : erpGroupReq) {
				String gstin = erpDtos.getGstin();
				ErpInfoEntity erp = new ErpInfoEntity();
				GSTNDetailEntity entityInfo = gSTNDetailRepository
						.findByGroupAndGstnAll(groupCode, entityId, gstin);
				if(entityInfo != null ){
				erp.setId(erpDtos.getErpId());
				erp.setHostName(erpDtos.getErpName());
				entityInfo.setHeader(erp);
				gSTNDetailRepository.save(entityInfo);
				}
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Erp details ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
		return new ResponseEntity<>("SUCCESS",
				HttpStatus.OK);
	}

}
