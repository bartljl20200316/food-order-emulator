package com.cloud.kitchen.food.order.emulator.services.kafka;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.dto.TempEnum;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.kafka.test.assertj.KafkaConditions.value;

@RunWith(SpringRunner.class)
@EmbeddedKafka
public class KafkaServiceTest {

    private static String TOPIC = "order";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private KafkaMessageListenerContainer<String, Order> container;
    private BlockingQueue<ConsumerRecord<String, Order>> records = new LinkedBlockingQueue<>();
    private KafkaTemplate<String, Order> template;


    @Before
    public void setup() {
        // Consumer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("consumer", "false", this.embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        DefaultKafkaConsumerFactory<String, Order> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new JsonDeserializer<>(Order.class));
        ContainerProperties containerProperties = new ContainerProperties(TOPIC);
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        container.setupMessageListener((MessageListener<String, Order>) records::add);
        container.start();

        ContainerTestUtils.waitForAssignment(container, this.embeddedKafkaBroker.getPartitionsPerTopic());

        // Producer
        Map<String, Object> senderProps = KafkaTestUtils.senderProps(this.embeddedKafkaBroker.getBrokersAsString());
        senderProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        senderProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        ProducerFactory<String, Order> pf = new DefaultKafkaProducerFactory<>(senderProps);
        template = new KafkaTemplate<>(pf);
        template.setDefaultTopic(TOPIC);
    }

    @After
    public void tearDown() {
        container.stop();
    }

    @Test
    public void testKafkaSendReceiveOrder() throws InterruptedException {
        Order order = new Order();
        order.setName("Banana Split");
        order.setTemp(TempEnum.FROZEN);
        order.setShelfLife(20);
        order.setDecayRate(0.5f);

        template.send(TOPIC, order);

        ConsumerRecord<String, Order> received =
                records.poll(1, TimeUnit.SECONDS);

        assertThat(received).has(value(order));
    }

}
