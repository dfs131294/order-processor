package com.diego.orders.processor.application.util;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


@Slf4j
public class BatchProcessor {

    public static <T> void run(List<T> list, int batchSize, Consumer<List<T>> consumer) {
        AtomicInteger count = new AtomicInteger(1);
        log.info("[BatchProcessor] Initiating batch processing... batch size: {}, total records: {}",
                batchSize, list.size());
        System.out.println(" ");
        for (List<T> batch : partition(list, batchSize)) {
            log.info("[OrderProcessUseCase] Starting chunk: {}, number of elements: {}", count, batch.size());
            consumer.accept(batch);

            log.info("[OrderProcessUseCase] Finishing chunk: {}, number of elements: {}", count, batch.size());
            count.getAndIncrement();
            System.out.println(" ");
        }
    }

    private static <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }

        return result;
    }
}
