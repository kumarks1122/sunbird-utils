package org.sunbird;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.pojo.CertificateRC;
import org.sunbird.util.*;

import java.util.*;

class CertificateMigrator{
    private static ObjectMapper objMapper = new ObjectMapper();
    private static CassandraOperation cassandraOperation = new CassandraOperationImpl();
    public static void main(String[] args) {
        System.out.println("Start of Old Certificate Migrator");
        //Fetch data from cert_registry table, filter the records for only valid certificates which were not revoked
        Response response = cassandraOperation.getRecordsWithLimit("sunbird","cert_registry", new HashMap<>(),new ArrayList<>(),10);
        System.out.println(response.getResult().size());
//        cqlsh:sunbird> select * from sunbird.cert_registry where id='6484b573-c33d-45e1-b06e-512818f6cc2f';
//
//        id                                   | accesscode | createdat                       | createdby | data
//        | isrevoked | jsonurl                                                                                        | pdfurl
//        | qrcodeurl | reason | recipient                                                                              | related | updatedat | updatedby
//        3dc055eb-5124-4153-a720-a83a7ac99cd3 |
//                N7Y7L5 |
//                2020-09-03 15:57:10.372000+0000 |
//                null |
//                {"id":"https://preprod.ntp.net.in/certs/01269878797503692810_01310008965383782458/3dc055eb-5124-4153-a720-a83a7ac99cd3","type":["Assertion","Extension","extensions:CertificateExtension"],"issuedOn":"2020-09-03T00:00:00Z","recipient":{"identity":"08631a74-4b94-4cf7-a818-831135248a4a","type":["id"],"hashed":false,"name":"Content_reviewer_TN","@context":"https://preprod.ntp.net.in/certs/v1/context.json"},"badge":{"id":"https://preprod.ntp.net.in/certs/01269878797503692810_01310008965383782458/Badge.json","type":["BadgeClass"],"name":"Test course for multi cert template","criteria":{"type":["Criteria"],"narrative":"course completion certificate"},"issuer":{"context":"https://preprod.ntp.net.in/certs/v1/context.json","id":"https://preprod.ntp.net.in/certs/Issuer.json","type":["Issuer"],"name":"Gujarat Council of Educational Research and Training","url":"https://gcert.gujarat.gov.in/gcert/","publicKey":["https://preprod.ntp.net.in/certs/keys/7_publicKey.json","https://preprod.ntp.net.in/certs/keys/8_publicKey.json"]},"@context":"https://preprod.ntp.net.in/certs/v1/context.json"},"verification":{"type":["hosted"]},"revoked":false,"signatory":[{"identity":"CEO","type":["Extension","extensions:SignatoryExtension"],"hashed":false,"designation":"CEO","image":"https://cdn.pixabay.com/photo/2014/11/09/08/06/signature-523237__340.jpg","@context":"https://preprod.ntp.net.in/certs/v1/extensions/SignatoryExtension/context.json"}],"@context":"https://preprod.ntp.net.in/certs/v1/context.json"} |
//        False |
//                https://preprod.ntp.net.in/certs/01269878797503692810_01310008965383782458/3dc055eb-5124-4153-a720-a83a7ac99cd3.json |
//        https://preprod.ntp.net.in/certs/01269878797503692810_01310008965383782458/File-01310009036587008085.svg1ce4e360-edfe-11ea-b19e-d7b8942c3307.pdf |
//        null |
//                {"name":"Content_reviewer_TN","email":null,"phone":null,"id":"08631a74-4b94-4cf7-a818-831135248a4a","type":null} |
//        {"type":"course completion certificate prad","batchId":"01310008965383782458","courseId":"do_21310008078526054412987"} |
//        null |
//                null
        List<Map<String, Object>> validCertList = new ArrayList<>();
        List<String> revokedIdsForDeletion = new ArrayList<>();
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) response.getResult().get(JsonKey.RESPONSE);
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (Map<String, Object> certInfo:resultList) {
                try {
                    Boolean isRevoked = objMapper.readValue((String) certInfo.get("isrevoked"), Boolean.class);
                    if(isRevoked) revokedIdsForDeletion.add(objMapper.readValue((String) certInfo.get("id"), String.class));
                    else validCertList.add(certInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("CertsServiceImpl:read:exception occurred:" + e.getMessage());
                }
            }
        } else {
            System.out.println("No records to process");
        }
        //For each record fetch the templateUrl from course_batch table
        if(!validCertList.isEmpty()) {
            for (Map<String, Object> certInfo:validCertList) {
                try {
                    Map<String, Object> data = objMapper.readValue((String) certInfo.get(JsonKey.DATA), new TypeReference<Map<String, Object>>() {});
                    Map<String, Object> relatedData = objMapper.readValue((String) certInfo.get("related"), new TypeReference<Map<String, Object>>() {});
                    String batchId = (String) relatedData.get("batchId");
                    String courseId = (String) relatedData.get("courseId");
                    Map<String, Object> primaryKey = new HashMap<>();
                    primaryKey.put(JsonKey.COURSE_ID, courseId);
                    primaryKey.put(JsonKey.BATCH_ID, batchId);
                    Response courseBatchResponse = cassandraOperation.getRecordById("sunbird_courses","course_batch", primaryKey, new ArrayList<>(){{add("cert_templates");}});
                    Map<String, Object> objectMap = (Map<String, Object>)courseBatchResponse.getResult().get(JsonKey.RESPONSE);
                    Map<String, Object> svgTemplateMap = (Map<String, Object>) objectMap.values().stream().findFirst().get();
                    String templateUrl = (String) svgTemplateMap.get("url");
                    //TODO replace the placeholder value in the template url
                    //Create request Object for RC
                    CertificateRC rcRequestObj = new CertificateRC(data,batchId,courseId,templateUrl);
                    //Make a Call to RC
                    HttpResponse<String> rcResponse = HttpUtils.post("http://localhost:8081/api/v1/TrainingCertificate",JSONUtils.serialize(rcRequestObj));
                    if(rcResponse.getStatus() == 200){
                        System.out.println("Successfully sent RC Request");
                        //Fetch the osid from the response
                    } else{
                        // TODO: Fetch error message from response and add to exception message
                        System.out.println("Status code is not 200 while sending RC Request");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("CertsServiceImpl:read:exception occurred:" + e.getMessage());
                }
            }
        }

        //TODO Tasks
        //Insert the data into back_up table for tracking the migration
        //Delete the data from cert_registry
        //Delete the data from ES
    }
}