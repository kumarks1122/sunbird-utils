package org.sunbird.cloud;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import org.apache.tika.Tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Locale;

public class AzureConnectionManager {
    private static String storageAccountString;
    private static CloudBlobClient blobClient;

    public AzureConnectionManager(String accountName, String accountKey) {
        storageAccountString = "DefaultEndpointsProtocol=https;AccountName=" + accountName + ";AccountKey=" + accountKey + ";EndpointSuffix=core.windows.net";
    }

    public CloudBlobContainer getContainer(String containerName, boolean isPublicAccess) {
        try {
            CloudBlobClient cloudBlobClient;
            if (null == blobClient) {
                cloudBlobClient = this.getBlobClient();
            } else {
                cloudBlobClient = blobClient;
            }

            CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName.toLowerCase(Locale.ENGLISH));
            boolean response = container.createIfNotExists();
            System.out.println("container creation done if not exist==" + response);
            if (isPublicAccess) {
                BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
                containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);
                container.uploadPermissions(containerPermissions);
            }
            return container;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception occurred while fetching container" + ex.getMessage());
            return null;
        }
    }

    private CloudBlobClient getBlobClient() {
        CloudBlobClient cblobClient = null;
        try {
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageAccountString);
            cblobClient = storageAccount.createCloudBlobClient();
        } catch (URISyntaxException uriSyntaxException) {
            uriSyntaxException.printStackTrace();
            System.out.println("CloudBlobClient:getBlobClient" + uriSyntaxException.getMessage());
        } catch (InvalidKeyException invalidKeyException) {
            invalidKeyException.printStackTrace();
            System.out.println("CloudBlobClient:getBlobClient" + invalidKeyException.getMessage());
        }
        return cblobClient;
    }

    public String uploadFile(String containerName, File source) {
        String containerPath = "";
        String filePath = "";
        Tika tika = new Tika();
        if (containerName.startsWith("/")) {
            containerName = containerName.substring(1);
        }

        String[] str = containerName.split("/");
        containerPath = str[0] + "/";
        filePath = containerName.replace(containerPath, "");
        CloudBlobContainer container = this.getContainer(containerPath, true);
        CloudBlockBlob blob = null;
        String fileUrl = null;
        FileInputStream fis = null;
        try {
            blob = container.getBlockBlobReference(filePath + source.getName());
            fis = new FileInputStream(source);
            String mimeType = tika.detect(source);
            PrintStream printStream = System.out;
            String sourceName = source.getName();
            printStream.println("File - " + sourceName + " mimeType " + mimeType);
            blob.getProperties().setContentType(mimeType);
            blob.upload(fis, source.length());
            fileUrl = blob.getUri().toString();
            System.out.println("Uploaded file URL::  " + fileUrl);
        } catch (IOException | URISyntaxException exception) {
            exception.printStackTrace();
            System.out.println("Unable to upload file :" + source.getName());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    System.out.println(ioException.getMessage());
                }
            }
        }
        return fileUrl;
    }

    public boolean deleteFile(String containerName, File source) {
        boolean isDeleted = false;
        String containerPath = "";
        String filePath = "";
        if (containerName.startsWith("/")) {
            containerName = containerName.substring(1);
        }
        String[] str = containerName.split("/");
        containerPath = str[0] + "/";
        filePath = containerName.replace(containerPath, "");
        CloudBlobContainer container = this.getContainer(containerPath, true);
        CloudBlockBlob blob = null;
        try {
            blob = container.getBlockBlobReference(filePath + source.getName());
            PrintStream printStream = System.out;
            String sourceName = source.getName();
            printStream.println("File - " + sourceName);
            isDeleted = blob.deleteIfExists();
            System.out.println("Deleted file with path::  " + filePath + source.getName());
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
            System.out.println("Unable to delete file :" + source.getName());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return isDeleted;
    }

    public void downloadFile(String containerName, File source,String destinationPath) {
        String containerPath = "";
        String filePath = "";
        if (containerName.startsWith("/")) {
            containerName = containerName.substring(1);
        }
        String[] str = containerName.split("/");
        containerPath = str[0] + "/";
        filePath = containerName.replace(containerPath, "");
        CloudBlobContainer container = this.getContainer(containerPath, true);
        CloudBlockBlob blob = null;
        try {
            blob = container.getBlockBlobReference(filePath + source.getName());
            PrintStream printStream = System.out;
            String sourceName = source.getName();
            printStream.println("File - " + sourceName);
            blob.downloadToFile(destinationPath);
            System.out.println("Download file with path::  " + filePath + source.getName());
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
            System.out.println("Unable to download file :" + source.getName());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}