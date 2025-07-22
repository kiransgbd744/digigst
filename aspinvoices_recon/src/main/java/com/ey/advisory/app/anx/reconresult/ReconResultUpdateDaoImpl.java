package com.ey.advisory.app.anx.reconresult;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository("ReconResultUpdateDaoImpl")
public class ReconResultUpdateDaoImpl implements ReconResultUpdateDao{

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;


	@Transactional(value = "clientTransactionManager")
	public int updateReconUserActionsInDB(String taxPeriod, String reportName,
			String userAction, 
			List<String> a2KeyList, List<String> prKeyList , 
			List<BigInteger> reconId, List<String> gstinsList) {
		
		List<String> a2KeyListUni = a2KeyList.stream().distinct()
				.collect(Collectors.toList());
		
		List<String> prKeyListUni = prKeyList.stream().distinct()
				.collect(Collectors.toList());
		
		List<BigInteger> reconIdUni = reconId.stream().distinct()
				.collect(Collectors.toList());
		
		List<String> gstinListUni = gstinsList.stream().distinct().
				collect(Collectors.toList());
		
		 
		String query =" UPDATE LINK_A2_PR SET USER_RESPONSE "
				+ " = :action WHERE "; 
	    String conditions = createQuery(a2KeyListUni, prKeyListUni 
	    		, reconIdUni, gstinListUni, taxPeriod);	
	    String updateQuery = query+" "+conditions;
	   
	    Query queryEntity = entityManager.createNativeQuery(updateQuery);
	    queryEntity.setParameter("action",userAction);
	    if (!(a2KeyListUni == null
				||a2KeyListUni.isEmpty())) {
	    	queryEntity.setParameter("a2Key", a2KeyListUni);

		}

		if (!(prKeyListUni == null
				|| prKeyListUni.isEmpty())) {
			queryEntity.setParameter("prKey",prKeyListUni );				

		}

		if (reconIdUni != null ) {
			queryEntity.setParameter("reconId", reconIdUni);	
		}
		
		queryEntity.setParameter("taxPeriod", taxPeriod);
	    int updateNo = queryEntity.executeUpdate();
	    return updateNo;
	
	}


   public String createQuery(List<String> a2KeyListUni, List<String> prKeyListUni , 
		  List<BigInteger> reconIdUni, List<String> gstinListUni, String taxPeriod) {
	   StringBuffer condition = new StringBuffer();
	
	   
	   if (!(a2KeyListUni == null
				||a2KeyListUni.isEmpty())) {
		  condition.append(" A2_INVOICE_KEY IN (:a2Key) AND");

		}

		if (!(prKeyListUni == null
				|| prKeyListUni.isEmpty())) {
		  condition.append(" PR_INVOICE_KEY IN (:prKey) AND");
		}

		if (reconIdUni != null) {
			condition.append(" RECON_LINK_ID IN (:reconId) AND");
		}
		
		condition.append(" TAX_PERIOD = :taxPeriod ");
		return condition.toString();
	   
   } 
	   
   }
