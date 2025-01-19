package org.example.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {

    private final static Logger logger = LoggerFactory.getLogger(ProducerDemo.class);
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
        producer.send(producerRecord);

        // flush and close producer
        producer.flush(); // sync sending
        producer.close();
    }
}
