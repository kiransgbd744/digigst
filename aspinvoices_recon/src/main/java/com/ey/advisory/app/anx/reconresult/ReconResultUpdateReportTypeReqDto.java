/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReconResultUpdateReportTypeReqDto {
	
	@Expose
	private List<UpdateInnerDto> linkIdList;

}
