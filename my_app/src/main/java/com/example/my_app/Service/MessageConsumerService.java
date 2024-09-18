package com.example.my_app.Service;


import com.example.my_app.Config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.nio.channels.Channel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Service
@EnableAsync
public class MessageConsumerService {

    private final RabbitTemplate rabbitTemplate;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumerService.class);
    private long totalProcessingTime = 0;

    @Autowired
    public MessageConsumerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    @RabbitListener(queues = {RabbitMQConfig.QUEUE_NAME}, concurrency = "5-10")
    public void receiveMessage(String message) {
        long startTime = System.currentTimeMillis();

        executorService.submit(() -> {
            LOGGER.info("Received message: {}", message);

            // Process the message here

            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;
            synchronized (this) {
                totalProcessingTime += timeTaken;
            }

            LOGGER.info("Time taken to process message: {} ms", timeTaken);
        });
    }
}


