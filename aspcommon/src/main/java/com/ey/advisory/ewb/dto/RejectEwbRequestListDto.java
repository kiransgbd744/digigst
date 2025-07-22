/**
 * 
 */
package com.ey.advisory.ewb.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author Khalid1.Khan
 *
 */
@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RejectEwbRequestListDto {
	
	@Expose
	@SerializedName("req")
	private List<RejectEwbReqDto> rejectEwbReqDtoList;
	
	@XmlElement(name = "id-token")
	private String idToken;

}
