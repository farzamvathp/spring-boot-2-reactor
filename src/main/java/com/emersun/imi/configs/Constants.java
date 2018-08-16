package com.emersun.imi.configs;

public class Constants {
    public static final String SAMPLE_SOAP_REQUEST = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\">\n" +
            "<soap:Header/>\n" +
            "<soap:Body>\n" +
            "<tem:XmsRequest>\n" +
            "\n" +
            "<tem:requestData><![CDATA[<xmsrequest>\n%s" +
            "</xmsrequest>]]></tem:requestData>\n" +
            "</tem:XmsRequest>\n" +
            "</soap:Body>\n" +
            "</soap:Envelope>";
    public static final String ACCESS_TOKEN_AUDIENCE = "access_token_audience";
    public static final String REFRESH_TOKEN_AUDIENCE = "refresh_token_audience";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String OPERATOR_ROLE = "OPERATOR";
}
