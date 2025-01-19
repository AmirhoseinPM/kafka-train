package org.example.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemo {

    private final static Logger logger = LoggerFactory.getLogger(ConsumerDemo.class);
    public static void main(String[] args) {
        logger.info("Starting consumer demo");
        /// Dependencies
        ///      kafka-client
        ///      slf4j
        ///      slf4j-simple

        // create consumer properties
        String bootstrapServer = "127.0.0.1:9092";
        String groupId = "my-second-app";
        String topic = "demo_topic";

        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        /// none: if no previous offset found then do not even start,
        /// earliest: read from very beginning of topic
        /// latest: read from latest and after it

        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);


        // subscribe topics for polling
        consumer.subscribe(Collections.singletonList(topic));
//        consumer.subscribe(Arrays.asList(topic, "topic2", "topic3"));

        // read data from subscribed topics
        while(true) {
            logger.info("Waiting for messages| POLLING");
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(1000)); // if don't receive any record after 1000 ms then go to next line

            records.forEach(record -> {
                logger.info("Key: {}, Value: {}", record.key(), record.value());
                logger.info("Partition: {}, Offset: {}", record.partition(), record.offset());
            });
        }
        // Because consumer do not shutdowns
        // if run program twice, it starts from latest offset of first run

    }
}
