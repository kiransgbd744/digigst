/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hemasundar.J
 *
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class OutwardErrorItemDto {
	
	@XmlElement(name="ItemNo")
	private Long itemNo ;

	@XmlElement(name="ErrorFields")
	private String errorField ;
	
	@XmlElement(name="ErrorCode")
	private String errorCode ;
	
	@XmlElement(name="ErrorType")
	private String errorType ;
	
	@XmlElement(name="ErrorDesc")
	private String errorDesc ;

}
