package org.sunbird.pojo;

import java.util.Map;

public class Training {
    //CourseId
    private String id;
    private String type;
    private String name;
    private String batchId;

    public Training(String name,String batchId,String courseId){
        setName(name);
        setBatchId(batchId);
        setType("course");
        setId(courseId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
}
