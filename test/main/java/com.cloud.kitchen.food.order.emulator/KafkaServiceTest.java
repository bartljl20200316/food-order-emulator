package com.cloud.kitchen.food.order.emulator;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
//@EmbeddedKafka
public class KafkaServiceTest {

    PriorityBlockingQueue<Order> queue = new PriorityBlockingQueue<>();
    Order order1, order2;

    @Before
    public void Setup() {
        order1 = new Order();
        order2 = new Order();

        order1.setName("Banana Split 1");
        order1.setTemp(TempEnum.FROZEN);
        order1.setShelfLife(20);
        order1.setDecayRate(0.63f);
        order1.setOnShelfTime(System.currentTimeMillis());

        order2.setName("Banana Split 2");
        order2.setTemp(TempEnum.FROZEN);
        order2.setShelfLife(20);
        order2.setDecayRate(0.63f);
        order2.setOnShelfTime(System.currentTimeMillis() - 2000L);
    }

    @Test
    public void testSendReceiveOrder() throws InterruptedException {
        queue.add(order1);
        queue.add(order2);
        queue.forEach(order -> System.out.println(order.toString() + "  " + order.getValue()));
    }

}
