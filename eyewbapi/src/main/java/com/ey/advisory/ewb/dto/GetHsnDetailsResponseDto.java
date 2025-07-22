
package com.ey.advisory.ewb.dto; 

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetHsnDetailsResponseDto implements Serializable
{

    @SerializedName("hsnCode")
    @Expose
    private String hsnCode;
    @SerializedName("hsnDesc")
    @Expose
    private String hsnDesc;
    @SerializedName("errorCode")
    @Expose
    private String errorCode;
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
    private static final long serialVersionUID = 1L;

}
