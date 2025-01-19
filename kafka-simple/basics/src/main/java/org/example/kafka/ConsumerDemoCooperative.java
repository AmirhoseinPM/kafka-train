package org.example.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemoCooperative {

    private final static Logger logger = LoggerFactory.getLogger(ConsumerDemoCooperative.class);
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
        props.setProperty(
                ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                CooperativeStickyAssignor.class.getName()); // when new consumer add to group -> do not stop consuming and revoking all partitions
//        props.setProperty(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "");  // static consumer -> if it closed and up again before session timout -> assigned past partitions


        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // get reference to current thread
        final Thread mainThread = Thread.currentThread();
        // add the shutdown hook
        // below thread execute after program shutting down
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("Shutting down consumer demo");
                consumer.wakeup(); // next time that call consumer.poll() -> throw Exception and leave from while loop

                // join the main thread to allow the execution of the code in the main thread
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        try {
            // subscribe topics for polling
            consumer.subscribe(Collections.singletonList(topic));
//        consumer.subscribe(Arrays.asList(topic, "topic2", "topic3"));

            // read data from subscribed topics
            while (true) {
                logger.info("Waiting for messages| POLLING");
                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofMillis(1000)); // if don't receive any record after 1000 ms then go to next line

                records.forEach(record -> {
                    logger.info("Key: {}, Value: {}", record.key(), record.value());
                    logger.info("Partition: {}, Offset: {}", record.partition(), record.offset());
                });
            }
        } catch (WakeupException e) {
            logger.info("Wake up exception in consumer demo");
            // we ignore this as an expected exception when closing consumer
        } catch (Exception e) {
            logger.error("Unexpected exception in consumer demo", e);
        } finally {
            consumer.close();  // this will also commit the offset if need be
            logger.info("Closed consumer demo gracefully");
        }


        ///  if multiple consumer run with same groupId
        ///  every one assigned and consume from different partition of topic to load balancing
        /// and use offset of that partition reading in group consumer
        /// if one of consumer in the group closed ->
        /// then other consumers rebalanced to consume messages from assigned partition


        ///  moving partitions between consumers is called reBalancing
        /// reAssignment of partitions happen when a consumer leaves or join a group
        /// or ner partition added to topic



    }

}
