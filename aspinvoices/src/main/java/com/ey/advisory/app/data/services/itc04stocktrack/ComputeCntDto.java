/**
 * 
 */
package com.ey.advisory.app.data.services.itc04stocktrack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Siva.Reddy
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComputeCntDto {

	private int openChallanGr365IG = 0;
	private int openChallanLs365IG = 0;
	private int openChallanGr365CG = 0;
	private int openChallanLs365CG = 0;

	private String reportStatus;
	private String initiatedOn;
}
