package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


	
	@Entity
	@Table(name = "GSTR2_NILNONEXMT_DETAILS")
	public class Gstr2NilDetailsEntity {

		@Expose
		@SerializedName("id")
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "ID")
		protected Long id;
		
		@Expose
		@SerializedName("fileId")
		@Column(name = "FILE_ID")
		private Long fileId;

		@Expose
		@SerializedName("sgstin")
		@Column(name = "GSTIN")
		protected String sgstin;

		@Expose
		@SerializedName("returnPeriod")
		@Column(name = "RET_PERIOD")
		protected String returnPeriod;
		
		@Expose
		@SerializedName("derivedRetPeriod")
		@Column(name = "DERIVED_RET_PERIOD")
		protected Integer derivedRetPeriod;
		
		@Expose
		@SerializedName("typeOfSupply")
		@Column(name = "SUPPLY_TYPE")
		private String typeOfSupply;

		@Expose
		@SerializedName("tableType")
		@Column(name = "TABLE_TYPE")
		private String tableType;
		
		@Expose
		@SerializedName("taxAmount")
		@Column(name = "TAX_AMT")
		private BigDecimal taxAmount = BigDecimal.ZERO;

		@Expose
		@SerializedName("nilKey")
		@Column(name = "NIL_KEY")
		private String nilKey;

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

		public Integer getDerivedRetPeriod() {
			return derivedRetPeriod;
		}

		public void setDerivedRetPeriod(Integer derivedRetPeriod) {
			this.derivedRetPeriod = derivedRetPeriod;
		}

		public String getTypeOfSupply() {
			return typeOfSupply;
		}

		public void setTypeOfSupply(String typeOfSupply) {
			this.typeOfSupply = typeOfSupply;
		}

		public String getTableType() {
			return tableType;
		}

		public void setTableType(String tableType) {
			this.tableType = tableType;
		}

		public BigDecimal getTaxAmount() {
			return taxAmount;
		}

		public void setTaxAmount(BigDecimal taxAmount) {
			this.taxAmount = taxAmount;
		}

		public String getNilKey() {
			return nilKey;
		}

		public void setNilKey(String nilKey) {
			this.nilKey = nilKey;
		}

		@Override
		public String toString() {
			return "Gstr2NilDetailsEntity [id=" + id + ", fileId=" + fileId
					+ ", sgstin=" + sgstin + ", returnPeriod=" + returnPeriod
					+ ", derivedRetPeriod=" + derivedRetPeriod + ", typeOfSupply="
					+ typeOfSupply + ", tableType=" + tableType + ", taxAmount="
					+ taxAmount + ", nilKey=" + nilKey + "]";
		}

			
	}



