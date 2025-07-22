package com.ey.advisory.app.data.entities.client;


	
	import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 
 * @author Anand3.M
 *
 */
	@Entity
	@Table(name = "FILE_STATUS")
	public class Gstr2FileStatusEntity {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "ID")
		private Long id;
		
		@Column(name = "FILE_NAME")
		private String fileName;

		@Column(name = "FILE_TYPE")
		private String fileType;
		
		@Column(name = "FILE_STATUS")
		private String fileStatus;

		@Column(name = "UPLOAD_START_TIME")
		private LocalDateTime startOfUploadTime;

		@Column(name = "UPLOAD_END_TIME")
		private LocalDateTime endOfUploadTime;
		
		@Column(name = "TOTAL_RECORDS")
		private Integer total;

		@Column(name = "PROCESSED_RECORDS")
		private Integer processed;

		@Column(name = "ERROR_RECORDS")
		private Integer error;

		@Column(name = "INFORMATION_RECORDS")
		private Integer information;

		@Column(name = "CREATED_BY")
		private String updatedBy;
		
		@Column(name = "CREATED_ON")
		private LocalDateTime updatedOn;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileType() {
			return fileType;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}

		public String getFileStatus() {
			return fileStatus;
		}

		public void setFileStatus(String fileStatus) {
			this.fileStatus = fileStatus;
		}

		
		public LocalDateTime getStartOfUploadTime() {
			return startOfUploadTime;
		}

		public void setStartOfUploadTime(LocalDateTime startOfUploadTime) {
			this.startOfUploadTime = startOfUploadTime;
		}

		public LocalDateTime getEndOfUploadTime() {
			return endOfUploadTime;
		}

		public void setEndOfUploadTime(LocalDateTime endOfUploadTime) {
			this.endOfUploadTime = endOfUploadTime;
		}

		public Integer getTotal() {
			return total;
		}

		public void setTotal(Integer total) {
			this.total = total;
		}

		public Integer getProcessed() {
			return processed;
		}

		public void setProcessed(Integer processed) {
			this.processed = processed;
		}

		public Integer getError() {
			return error;
		}

		public void setError(Integer error) {
			this.error = error;
		}

		public Integer getInformation() {
			return information;
		}

		public void setInformation(Integer information) {
			this.information = information;
		}

		public String getUpdatedBy() {
			return updatedBy;
		}

		public void setUpdatedBy(String updatedBy) {
			this.updatedBy = updatedBy;
		}

		
		public LocalDateTime getUpdatedOn() {
			return updatedOn;
		}

		public void setUpdatedOn(LocalDateTime updatedOn) {
			this.updatedOn = updatedOn;
		}

		@Override
		public String toString() {
			return "Gstr2FileStatusEntity [id=" + id + ", fileName=" + fileName
					+ ", fileType=" + fileType + ", fileStatus=" + fileStatus
					+ ", startOfUploadTime=" + startOfUploadTime
					+ ", endOfUploadTime=" + endOfUploadTime + ", total=" + total
					+ ", processed=" + processed + ", error=" + error
					+ ", information=" + information + ", updatedBy=" + updatedBy
					+ ", updatedOn=" + updatedOn + "]";
		}



}
