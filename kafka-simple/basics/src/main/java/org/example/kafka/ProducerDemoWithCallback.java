package org.example.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {

    private final static Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallback.class);
    public static void main(String[] args) {
        logger.info("Starting producer demo");
        /// Dependencies
        ///      kafka-client
        ///      slf4j
        ///      slf4j-simple
        // create produce properties
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, " 172.23.117.89:9092");
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // create record
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>("demo_topic", "Hello World");

        // send data  --> async
        for (int i = 0; i<10; i++) {
            producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // execute every time a record is successfully sent or an exception is thrown
                    if (e == null) {
                        logger.info("Received new Metadata"+"\n" +
                                "Topic: " + recordMetadata.topic() + "\n" +
                                "Partiotion: " + recordMetadata.partition() + "\n" +
                                "Offset: " + recordMetadata.offset() + "\n" +
                                "Timestamp: " + recordMetadata.timestamp());
                    } else
                        logger.error("Error in producer", e);
                }
            });
        }

        /// by default producer send msg between partitions StickyPartitioner
        // and messages placed in same partition
        /// when we sleep between sending -> messages placed in different partitions
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // flush and close producer
        producer.flush(); // sync sending
        producer.close();
    }
}
