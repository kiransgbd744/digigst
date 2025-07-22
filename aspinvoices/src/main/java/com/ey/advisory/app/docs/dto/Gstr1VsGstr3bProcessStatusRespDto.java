package com.ey.advisory.app.docs.dto;

import lombok.Data;

@Data
public class Gstr1VsGstr3bProcessStatusRespDto {
    private String gstin;

    private String status;

    private String lastUpdatedTime;
}
