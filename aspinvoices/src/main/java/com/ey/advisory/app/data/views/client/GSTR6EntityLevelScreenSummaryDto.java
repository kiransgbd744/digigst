package com.ey.advisory.app.data.views.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GSTR6EntityLevelScreenSummaryDto {
    private String GSTIN;
    private String TableDescription;
    private String Count;
    private String invoiceValue;
    private String taxableValue;
    private String totalTax;
    private String IGST;
    private String CGST;
    private String SGST;
    private String Cess;
}
