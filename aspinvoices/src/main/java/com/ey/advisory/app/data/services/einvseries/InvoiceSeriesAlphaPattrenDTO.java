package com.ey.advisory.app.data.services.einvseries;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

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
public class InvoiceSeriesAlphaPattrenDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String alphaPattren;
	private String alphaPattrenDelimiter;
	private List<String> alphaPattrenDelimitNumber;

	public Long getMinDelimitValue(List<Long> list) {
		return list.stream().min(Comparator.comparing(Long::valueOf)).get();
	}

	/**
	 * getMaxDelimitValue
	 * 
	 * @param list
	 * @return String
	 */
	public Long getMaxDelimitValue(List<Long> list) {
		return list.stream().max(Comparator.comparing(Long::valueOf)).get();
	}
}
