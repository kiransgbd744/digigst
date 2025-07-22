
package com.ey.advisory.einv.client;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EinvoiceReqDto {

    @Expose
    private EinvoiceHeaderDto hdr;
   

}
