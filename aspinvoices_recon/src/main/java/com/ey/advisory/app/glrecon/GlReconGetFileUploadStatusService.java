package com.ey.advisory.app.glrecon;

import java.util.List;

import org.javatuples.Pair;

/**
 * 
 * @author Sakshi.jain
 *
 */
/*
 * This interface represent the Service for uploading the files and reading data
 * from file and pushing data to Document Service
 */
public interface GlReconGetFileUploadStatusService {
	 Pair<List<GlFileStatusDto>, Integer> getFileUploadStatus(GlFileStatusReqDto reqDto, int pageNum, int pageSize);

}
