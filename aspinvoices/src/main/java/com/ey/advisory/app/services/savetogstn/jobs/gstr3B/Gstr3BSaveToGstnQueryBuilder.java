package com.ey.advisory.app.services.savetogstn.jobs.gstr3B;

public interface Gstr3BSaveToGstnQueryBuilder {
	
	public String buildGstr3BQuery(String gstin, String retPeriod);
	public String buildGstr3BGstnQuery(String gstin, String retPeriod);

	/*public String buildOsupDetailsQuery(String gstin, String retPeriod,
			String docType, List<Long> docIds);
	
	public String buildOsupZeroDetailsQuery(String gstin, String retPeriod,
			String docType, List<Long> docIds);
	
	public String buildIsupRevDetailsQuery(String gstin, String retPeriod,
			String docType, List<Long> docIds);
	
	public String buildOsupNongstDetailsQuery(String gstin, String retPeriod,
			String docType, List<Long> docIds);
	
	public String buildUnregDetailsQuery(String gstin, String retPeriod,
			String docType, List<Long> docIds);
	
	public String buildCompDetailsQuery(String gstin, String retPeriod,
			String docType, List<Long> docIds);
	
	public String buildUinDetailsQuery(String gstin, String retPeriod,
			String docType, List<Long> docIds);
	
	public String buildItcAvlDetailsQuery(String gstin, String retPeriod,
			String docType, List<Long> docIds);
	
	public String buildItcRevDetailsQuery(String gstin, String retPeriod,
			String docType, List<Long> docIds);
	
	public String buildItcNetDetailsQuery(String gstin, String retPeriod,
			String docType, List<Long> docIds);
	
	public String buildItcInelgDetailsQuery(String gstin, String retPeriod,
			String docType, List<Long> docIds);
	
	public String buildIsupDetailsQuery(String gstin, String retPeriod,
			String docType, List<Long> docIds);
	
	public String buildIntrDetailsQuery(String gstin, String retPeriod,
			String docType, List<Long> docIds);*/
	
}
