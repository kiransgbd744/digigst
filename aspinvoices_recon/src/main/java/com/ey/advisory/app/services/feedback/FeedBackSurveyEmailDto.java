package com.ey.advisory.app.services.feedback;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class FeedBackSurveyEmailDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("sapPrimaryEmail")
	@Expose
	private String sapPrimaryEmail;

	@SerializedName("sapSecondaryEmail")
	@Expose
	private List<String> sapSecondaryEmail;

	@SerializedName("source")
	@Expose
	private String source;

	@SerializedName("groupName")
	@Expose
	private String groupName;

	@SerializedName("groupCode")
	@Expose
	private String groupCode;

	@SerializedName("emailTriggeredOn")
	@Expose
	private String emailTriggeredOn;
}
