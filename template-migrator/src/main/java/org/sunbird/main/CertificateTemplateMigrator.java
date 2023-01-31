package org.sunbird.main;

import org.apache.commons.io.FileUtils;
import org.sunbird.cloud.AzureConnectionManager;

import java.io.*;
import java.util.*;

public class CertificateTemplateMigrator {
    private static AzureConnectionManager connectionManager = null;
    public static void main(String[] args) {
        String containerName = "Template";
        String accountName = "";
        String accountKey = "";
        String svgLocation = "/Users/prasad/LERN_Cert/Template";
        String backUpLocation = "/Users/prasad/LERN_Cert/Backup";
        connectionManager = new AzureConnectionManager(accountName, accountKey);
        List<File> fileList = listAllFiles(svgLocation);
        //System.out.println(fileList);
        Map<String, File> fileMap = new HashMap();
        try {
            processFiles(backUpLocation, fileList, containerName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(fileMap);
    }

    private static void processFiles(String backUpLocation, List<File> fileList,String containerName) throws IOException {
        Iterator fileIterator = fileList.iterator();
        String url;
        while(fileIterator.hasNext()) {
            File file = (File)fileIterator.next();
            //System.out.println(file.getAbsolutePath());
            String filePath = file.getAbsolutePath();
            //TODO Check this logic as per the file structure in Blob Container
            String[] strArray = filePath.split(containerName);
            url = strArray[1];
            //Copy file to back up container or location
            //TODO remove this one and use the blob container url or base file path
            File backUpFile = new File(backUpLocation+url);
            connectionManager.downloadFile(containerName,file,backUpLocation+url);
            //FileUtils.copyFile(file,backUpFile,true);
            //Read from the backed up file and modify the contents
            updateFile(backUpFile);
            //Delete the source file
            connectionManager.deleteFile(containerName,file);
            //Upload the modified content to the source location from where we copied it initially
            String[] container = url.split("/");
            StringBuilder containerPath = new StringBuilder();

            for(int i = 3; i < container.length - 1; ++i) {
                containerPath.append("/").append(container[i]);
            }
            connectionManager.uploadFile(containerPath.append("/").toString(), backUpFile);
        }
    }

    public static List<File> listAllFiles(String directoryName) {
        File directory = new File(directoryName);
        List<File> resultList = new ArrayList();
        File[] fList = directory.listFiles();
        for(int i = 0; i < fList.length; ++i) {
            File file = fList[i];
            if (file.isFile() && file.getAbsolutePath().endsWith(".svg")) {
                resultList.add(file);
            } else if (file.isDirectory()) {
                resultList.addAll(listAllFiles(file.getAbsolutePath()));
            }
        }
        return resultList;
    }

    private static void updateFile(File newFile) {
        BufferedWriter bw = null;
        try {
                FileWriter myWriter = new FileWriter(newFile, true);
                bw = new BufferedWriter(myWriter);
                BufferedReader br = new BufferedReader(new InputStreamReader(FileUtils.openInputStream(newFile)));
                String st;
                while((st = br.readLine()) != null) {
                    if (st.contains("${recipientName}")) {
                        st = st.replace("${recipientName}", "{{credentialSubject.recipientName}}");
                    }

                    if (st.contains("${qrCodeImage}")) {
                        st = st.replace("${qrCodeImage}", "{{qrCode}}");
                    }

                    if (st.contains("${courseName}")) {
                        st = st.replace("${courseName}", "{{credentialSubject.trainingName}}");
                    }

                    if (st.contains("${issuedDate}")) {
                        st = st.replace("${issuedDate}", "{{dateFormat issuedDate \"DD MMMM  YYYY\"}}");
                    }

                    if (st.contains("${maxFontSize}")) {
                        st = st.replace("${maxFontSize}", "{{maxFontSize}}");
                    }

                    if (st.contains("${minFontSize}")) {
                        st = st.replace("${minFontSize}", "{{minFontSize}}");
                    }

                    bw.write(st);
                    bw.newLine();
                }
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("Exception while writing svg file.");
        } finally {
            try {
                bw.close();
            } catch (IOException ioException) {
                System.out.println("Exception while closing the file.");
            }

        }
    }
}
