/**
 * 
 */
package com.ey.advisory.services.days180.api.push;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ErrorItem {

	@XmlElement(name = "ERRORCODE")
	private String errorCode;

	@XmlElement(name = "ERRORDESC")
	private String errorDesc;

}
