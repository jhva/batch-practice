package com.batchapplication.springbatchapplication.part3;

import org.springframework.batch.item.ItemProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DuplicateValidationProcessor<T> implements ItemProcessor<T, T> {

    private final Map<String, Object> keyPool = new ConcurrentHashMap<>();
    private final Function<T, String> keyExtracor;
    private final boolean allowDuplicate;

    public DuplicateValidationProcessor(Function<T, String> keyExtracor, boolean allowDuplicate) {
        this.keyExtracor = keyExtracor;
        this.allowDuplicate = allowDuplicate;
    }

    @Override
    public T process(T item) throws Exception {
        if (allowDuplicate) {
            return item;
        }
        String key = keyExtracor.apply(item); //해당 하이템으로부터 추출

        if (keyPool.containsKey(key)) {
            return null;
        }

        keyPool.put(key, key);

        return item;
    }
}
