/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author Arun KA
 *
 */

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CancelIrnResponseListDto {
	
	@SerializedName("req")
	@Expose
	@XmlElement(name = "cancel-einv-resp")
	List<CancelIrnERPResponseDto> respList;

}
