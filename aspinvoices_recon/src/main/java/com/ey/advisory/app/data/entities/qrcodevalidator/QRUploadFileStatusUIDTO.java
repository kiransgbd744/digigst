package com.ey.advisory.app.data.entities.qrcodevalidator;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class QRUploadFileStatusUIDTO {

	@Expose
	@SerializedName("feature")
	private String feature;

	@Expose
	@SerializedName("data")
	private List<QRUploadFileStatusDTO> data;
}
