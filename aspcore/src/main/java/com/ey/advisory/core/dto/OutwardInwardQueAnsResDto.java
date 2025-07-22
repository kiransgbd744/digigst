package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OutwardInwardQueAnsResDto {

    @Expose
    @SerializedName("question")
    public String question;
    @Expose
    @SerializedName("selected")
    public String selected;
    @Expose
    @SerializedName("id")
    public Long id;
    
    @Expose
    @SerializedName("items")
    public OutwardInwardQueAnsItem items;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OutwardInwardQueAnsItem getItems() {
        return items;
    }

    public void setItems(OutwardInwardQueAnsItem items) {
        this.items = items;
    }

    @Override
    public String toString() {
	return "OutwardInwardQueAnsResDto [question=" + question + ", selected="
		+ selected + ", id=" + id + ", items=" + items + "]";
    }
}
