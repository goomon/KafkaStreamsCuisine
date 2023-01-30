package com.goomon.kafkastreams.clients.producer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.goomon.kafkastreams.model.Purchase;
import com.goomon.kafkastreams.util.datagen.DataGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class MockDataProducer {

    private static Producer<String, String> producer;
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private static ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static Callback callback;
    private static final String TRANSACTIONS_TOPIC = "transactions";
    private static volatile boolean keepRunning = true;
    private static volatile boolean producingIQData = false;


    public static void producePurchaseData() {
        producePurchaseData(DataGenerator.DEFAULT_NUM_PURCHASES, DataGenerator.NUM_ITERATIONS, DataGenerator.NUMBER_UNIQUE_CUSTOMERS);
    }

    public static void producePurchaseData(int numberPurchases, int numberIterations, int numberCustomers) {
        Runnable generateTask = () -> {
            init();
            int counter = 0;
            while (counter++ < numberIterations  && keepRunning) {
                List<Purchase> purchases = DataGenerator.generatePurchases(numberPurchases, numberCustomers);
                List<String> jsonValues = convertToJson(purchases);
                for (String value : jsonValues) {
                    ProducerRecord<String, String> record = new ProducerRecord<>(TRANSACTIONS_TOPIC, null, value);
                    producer.send(record, callback);
                }
                log.info("Record batch sent");
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            log.info("Done generating purchase data");
        };
        executorService.submit(generateTask);
    }

    public static void shutdown() {
        log.info("Shutting down data generation");
        keepRunning = false;

        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
        if (producer != null) {
            producer.close();
            producer = null;
        }

    }

    private static void init() {
        if (producer == null) {
            log.info("Initializing the producer");
            Properties properties = new Properties();
            properties.put("bootstrap.servers", "localhost:19092");
            properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("acks", "1");
            properties.put("retries", "3");

            producer = new KafkaProducer<>(properties);

            callback = (metadata, exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                }
            };
            log.info("Producer initialized");
        }
    }

    private static <T> List<String> convertToJson(List<T> generatedDataItems) {
        List<String> jsonList = new ArrayList<>();
        for (T generatedData : generatedDataItems) {
            jsonList.add(convertToJson(generatedData));
        }
        return jsonList;
    }

    private static <T> String convertToJson(T generatedDataItem) {
        return gson.toJson(generatedDataItem);
    }
}
