package com.ey.advisory.controller;

public interface SFTPFileTransferServiceTest {

	public boolean uploadFile(String localFilePath, String remoteFilePath);

	public boolean downloadFile(String localFilePath, String remoteFilePath);
	
	public boolean downloadFiles(String localDir, String remoteFilePath);
}
