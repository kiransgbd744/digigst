/**
 * 
 */
package com.ey.advisory.app.anx1.counterparty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Vishal.Verma
 *
 */
@Slf4j
@Component("CounterPartySummaryServiceImpl")
public class CounterPartySummaryServiceImpl implements
CounterPartySummaryService{
	
	@Autowired
	@Qualifier("CounterPartyInfoDaoImpl")
	CounterPartyInfoDao counterPartyInfoDao;
	
	@Override
    public List<CounterPartyInfoResponseSummaryDto> getCounterPartySummary(
                    List<String> sgstinList, String taxPeriod, 
                    List<String> tableSection,List<String> docType) {
			if(LOGGER.isDebugEnabled()){
				String msg = "Begin CounterPartySummaryServiceImpl"
						+ ".getCounterPartySummary ,calling counterPartyInfoDao"
						+ "to get List<CounterPartyInfoDto>";
				LOGGER.debug(msg);
			}
            List<CounterPartyInfoDto> result = counterPartyInfoDao
                            .getAllCounterPartyInfo
                            (taxPeriod, sgstinList,tableSection, docType);
            
            /*	Here reducing() is taking identity as one of parameters,
             *  which is creating multiple objects.
             *  Alternative we are using .of() as a mutable one
             * 	this is why we are using SuppressWarning unused for
             *  future reference .
             */
 
            @SuppressWarnings("unused")
			Collector<CounterPartyInfoDto, ?, 
                            CounterPartyInfoResponseSummaryDto> level2Coll = 
                            Collectors
                            .reducing(new CounterPartyInfoResponseSummaryDto(),
                                    cpi -> convert(cpi), 
                                    (cps1, cps2) -> addToCPS(cps1, cps2));
            
            Collector<CounterPartyInfoDto, ?, 
                    CounterPartyInfoResponseSummaryDto> level2MutableColl =
                    Collector.of(
                    		() -> new CounterPartyInfoResponseSummaryDto(),
                    		(cps, cpi) -> addToCPSMutable(cps, cpi),
                    		(cps1, cps2) -> addToCPS(cps1, cps2),
                    		Collector.Characteristics.IDENTITY_FINISH
                    );

            Collector<CounterPartyInfoDto, ?, Map<String, 
                            CounterPartyInfoResponseSummaryDto>> coll2 = 
                            Collectors
                            .groupingBy(e -> e.getAction(), level2MutableColl);
            
            Map<String, CounterPartyInfoResponseSummaryDto> map = result
                    .stream()
                    .filter(cpi -> CollectionUtils.isEmpty(tableSection) ||
                    		tableSection.contains(cpi.getTableSection()))
                    .collect(coll2);
                    
            Map<String, CounterPartyInfoResponseSummaryDto> respMap = 
                    ImmutableList
                    .<String>of("S", "NS", "A", "R", "P", "N", "U")
                    .stream()
                    .map(o -> new CounterPartyInfoResponseSummaryDto(o))
                    .collect(Collectors
                    .toMap(o -> o.getType(),
                    		o -> o,
                    		(x,y) -> {throw new IllegalStateException(
                    				String.format("Duplicate key %s", x));}
                    		,LinkedHashMap::new));
            respMap.putAll(map);

            ArrayList<CounterPartyInfoResponseSummaryDto> arrList
                            = new ArrayList<>(respMap.values());

            //calculating percentage based on actions.
            Integer sum = arrList.stream()
            		.filter(o -> (!(o.getType().equals("S")||
            				o.getType().equals("NS"))))
            		.collect(Collectors.summingInt(o -> o.getCount()));

            arrList.forEach(e -> e.setPercent(
            		sum != 0 ? (e.getCount() * 100 / sum) : 0));
            
            Integer sumProccesed = arrList.stream()
            		.filter(o -> (o.getType().equals("S")||
    				o.getType().equals("NS")))
    		.collect(Collectors.summingInt(o -> o.getCount()));
            
            arrList.stream().filter(o -> (o.getType().equals("S")||
    				o.getType().equals("NS"))).
            forEach(e -> e.setPercent(
            	sumProccesed != 0 ? (e.getCount() * 100 / sumProccesed) : 0));
            
            
            if(LOGGER.isDebugEnabled()){
				String msg = "End CounterPartySummaryServiceImpl"
						+ ".getCounterPartySummary "
						+ ",List<CounterPartyInfoResponseSummaryDto> >";
				LOGGER.debug(msg);
			}
            return arrList;

    }


    private CounterPartyInfoResponseSummaryDto convert(
                    CounterPartyInfoDto cpi) {
            CounterPartyInfoResponseSummaryDto cpis 
                                    = new CounterPartyInfoResponseSummaryDto();
    
            cpis.setType(cpi.getAction());
            cpis.setCess(cpi.getCessAmt());
            cpis.setCgst(cpi.getCgstAmt());
            cpis.setSgst(cpi.getCgstAmt());
            cpis.setCount(cpi.getCnt());
            cpis.setTaxPayable(cpi.getTaxPayable());
            cpis.setTaxableVal(cpi.getTaxableValue());
            cpis.setIgst(cpi.getIgstAmt());

            return cpis;
    }

    private CounterPartyInfoResponseSummaryDto addToCPS(
                    CounterPartyInfoResponseSummaryDto cps1,
                    CounterPartyInfoResponseSummaryDto cps2) {
            CounterPartyInfoResponseSummaryDto cps = 
                            new CounterPartyInfoResponseSummaryDto();
            cps.setType(cps2.getType());
            cps.setCgst(cps1.getCgst().add(cps2.getCgst()));
            cps.setIgst(cps1.getIgst().add(cps2.getIgst()));
            cps.setCess(cps1.getCess().add(cps2.getCess()));
            cps.setCount(cps1.getCount() + cps2.getCount());
            cps.setTaxableVal(cps1.getTaxableVal().add(cps2.getTaxableVal()));
            cps.setTaxPayable(cps1.getTaxPayable().add(cps2.getTaxPayable()));
            cps.setSgst(cps1.getSgst().add(cps2.getSgst()));

            return cps;
    }
    
    
    
	private void addToCPSMutable(CounterPartyInfoResponseSummaryDto cps,
			CounterPartyInfoDto cpi) {
		cps.setType(cpi.getAction());
		cps.setCgst(cps.getCgst().add(cpi.getCgstAmt()));
		cps.setIgst(cps.getIgst().add(cpi.getIgstAmt()));
		cps.setCess(cps.getCess().add(cpi.getCessAmt()));
		cps.setCount(cps.getCount() + cpi.getCnt());
		cps.setTaxableVal(cps.getTaxableVal().add(cpi.getTaxableValue()));
		cps.setTaxPayable(cps.getTaxPayable().add(cpi.getTaxPayable()));
		cps.setSgst(cps.getSgst().add(cpi.getSgstAmt()));
	}


}
