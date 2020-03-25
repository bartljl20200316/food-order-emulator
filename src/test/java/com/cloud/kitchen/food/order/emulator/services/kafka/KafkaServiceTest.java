package com.cloud.kitchen.food.order.emulator.services.kafka;

import com.cloud.kitchen.food.order.emulator.config.kafka.OrderConsumerConfig;
import com.cloud.kitchen.food.order.emulator.config.kafka.OrderProducerConfig;
import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.dto.TempEnum;
import com.cloud.kitchen.food.order.emulator.services.kafka.OrderConsumer;
import com.cloud.kitchen.food.order.emulator.services.kafka.OrderProducer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
//@SpringBootTest
//@EmbeddedKafka
public class KafkaServiceTest {

    @Before
    public void Setup() {

    }

    @Test
    public void testKafkaSendReceiveOrder() throws InterruptedException {

    }

}
