package com.ey.advisory.app.sftp.service;

import java.util.List;

public interface SFTPFileTransferService {

	public boolean uploadFile(String localFilePath, String remoteFilePath);
	
	public boolean uploadFiles(List<String> localFilePath, String remoteFilePath);

	public boolean downloadFile(String localFilePath, String remoteFilePath);

	public boolean downloadFiles(String localDir, String sftpSource,
			List<String> fileExtns);

	public boolean moveFile(String source, String destination,
			String repoSavedFile);

	public boolean moveFiles(List<String> repoSavedFile, String sftpSource,
			String sftpDestination);

}
