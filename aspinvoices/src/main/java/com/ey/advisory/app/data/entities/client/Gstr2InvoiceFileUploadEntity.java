package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


	@Entity
	@Table(name = "GSTR2_INVOICE_SERIES_DETAILS")
	public class Gstr2InvoiceFileUploadEntity {

		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "ID")
		private Long id;
		
		@Expose
		@SerializedName("fileId")
		@Column(name = "FILE_ID")
		private Long fileId;

		@Expose
		@SerializedName("sgstin")
		@Column(name = "SUPPLIER_GSTIN")
		private String sgstin;

		@Expose
		@SerializedName("returnPeriod")
		@Column(name = "RET_PERIOD")
		private String returnPeriod;

		@Expose
		@SerializedName("serialNo")
		@Column(name = "SERIAL_NUM")
		private Integer serialNo;

		@Expose
		@SerializedName("natureOfDocument")
		@Column(name = "NATURE_OF_DOC")
		private String natureOfDocument;

		@Expose
		@SerializedName("from")
		@Column(name = "DOC_SERIES_FROM")
		private String from;

		@Expose
		@SerializedName("to")
		@Column(name = "DOC_SERIES_TO")
		private String to;

		@Expose
		@SerializedName("totalNumber")
		@Column(name = "TOT_NUM")
		private Integer totalNumber;

		@Expose
		@SerializedName("cancelled")
		@Column(name = "CANCELED")
		private Integer cancelled;

		@Expose
		@SerializedName("netNumber")
		@Column(name = "NET_NUM")
		private Integer netNumber;

		@Expose
		@SerializedName("invoiceKey")
		@Column(name = "INV_KEY")
		private String invoiceKey;
		
		@Expose
		@SerializedName("derivedRetPeriod")
		@Column(name = "DERIVED_RET_PERIOD")
		protected Integer derivedRetPeriod;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getFileId() {
			return fileId;
		}

		public void setFileId(Long fileId) {
			this.fileId = fileId;
		}

		public String getSgstin() {
			return sgstin;
		}

		public void setSgstin(String sgstin) {
			this.sgstin = sgstin;
		}

		public String getReturnPeriod() {
			return returnPeriod;
		}

		public void setReturnPeriod(String returnPeriod) {
			this.returnPeriod = returnPeriod;
		}

		public Integer getSerialNo() {
			return serialNo;
		}

		public void setSerialNo(Integer serialNo) {
			this.serialNo = serialNo;
		}

		public String getNatureOfDocument() {
			return natureOfDocument;
		}

		public void setNatureOfDocument(String natureOfDocument) {
			this.natureOfDocument = natureOfDocument;
		}

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public Integer getTotalNumber() {
			return totalNumber;
		}

		public void setTotalNumber(Integer totalNumber) {
			this.totalNumber = totalNumber;
		}

		public Integer getCancelled() {
			return cancelled;
		}

		public void setCancelled(Integer cancelled) {
			this.cancelled = cancelled;
		}

		public Integer getNetNumber() {
			return netNumber;
		}

		public void setNetNumber(Integer netNumber) {
			this.netNumber = netNumber;
		}

		public String getInvoiceKey() {
			return invoiceKey;
		}

		public void setInvoiceKey(String invoiceKey) {
			this.invoiceKey = invoiceKey;
		}

		public Integer getDerivedRetPeriod() {
			return derivedRetPeriod;
		}

		public void setDerivedRetPeriod(Integer derivedRetPeriod) {
			this.derivedRetPeriod = derivedRetPeriod;
		}

		@Override
		public String toString() {
			return "Gstr2InvoiceFileUploadEntity [id=" + id + ", fileId=" + fileId
					+ ", sgstin=" + sgstin + ", returnPeriod=" + returnPeriod
					+ ", serialNo=" + serialNo + ", natureOfDocument="
					+ natureOfDocument + ", from=" + from + ", to=" + to
					+ ", totalNumber=" + totalNumber + ", cancelled=" + cancelled
					+ ", netNumber=" + netNumber + ", invoiceKey=" + invoiceKey
					+ ", derivedRetPeriod=" + derivedRetPeriod + "]";
		}
	}




