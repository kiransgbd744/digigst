package com.ey.advisory.app.gstr1.einv;

public interface Gstr1GSTINDeleteDataService {

	void validateandSaveFileData(Long fileId, String fileName,
			String folderName);
}
