package com.ey.advisory.app.anx2.vendorsummary;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.BasicCommonSecParam;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;


@Service("VendorReconSummaryServiceImpl")
@Slf4j
public class VendorReconSummaryServiceImpl
		implements VendorReconSummaryService {

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;
	
	@Autowired
	@Qualifier("VendorReconSummaryDaoImpl")
	VendorReconSummaryDao vendoreReconDao;
	
	public static BigDecimal PERCENTAGE = new BigDecimal(100);
	
	@Override
	public List<VendorReconSummaryResponseDto> 
	    getReconSummaryDetails(VendorReconSummaryReqDto criteria) {
		
	
		Collection<VendorReconSummaryResponseDto> l3Coll =  vendoreReconDao.
				loadReconSummaryDetails(criteria);
				
		
		
				// Create an L2 Level collection at Table type level, by
		// summing up elements in the above Level 3 collection.

		Collection<VendorReconSummaryResponseDto> l2Coll = l3Coll.stream()
				.collect(Collectors.groupingBy(o1 -> o1.getGstin(),
						getCollectorForLevel("L2")))
				.values();

		// Create an L1 level collection at GSTIN level, by summing up
		// elements in the above Level 2 collection.
		Collection<VendorReconSummaryResponseDto> l1Coll = l2Coll.stream()
				.collect(Collectors.groupingBy(o1 -> o1.getVendorPan(),
						getCollectorForLevel("L1")))
				.values();
	/*
		//taking average of counts for percentage in L1
		l1Coll.forEach(o -> 
				{
					o.setPrMatchPer(o.getPrMatchPer()
							.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
					o.setPrMissMatPer(o.getPrMissMatPer()
							.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
					o.setPrProbPer(o.getPrProbPer()
							.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
					o.setPrForcMatPer(o.getPrForcMatPer()
							.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
					o.setAddanx2Per(o.getAddanx2Per()
							.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
					o.setAddPrPer(o.getAddPrPer()
							.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
				});
	*/	
		List<VendorReconSummaryResponseDto> list = new 
				ImmutableList.Builder<VendorReconSummaryResponseDto>()
				.addAll(l1Coll).addAll(l2Coll).addAll(l3Coll).build();
		
		if(LOGGER.isDebugEnabled()){
			String msg = String.format("The VendorReconSummaryResponseDto "
					+ " has been generated with %d records", 
					list.size());
			System.out.println(msg);
		}

		// Sort the above list by a key that orders the elements in a
		// hierarchical manner for displaying in the UI as a hierarchy.
		List<VendorReconSummaryResponseDto> retList = list.stream()
				.sorted(Comparator.comparing(o -> o.getKey()))
				.collect(Collectors.toList());
		return retList;

	}

	private Collector<VendorReconSummaryResponseDto, ?, 
			VendorReconSummaryResponseDto> getCollectorForLevel(
			String level) {
		return Collectors.reducing(new VendorReconSummaryResponseDto(level),
				(o1, o2) -> add(o1, o2));
	}

	private VendorReconSummaryResponseDto add(
			VendorReconSummaryResponseDto cpt1,
			VendorReconSummaryResponseDto cpt2) {
		
		String cgstin = "L1".equals(cpt1.getLevel()) ? null : cpt2.getGstin();
		String taxDocType = "L1".equals(cpt1.getLevel())/*||"L2".equals(cpt1.getLevel())*/ ? null :
			cpt2.getDocType();
		
		if(cpt1.getLevel().equals("L1")) {
			
			return new VendorReconSummaryResponseDto(
				cpt1.getLevel(), 
				cpt2.getVendorPan(),
				cpt2.getVendorName(), //do changes here 
				cgstin,
				taxDocType,
				cpt1.getPrMatchAmt().add(cpt2.getPrMatchAmt()),
				cpt1.getAnx2MatchAmt().add(cpt2.getAnx2MatchAmt()),
				cpt1.getPrMissMatAmt().add(cpt2.getPrMissMatAmt()),
				cpt1.getAnxrMisMatAmt().add(cpt2.getAnxrMisMatAmt()),
				cpt1.getPrProbMat().add(cpt2.getPrProbMat()),
				cpt1.getAnx2ProbMat().add(cpt2.getAnx2ProbMat()),
				cpt1.getPrForcMat().add(cpt2.getPrForcMat()),
				cpt1.getAnx2ForcMat().add(cpt2.getAnx2ForcMat()),
				
				cpt1.getAddPr().add(cpt2.getAddPr()),
				cpt1.getAddAnx2().add(cpt2.getAddAnx2()),
				
				cpt2.getPrMatchPer(),
				cpt2.getPrMissMatPer(), 
				cpt2.getPrProbPer(),
				cpt2.getPrForcMatPer(), 
				cpt2.getAddanx2Per(),
				cpt2.getAddPrPer(),
				cpt2.getCount()+cpt1.getCount()
		  );	
		}
		
		
		
		if("CR".equalsIgnoreCase(taxDocType) ) {
			return new VendorReconSummaryResponseDto(
					cpt1.getLevel(), 
					cpt2.getVendorPan(),
					cpt2.getVendorName(),
					cgstin,
					taxDocType,
					cpt1.getPrMatchAmt().subtract(cpt2.getPrMatchAmt()),
					cpt1.getAnx2MatchAmt().subtract(cpt2.getAnx2MatchAmt()),
					cpt1.getPrMissMatAmt().subtract(cpt2.getPrMissMatAmt()),
					cpt1.getAnxrMisMatAmt().subtract(cpt2.getAnxrMisMatAmt()),
					cpt1.getPrProbMat().subtract(cpt2.getPrProbMat()),
					cpt1.getAnx2ProbMat().subtract(cpt2.getAnx2ProbMat()),
					cpt1.getPrForcMat().subtract(cpt2.getPrForcMat()),
					cpt1.getAnx2ForcMat().subtract(cpt2.getAnx2ForcMat()),
					
					cpt1.getAddPr().subtract(cpt2.getAddPr()),
					cpt1.getAddAnx2().subtract(cpt2.getAddAnx2()),
					
					cpt2.getPrMatchPer(),
					cpt2.getPrMissMatPer(), 
					cpt2.getPrProbPer(),
					cpt2.getPrForcMatPer(), 
					cpt2.getAddanx2Per(),
					cpt2.getAddPrPer(),
					cpt1.getCount()+cpt2.getCount()+1
				
				);
		}
		
		return new VendorReconSummaryResponseDto(
				cpt1.getLevel(), 
				cpt2.getVendorPan(),
				cpt2.getVendorName(),
				cgstin,
				taxDocType,
				cpt1.getPrMatchAmt().add(cpt2.getPrMatchAmt()),
				cpt1.getAnx2MatchAmt().add(cpt2.getAnx2MatchAmt()),
				cpt1.getPrMissMatAmt().add(cpt2.getPrMissMatAmt()),
				cpt1.getAnxrMisMatAmt().add(cpt2.getAnxrMisMatAmt()),
				cpt1.getPrProbMat().add(cpt2.getPrProbMat()),
				cpt1.getAnx2ProbMat().add(cpt2.getAnx2ProbMat()),
				cpt1.getPrForcMat().add(cpt2.getPrForcMat()),
				cpt1.getAnx2ForcMat().add(cpt2.getAnx2ForcMat()),
				
				cpt1.getAddPr().add(cpt2.getAddPr()),
				cpt1.getAddAnx2().add(cpt2.getAddAnx2()),
				
				cpt2.getPrMatchPer(),
				cpt2.getPrMissMatPer(), 
				cpt2.getPrProbPer(),
				cpt2.getPrForcMatPer(), 
				cpt2.getAddanx2Per(),
				cpt2.getAddPrPer(),
				cpt1.getCount()+cpt2.getCount()+1
			);
		
		
	}
	
}
