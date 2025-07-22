/**
 * 
 */
package com.ey.advisory.app.services.search.anx2;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.common.SupplierDaoImpl;
import com.ey.advisory.app.data.daos.client.anx2.VendorPRSummaryDaoImpl;
import com.ey.advisory.app.docs.dto.VendorSummaryRespDto;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.core.dto.VendorSummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;
import com.google.common.collect.ImmutableList;

/**
 * @author BalaKrishna S
 *
 */
@Component("VendorPRSummaryService")
public class VendorPRSummaryService implements SearchService{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(VendorPRSummaryService.class);
	
	@Autowired
	@Qualifier("VendorPRSummaryDaoImpl")
	VendorPRSummaryDaoImpl vendorDao;

	@Autowired
	@Qualifier("SupplierDaoImpl")
	SupplierDaoImpl supplierDao;
	
	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;
	
	/**
     * Gets a reducing collector for the specified level. The only difference
     * between these collectors is the identity object. The identity object
     * will be set to appropriate level, before the reduction process.
     *
     * @param level Either L1 or L2 or L3
     * @return The collector capable of reducing a stream of L4 objects to
     *  an L3 object OR a stream of L3 objects to an L2 object or a stream
     *  of L2 objets to an L1 object.
     */
	private Collector<VendorSummaryRespDto, ?, VendorSummaryRespDto> 
	getCollectorForLevel(String level) {  
      return Collectors.reducing(new VendorSummaryRespDto(level),
	  (o1, o2) -> add(o1, o2));
    }


	@SuppressWarnings("unchecked")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		
		
		VendorSummaryReqDto req= (VendorSummaryReqDto) criteria;
		// adding DataSecurity Attributes For Request
		VendorSummaryReqDto datasceReq = basicCommonSecParam
				.setInwardPRSummaryDataSecuritySearchParams(req);
		
		// Get the L4 level data at Doc Type level 
		// (lowest level to be displayed) by executing the Dao.
		// Note that the Supplier Name is not present in the collection
		// returned from the Dao. This is because, since we don't have a 
		// GSTIN master for all the suppliers for the buyer, we need to 
		// extract the name from the invward invoice information. Since the
		// name appears at an invoice level, the same name can appear 
		// differently in different invoices due to manual entry errors etc
		// while entering the PR into the buyer's system.		
		Collection<VendorSummaryRespDto> l4Coll = 
				vendorDao.loadVendorPRSummary(datasceReq);

		// Since the name is not available in the above list, we need extract
		// the Supplier PANs from the above list using the Supplier GSTINs
		// and find the name of the supplier from the inward invoice table.		
		l4Coll = setVendorNames(l4Coll);		
		
		// Create an L3 Level collection at Table type level, by 
		// summing up elements in the above Level 4 collection.
		
		Collection<VendorSummaryRespDto> l3Coll = l4Coll.stream()
				.collect(Collectors.groupingBy(o1 -> o1.getVendorPAN() + 
							o1.getGstin() +  o1.getTableType(),
						getCollectorForLevel("L3"))).values();
		
		// Create an L2 level collection at GSTIN level, by summing up 
		// elements in the above Level 3 collection.
		Collection<VendorSummaryRespDto> l2Coll = l3Coll.stream()
				.collect(Collectors.groupingBy(
						o1 -> o1.getVendorPAN() + o1.getGstin(),
						getCollectorForLevel("L2"))).values();				

		// Create an L1 level collection at PAN level, by summing up 
		// elements in the above Level 2 collection.
		Collection<VendorSummaryRespDto> l1Coll = l3Coll.stream()
				.collect(Collectors.groupingBy(
						o1 -> o1.getVendorPAN(),
						getCollectorForLevel("L1"))).values();		
		
		// Merge all the Level 4, Level 3, Level 2 and Level 1 collections
        // into a single list.
		List<VendorSummaryRespDto> list = 
				new ImmutableList.Builder<VendorSummaryRespDto>()
					.addAll(l1Coll)
					.addAll(l2Coll)
					.addAll(l3Coll)
					.addAll(l4Coll)
					.build();
		
		// Sort the above list by a key that orders the elements in a
        // hierarchical manner for displaying in the UI as a hierarchy.
		List<VendorSummaryRespDto> retList = list.stream()
				.sorted(Comparator.comparing(o -> o.getKey()))
				.collect(Collectors.toList());

		
		return (SearchResult<R>) new SearchResult<>(retList);
		
	}

	
	private Collection<VendorSummaryRespDto> setVendorNames(
							Collection<VendorSummaryRespDto> vendors) {
		// Get the list of supplier PANs available in the above L4 collection.
		List<String> sPans = vendors.stream()
									.map(e -> e.getVendorPAN())
									.distinct()
									.collect(Collectors.toList());
		
		Map<String, String> panToNameMap = 
				supplierDao.getSupplierNamesForPans(sPans);		
		vendors.forEach(vsr -> {
			String pan = vsr.getVendorPAN();
			String name = panToNameMap.getOrDefault(pan, "-");
			vsr.setVendorName(name);
		});		
		
		return vendors;
	}
	
	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private VendorSummaryRespDto add(VendorSummaryRespDto cpt1,
			VendorSummaryRespDto cpt2) {
	
		String level = cpt1.getLevel();
		
		String sgstin = "L1".equals(level) ? null : cpt2.getGstin();
		String tableType = ("L1".equals(level)  || "L2".equals(level)) ? null
					: cpt2.getTableType();
		String docType = "L4".equals(level) ? cpt2.getDocType() : null;
		
		return new VendorSummaryRespDto(
				cpt1.getLevel(),				
				cpt2.getVendorPAN(),
				cpt2.getVendorName(),				
				sgstin,
				docType,
				tableType,
				cpt1.getCount() + cpt2.getCount(),
				cpt1.getTaxableValue().add(cpt2.getTaxableValue()),
				cpt1.getTotalTaxPayable().add(cpt2.getTotalTaxPayable()),
				cpt1.getTotalCreditEligible().add(
						cpt2.getTotalCreditEligible()),
				cpt1.getTpIGST().add(cpt2.getTpIGST()),
				cpt1.getTpCGST().add(cpt2.getTpCGST()),
				cpt1.getTpSGST().add(cpt2.getTpSGST()),				
				cpt1.getTpCess().add(cpt2.getTpCess()),				
				cpt1.getCeIGST().add(cpt2.getCeIGST()),
				cpt1.getCeCGST().add(cpt2.getCeCGST()),
				cpt1.getCeSGST().add(cpt2.getCeSGST()),				
				cpt1.getCeCess().add(cpt2.getCeCess()),
				cpt1.getInvValue().add(cpt2.getInvValue())
		);
	}



}
