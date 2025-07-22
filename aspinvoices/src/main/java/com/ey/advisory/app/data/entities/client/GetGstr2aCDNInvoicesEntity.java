/*package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "GETGSTR2A_CDN_CDNA_DETAILS")
public class GetGstr2aCDNInvoicesEntity {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "ID")
		protected Long id;

		@Column(name = "GSTIN")
		protected String gstin;
		
		@Column(name = "RET_PERIOD")
		protected String returnPeriod;

		@Column(name = "CTIN")
		protected String countergstin;

		@Column(name = "CFS")
		protected String counFillStatus;

		@Column(name = "CHKSUM")
		protected String checkSum;

		@Column(name = "NOTE_TYPE")
		protected String credDebRefVoucher;

		@Column(name = "NOTE_NUMBER")
		protected String credDebRefVoucherNum;
		
		@Column(name = "ORG_NOTE_NUMBER")
		protected String oriCredDebNum;

		@Column(name = "NOTE_DATE")
		protected LocalDate credDebRefVoucherDate;
		
		@Column(name = "ORG_NOTE_DATE")
		protected LocalDate oriCredDebDate;

		@Column(name = "INV_NUMBER")
		protected String invNum;
		
		@Column(name = "P_GST")
		protected String preGst;
		
		@Column(name = "DIFF_PERCENT")
		protected BigDecimal diffvalue;
		
		@Column(name = "NOTE_VAL")
		protected BigDecimal notevalue;

		@Column(name = "INV_DATE")
		protected LocalDate invDate;
	
		@Column(name = "ITEM_NUMBER")
		protected Integer itemnum;
		
		@Column(name = "TAX_VAL")
		protected BigDecimal taxval;

		@Column(name = "IGST_AMT")
		protected BigDecimal igstamt;

		@Column(name = "CGST_AMT")
		protected BigDecimal cgstamt;

		@Column(name = "SGST_AMT")
		protected BigDecimal sgstamt;

		@Column(name = "CESS_AMT")
		protected BigDecimal cessamt;
		
		@Column(name = "TAX_RATE")
		protected BigDecimal taxrate;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getGstin() {
			return gstin;
		}

		public void setGstin(String gstin) {
			this.gstin = gstin;
		}

		public String getReturnPeriod() {
			return returnPeriod;
		}

		public void setReturnPeriod(String returnPeriod) {
			this.returnPeriod = returnPeriod;
		}

		public String getCountergstin() {
			return countergstin;
		}

		public void setCountergstin(String countergstin) {
			this.countergstin = countergstin;
		}

		public String getCounFillStatus() {
			return counFillStatus;
		}

		public void setCounFillStatus(String counFillStatus) {
			this.counFillStatus = counFillStatus;
		}

		public String getCheckSum() {
			return checkSum;
		}

		public void setCheckSum(String checkSum) {
			this.checkSum = checkSum;
		}

		public String getCredDebRefVoucher() {
			return credDebRefVoucher;
		}

		public void setCredDebRefVoucher(String credDebRefVoucher) {
			this.credDebRefVoucher = credDebRefVoucher;
		}

		public String getCredDebRefVoucherNum() {
			return credDebRefVoucherNum;
		}

		public void setCredDebRefVoucherNum(String credDebRefVoucherNum) {
			this.credDebRefVoucherNum = credDebRefVoucherNum;
		}

		public String getOriCredDebNum() {
			return oriCredDebNum;
		}

		public void setOriCredDebNum(String oriCredDebNum) {
			this.oriCredDebNum = oriCredDebNum;
		}

		public LocalDate getCredDebRefVoucherDate() {
			return credDebRefVoucherDate;
		}

		public void setCredDebRefVoucherDate(LocalDate credDebRefVoucherDate) {
			this.credDebRefVoucherDate = credDebRefVoucherDate;
		}

		public LocalDate getOriCredDebDate() {
			return oriCredDebDate;
		}

		public void setOriCredDebDate(LocalDate oriCredDebDate) {
			this.oriCredDebDate = oriCredDebDate;
		}

		public String getInvNum() {
			return invNum;
		}

		public void setInvNum(String invNum) {
			this.invNum = invNum;
		}

		public String getPreGst() {
			return preGst;
		}

		public void setPreGst(String preGst) {
			this.preGst = preGst;
		}

		public BigDecimal getDiffvalue() {
			return diffvalue;
		}

		public void setDiffvalue(BigDecimal diffvalue) {
			this.diffvalue = diffvalue;
		}

		public BigDecimal getNotevalue() {
			return notevalue;
		}

		public void setNotevalue(BigDecimal notevalue) {
			this.notevalue = notevalue;
		}

		public LocalDate getInvDate() {
			return invDate;
		}

		public void setInvDate(LocalDate invDate) {
			this.invDate = invDate;
		}

		public Integer getItemnum() {
			return itemnum;
		}

		public void setItemnum(Integer itemnum) {
			this.itemnum = itemnum;
		}

		public BigDecimal getTaxval() {
			return taxval;
		}

		public void setTaxval(BigDecimal taxval) {
			this.taxval = taxval;
		}

		public BigDecimal getIgstamt() {
			return igstamt;
		}

		public void setIgstamt(BigDecimal igstamt) {
			this.igstamt = igstamt;
		}

		public BigDecimal getCgstamt() {
			return cgstamt;
		}

		public void setCgstamt(BigDecimal cgstamt) {
			this.cgstamt = cgstamt;
		}

		public BigDecimal getSgstamt() {
			return sgstamt;
		}

		public void setSgstamt(BigDecimal sgstamt) {
			this.sgstamt = sgstamt;
		}

		public BigDecimal getCessamt() {
			return cessamt;
		}

		public void setCessamt(BigDecimal cessamt) {
			this.cessamt = cessamt;
		}

		public BigDecimal getTaxrate() {
			return taxrate;
		}

		public void setTaxrate(BigDecimal taxrate) {
			this.taxrate = taxrate;
		}
		
		
		@Override
		public String toString() {
			return "GetGstr2CDNInvoicesEntity [id=" + id + ", gstin=" + gstin
					+ ", returnPeriod=" + returnPeriod + ", countergstin="
					+ countergstin + ", counFillStatus=" + counFillStatus + ", checkSum="
					+ checkSum + ", credDebRefVoucher="
					+ credDebRefVoucher + ", credDebRefVoucherNum=" 
					+ credDebRefVoucherNum + ", oriCredDebNum="
					+ oriCredDebNum + ", credDebRefVoucherDate=" + credDebRefVoucherDate
					+ ", oriCredDebDate=" + oriCredDebDate + ", invNum="
					+ invNum + ", preGst="
					+ preGst + ", diffvalue="
					+ diffvalue + ", notevalue=" + notevalue
					+ ", invDate=" + invDate + ", itemnum=" + itemnum
					+ ", taxval=" + taxval + ", igstamt="
					+ igstamt + ", cgstamt=" + cgstamt + ", sgstamt="
					+ sgstamt + ", cessamt=" + cessamt + ", taxrate=" + taxrate
					+ "]";
		
		}



}
*/