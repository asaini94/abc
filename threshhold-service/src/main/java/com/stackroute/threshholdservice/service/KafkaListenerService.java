package com.stackroute.threshholdservice.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stackroute.threshholdservice.model.AlertObject;
import com.stackroute.threshholdservice.model.MetricsThreshold;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KafkaListenerService {



    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC =  "Kafka_Example_Alert";

    private ThresholdServiceImpl thresholdServiceImpl;

    @Autowired
    public KafkaListenerService(ThresholdServiceImpl thresholdServiceImpl, KafkaTemplate kafkaTemplate) {
        this.thresholdServiceImpl = thresholdServiceImpl;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "Kafka_Example_Test_Threshold", groupId = "group_id")
    public void consumeProcessorServiceData(String message) {

        JsonParser jsonParser = new JsonParser();

        JsonObject obj = (JsonObject) jsonParser.parse(message);

        MetricsThreshold metricsThreshold = new MetricsThreshold();

        metricsThreshold.setUserName(obj.get("userName").toString().replace("\"",""));
        metricsThreshold.setServiceName(obj.get("serviceName").toString().replace("\"",""));
        metricsThreshold.setCpu(Float.parseFloat(obj.get("cpu").toString()));
        metricsThreshold.setMem(Float.parseFloat(obj.get("mem").toString()));

        thresholdServiceImpl.saveMetricsThreshold(metricsThreshold);


    }

    @KafkaListener(topics = "Kafka_Example_Test_Thread1", groupId = "group_id2")
    public void consumeDataCollectorServiceData(String message) throws IOException {


        JsonParser jsonParser = new JsonParser();

        JsonObject obj = (JsonObject) jsonParser.parse(message);

        JsonObject metricObj = obj.getAsJsonObject("metrics");

        String userName = obj.get("userName").toString().replace("\"","");
        String serviceName = obj.get("serviceName").toString().replace("\"","");

        AlertObject alertObject = new AlertObject();
        alertObject.setUserName(userName);
        alertObject.setServiceName(serviceName);

        float cpuCurrent = Float.parseFloat(metricObj.get("cpu").toString().replace("\"","").replace("%",""));
        float memCurrent = Float.parseFloat(metricObj.get("mem").toString().replace("\"","").replace("%",""));


        MetricsThreshold currentThresholds = new MetricsThreshold();

        currentThresholds = thresholdServiceImpl.getMetricThresholdObj(serviceName, userName);


        compareCurrentValtoThreshold(cpuCurrent, memCurrent, currentThresholds.getCpu(), currentThresholds.getMem(), alertObject);

    }



    public void compareCurrentValtoThreshold(float cpuCurrent, float memCurrent, float cpuThreshold, float memThreshold, AlertObject alertObject) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        if(cpuCurrent > 1.2 * cpuThreshold){
            alertObject.setAlert("WARNING!! CPU USAGE EXCEEDED THRESHHOLD" + Float.toString(cpuCurrent));
            String alertString = objectMapper.writeValueAsString(alertObject);
            kafkaTemplate.send(TOPIC, alertString);
        }
        if(memCurrent > 1.2 * memThreshold) {
            alertObject.setAlert("WARNING!! MEMORY USAGE EXCEEDED THRESHHOLD" + Float.toString(memCurrent));
            String alertString = objectMapper.writeValueAsString(alertObject);
            kafkaTemplate.send(TOPIC, alertString);
        }



    }
}
