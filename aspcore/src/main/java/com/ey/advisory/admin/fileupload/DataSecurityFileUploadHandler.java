package com.ey.advisory.admin.fileupload;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.OnboardingFileTraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;

@Component("DataSecurityFileUploadHandler")
public class DataSecurityFileUploadHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DataSecurityFileUploadHandler.class);

	@Autowired
	@Qualifier("OnboardingDefaultTraverserFactoryImpl")
	private OnboardingFileTraverserFactory onboardingFileTraverserFactory;

	@Autowired
	@Qualifier("DataSecurityFileUploadServiceImpl")
	private DataSecurityFileUploadServiceImpl dataSecFileUploadService;

	public List<String> dataStatusFileUpload(
			@RequestParam("file") MultipartFile[] files, String groupCode,
			Long entityId) {
		List<String> errorMsg = new ArrayList<>();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"DataSecurityFileUploadHandler dataStatusFileUpload Begin");
		}
		try {
			// Assuming that multipart request has only one part
			MultipartFile file = files[0];

			// Get uploaded file Name and reference to the input stream of the
			// uploaded file
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());

			InputStream stream = file.getInputStream();

			TabularDataSourceTraverser traverser = onboardingFileTraverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(18);

			// Add Dummy row Handler that will keep the counting
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(stream, layout, rowHandler, null);
			Object[] getHeaders = rowHandler.getHeaderRow();
			List<Object[]> list = rowHandler.getFileUploadList();
			removeNullRecords(list, 18);

			errorMsg = dataSecFileUploadService.uploadDataSecurity(list,
					getHeaders, groupCode, entityId);
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"DataSecurityFileUploadHandler dataStatusFileUpload Begin");
		}
		return errorMsg;
	}

	private void removeNullRecords(List<Object[]> list, int count) {
		List<Object[]> matchedNullList = new ArrayList<>();
		list.forEach(obj -> {
			List<Object> nullList = Arrays.asList(obj).stream()
					.filter(val -> val == null).collect(Collectors.toList());
			if (Arrays.asList(obj).contains(null) && nullList.size() == count) {
				matchedNullList.add(obj);
			}
		});
		list.removeAll(matchedNullList);
	}
}
