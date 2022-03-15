package com.chung.example.dbwritingservice;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class MyFlowDbWriter {

    Logger logger = LoggerFactory.getLogger(MyFlowDbWriter.class);

    @Autowired
    MyFlowDataRepository myFlowDataRepository;

    @RabbitListener(queues = "${myflow.rabbitmq.queue}")
    public void recievedMessage(String message) {
        ObjectMapper om = new ObjectMapper();
        try {
            MyFlowData myFlowData = om.readValue(message,MyFlowData.class);
            myFlowDataRepository.save(myFlowData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }


}
