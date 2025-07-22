package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.data.views.client.GstnSaveAndSubmitView;
import com.ey.advisory.app.docs.dto.GstnSaveSubmitReqDto;

/**
 * 
 * @author Mohana.Dasari
 *
 */
public interface GstnSaveAndSubmitDao {

	List<GstnSaveAndSubmitView> getGstnSaveAdnSubmitDocs
												(GstnSaveSubmitReqDto request);
}
