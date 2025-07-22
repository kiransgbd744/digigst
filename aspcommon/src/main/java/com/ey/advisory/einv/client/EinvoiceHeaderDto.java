
package com.ey.advisory.einv.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EinvoiceHeaderDto {

    @SerializedName("accvoucherno")
    @Expose
    private Integer accvoucherno;
    @SerializedName("accvoucherdt")
    @Expose
    private LocalDate accvoucherdt;
    @SerializedName("userid")
    @Expose
    private Integer userid;
    @SerializedName("sourcefilename")
    @Expose
    private String sourcefilename;
    @SerializedName("ewbnumber")
    @Expose
    private Integer ewbnumber;
    @SerializedName("ewbdate")
    @Expose
    private LocalDate ewbdate;
    @SerializedName("returnperiod")
    @Expose
    private String returnperiod;
    @SerializedName("originaldocumenttype")
    @Expose
    private String originaldocumenttype;
    @SerializedName("originalcustomergstin")
    @Expose
    private String originalcustomergstin;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("doccategory")
    @Expose
    private String doccategory;
    @SerializedName("documenttype")
    @Expose
    private String documenttype;
    @SerializedName("documentnumber")
    @Expose
    private String documentnumber;
    @SerializedName("documentdate")
    @Expose
    private LocalDate documentdate;
    @SerializedName("revchargeflag")
    @Expose
    private String revrchargeflag;
    @SerializedName("suppliergstin")
    @Expose
    private String suppliergstin;
    @SerializedName("suppliertradename")
    @Expose
    private String suppliertradename;
    @SerializedName("supplierlegalname")
    @Expose
    private String supplierlegalname;
    @SerializedName("supplierlocation")
    @Expose
    private String supplierlocation;
    @SerializedName("supplierpincode")
    @Expose
    private Integer supplierpincode;
    @SerializedName("supplierstatecode")
    @Expose
    private Integer supplierstatecode;
    @SerializedName("customergstin")
    @Expose
    private String customergstin;
    @SerializedName("customertradename")
    @Expose
    private String customertradename;
    @SerializedName("division")
    @Expose
    private String division;
    @SerializedName("customerlegalname")
    @Expose
    private String customerlegalname;
    @SerializedName("customerbuildingnumber")
    @Expose
    private String customerbuildingnumber;
    @SerializedName("customerbuildingname")
    @Expose
    private String customerbuildingname;
    @SerializedName("customerfloornumber")
    @Expose
    private String customerfloornumber;
    @SerializedName("customerlocation")
    @Expose
    private String customerlocation;
    @SerializedName("customerpincode")
    @Expose
    private Integer customerpincode;
    @SerializedName("customerstatecode")
    @Expose
    private Integer customerstatecode;
    @SerializedName("billingpos")
    @Expose
    private String billingpos;
    @SerializedName("dispatchergstin")
    @Expose
    private String dispatchergstin;
    @SerializedName("dispatchertradename")
    @Expose
    private String dispatchertradename;
    @SerializedName("dispatcherlocation")
    @Expose
    private String dispatcherlocation;
    @SerializedName("dispatcherpincode")
    @Expose
    private Integer dispatcherpincode;
    @SerializedName("dispatcherstatecode")
    @Expose
    private Integer dispatcherstatecode;
    @SerializedName("shiptogstin")
    @Expose
    private String shiptogstin;
    @SerializedName("shiptotradename")
    @Expose
    private String shiptotradename;
    @SerializedName("shiptolegalname")
    @Expose
    private String shiptolegalname;
    @SerializedName("shiptolocation")
    @Expose
    private String shiptolocation;
    @SerializedName("shiptopincode")
    @Expose
    private Integer shiptopincode;
    @SerializedName("shiptostatecode")
    @Expose
    private Integer shiptostatecode;
    @SerializedName("customertype")
    @Expose
    private String customertype;
    @SerializedName("customercode")
    @Expose
    private Integer customercode;
    @SerializedName("invoiceassessableamount")
    @Expose
    private BigDecimal invoiceassessableamount;
    @SerializedName("invoiceigstamount")
    @Expose
    private BigDecimal invoiceigstamount;
    @SerializedName("invoicecgstamount")
    @Expose
    private BigDecimal invoicecgstamount;
    @SerializedName("invoicesgstamount")
    @Expose
    private BigDecimal invoicesgstamount;
    @SerializedName("invoicecessadvaloremamount")
    @Expose
    private BigDecimal invoicecessadvaloremamount;
    @SerializedName("invoicecessspecificamount")
    @Expose
    private BigDecimal invoicecessspecificamount;
    @SerializedName("invoicestatecessamount")
    @Expose
    private BigDecimal invoicestatecessamount;
    @SerializedName("taxtotal")
    @Expose
    private BigDecimal taxtotal;
    @SerializedName("foreigncurrency")
    @Expose
    private String foreigncurrency;
    @SerializedName("countrycode")
    @Expose
    private String countrycode;
    @SerializedName("invoicevaluefc")
    @Expose
    private BigDecimal invoicevaluefc;
    @SerializedName("portcode")
    @Expose
    private String portcode;
    @SerializedName("shippingbillnumber")
    @Expose
    private String shippingbillnumber;
    @SerializedName("shippingbilldate")
    @Expose
    private LocalDate shippingbilldate;
    @SerializedName("invoiceperiodstartdate")
    @Expose
    private LocalDate invoiceperiodstartdate;
    @SerializedName("invoiceperiodenddate")
    @Expose
    private LocalDate invoiceperiodenddate;
    @SerializedName("paidamount")
    @Expose
    private BigDecimal paidamount;
    @SerializedName("balanceamount")
    @Expose
    private BigDecimal balanceamount;
    @SerializedName("accountdetail")
    @Expose
    private String accountdetail;
    @SerializedName("ecomtransaction")
    @Expose
    private String ecomtransaction;
    @SerializedName("ecomgstin")
    @Expose
    private String ecomgstin;
    @SerializedName("ecompos")
    @Expose
    private String ecompos;
    @SerializedName("transactiontype")
    @Expose
    private String transactiontype;
    @SerializedName("subsupplytype")
    @Expose
    private String subsupplytype;
    @SerializedName("othersupplytypedescription")
    @Expose
    private String othersupplytypedescription;
    @SerializedName("transporterid")
    @Expose
    private Integer transporterid;
    @SerializedName("transportername")
    @Expose
    private String transportername;
    @SerializedName("transportmode")
    @Expose
    private String transportmode;
    @SerializedName("transportdocno")
    @Expose
    private Integer transportdocno;
    @SerializedName("transportdocdate")
    @Expose
    private LocalDate transportdocdate;
    @SerializedName("distance")
    @Expose
    private Integer distance;
    @SerializedName("vehicleno")
    @Expose
    private String vehicleno;
    @SerializedName("vehicletype")
    @Expose
    private String vehicletype;
    @SerializedName("diffperflag")
    @Expose
    private String diffperflag;
    @SerializedName("sec7igstflag")
    @Expose
    private String sec7igstflag;
    @SerializedName("claimrefundflag")
    @Expose
    private String claimrefundflag;
    @SerializedName("autopoprefund")
    @Expose
    private String autopoprefund;
    @SerializedName("crdrpregst")
    @Expose
    private String crdrpregst;
    @SerializedName("tcsflag")
    @Expose
    private String tcsflag;
    @SerializedName("stateapplyingcess")
    @Expose
    private String stateapplyingcess;
    @SerializedName("exchangerate")
    @Expose
    private Integer exchangerate;
    @SerializedName("itcentitlement")
    @Expose
    private String itcentitlement;
    
    @SerializedName("lineitems")
    @Expose
    private List<EinvoiceLineItemDto> lineItems = new ArrayList<>();

}
