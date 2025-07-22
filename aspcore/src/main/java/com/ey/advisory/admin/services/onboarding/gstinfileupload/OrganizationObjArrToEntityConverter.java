/**
 * 
 */
package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityAtConfigEntity;
import com.ey.advisory.admin.data.entities.client.EntityAtValueEntity;
import com.ey.advisory.admin.data.repositories.client.EntityAtConfiRepository;
import com.ey.advisory.admin.data.repositories.client.EntityAtValueRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.GroupService;

/**
 * @author Sasidhar Reddy
 *
 */

@Service("OrganizationObjArrToEntityConverter")
public class OrganizationObjArrToEntityConverter {

	@Autowired
	@Qualifier("entityAtValueRepository")
	private EntityAtValueRepository entityAtValueRepository;

	@Autowired
	@Qualifier("entityAtConfiRepository")
	private EntityAtConfiRepository entityAtConfiRepository;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("groupService")
	private GroupService groupService;

	protected static final String[] HEADERS_ARR = { "Plant Code", "Division",
			"Sub Division", "Location", "Sales Organization",
			"Distribution Channel", "Purchase Organization", "Profit Centre 1",
			"Profit Centre 2", "Profit Centre 3", "Profit Centre 4",
			"Profit Centre 5", "Profit Centre 6", "Profit Centre 7",
			"Profit Centre 8", "Source ID" };

	public Set<EntityAtValueEntity> convert(
			List<Object[]> organizationConfigObjects, Object[] header,
			String groupCode, Long entityId) {

		// Get the group id.
		Long groupId = groupInfoDetailsRepository.findByGroupId(groupCode);

		List<Long> ids = new ArrayList<>();
		validateHeadersFromExcelSheet(header);
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		Set<EntityAtValueEntity> entityAtValueEntities = new HashSet<EntityAtValueEntity>();

		for (Object[] organizationConfig : organizationConfigObjects) {

			// Get the list of all Attr Configs for the entity id from the
			// database.
			List<EntityAtConfigEntity> entityAtConfigEntities = entityAtConfiRepository
					.findAllEntityAtConfigEntity(groupCode, entityId);
			// Build a map, with key as the attr name and value as the attribute
			// config.
			Map<String, List<EntityAtConfigEntity>> map = entityAtConfigEntities
					.stream()
					.collect(Collectors.groupingBy(x -> x.getAtName()));
			IntStream.range(0, HEADERS_ARR.length).forEach(idx -> {
				String headerName = HEADERS_ARR[idx];
				String data = organizationConfig[idx] != null
						? String.valueOf(organizationConfig[idx]) : null;

				// Trim white spaces.
				// If the string in the current cell is empty, then
				// do not insert an AttrValue. Proceed with the next.
				if (data != null && !data.trim().isEmpty()) {
					List<EntityAtConfigEntity> configs = map.get(headerName);
					EntityAtConfigEntity config = (configs != null)
							? configs.get(0) : null;
					if (config == null) {
						String error = "Column data not present for the header -> "
								+ headerName + " in the uploaded excel";
						throw new OnboardingFileUploadException(error);
					}
					if (config != null && data != null
							&& !data.trim().isEmpty()) {
						Long id = entityAtValueRepository
								.getIdsBasedOnAtValue(data, entityId);
						ids.add(id);
						EntityAtValueEntity entityAtValueEntity = new EntityAtValueEntity();
						entityAtValueEntity.setGroupId(groupId);
						entityAtValueEntity.setGroupCode(groupCode);
						entityAtValueEntity.setEntityId(entityId);
						entityAtValueEntity.setAtCode(config.getAtCode());
						// Id of the Attr Config
						entityAtValueEntity.setEntityAtConfigId(config.getId());
						entityAtValueEntity.setAtValue(data);
						entityAtValueEntity.setDelete(false);
						entityAtValueEntity.setCreatedBy(userName);
						entityAtValueEntity.setCreatedOn(EYDateUtil
								.toISTDateTimeFromUTC(LocalDateTime.now()));
						entityAtValueEntity.setModifiedBy(userName);
						entityAtValueEntity.setModifiedOn(EYDateUtil
								.toISTDateTimeFromUTC(LocalDateTime.now()));
						entityAtValueEntities.add(entityAtValueEntity);
					}
				}
			});
		}
		if (!ids.isEmpty()) {
			entityAtValueRepository.deleteOldRecordForEntity(ids);
		}
		return entityAtValueEntities;
	}

	private void validateHeadersFromExcelSheet(Object[] header) {

		// Insufficient columns in the excel sheet.
		if (header.length < HEADERS_ARR.length) {
			String msg = "Insufficient number of columns found "
					+ "in the uploaded excel sheet.";
			throw new OnboardingFileUploadException(msg);
		}

		// Expected no of columns.
		int reqNoOfCols = HEADERS_ARR.length;
		List<String> errCols = new ArrayList<>();

		IntStream.range(0, reqNoOfCols).forEach(idx -> {
			String reqHeaderName = HEADERS_ARR[idx];
			String hdrNameFromExcel = String.valueOf(header[idx]);
			if (hdrNameFromExcel == null) {
				// is an error.
				errCols.add(reqHeaderName);
			} else {
				if (!reqHeaderName.equals(hdrNameFromExcel)) {
					errCols.add(reqHeaderName);
				}
			}
		});

		if (!errCols.isEmpty()) {
			String msg = "Invalid Column Headers found in uploaded file.";
			throw new OnboardingFileUploadException(msg);
		}
	}

}
