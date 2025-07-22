/**
 * 
 */
package com.ey.advisory.app.services.feedback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */

@Data
public class FeedbackReportDto {

	@Expose
	@SerializedName("groupCode")
	private String groupCode;

	@Expose
	@SerializedName("userName")
	private String userName;

	@Expose
	@SerializedName("submittedOn")
	private String submittedOn;

	@Expose
	@SerializedName("answerQ1")
	private String answerQ1;

	@Expose
	@SerializedName("answerQ2")
	private String answerQ2;

	@Expose
	@SerializedName("answerQ3")
	private String answerQ3;

	@Expose
	@SerializedName("answerQ4")
	private String answerQ4;

	@Expose
	@SerializedName("answerQ5")
	private String answerQ5;

	@Expose
	@SerializedName("isFileReqQ2")
	private Boolean isFileReqQ2;

	@Expose
	@SerializedName("attachmentAvailableQ2")
	private String attachmentAvailableQ2;
	
	@Expose
	@SerializedName("quesIdQ2")
	private Long quesIdQ2;
}
