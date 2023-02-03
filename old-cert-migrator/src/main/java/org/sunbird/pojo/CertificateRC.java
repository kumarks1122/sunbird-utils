package org.sunbird.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CertificateRC {

    private String certificateLabel;
    private String status;
    private String issued;
    private Recipient recipient;
    private Training training;
    private Issuer issuer;
    private List<Signatory> signatory;
    private String svgTemplate;

    public CertificateRC(Map<String,Object> certificateData,String batchId,String courseId,String svgTemplate){
      setIssued((String) certificateData.get("issuedOn"));
      setRecipient(new Recipient((Map<String, Object>) certificateData.get("recipient")));
      Map<String, Object> badgeData =  (Map<String, Object>) certificateData.get("badge");
      setCertificateLabel((String) badgeData.get("name"));
      setStatus("ACTIVE");
      setIssuer(new Issuer((Map<String, Object>) badgeData.get("issuer")));
      setSignatory(getSignatoryList((List<Map<String, Object>>) certificateData.get("signatory")));
      setTraining(new Training((String) badgeData.get("name"),batchId, courseId));
      setSvgTemplate(svgTemplate);
    }

    private List<Signatory> getSignatoryList(List<Map<String,Object>> mapList){
        List<Signatory> signatoryList = new ArrayList<>();
        for (Map<String,Object> signMap:mapList) {
            signatoryList.add(new Signatory(signMap));
        }
        return signatoryList;
    }

    public String getCertificateLabel() {
        return certificateLabel;
    }

    public void setCertificateLabel(String certificateLabel) {
        this.certificateLabel = certificateLabel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public Training getTraining() {
        return training;
    }

    public void setTraining(Training training) {
        this.training = training;
    }

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    public List<Signatory> getSignatory() {
        return signatory;
    }

    public void setSignatory(List<Signatory> signatory) {
        this.signatory = signatory;
    }

    public String getSvgTemplate() {
        return svgTemplate;
    }

    public void setSvgTemplate(String svgTemplate) {
        this.svgTemplate = svgTemplate;
    }
}
