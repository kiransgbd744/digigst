/**
 * 
 */
package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReversalComputeDto {

	private String gstin;
	private String subSectionName;
	private String taxbleValue;

}
