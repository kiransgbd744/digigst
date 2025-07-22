package com.ey.advisory.app.gstr3b;

import java.io.InputStream;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr3BSaveStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveStatusRepository;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.core.api.APIConstants;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Component("Gstr3BSaveStatusDownloadImpl")
@Slf4j
public class Gstr3BSaveStatusDownloadImpl implements Gstr3BSaveStatusDownload {

	@Autowired
	@Qualifier("gstr3BSaveStatusRepository")
	Gstr3BSaveStatusRepository gstr3BSaveStatusRepository;

	@Override
	public void downloadSaveStatusDetails(Long id, HttpServletResponse response)
			throws Exception {

		Optional<Gstr3BSaveStatusEntity> entity = gstr3BSaveStatusRepository
				.findById(id);
		if (!entity.isPresent())
			return;

		String fileName = entity.get().getFilePath();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Filename to be downloaded : %s ",
					fileName);
			LOGGER.debug(msg);
		}

		Document document = DocumentUtility.downloadDocument(fileName,
				APIConstants.GSTR3B_SAVE_ERROR_FOLDER);

		if (document == null)
			return;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Document is available in the Repo, Doc Name is %s",
					fileName);
			LOGGER.debug(msg);
		}
		InputStream inputStream = document.getContentStream().getStream();
		response.setContentType("application/json");
		response.setHeader("Content-Disposition",
				String.format("attachment; filename =%s ", fileName));
		IOUtils.copy(inputStream, response.getOutputStream());
		response.flushBuffer();
		

	}

}
