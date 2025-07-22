package com.ey.advisory.admin.onboarding.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.GroupInfoEntity;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.common.multitenancy.TenantContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Sasidhar Reddy
 * 
 *         This Class represent controller for uploading files from various
 *         sources and upload into Document Repository
 */
@RestController
public class EntityDetailsFileUploadController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EntityDetailsFileUploadController.class);

	@Autowired
	@Qualifier("EntityDetailsFileUploadHelper")
	private EntityDetailsFileUploadHelper uploadHelper;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	/**
	 * ELEntitlementEntityUpload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */
	@PostMapping(value = "/elEntitlementEntityUpload", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> gstinRegUpload(
			@RequestParam("file") MultipartFile[] files,
			HttpServletRequest req) {
		try {
			String groupCode = TenantContext.getTenantId();
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("GroupCode: {}",groupCode);
			}
			Long groupId = groupInfoDetailsRepository.findByGroupId(groupCode);
			if (groupId == null) {
				GroupInfoEntity entity = new GroupInfoEntity();
				entity.setGroupcode(groupCode);
				entity.setGroupname(groupCode);
				entity.setPancount(Long.valueOf(1));
				entity.setAccountno("0");
				entity.setIs_active(false);
				entity.setGstincount(Long.valueOf(1));
				entity.setCreated_date(LocalDateTime.now());
				entity.setUpdate_date(LocalDateTime.now());
				GroupInfoEntity groupInfoEntity = groupInfoDetailsRepository
						.save(entity);
				groupId = groupInfoEntity.getId();
			}
			return uploadHelper.gstinEntitlementEntityUpload(files, groupCode,
					groupId);
		} catch (Exception ex) {
			String msg = "Error occurred while processing "
					+ "the Entity Registration Upload";
			LOGGER.error(msg, ex);
			List<String> list = new ArrayList<>();
			list.add(msg);
			return uploadHelper.createGstinRegFailureResp(list);
		}
	}

}
