package com.ey.advisory.app.data.daos.client.simplified;

import org.springframework.stereotype.Service;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("Gstr1InvSeriesSectionQueryBuilderImpl")
public class Gstr1InvSeriesSectionQueryBuilderImpl
		implements Gstr1InvSeriesSectionQueryBuilder {

	public String createGstinViewQueryString(String buildQuery) {

		StringBuilder builder = new StringBuilder();
		builder.append(
				"SELECT SERIAL_NUM,IFNULL(SUM(TOT_NUM),0) AS TOT_NUM,");
		builder.append(
				"IFNULL(SUM(CANCELED),0) AS CANCELED,IFNULL(SUM(NET_NUM),0) AS NET_ISSUED ");
		builder.append(" FROM GSTR1_PROCESSED_INV_SERIES ");
		builder.append("WHERE IS_DELETE=FALSE  ");
		builder.append(buildQuery);
		builder.append(" GROUP BY SERIAL_NUM ");
		return builder.toString();
	}

	@Override
	public String createGstinVerticalViewQueryString(String buildQuery) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT SERIAL_NUM,DOC_SERIES_FROM,DOC_SERIES_TO, ");
		builder.append("IFNULL(SUM(TOT_NUM),0) AS TOT_NUM,IFNULL(SUM(CANCELED),0) AS CANCELED, ");
		builder.append("IFNULL(SUM(NET_NUM),0) AS NET_ISSUED ,ID FROM GSTR1_PROCESSED_INV_SERIES  ");
		builder.append("WHERE IS_DELETE=FALSE  ");
		builder.append(buildQuery);
		builder.append(" GROUP BY SERIAL_NUM,DOC_SERIES_FROM,DOC_SERIES_TO,ID  ");
		return builder.toString();
	}
	
	@Override
	public String createGstr1aGstinVerticalViewQueryString(String buildQuery) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT SERIAL_NUM,DOC_SERIES_FROM,DOC_SERIES_TO, ");
		builder.append("IFNULL(SUM(TOT_NUM),0) AS TOT_NUM,IFNULL(SUM(CANCELED),0) AS CANCELED, ");
		builder.append("IFNULL(SUM(NET_NUM),0) AS NET_ISSUED ,ID FROM GSTR1A_PROCESSED_INV_SERIES  ");
		builder.append("WHERE IS_DELETE=FALSE  ");
		builder.append(buildQuery);
		builder.append(" GROUP BY SERIAL_NUM,DOC_SERIES_FROM,DOC_SERIES_TO,ID  ");
		return builder.toString();
	}
	
	public String createGstr1aGstinViewQueryString(String buildQuery) {

		StringBuilder builder = new StringBuilder();
		builder.append(
				"SELECT SERIAL_NUM,IFNULL(SUM(TOT_NUM),0) AS TOT_NUM,");
		builder.append(
				"IFNULL(SUM(CANCELED),0) AS CANCELED,IFNULL(SUM(NET_NUM),0) AS NET_ISSUED ");
		builder.append(" FROM GSTR1A_PROCESSED_INV_SERIES ");
		builder.append("WHERE IS_DELETE=FALSE  ");
		builder.append(buildQuery);
		builder.append(" GROUP BY SERIAL_NUM ");
		return builder.toString();
	}
}
