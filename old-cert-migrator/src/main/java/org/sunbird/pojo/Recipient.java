package org.sunbird.pojo;

import java.util.Map;

public class Recipient {
    private String id;
    private String name;
    private String type;

    public Recipient(Map<String,Object> recipientData){
        setId((String) recipientData.get("identity"));
        setName((String) recipientData.get("name"));
        setType("user");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
