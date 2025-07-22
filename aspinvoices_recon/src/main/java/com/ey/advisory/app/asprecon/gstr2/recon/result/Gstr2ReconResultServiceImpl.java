package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2ReconResultServiceImpl")
public class Gstr2ReconResultServiceImpl implements Gstr2ReconResultService {

	@Autowired
	@Qualifier("Gstr2ReconResultDaoImpl")
	private Gstr2ReconResultDao dao;

	@Autowired
	@Qualifier("Gstr2ReconResult2BPRDaoImpl")
	private Gstr2ReconResult2BPRDao dao2bpr;

	@Override
	public Triplet<
    List<Gstr2ReconResultDto>,   
    List<ReconSummaryDto>,        
    Integer> findReconResult(
			Gstr2ReconResultReqDto reqDot, int pageNum, int pageSize) {

		Triplet<
	    List<Gstr2ReconResultDto>,   
	    List<ReconSummaryDto>,        
	    Integer> respList = null;
		List<Gstr2ReconResultDto> newRespList = new ArrayList<>();

		try {

			respList = dao.getReconResult(reqDot, pageNum, pageSize);

			// Map<String, List<Gstr2ReconResultDto>> map = respList.stream()
			// .collect(Collectors.groupingBy(e -> e.getReportType()));
			//
			// /*
			// * List<String> desirList =
			// * Arrays.asList("Absolute Match","Mismatch",
			// * "Document Number Mismatch","Potential Match (Gold)",
			// * "Potential Match (Silver)","Fuzzy Match","ForceMatch/GSTR3B",
			// * "Addition in PR","Addition in 2A");
			// */
			//
			// List<String> desirList = Arrays.asList("Exact Match",
			// "Match With Tolerance", "Value Mismatch", "POS Mismatch",
			// "Doc Date Mismatch", "Doc Type Mismatch",
			// "Doc No Mismatch I", "Multi-Mismatch", "Potential-I",
			// "Doc No Mismatch II", "Potential-II", "Logical Match",
			// "Addition in PR", "Addition in 2A", "ForceMatch/GSTR3B");
			//
			// for (String repType : desirList) {
			// if (map.containsKey(repType)) {
			// newRespList.addAll(map.get(repType));
			//
			// }
			// }

		} catch (Exception ex) {
			String msg = String
					.format("Error occured while fetching db resp %s", ex);
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		return respList;
	}

	@Override
	public Triplet<
    List<Gstr2ReconResultDto>,    
    List<ReconSummaryDto>,        
    Integer> findReconResult2bpr(
			Gstr2ReconResultReqDto reqDot, int pageNum, int pageSize) {

		Triplet<
	    List<Gstr2ReconResultDto>,    
	    List<ReconSummaryDto>,        
	    Integer> respList = null;
		List<Gstr2ReconResultDto> newRespList = new ArrayList<>();

		try {

			respList = dao2bpr.getReconResult2BPR(reqDot, pageNum, pageSize);

//			Map<String, List<Gstr2ReconResultDto>> map = respList.stream()
//					.collect(Collectors.groupingBy(e -> e.getReportType()));

			/*
			 * List<String> desirList =
			 * Arrays.asList("Absolute Match","Mismatch",
			 * "Document Number Mismatch","Potential Match (Gold)",
			 * "Potential Match (Silver)","Fuzzy Match","ForceMatch/GSTR3B",
			 * "Addition in PR","Addition in 2A");
			 */

//			List<String> desirList = Arrays.asList("Exact Match",
//					"Match With Tolerance", "Value Mismatch", "POS Mismatch",
//					"Doc Date Mismatch", "Doc Type Mismatch",
//					"Doc No Mismatch I", "Multi-Mismatch", "Potential-I",
//					"Doc No Mismatch II", "Potential-II", "Logical Match",
//					"Addition in PR", "Addition in 2B", "ForceMatch/GSTR3B");
//
//			for (String repType : desirList) {
//				if (map.containsKey(repType)) {
//					newRespList.addAll(map.get(repType));
//
//				}
//			}

		} catch (Exception ex) {
			String msg = String
					.format("Error occured while fetching db resp %s", ex);
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		return respList;
	}

}
