package com.goomon.kafkastreams.streams;

import com.goomon.kafkastreams.model.Purchase;
import com.goomon.kafkastreams.model.PurchasePattern;
import com.goomon.kafkastreams.model.RewardAccumulator;
import com.goomon.kafkastreams.util.serde.StreamsSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@Slf4j
@SpringBootApplication
public class ZMartKafkaStreamsApp {

    private static final String TRANSACTION_TOPIC = "transactions";
    private static final String PATTERN_TOPIC = "patterns";
    private static final String REWARD_TOPIC = "rewards";

    public static void main(String[] args) {
        SpringApplication.run(ZMartKafkaStreamsApp.class);
    }

    @Bean
    public ApplicationRunner zMarketRunner() {
        return args -> {
            StreamsConfig streamsConfig = new StreamsConfig(getProps());

            Serde<Purchase> purchaseSerde = StreamsSerde.PurchaseSerde();
            Serde<PurchasePattern> purchasePatternSerde = StreamsSerde.PurchasePatternSerde();
            Serde<RewardAccumulator> rewardAccumulatorSerde = StreamsSerde.RewardAccumulatorSerde();
            Serde<String> stringSerde = Serdes.String();

            StreamsBuilder streamsBuilder = new StreamsBuilder();
            KStream<String, Purchase> purchaseKStream = streamsBuilder.stream(TRANSACTION_TOPIC, Consumed.with(stringSerde, purchaseSerde));
            KStream<String, PurchasePattern> patternKStream = purchaseKStream.mapValues(p -> PurchasePattern.build(p));
            patternKStream.print(Printed.<String, PurchasePattern>toSysOut().withLabel(PATTERN_TOPIC));
            patternKStream.to(PATTERN_TOPIC, Produced.with(stringSerde, purchasePatternSerde));

            KStream<String, RewardAccumulator> rewardKStream = purchaseKStream.mapValues(p -> RewardAccumulator.build(p));
            rewardKStream.print(Printed.<String, RewardAccumulator>toSysOut().withLabel(REWARD_TOPIC));
            rewardKStream.to(REWARD_TOPIC, Produced.with(stringSerde, rewardAccumulatorSerde));

            KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), streamsConfig);
            log.info("ZMart First Kafka Streams Application Started");
            kafkaStreams.start();

            Thread.sleep(60000);

            log.info("ZMart First Kafka Streams Application Shut down");
            kafkaStreams.close();
        };
    }

    static private Properties getProps() {
        Properties props = new Properties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "zmart-purchases");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "first_zmart_kafka_streams_app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        return props;
    }
}
