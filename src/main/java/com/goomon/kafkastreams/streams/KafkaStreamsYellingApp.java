package com.goomon.kafkastreams.streams;

import com.goomon.kafkastreams.KafkastreamsApplication;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@SpringBootApplication
public class KafkaStreamsYellingApp {

    public static void main(String[] args) {
        SpringApplication.run(KafkastreamsApplication.class);
    }

    @Bean
    public ApplicationRunner run() {
        return args -> {
            Properties props = new Properties();
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, "yelling_app_id");
            props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");

            StreamsConfig streamsConfig = new StreamsConfig(props);

            Serde<String> stringSerde = Serdes.String();
            StreamsBuilder builder = new StreamsBuilder();

            KStream<String, String> simpleFirstStream = builder.stream("src-topic", Consumed.with(stringSerde, stringSerde));
            KStream<String, String> upperCasedStream = simpleFirstStream.mapValues(s -> s.toUpperCase());
            upperCasedStream.to("out-topic", Produced.with(stringSerde, stringSerde));

            KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamsConfig);
            kafkaStreams.start();
            Thread.sleep(30000);
            kafkaStreams.close();
        };
    }
}
