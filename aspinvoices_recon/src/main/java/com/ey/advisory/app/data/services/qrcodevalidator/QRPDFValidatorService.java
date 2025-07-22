package com.ey.advisory.app.data.services.qrcodevalidator;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.ey.advisory.core.config.Config;

public interface QRPDFValidatorService {

	public void revIntegrateQRPDFData(List<Long> activeIds,
			Map<String, Config> configMap, File tempDir);

}
