package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.VendorGstinFilingTypeEntity;
import com.ey.advisory.app.data.repositories.client.VendorGstinFilingTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterConfigEntityRepository;
import com.ey.advisory.app.data.services.compliancerating.VendorComplianceRatingHelperService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstnapi.services.GenerateAuthTokenService;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Component("VendorGstinFilingTypeServiceImpl")
public class VendorGstinFilingTypeServiceImpl
		implements VendorGstinFilingTypeService {

	@Autowired
	@Qualifier("VendorMasterConfigEntityRepository")
	private VendorMasterConfigEntityRepository vendorMasterConfigRepo;

	@Autowired
	@Qualifier("VendorGstinFilingTypeRepository")
	private VendorGstinFilingTypeRepository vendorGstinFilingTypeRepo;

	@Autowired
	private GstnReturnFilingStatus gstnReturnFiling;

	@Autowired
	@Qualifier("GenerateAuthTokenServiceImpl")
	GenerateAuthTokenService authTokenService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService gstnAuthService;

	@Autowired
	@Qualifier("VendorComplianceRatingHelperServiceImpl")
	private VendorComplianceRatingHelperService ratingHelperService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	@Override
	public void stampVendorFilingType() {
		try {

			if ("I".equals(gstnAuthService.getAuthTokenStatusForGstin(
					APIConstants.DEFAULT_PUBLIC_API_GSTIN))
					&& !authTokenService.generateAuthToken(
							APIConstants.DEFAULT_PUBLIC_API_GSTIN, null)) {
				LOGGER.error(
						"Not able to generate Public Auth Token while Fetching Filling Status");
				throw new AppException(
						"Not able to generate Public Auth Token");
			}

			// get Distinct Vendor Master config GSTIN.
			List<String> vendorGstins = vendorMasterConfigRepo
					.getDistinctVendorGstins();

			vendorGstins.addAll(ratingHelperService
					.getListOfCustomerGstin(new ArrayList<>()));

			vendorGstins.addAll(gstNDetailRepository.getActiveGstins());

			if (vendorGstins.isEmpty()) {
				LOGGER.info("No vendorGstins found in Vendor Master Config");
				return;
			}
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String
						.format("Number of Source vendors  present in Vendor  "
								+ "Master Config are %s", vendorGstins.size());
				LOGGER.debug(logMsg);
			}
			List<String> fyList = getTwoPreviousFys();

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Inside VendorGstinFilingTypeServiceImpl"
								+ " starting stamping process for fy's" + " %s",
						fyList);
				LOGGER.debug(logMsg);
			}

			Map<String, Integer> currentMap = vendorGstins.stream().distinct()
					.collect(Collectors.toMap(s -> s.concat(fyList.get(0)),
							s -> 0));
			Map<String, Integer> previousMap = vendorGstins.stream().distinct()
					.collect(Collectors.toMap(s -> s.concat(fyList.get(1)),
							s -> 0));

			Map<String, Integer> overAllMap = new HashMap<>();
			overAllMap.putAll(currentMap);
			overAllMap.putAll(previousMap);

			List<VendorGstinFilingTypeEntity> stampedVendors = new ArrayList<>();

			int count = vendorGstinFilingTypeRepo.getOverallCount();
			if (count > 0) {
				List<List<String>> chunks = Lists.partition(vendorGstins, 2000);
				for (List<String> chunk : chunks) {
					List<VendorGstinFilingTypeEntity> stampedVendorChunks = new ArrayList<>();
					stampedVendorChunks = vendorGstinFilingTypeRepo
							.findByGstinInAndFyIn(chunk, fyList);
					stampedVendors.addAll(stampedVendorChunks);
				}
			}
			if (!stampedVendors.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							"Number of Stampted vendors already present in "
									+ " VendorGstinFilingType are %s",
							stampedVendors.size());
					LOGGER.debug(logMsg);
				}
				Set<String> stampedSet = stampedVendors.stream()
						.map(p -> p.getGstin().concat(p.getFy()))
						.collect(Collectors.toSet());
				overAllMap.keySet().removeAll(stampedSet);
			}

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Doing GET call for number of gstins are  %s",
						vendorGstins.size() - stampedVendors.size());
				LOGGER.debug(logMsg);
			}

			List<VendorGstinFilingTypeEntity> entitiesToBePersisted = new ArrayList<>();
			for (Map.Entry<String, Integer> entry : overAllMap.entrySet()) {
				if (entry.getKey().length() == 22) {
					String gstin = entry.getKey().substring(0, 15);
					String finYear = entry.getKey().substring(15, 22);

					PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
							PublicApiConstants.VFILING_TYPE_JOB);
					gstnReturnFiling.callGstnApi(gstin, finYear, "GSTR1",
							entitiesToBePersisted);
				}
			}
			if (!entitiesToBePersisted.isEmpty())
				vendorGstinFilingTypeRepo.saveAll(entitiesToBePersisted);

		} catch (Exception ee) {
			String msg = "Exception occured while stamping vendorGstins with"
					+ " their respective filyingType";
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}
	}

	private List<String> getTwoPreviousFys() {
		String currentFy = GenUtil.getCurrentFinancialYear();
		int previousFy2 = Integer.valueOf(currentFy.substring(5, 7)) - 1;
		int previousFy1 = Integer.valueOf(currentFy.substring(2, 4)) - 1;

		String previousFy = currentFy.substring(0, 2)
				.concat(String.valueOf(previousFy1).concat("-")
						.concat(String.valueOf(previousFy2)));
		List<String> fyList = new ArrayList<>();
		fyList.add(currentFy);
		fyList.add(previousFy);
		return fyList;
	}

}
