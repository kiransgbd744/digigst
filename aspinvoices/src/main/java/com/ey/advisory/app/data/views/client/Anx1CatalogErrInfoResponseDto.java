/**
 * 
 */
package com.ey.advisory.app.data.views.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Laxmi.Salukuti
 *
 */
@Setter
@Getter
@ToString
public class Anx1CatalogErrInfoResponseDto {
	
	private String errorCode;
	private String errorDesc;
	private String infoCode;
	private String infoDesc;

}
