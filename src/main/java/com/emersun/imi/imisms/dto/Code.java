package com.emersun.imi.imisms.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;


@JacksonXmlRootElement(localName = "code")
public class Code {
    @JacksonXmlProperty(isAttribute = true,localName = "id")
    private String id;
    @JacksonXmlText
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
