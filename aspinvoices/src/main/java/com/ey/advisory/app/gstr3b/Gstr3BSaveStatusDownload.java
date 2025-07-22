package com.ey.advisory.app.gstr3b;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Arun.KA
 *
 */
public interface Gstr3BSaveStatusDownload {

	public void downloadSaveStatusDetails(Long id,
			HttpServletResponse response) throws Exception;

}
