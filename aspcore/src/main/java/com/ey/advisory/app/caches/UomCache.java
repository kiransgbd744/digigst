package com.ey.advisory.app.caches;

import java.util.Map;


/**
 * @author Siva.Nandam
 *
 */
public interface UomCache {
	public int finduom(String uom);
	public int finduomDesc(String Desc);
	public int finduomMergeDesc(String Desc);
	public  Map<String, String> uQcDescAndCodemap();
	public Map<String, String> uQcDesc();
}
