package com.ey.advisory.app.docs.dto;

public class UploadFileResponseDto {
	private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
/*
    public UploadFileResponseDto() {
		super();
	}

	public UploadFileResponseDto(String fileName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }
*/
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

	@Override
	public String toString() {
		return "UploadFileResponseDto [fileName=" + fileName
				+ ", fileDownloadUri=" + fileDownloadUri + ", fileType="
				+ fileType + ", size=" + size + "]";
	}
    
    
}
