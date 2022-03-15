package com.chung.example.dataretrievalservice;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@ConfigurationProperties(prefix = "thunderstorm")
@Data
public class FileHandlingService {
    Logger logger = LoggerFactory.getLogger(FileHandlingService.class);


    private String aisSourceUrl;
    private String sourceDataFileLocation;
    private String sourceDataFileUnzipLocation;

    @Autowired
    RestTemplate restTemplate;

    public boolean downloadFile(String urlString, String datafile){
        logger.info("download requested..");
        URL url;
        try{

//            url= new URL("https://coast.noaa.gov/htdata/CMSP/AISDataHandler/2021/AIS_2021_01_03.zip");
              url = new URL(urlString);
               //String downloadFile = this.sourceDataFileLocation+datafile;
            logger.info("download started..");
            logger.info("datafile: "+datafile);
            try (BufferedInputStream in = new BufferedInputStream(url.openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(datafile)) {
//                 FileOutputStream fileOutputStream = new FileOutputStream("./data/AIS_2021_01_03.zip")) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                logger.info("download completed..");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean unzip(String targetFileToUnzip){
        logger.info("unzip requested..");
//        String fileZip = "./data/AIS_2021_01_02.zip";

//        File destDir = new File("./unzip");
        logger.info("sourceDataFileUnzipLocation: "+sourceDataFileUnzipLocation);
        logger.info("targetFileToUnzip: "+targetFileToUnzip);
        File destDir = new File(sourceDataFileUnzipLocation);
        File newFile = new File("");
        try {

            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(targetFileToUnzip));
            ZipEntry zipEntry = zis.getNextEntry();

            logger.info("unzip started..");
            while (zipEntry != null) {
                newFile = this.newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
            logger.info("unzip completed..");

            //newFile.delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }



    public File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
