/**
 * 
 */
package com.ey.advisory.common;

import java.time.LocalDate;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Khalid1.Khan
 *
 */

public class LocalDateAdapter  extends XmlAdapter<String, LocalDate>{
	 public LocalDate unmarshal(String v) throws Exception {
	        return LocalDate.parse(v);
	    }

	    public String marshal(LocalDate v) throws Exception {
	        return v.toString();
	    }
}
