package com.ey.advisory.app.asprecon.reconresponse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author sakshi.jain
 *
 */

@Setter
@Getter
@ToString
public class Gstr2ReconResponsePosDto {

	private String pos;


	public Gstr2ReconResponsePosDto(String pos)
{
	this.pos = pos;
	}
}

