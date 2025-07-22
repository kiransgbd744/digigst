package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EInvoiceDataStatusItemDto {

	@Expose
	@SerializedName(value = "receivedDate")
	private String receivedDate;
	@Expose
	@SerializedName(value = "totalRecords")
	private BigInteger totalRecords = BigInteger.ZERO;
	@Expose
	@SerializedName(value = "processed")
	private BigInteger processed = BigInteger.ZERO;
	@Expose
	@SerializedName(value = "error")
	private BigInteger error = BigInteger.ZERO;
	@Expose
	@SerializedName(value = "EInvNA")
	private BigInteger eInvNA = BigInteger.ZERO;
	@Expose
	@SerializedName(value = "EInvProcessd")
	private BigInteger eInvProcessd = BigInteger.ZERO;
	@Expose
	@SerializedName(value = "EInvError")
	private BigInteger eInvError = BigInteger.ZERO;
	@Expose
	@SerializedName(value = "EWBNA")
	private BigInteger ewBNA = BigInteger.ZERO;
	@Expose
	@SerializedName(value = "EWBProcessd")
	private BigInteger ewbProcessd = BigInteger.ZERO;
	@Expose
	@SerializedName(value = "EWBError")
	private BigInteger ewbError = BigInteger.ZERO;
	@Expose
	@SerializedName(value = "GSTRetNA")
	private BigInteger gstRetNA = BigInteger.ZERO;
	@Expose
	@SerializedName(value = "GSTRetProcessd")
	private BigInteger gstRetProcessd = BigInteger.ZERO;
	@Expose
	@SerializedName(value = "GSTRetError")
	private BigInteger gstRetError = BigInteger.ZERO;
}
