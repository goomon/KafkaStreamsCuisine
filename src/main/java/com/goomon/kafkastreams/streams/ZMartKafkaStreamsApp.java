package com.goomon.kafkastreams.streams;

import com.goomon.kafkastreams.clients.producer.MockDataProducer;
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

    private static final String TRANSACTIONS_TOPIC = "transactions";
    private static final String PATTERNS_TOPIC = "patterns";
    private static final String REWARDS_TOPIC = "rewards";
    private static final String PURCHASES_TOPIC = "purchases";

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
            KStream<String, Purchase> purchaseKStream = streamsBuilder.stream(TRANSACTIONS_TOPIC, Consumed.with(stringSerde, purchaseSerde))
                    .mapValues(p -> Purchase.builder(p).maskCreditCard().build());

            KStream<String, PurchasePattern> patternKStream = purchaseKStream.mapValues(purchase -> PurchasePattern.builder(purchase).build());

            patternKStream.print(Printed.<String, PurchasePattern>toSysOut().withLabel(PATTERNS_TOPIC));
            patternKStream.to(PATTERNS_TOPIC, Produced.with(stringSerde, purchasePatternSerde));

            KStream<String, RewardAccumulator> rewardKStream = purchaseKStream.mapValues(p -> RewardAccumulator.builder(p).build());

            rewardKStream.print(Printed.<String, RewardAccumulator>toSysOut().withLabel(REWARDS_TOPIC));
            rewardKStream.to(REWARDS_TOPIC, Produced.with(stringSerde, rewardAccumulatorSerde));

            purchaseKStream.print(Printed.<String, Purchase>toSysOut().withLabel(PURCHASES_TOPIC));
            purchaseKStream.to(PURCHASES_TOPIC, Produced.with(stringSerde, purchaseSerde));

            MockDataProducer.producePurchaseData();

            KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), streamsConfig);
            log.info("ZMart First Kafka Streams Application Started");
            kafkaStreams.start();
            Thread.sleep(60000);
            log.info("ZMart First Kafka Streams Application Shut down");
            kafkaStreams.close();
            MockDataProducer.shutdown();
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
