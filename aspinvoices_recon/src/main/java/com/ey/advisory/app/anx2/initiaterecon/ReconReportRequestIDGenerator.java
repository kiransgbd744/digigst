package com.ey.advisory.app.anx2.initiaterecon;

/**
 * @author Arun.KA
 *
 */

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReconReportRequestIDGenerator implements IdentifierGenerator {

	private BigInteger getNextSequencevalue(Session session) {

		String queryStr =
				"SELECT RECON_REPORT_SEQ.nextval FROM DUMMY";

		Query query = session.createQuery(queryStr);

		List<BigInteger> resultList = query.list();
		if (resultList != null && !resultList.isEmpty()) {
			return (BigInteger) resultList.get(0);
		}

		throw new IllegalArgumentException("Unable to query the "
				+ "last sequence value for ReconReports.");

	}

	@Override
	public Serializable generate(SharedSessionContractImplementor session,
			Object object) throws HibernateException {
		String reportId = "";
		String digits = "";

		try {

			BigInteger nextSequencevalue = getNextSequencevalue(
					session.getFactory().getCurrentSession());
			// Integer nextSequencevalue = nextSequenceval.intValue();

			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
			String currentDate = "" + currentYear + currentMonth;
			if (nextSequencevalue != null
					&& nextSequencevalue.compareTo(BigInteger.ZERO) > 0) {
				digits = String.format("%06d", nextSequencevalue);
				reportId = currentDate.concat(digits);
			}

		} catch (Exception e) {

			LOGGER.error("Error in genrating the Request ReportId ");
		}

		return Long.valueOf(reportId);
	}

}
