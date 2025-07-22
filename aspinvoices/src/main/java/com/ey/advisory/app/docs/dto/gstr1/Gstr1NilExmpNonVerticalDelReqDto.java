package com.ey.advisory.app.docs.dto.gstr1;

import java.util.List;

import lombok.Data;

@Data
public class Gstr1NilExmpNonVerticalDelReqDto {
    private String docType = "";
    private List<Long> id;
    private List<String> docKey;
    private String gstin = "";
    private String taxPeriod = "";

}
