package com.ey.advisory.controller.inward.einvoice;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QrPdfVsJsonDownloadDto {

	@Expose
	private String fileId;

	public QrPdfVsJsonDownloadDto() {
	}

}
