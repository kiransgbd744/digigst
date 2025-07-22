/**
 * 
 */
package com.ey.advisory.app.data.services.gstr9;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr9.Gstr9DownloadInnerDto;

/**
 * @author vishal.verma
 *
 */


@Component("GSTR9ReportFormulaCalculationService")
public class GSTR9ReportFormulaCalculationService {

	public  List<Gstr9DownloadInnerDto> getFormulaValues(
			List<Gstr9DownloadInnerDto> dtoList) {

		if (dtoList != null && !dtoList.isEmpty()) {

			Gstr9DownloadInnerDto calculated4HObj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("4A")
							|| o.getSubSection().equalsIgnoreCase("4B")
							|| o.getSubSection().equalsIgnoreCase("4C")
							|| o.getSubSection().equalsIgnoreCase("4D")
							|| o.getSubSection().equalsIgnoreCase("4E")
							|| o.getSubSection().equalsIgnoreCase("4F")
							|| o.getSubSection().equalsIgnoreCase("4G")
							|| o.getSubSection().equalsIgnoreCase("4G1"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			calculated4HObj.setSubSection("4H");

		       // Now find the original "4H" in the list and update its value
	        Optional<Gstr9DownloadInnerDto> existing4H = dtoList.stream()
	                .filter(o -> o.getSubSection().equalsIgnoreCase("4H"))
	                .findFirst();

	        if (existing4H.isPresent()) {
	            // Update the existing "4H" value
	            Gstr9DownloadInnerDto dto4H = existing4H.get();
	            dto4H.setTaxableValue(calculated4HObj.getTaxableValue()); // Update the taxable value or any other field
	        } else {
	            // If "4H" doesn't exist in the list, add it
	            dtoList.add(calculated4HObj);
	        }
			
			//4M n 5L --> -(4I,4L) + (4j,4k)
		/*	Gstr9DownloadInnerDto calculated4MObj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("4I")
							|| o.getSubSection().equalsIgnoreCase("4J")
							|| o.getSubSection().equalsIgnoreCase("4K")
							|| o.getSubSection().equalsIgnoreCase("4L"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			calculated4MObj.setSubSection("4M");*/
			
			
			Gstr9DownloadInnerDto calculated4JKobj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("4J")
							|| o.getSubSection().equalsIgnoreCase("4K"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			
			Gstr9DownloadInnerDto calculated4ILobj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("4I")
							|| o.getSubSection().equalsIgnoreCase("4L"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			
			if( calculated4ILobj == null){
				setCopyValue(
						calculated4ILobj, "4IL");
			}
			Gstr9DownloadInnerDto calculated4MObj = 
					subDto(calculated4JKobj, calculated4ILobj);
			if(calculated4MObj != null)
			calculated4MObj.setSubSection("4M");
			
				Gstr9DownloadInnerDto calculated4NObj = addDto(calculated4HObj, 
					calculated4MObj);
			calculated4NObj.setSubSection("4N");

			//dtoList.add(calculated4HObj);
			dtoList.add(calculated4MObj);
			dtoList.add(calculated4NObj);

			Gstr9DownloadInnerDto calculated5GObj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("5A")
							|| o.getSubSection().equalsIgnoreCase("5B")
							|| o.getSubSection().equalsIgnoreCase("5C")
							|| o.getSubSection().equalsIgnoreCase("5C1")
							|| o.getSubSection().equalsIgnoreCase("5D")
							|| o.getSubSection().equalsIgnoreCase("5E")
							|| o.getSubSection().equalsIgnoreCase("5F"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			calculated5GObj.setSubSection("5G");

			
			// 5L ---> -(5K,5H) +(5I,5J)
			/*Gstr9DownloadInnerDto calculated5LObj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("5H")
							|| o.getSubSection().equalsIgnoreCase("5I")
							|| o.getSubSection().equalsIgnoreCase("5J")
							|| o.getSubSection().equalsIgnoreCase("5K"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			calculated5LObj.setSubSection("5L");
			*/
			
			Gstr9DownloadInnerDto calculated5IJobj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("5I")
							|| o.getSubSection().equalsIgnoreCase("5J"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
		
			Gstr9DownloadInnerDto calculated5HKobj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("5H")
							|| o.getSubSection().equalsIgnoreCase("5K"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			
			Gstr9DownloadInnerDto calculated5LObj = subDto(calculated5IJobj, 
					calculated5HKobj);
			if(calculated5LObj != null)
			calculated5LObj.setSubSection("5L");
			
			
			
			
			
			
			
			
			
			

			Gstr9DownloadInnerDto calculated5MObj = addDto(calculated5GObj, 
					calculated5LObj);
			calculated5MObj.setSubSection("5M");

			Gstr9DownloadInnerDto sum5MAnd4N = addDto(calculated4NObj,
					calculated5MObj);
			Gstr9DownloadInnerDto calculated4GG1obj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("4G")
							|| o.getSubSection().equalsIgnoreCase("4G1"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			Gstr9DownloadInnerDto calculated5NObj = sum5MAnd4N;
			if (calculated4GG1obj != null) {
				 calculated5NObj = subDto(sum5MAnd4N,
						 calculated4GG1obj);
				calculated5NObj.setSubSection("5N");
				dtoList.add(calculated5NObj);
			}
			dtoList.add(calculated5GObj);
			dtoList.add(calculated5LObj);
			dtoList.add(calculated5MObj);
			
			//6I
			
			Gstr9DownloadInnerDto calculated6IObj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("6B1")
							|| o.getSubSection().equalsIgnoreCase("6B2")
							|| o.getSubSection().equalsIgnoreCase("6B3")
							|| o.getSubSection().equalsIgnoreCase("6C1")
							|| o.getSubSection().equalsIgnoreCase("6C2")
							|| o.getSubSection().equalsIgnoreCase("6C3")
							|| o.getSubSection().equalsIgnoreCase("6D1")
							|| o.getSubSection().equalsIgnoreCase("6D2")
							|| o.getSubSection().equalsIgnoreCase("6D3")
							|| o.getSubSection().equalsIgnoreCase("6E1")
							|| o.getSubSection().equalsIgnoreCase("6E2")
							|| o.getSubSection().equalsIgnoreCase("6F")
							|| o.getSubSection().equalsIgnoreCase("6G")
							|| o.getSubSection().equalsIgnoreCase("6H"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			calculated6IObj.setSubSection("6I");
			dtoList.add(calculated6IObj);
			
			List<Gstr9DownloadInnerDto> obj6A = dtoList.stream()
					.filter(o -> o.getSubSection().startsWith("6A"))
					.collect(Collectors.toCollection(ArrayList::new));
			
			Gstr9DownloadInnerDto calculated6JObj = setCopyValue(
					calculated6IObj, "6J");
			if (obj6A != null  && !obj6A.isEmpty()) {
				 calculated6JObj = subDto(calculated6IObj,
						obj6A.get(0));
				calculated6JObj.setSubSection("6J");
				dtoList.add(calculated6JObj);
			}else{
				calculated6JObj.setSubSection("6J");
				dtoList.add(calculated6JObj);
			}
			
			Gstr9DownloadInnerDto calculated6NObj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("6K")
							|| o.getSubSection().equalsIgnoreCase("6L")
							|| o.getSubSection().equalsIgnoreCase("6M"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			calculated6NObj.setSubSection("6N");
			dtoList.add(calculated6NObj);
			
			
			Gstr9DownloadInnerDto calculated6OObj = addDto(calculated6IObj, 
					calculated6NObj);
			calculated6OObj.setSubSection("6O");
			dtoList.add(calculated6OObj);
			
			//7I
			
			Gstr9DownloadInnerDto calculated7IObj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("7A")
							|| o.getSubSection().equalsIgnoreCase("7B")
							|| o.getSubSection().equalsIgnoreCase("7C")
							|| o.getSubSection().equalsIgnoreCase("7D")
							|| o.getSubSection().equalsIgnoreCase("7E")
							|| o.getSubSection().equalsIgnoreCase("7F")
							|| o.getSubSection().equalsIgnoreCase("7G")
							|| o.getSubSection().equalsIgnoreCase("7H"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			calculated7IObj.setSubSection("7I");
			dtoList.add(calculated7IObj);
			
			Gstr9DownloadInnerDto calculated7JObj = setCopyValue(
					calculated6OObj, "7J");
			
			if (calculated6OObj != null ) {
				 calculated7JObj = subDto(calculated6OObj, 
						calculated7IObj);
				calculated7JObj.setSubSection("7J");
				dtoList.add(calculated7JObj);
			}
			
			//8B
			Gstr9DownloadInnerDto calculated8BObj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("6B1")
							|| o.getSubSection().equalsIgnoreCase("6B2")
							|| o.getSubSection().equalsIgnoreCase("6B3")
							|| o.getSubSection().equalsIgnoreCase("6H"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			calculated8BObj.setSubSection("8B");
			dtoList.add(calculated8BObj);
			
			Gstr9DownloadInnerDto calculated8DObj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("8B")
							|| o.getSubSection().equalsIgnoreCase("8C"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			
				List<Gstr9DownloadInnerDto> obj8A = dtoList.stream()
						.filter(o -> o.getSubSection().startsWith("8A"))
						.collect(Collectors.toCollection(ArrayList::new));
				if (obj8A != null && !obj8A.isEmpty()) {
				 calculated8DObj = subDto(obj8A.get(0), 
						calculated8DObj);
				} else{
					Gstr9DownloadInnerDto obj8ADefault = defaultVal("8A");
					calculated8DObj = subDto(obj8ADefault, calculated8DObj);
				}
			calculated8DObj.setSubSection("8D");
			dtoList.add(calculated8DObj);
			
			Gstr9DownloadInnerDto calculated8HObj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("6E1")
							|| o.getSubSection().equalsIgnoreCase("6E2"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			calculated8HObj.setSubSection("8H");
			dtoList.add(calculated8HObj);
			
			
			Gstr9DownloadInnerDto calculated8IObj = null;
			List<Gstr9DownloadInnerDto> obj8G = dtoList.stream()
					.filter(o -> o.getSubSection().startsWith("8G"))
					.collect(Collectors.toCollection(ArrayList::new));
			
			if(obj8G != null && !obj8G.isEmpty()){
				calculated8IObj = setCopyValue(obj8G.get(0), "8I");
				calculated8IObj = subDto(obj8G.get(0), 
						calculated8HObj);
				calculated8IObj.setSubSection("8I");
				dtoList.add(calculated8IObj);
			}
			
			//8I = 8J
			Gstr9DownloadInnerDto calculated8JObj = setCopyValue(
					calculated8IObj, "8J");
			dtoList.add(calculated8JObj);
			
			Gstr9DownloadInnerDto calculated8KObj = dtoList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("8E")
							|| o.getSubSection().equalsIgnoreCase("8F")
							|| o.getSubSection().equalsIgnoreCase("8J"))
					.collect(Collectors.reducing(new Gstr9DownloadInnerDto(),
							(a, b) -> addDto(a, b)));
			calculated8KObj.setSubSection("8K");
			dtoList.add(calculated8KObj);

		}

		return dtoList;

	}

	private Gstr9DownloadInnerDto setCopyValue(
			Gstr9DownloadInnerDto obj, String subSection) {
		Gstr9DownloadInnerDto dto = new Gstr9DownloadInnerDto();
		BigDecimal zero = BigDecimal.ZERO;
		
		dto.setCess(obj != null ? obj.getCess() : zero);
		dto.setCgst(obj != null ? obj.getCgst() : zero);
		dto.setIgst(obj != null ? obj.getIgst() : zero);
		dto.setSgst(obj != null ? obj.getSgst() : zero);
		dto.setTaxableValue(obj != null ? obj.getTaxableValue() : zero);
		dto.setInterest(obj != null ? obj.getInterest() : zero);
		dto.setLateFee(obj != null ? obj.getLateFee() : zero);
		dto.setPenalty(obj != null ? obj.getPenalty() : zero);
		dto.setOther(obj != null ? obj.getOther() : zero);
		dto.setSubSection(subSection);

		return dto;
	}

	private  Gstr9DownloadInnerDto addDto(Gstr9DownloadInnerDto a,
			Gstr9DownloadInnerDto b) {
		Gstr9DownloadInnerDto dto = new Gstr9DownloadInnerDto();
		dto.setCess(a.getCess().add(b.getCess()));
		dto.setCgst(a.getCgst().add(b.getCgst()));
		dto.setIgst(a.getIgst().add(b.getIgst()));
		dto.setSgst(a.getSgst().add(b.getSgst()));
		dto.setTaxableValue(a.getTaxableValue().add(b.getTaxableValue()));
		dto.setInterest(a.getInterest().add(b.getInterest()));
		dto.setLateFee(a.getLateFee().add(b.getLateFee()));
		dto.setPenalty(a.getPenalty().add(b.getPenalty()));
		dto.setOther(a.getOther().add(b.getOther()));

		return dto;
	}

	private  Gstr9DownloadInnerDto subDto(Gstr9DownloadInnerDto a,
			Gstr9DownloadInnerDto b) {
		Gstr9DownloadInnerDto dto = new Gstr9DownloadInnerDto();
		dto.setCess(a.getCess().subtract(b.getCess()));
		dto.setCgst(a.getCgst().subtract(b.getCgst()));
		dto.setIgst(a.getIgst().subtract(b.getIgst()));
		dto.setSgst(a.getSgst().subtract(b.getSgst()));
		dto.setTaxableValue(a.getTaxableValue().subtract(b.getTaxableValue()));
		dto.setInterest(a.getInterest().subtract(b.getInterest()));
		dto.setLateFee(a.getLateFee().subtract(b.getLateFee()));
		dto.setPenalty(a.getPenalty().subtract(b.getPenalty()));
		dto.setOther(a.getOther().subtract(b.getOther()));

		return dto;
	}

	private  Gstr9DownloadInnerDto defaultVal(String subSection){
		Gstr9DownloadInnerDto dto = new Gstr9DownloadInnerDto();
		BigDecimal zero = BigDecimal.ZERO;
		dto.setCess(zero);
		dto.setCgst(zero);
		dto.setIgst(zero);
		dto.setSgst(zero);
		dto.setTaxableValue(zero);
		dto.setInterest(zero);
		dto.setLateFee(zero);
		dto.setPenalty(zero);
		dto.setOther(zero);
		dto.setSubSection(subSection);

		return dto;
	}
}
