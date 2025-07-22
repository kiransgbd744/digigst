package com.ey.advisory.controllers.gstr3b;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.gstr3b.Gstr3BSaveStatusDownload;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */

@RestController
@Slf4j
public class Gstr3BSaveStatusDownloadController {

	@Autowired
	@Qualifier("Gstr3BSaveStatusDownloadImpl")
	Gstr3BSaveStatusDownload gstr3BSaveStatusDownload;

	@GetMapping(value = "/ui/download3BSaveResponse", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadSaveStatusRecords(HttpServletRequest request,
			HttpServletResponse response) {
		JsonObject resp = new JsonObject();
		try {
			String fileId = request.getParameter("id");
			if (fileId == null || fileId.isEmpty()) {
				resp.add("hdr", new Gson()
						.toJsonTree(new APIRespDto("E", "Id is Mandatory")));
				response.getWriter().println(resp.toString());
				return;
			}
			Long id = Long.valueOf(fileId);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Begin download save status in GSTR3 with id : %d ",
						id);
				LOGGER.debug(msg);
			}

			gstr3BSaveStatusDownload.downloadSaveStatusDetails(id, response);

		} catch (Exception ex) {
			String msg = " Exception while Downloading the 3B Error Json ";
			LOGGER.error(msg, ex);
		}
	}

}
