package com.example.my_app.Service;

import com.example.my_app.Config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@EnableAsync
@Service
public class MessageProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    private static final int TOTAL_MESSAGES = 1000000;
    private static final int THREAD_COUNT = 4;


    public String sendMessagesWithThreads() {
        long startTime = System.currentTimeMillis();

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
            List<Future<?>> futures = new ArrayList<>();

            int messagesPerThread = TOTAL_MESSAGES / THREAD_COUNT;
            for (int i = 0; i < THREAD_COUNT; i++) {
                futures.add(executorService.submit(() -> sendMessage(messagesPerThread)));
            }

            // Wait all tasks to complete.
            for (Future<?> future : futures) {
                future.get();
            }

            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("total time for producing message -> " + totalTime + " ms");
        return String.valueOf(totalTime);
    }



    @Async
    public void sendMessage(int count) {
        for (int i = 0; i < count; i++) {
            String message = generateMessage();
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);
            //System.out.println("Sended messages to queue:" + message);
        }
    }

    /*
    generating message
     */
    private String generateMessage() {
        UUID uuid = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();
        String threadName = Thread.currentThread().getName();
        return String.format("\"id\": \"%s\", \"timestamp\": \"%s\", \"thread\": \"%s\"}",
                uuid, timestamp, threadName);
    }


}
