package com.chung.example.dataretrievalservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.messaging.converter.JsonbMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.charset.StandardCharsets;

@RestController
@ConfigurationProperties(prefix = "thunderstorm")
@Data
public class MainController {

    Logger logger = LoggerFactory.getLogger(MainController.class);

    private String aisSourceUrl;
    private String sourceDataFileLocation;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    FileHandlingService fileHandlingService;

    @GetMapping("/senddata")
    public String sendData(){
        MyFlowData mfd = new MyFlowData();
        mfd.setFirstName("Chung");
        mfd.setLastName("Ha");
        ObjectMapper om = new ObjectMapper();
        try {
            String message = om.writeValueAsString(mfd);
            amqpTemplate.convertAndSend("myflow-fanout-exchange", "", message);

            return "Message sent to the RabbitMQ Fanout Exchange Successfully";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "failed";
    }

    @GetMapping("/getaisdata/{datafile}")
    public String getAisData(@PathVariable String datafile){
//        File file = new File ("C:\\mydata\\myjava\\RabbitMQ\\MyRestAPI_FILE_DB_Flow\\HttpsResttemplate\\data\\AIS_2021_01_01.csv");
//        String filename = "C:\\mydata\\myjava\\RabbitMQ\\MyRestAPI_FILE_DB_Flow\\HttpsResttemplate\\data\\AIS_2021_01_01\\AIS_2021_01_01_01.csv";
//        String filename = "C:\\mydata\\myjava\\RabbitMQ\\MyRestAPI_FILE_DB_Flow\\HttpsResttemplate\\data\\AIS_2021_01_01\\AIS_2021_01_01.csv";
        String url = this.aisSourceUrl+datafile+".zip";
        String sourceZipFile = sourceDataFileLocation+datafile+".zip";
        try {
            logger.info("url: "+url);
            logger.info("sourceZipFile: "+sourceZipFile);
            if(fileHandlingService.downloadFile(url,sourceZipFile)) {
                fileHandlingService.unzip(sourceZipFile);
            }
          amqpTemplate.convertAndSend("aisData-topic-exchange", "aisdata.123", "C:\\mydata\\myjava\\RabbitMQ\\MyRestAPI_FILE_DB_Flow\\DataRetrievalService\\data\\unzip\\"+datafile+".csv");

            return "AIS sent to the RabbitMQ Topic Exchange Successfully";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "failed";
    }

}
