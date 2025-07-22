package com.ey.advisory.app.data.services.pdfreader.dto;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class PDFCountDto {

	@Expose
	@SerializedName("totalDoc")
	private int totalDocCnt;
	
	@Expose
	@SerializedName("totalDoc")
	private int excelRowCnt;
	
	public void incrementTotalCnt() {
		this.totalDocCnt++;
	}
	
	public void incrementExcelRowCnt() {
		this.excelRowCnt++;
	}

	public PDFCountDto() {
		super();
	}
}
