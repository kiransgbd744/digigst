package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OutwardInwardQueAnsItem {

    @Expose
    @SerializedName("answer")
    public String answer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
	return "OutwardInwardQueAnsItem [answer=" + answer + "]";
    }
    
    
}
