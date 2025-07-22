package com.ey.advisory.app.docs.enums.gstr9;

import com.google.common.collect.ImmutableList;

import lombok.Getter;

@Getter
public enum SectionType {
	
	
	OUTWARD(ImmutableList.of("4", "5")),
    INWARD(ImmutableList.of("6", "7", "8")),
    PY_TRANSACTION(ImmutableList.of("10", "11", "12", "13")),	
	HSNSECTION(ImmutableList.of("17","18"));


    private final ImmutableList<String> sections;

    SectionType(ImmutableList<String> sections) {
        this.sections = sections;
    }

    public ImmutableList<String> getSections() {
        return sections;
    }

}
