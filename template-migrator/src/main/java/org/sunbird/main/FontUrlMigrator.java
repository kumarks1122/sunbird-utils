package org.sunbird.main;

import org.apache.commons.io.FileUtils;
import org.sunbird.cloud.AzureConnectionManager;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FontUrlMigrator {
    private static AzureConnectionManager connectionManager = null;
    public static void main(String[] args) {
        String containerName = "Template";
        String accountName = "";
        String accountKey = "";
        String svgLocation = "/Users/prasad/LERN_Cert/Template";
        String backUpLocation = "/Users/prasad/LERN_Cert/Backup";
        String oldFontUrl = "https://sunbirddev.blob.core.windows.net/e-credentials";
        String newFontUrl = "https://obj.stage.sunbirded.org/e-credentials";
        connectionManager = new AzureConnectionManager(accountName, accountKey);
        List<File> fileList = listAllFiles(svgLocation);
        //System.out.println(fileList);
        Map<String, File> fileMap = new HashMap();
        try {
            processFiles(backUpLocation, fileList, containerName,oldFontUrl,newFontUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(fileMap);
    }

    private static void processFiles(String backUpLocation, List<File> fileList,String containerName,String oldUrl,String newUrl) throws IOException {
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
            updateFile(backUpFile,oldUrl,newUrl);
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

    private static void updateFile(File newFile,String oldUrl,String newUrl) {
        BufferedWriter bw = null;
        try {
            FileWriter myWriter = new FileWriter(newFile, true);
            bw = new BufferedWriter(myWriter);
            BufferedReader br = new BufferedReader(new InputStreamReader(FileUtils.openInputStream(newFile)));
            String st;
            while((st = br.readLine()) != null) {
                if (st.contains(oldUrl)) {
                    st = st.replace(oldUrl, newUrl);
                }
                bw.write(st);
                bw.newLine();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("Exception while updating font url in svg file.");
        } finally {
            try {
                bw.close();
            } catch (IOException ioException) {
                System.out.println("Exception while closing the file after updating font urls.");
            }

        }
    }
}
