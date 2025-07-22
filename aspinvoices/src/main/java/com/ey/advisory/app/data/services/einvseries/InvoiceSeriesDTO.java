package com.ey.advisory.app.data.services.einvseries;

import java.io.Serializable;

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
public class InvoiceSeriesDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String gstin;
	private String taxPeriod;
	private String invoiceseriesID;
	private String documentType;
	private String natureOfSupp;
	private String fromSeries;
	private String toSeries;
	private String totalNumber;
	private String cancelled;
	private String netNumber;
	private String pattern;
}
