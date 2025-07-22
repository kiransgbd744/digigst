package com.ey.advisory.app.docs.dto.gstr9;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Jithendra.B
 *
 */
@Getter
@Setter
public class Gst9StatusTimeStampsDto {

	private String updateGstnTimeStamp;
	private String saveToGstnTimeStamp;
	private boolean isDataAvlble;

}
