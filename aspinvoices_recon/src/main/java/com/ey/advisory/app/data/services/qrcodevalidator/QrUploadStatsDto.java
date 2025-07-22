/**
 * 
 */
package com.ey.advisory.app.data.services.qrcodevalidator;

import java.io.Serializable;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFJSONResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseSummaryEntity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class QrUploadStatsDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("qrUploadCounts")
	private QRCountDto uploadCounts;

	@Expose
	@SerializedName("misMatchCounts")
	private List<QRMisMatchCount> misMatCounts;

	@Expose
	@SerializedName("gstinStats")
	private List<QRGstnPanStats> gstnStats;

	@Expose
	@SerializedName("panStats")
	private List<QRGstnPanStats> panStats;

	@Expose
	@SerializedName("qrSummaryData")
	private List<QRResponseSummaryEntity> qrSummaryData;
	
	@Expose
	@SerializedName("qrpdfSummaryData")
	private List<QRPDFResponseSummaryEntity> qrpdfSummaryData;

	@Expose
	@SerializedName("qrpdfJsonSummaryData")
	private List<QRPDFJSONResponseSummaryEntity> qrpdfJsonSummaryData;

}
