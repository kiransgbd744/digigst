package com.ey.advisory.app.services.ledger;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GstnRcmLedgerDto {

    @Expose
    private String gstin;

    @Expose
    private RcmLedgerOpnBal opnbal;

    @Expose
    private String action;

}
