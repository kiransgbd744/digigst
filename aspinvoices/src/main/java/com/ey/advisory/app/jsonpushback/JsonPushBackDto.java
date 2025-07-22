package com.ey.advisory.app.jsonpushback;
import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class JsonPushBackDto implements Serializable
{

    @SerializedName("InvoiceDetails")
    @Expose
    public InvoiceDetails invoiceDetails;
    private final static long serialVersionUID = 5823102994791789205L;

}
