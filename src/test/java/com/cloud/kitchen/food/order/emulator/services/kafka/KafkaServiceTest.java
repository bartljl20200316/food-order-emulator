package com.cloud.kitchen.food.order.emulator.services.kafka;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
//@SpringBootTest
@EmbeddedKafka
@DirtiesContext
public class KafkaServiceTest {

    @Before
    public void Setup() {

    }

    @Test
    public void testKafkaSendReceiveOrder() throws InterruptedException {

    }

}
