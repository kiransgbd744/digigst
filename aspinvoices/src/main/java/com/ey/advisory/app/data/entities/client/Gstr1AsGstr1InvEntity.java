package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "GSTR1_GSTN_INV_SERIES")
@Setter
@Getter
@ToString
public class Gstr1AsGstr1InvEntity {

	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1_GSTN_INV_SERIES_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("asEnteredId")
	@Column(name = "AS_ENTERED_ID")
	private Long asEnteredId;

	@Expose
	@SerializedName("sgstin")
	@Column(name = "SUPPLIER_GSTIN")
	private String sgstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;
/*
	@Expose
	@SerializedName("natureOfDocument")
	@Column(name = "NATURE_OF_DOC")
	private String natureOfDocument;*/

	@Expose
	@SerializedName("serialNo")
	@Column(name = "SERIAL_NUM")
	private Integer serialNo;

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
	@Column(name = "INV_SERIES_GSTN_INVKEY")
	private String invoiceKey;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	private boolean isDelete;

	public Gstr1AsGstr1InvEntity add(Gstr1AsGstr1InvEntity obj) {
		this.sgstin = obj.sgstin;
		this.returnPeriod = obj.returnPeriod;
		this.serialNo = obj.serialNo;
		//this.natureOfDocument = obj.natureOfDocument;
		this.from = obj.from;
		this.to = obj.to;
		this.asEnteredId = obj.asEnteredId;
		this.fileId = obj.fileId;
		this.isDelete = obj.isDelete;
		this.totalNumber = add(this.totalNumber ,obj.totalNumber);
		this.cancelled = add(this.cancelled , obj.cancelled);
		this.netNumber = add(this.netNumber , obj.netNumber);
        return this;
	}

	private Integer add(Integer no1, Integer no2) {
		if(no1 == null  && no2 == null) 	return 0;
		if(no1 == null)		return no2;
		if(no2 == null)		return no1;
		return no1 + no2 ;
	}
}