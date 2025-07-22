package com.ey.advisory.app.anx2.vendorsummary;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.AppException;


@Service("VendorSgstinServiceImpl")
public class VendorSgstinServiceImpl implements VendoeSgstinService {

	@Autowired
	@Qualifier("VendorSgstinFilterDaoImpl")
	VendorSgstinFilterDao sgstinsDao;
	@Override
	public List<String> getSgstinsforCgstins(List<String> cgstins, 
								String taxPeriod) {
		List<String> sgstins = sgstinsDao
				 .findSgstinsForCgstins(cgstins, taxPeriod);
		if (CollectionUtils.isEmpty(sgstins)) {
			String msg = String.format("No Sgstins for following cgstin");
			throw new AppException(msg);
		}
		return sgstins;
	}

}