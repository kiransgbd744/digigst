package com.ey.advisory.app.data.services.qrcodevalidator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class QRCountDto {

	@Expose
	@SerializedName("totalDoc")
	private int totalDocCnt;

	@Expose
	@SerializedName("qrProc")
	private int qrCnt;

	@Expose
	@SerializedName("sigMisMatch")
	private int sigMisCnt;

	@Expose
	@SerializedName("fullMatch")
	private int fullMatCnt;

	@Expose
	@SerializedName("errMatch")
	private int errCnt;

	public void incrementTotalCnt() {
		this.totalDocCnt++;
	}

	public void incrementQrCnt() {
		this.qrCnt++;
	}

	public void incrementSigMisCnt() {
		this.sigMisCnt++;
	}

	public void incrementFullMatCnt() {
		this.fullMatCnt++;
	}

	public void incrementErrCnt() {
		this.errCnt++;
	}

	public QRCountDto(int totalDocCnt, int qrCnt, int sigMisCnt, int fullMatCnt,
			int errCnt) {
		this.totalDocCnt = totalDocCnt;
		this.qrCnt = qrCnt;
		this.sigMisCnt = sigMisCnt;
		this.fullMatCnt = fullMatCnt;
		this.errCnt = errCnt;
	}

	public QRCountDto() {
		super();
	}
}
