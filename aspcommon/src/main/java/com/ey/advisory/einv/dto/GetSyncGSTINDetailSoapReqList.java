/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author Siva Reddy
 *
 */

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GetSyncGSTINDetailSoapReqList implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	@XmlElement(name = "getsync-req-list")
	private List<GetSyncGSTINDetailSoapReqDto> reqDto;

	@XmlElement(name = "id-token")
	private String idToken;

}