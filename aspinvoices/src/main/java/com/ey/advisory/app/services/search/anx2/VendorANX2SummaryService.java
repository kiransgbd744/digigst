/**
 * 
 */
package com.ey.advisory.app.services.search.anx2;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.common.SupplierDaoImpl;
import com.ey.advisory.app.data.daos.client.anx2.VendorANX2SummaryDaoImpl;
import com.ey.advisory.app.docs.dto.VendorANX2SummaryRespDto;
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
@Component("VendorANX2SummaryService")
public class VendorANX2SummaryService implements SearchService {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory
			.getLogger(VendorANX2SummaryService.class);

	@Autowired
	@Qualifier("VendorANX2SummaryDaoImpl")
	VendorANX2SummaryDaoImpl vendorDao;

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
	private Collector<VendorANX2SummaryRespDto, ?, VendorANX2SummaryRespDto> 
	getCollectorForLevel(String level) {  
      return Collectors.reducing(new VendorANX2SummaryRespDto(level),
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
		Collection<VendorANX2SummaryRespDto> l4Coll = 
				vendorDao.loadVendorANX2Summary(datasceReq);

		// Since the name is not available in the above list, we need extract
		// the Supplier PANs from the above list using the Supplier GSTINs
		// and find the name of the supplier from the inward invoice table.		
	//	l4Coll = setVendorNames(l4Coll);		
		
		// Create an L3 Level collection at Table type level, by 
		// summing up elements in the above Level 4 collection.
		
		Collection<VendorANX2SummaryRespDto> l3Coll = l4Coll.stream()
				.collect(Collectors.groupingBy(o1 -> o1.getVendorPAN() + 
							o1.getGstin() +  o1.getTableType(),
						getCollectorForLevel("L3"))).values();
		
		// Create an L2 level collection at GSTIN level, by summing up 
		// elements in the above Level 3 collection.
		Collection<VendorANX2SummaryRespDto> l2Coll = l3Coll.stream()
				.collect(Collectors.groupingBy(
						o1 -> o1.getVendorPAN() + o1.getGstin(),
						getCollectorForLevel("L2"))).values();				

		// Create an L1 level collection at PAN level, by summing up 
		// elements in the above Level 2 collection.
		Collection<VendorANX2SummaryRespDto> l1Coll = l3Coll.stream()
				.collect(Collectors.groupingBy(
						o1 -> o1.getVendorPAN(),
						getCollectorForLevel("L1"))).values();		
		
		// Merge all the Level 4, Level 3, Level 2 and Level 1 collections
        // into a single list.
		List<VendorANX2SummaryRespDto> list = 
				new ImmutableList.Builder<VendorANX2SummaryRespDto>()
					.addAll(l1Coll)
					.addAll(l2Coll)
					.addAll(l3Coll)
					.addAll(l4Coll)
					.build();
		
		// Sort the above list by a key that orders the elements in a
        // hierarchical manner for displaying in the UI as a hierarchy.
		List<VendorANX2SummaryRespDto> retList = list.stream()
				.sorted(Comparator.comparing(o -> o.getKey()))
				.collect(Collectors.toList());

		
		return (SearchResult<R>) new SearchResult<>(retList);
		
	}

	
	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private VendorANX2SummaryRespDto add(VendorANX2SummaryRespDto cpt1,
			VendorANX2SummaryRespDto cpt2) {
	
		String level = cpt1.getLevel();
		
		String sgstin = "L1".equals(level) ? null : cpt2.getGstin();
		String tableType = ("L1".equals(level)  || "L2".equals(level)) ? null
					: cpt2.getTableType();
		String docType = "L4".equals(level) ? cpt2.getDocType() : null;
		
		return new VendorANX2SummaryRespDto(
				cpt1.getLevel(),				
				cpt2.getVendorPAN(),
				cpt2.getVendorName(),				
				sgstin,
				docType,
				tableType,
				cpt1.getCount() + cpt2.getCount(),
				cpt1.getTaxableValue().add(cpt2.getTaxableValue()),
				cpt1.getTpIGST().add(cpt2.getTpIGST()),
				cpt1.getTpCGST().add(cpt2.getTpCGST()),
				cpt1.getTpSGST().add(cpt2.getTpSGST()),				
				cpt1.getTpCess().add(cpt2.getTpCess()),
				cpt1.getInvValue().add(cpt2.getInvValue())
				);
	}


}
