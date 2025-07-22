package com.ey.advisory.app.services.savetogstn.jobs.gstr3B;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service("gstr3BSaveToGstnQueryBuilderImpl")
public class Gstr3BSaveToGstnQueryBuilderImpl implements Gstr3BSaveToGstnQueryBuilder {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr3BSaveToGstnQueryBuilderImpl.class);

	/*@Autowired
	@Qualifier("userCriteriaBuilderImpl")
	private UserCriteriaBuilder userCriteriaBuilder;*/

	@Override 
	public String buildGstr3BQuery(String gstin, String retPeriod) {

		LOGGER.debug("inside buildGstr3BQuery method with args {}{}",
				gstin, retPeriod);

		StringBuilder build = new StringBuilder();
		if (gstin != null && retPeriod != null) {
			build.append(" userInput.GSTIN = :gstin");
			build.append(" AND userInput.TAX_PERIOD = :retPeriod");
			build.append(" AND userInput.IS_ACTIVE = true");
		}

		return "SELECT * FROM GSTR3B_ASP_USER userInput WHERE "
		+ build;

	}
	
	 
	public String buildGstr3BGstnQuery(String gstin, String retPeriod) {

		LOGGER.debug("inside buildGstr3BGstnQuery method with args {}{}",
				gstin, retPeriod);

		StringBuilder build = new StringBuilder();
		if (gstin != null && retPeriod != null) {
			build.append(" userInput.GSTIN = :gstin");
			build.append(" AND userInput.TAX_PERIOD = :retPeriod");
			build.append(" AND userInput.IS_ACTIVE = true");
		}

		return "SELECT * FROM GSTR3B_GSTN userInput WHERE "
		+ build;

	}


	/*@Override
	public String buildOsupDetailsQuery(String gstin, String retPeriod, String docType, List<Long> docIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildOsupZeroDetailsQuery(String gstin, String retPeriod, String docType, List<Long> docIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildIsupRevDetailsQuery(String gstin, String retPeriod, String docType, List<Long> docIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildOsupNongstDetailsQuery(String gstin, String retPeriod, String docType, List<Long> docIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildUnregDetailsQuery(String gstin, String retPeriod, String docType, List<Long> docIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildCompDetailsQuery(String gstin, String retPeriod, String docType, List<Long> docIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildUinDetailsQuery(String gstin, String retPeriod, String docType, List<Long> docIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildItcAvlDetailsQuery(String gstin, String retPeriod, String docType, List<Long> docIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildItcRevDetailsQuery(String gstin, String retPeriod, String docType, List<Long> docIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildItcNetDetailsQuery(String gstin, String retPeriod, String docType, List<Long> docIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildItcInelgDetailsQuery(String gstin, String retPeriod, String docType, List<Long> docIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildIsupDetailsQuery(String gstin, String retPeriod, String docType, List<Long> docIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildIntrDetailsQuery(String gstin, String retPeriod, String docType, List<Long> docIds) {
		// TODO Auto-generated method stub
		return null;
	}*/


}
