package com.chung.example.dbwritingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class MyAisDataDbWriter {

    Logger logger = LoggerFactory.getLogger(MyAisDataDbWriter.class);

    @Autowired
    AisDataRepository aisDataRepository;

    @RabbitListener(queues = "${aisData.rabbitmq.queue}")
    public void recievedMessage(String filename) {
        logger.info("receiveMessage aisData ...");
        ObjectMapper om = new ObjectMapper();
        try {

            File file = new File(filename);
            if(this.saveToDb(file)) {
                logger.info("AIS Data saved...");
                file.delete();
                return;
            }
        } catch (Exception e) {
           e.printStackTrace();
       }
        logger.info("AIS Data save failed...");
    }


    public boolean saveToDb(File file){
        try {

            CSVParser parser;

            parser = CSVParser.parse(file, StandardCharsets.UTF_8, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase());

            for (CSVRecord csvRecord : parser) {
                AisData aisData = new AisData();
                aisData.setMMSI(csvRecord.get("MMSI"));
                aisData.setBaseDateTime(csvRecord.get("BaseDateTime"));
                aisData.setLAT(csvRecord.get("LAT"));
                aisData.setLON(csvRecord.get("LON"));
                aisData.setSOG(csvRecord.get("SOG"));
                aisData.setCOG(csvRecord.get("COG"));
                aisData.setHeading(csvRecord.get("Heading"));
                aisData.setVesselName(csvRecord.get("VesselName"));
                aisData.setIMO(csvRecord.get("IMO"));
                aisData.setCallSign(csvRecord.get("CallSign"));
                aisData.setVesselType(csvRecord.get("VesselType"));
                aisData.setStatus(csvRecord.get("Status"));
                aisData.setLength(csvRecord.get("Length"));
                aisData.setWidth(csvRecord.get("Width"));
                aisData.setDraft(csvRecord.get("Draft"));
                aisData.setCargo(csvRecord.get("Cargo"));
                aisData.setTranscieverClass(csvRecord.get("TranscieverClass"));
                aisDataRepository.save(aisData);
            }
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

}
