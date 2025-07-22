package com.ey.advisory.app.data.daos.client;

import java.util.List;

import org.javatuples.Pair;

public interface Gstr1EinvProcessedDao {

	public List<String> getActiveDocsById(List<String> docHeaderIds);

	public int updateStatusForProcessed(Long docHeaderId, String prevResp,
			String userResp);

	public Pair<Boolean, Boolean> checkSaveAndSentGstinStatus(Long valueOf);

}
