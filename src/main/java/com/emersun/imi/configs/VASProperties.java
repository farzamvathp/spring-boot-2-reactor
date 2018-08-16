package com.emersun.imi.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "vas")
public class VASProperties {
    private Integer userid;
    private String password;
    private Integer serviceid;
    private String originator;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getServiceid() {
        return serviceid;
    }

    public void setServiceid(Integer serviceid) {
        this.serviceid = serviceid;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }
}
