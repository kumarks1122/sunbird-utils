package org.sunbird.pojo;

import org.antlr.analysis.MachineProbe;

import java.util.List;
import java.util.Map;

public class Issuer{
    private String name;
    private String url;
    //OSID of the issuer
    private String publicKey;

    public Issuer(Map<String,Object> issuerData){
        setName((String) issuerData.get("name"));
        setUrl((String) issuerData.get("url"));
        setPublicKey((String) ((List) issuerData.get("publicKey")).get(0));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
