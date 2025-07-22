package com.ey.advisory.app.services.validation.sales;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.javatuples.Pair;


/**
 * @author Siva.Nandam
 *
 */
public class ProductMasterRateAndHsnValUtil {

	private ProductMasterRateAndHsnValUtil() {
	}

	public static boolean isValidHsn(
            Map<String, List<Pair<Integer, BigDecimal>>> map, String hsnsac,
            String sgstin) {

 

        if (map.get(sgstin) == null)
            return false;
        List<Pair<Integer, BigDecimal>> hsnAndRatePairList = new ArrayList<>();
        for (Entry<String, List<Pair<Integer, BigDecimal>>> entry : map
                .entrySet()) {
            hsnAndRatePairList = entry.getValue();
        }
        List<String> hsnlist = new ArrayList<>();
        if (hsnAndRatePairList.isEmpty())
            return false;
        for (Pair<Integer, BigDecimal> hsnAndRatePair : hsnAndRatePairList) {
            if (hsnAndRatePair != null && hsnAndRatePair.getValue0() != null) {
                String hsn = hsnAndRatePair.getValue0().toString();
                hsnlist.add(hsn);
            }
        }
        if(!hsnlist.contains(hsnsac)) return false;
        return true;
    }

	public static boolean isValidRate(
			Map<String, List<Pair<Integer, BigDecimal>>> map,
			BigDecimal rates,String sgstin) {

		if(map.get(sgstin)==null) return false;
		
		List<Pair<Integer, BigDecimal>> hsnAndRatePairList = new ArrayList<>();
		for (Entry<String, List<Pair<Integer, BigDecimal>>> entry : map
				.entrySet()) {
			hsnAndRatePairList = entry.getValue();
		}
		List<BigDecimal> ratelist = new ArrayList<>();
		if(hsnAndRatePairList.isEmpty()) return false;
		hsnAndRatePairList.forEach(hsnAndRatePair -> {
			BigDecimal rate = hsnAndRatePair.getValue1();

			ratelist.add(rate);
		});

		if(!ratelist.contains(rates.setScale(3))) return false;
		//return ratelist.contains(rates.setScale(3)) ? true : false;
		return true;
	}
	
	public static boolean isValidHsnAndRate(
			Map<String, List<Pair<Integer, BigDecimal>>> map, Integer hsn,
			BigDecimal rates,String sgstin) {
		if(map.get(sgstin)==null) return false;
		List<Pair<Integer, BigDecimal>> hsnAndRatePairList = new ArrayList<>();
		for (Entry<String, List<Pair<Integer, BigDecimal>>> entry : map
				.entrySet()) {
			hsnAndRatePairList = entry.getValue();
		}
		Pair<Integer, BigDecimal> hsnAndrate=new Pair<Integer,BigDecimal>(
				hsn, rates.setScale(3));
		
if(!hsnAndRatePairList.contains(hsnAndrate)) return false;
return true;
		//return hsnAndRatePairList.contains(hsnAndrate) ? true : false;
	}
	
	
	
}
