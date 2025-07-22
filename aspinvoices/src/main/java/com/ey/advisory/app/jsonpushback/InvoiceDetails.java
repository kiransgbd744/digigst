
package com.ey.advisory.app.jsonpushback;
import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class InvoiceDetails implements Serializable
{

    @SerializedName("INFRES")
    @Expose
    private Infres infres;
    private final static long serialVersionUID = 6952891384726116397L;

}
