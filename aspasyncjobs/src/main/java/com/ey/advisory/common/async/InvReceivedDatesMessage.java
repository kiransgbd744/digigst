package com.ey.advisory.common.async;

import java.time.LocalDate;
import java.util.List;

public class InvReceivedDatesMessage {

	private List<LocalDate> listOfDates;

	public List<LocalDate> getListOfDates() {
		return listOfDates;
	}

	public void setListOfDates(List<LocalDate> listOfDates) {
		this.listOfDates = listOfDates;
	}

	@Override
	public String toString() {
		return "InvReceivedDatesMessage [listOfDates=" + listOfDates + "]";
	}
	
	
}
