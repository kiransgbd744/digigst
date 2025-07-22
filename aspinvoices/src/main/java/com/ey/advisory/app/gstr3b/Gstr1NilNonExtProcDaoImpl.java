package com.ey.advisory.app.gstr3b;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.services.credit.reversal.CreditReversalProcessDaoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

/**
 * 
 * @author Sasidhar reddy
 *
 */
@Repository("Gstr1NilNonExtProcDaoImpl")
public class Gstr1NilNonExtProcDaoImpl {
    @PersistenceContext(unitName = "clientDataUnit")
    private EntityManager entityManager;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CreditReversalProcessDaoImpl.class);

    public int proceCallForComputeReversal(final String gstin,
            final Integer derivedRetPerFrom) {
        int count = 0;
        try {
            StoredProcedureQuery storedProcQuery = entityManager
                    .createStoredProcedureQuery("GSTR1_POPUP_ASPUI_NILEXTNON");
            storedProcQuery.registerStoredProcedureParameter("GSTIN",
                    String.class, ParameterMode.IN);
            storedProcQuery.registerStoredProcedureParameter(
                    "DERIVED_RET_PERIOD", Integer.class, ParameterMode.IN);

            storedProcQuery.setParameter("FROM_DERIVED_RET_PERIOD",
                    derivedRetPerFrom);
            storedProcQuery.setParameter("GSTIN", gstin);
            storedProcQuery.execute();
            StoredProcedureQuery storedProcQuery2 = entityManager
                    .createStoredProcedureQuery("GSTR1_POPUP_ASPUI_HSNSAC");
            storedProcQuery2.registerStoredProcedureParameter("GSTIN",
                    String.class, ParameterMode.IN);
            storedProcQuery2.registerStoredProcedureParameter(
                    "DERIVED_RET_PERIOD", Integer.class, ParameterMode.IN);

            storedProcQuery2.setParameter("DERIVED_RET_PERIOD",
                    derivedRetPerFrom);
            storedProcQuery2.setParameter("GSTIN", gstin);
            storedProcQuery2.execute();
            count = 1 + count;

        } catch (Exception e) {

            LOGGER.debug("Exception Occured:", e);
        }
        return count;
    }

}
