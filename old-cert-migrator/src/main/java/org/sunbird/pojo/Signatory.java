package org.sunbird.pojo;

import java.util.Map;

public class Signatory {
    private String identity;
    private String designation;
    private String image;
    private String name;

    public Signatory(Map<String,Object> signatoryData){
        setIdentity((String) signatoryData.get("identity"));
        setDesignation((String) signatoryData.get("designation"));
        setImage((String) signatoryData.get("image"));
        //TODO setName()
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
