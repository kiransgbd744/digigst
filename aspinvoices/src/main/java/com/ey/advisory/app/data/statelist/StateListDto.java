package com.ey.advisory.app.data.statelist;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Vishal.Verma
 *
 */

@Getter
@Setter
public class StateListDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String state;
	private String value;
	

	public StateListDto() {
		super();
	}



	/**
	 * @param state
	 * @param value
	 */
	public StateListDto(String state, String value) {
		super();
		this.state = state;
		this.value = value;
	}



	@Override
	public String toString() {
		return "StateListDto [state=" + state + ", value=" + value + "]";
	}
	
	public String getKey() {

		return state ;
	}

	
}
