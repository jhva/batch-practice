package com.batchapplication.springbatchapplication.part3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;

@Slf4j
public class PersonvalidationRetryProcessor implements ItemProcessor<Person, Person> {

    private final RetryTemplate retryTemplate;

    public PersonvalidationRetryProcessor() {
        this.retryTemplate = new RetryTemplateBuilder()
                .maxAttempts(3) //retry limit
                .retryOn(NotFoundNameException.class)
                .withListener(new SavepersonRetryListener())
                .build();
    }


    @Override
    public Person process(Person item) throws Exception {
        return this.retryTemplate.execute(context -> {

            //retry callBack  3번 호출하고나면 recovery callback 실행
            if (item.isNotEmptyName()) {
                return item;
            }
            throw new NotFoundNameException();
        }, context -> {

            //retry callback
            return item.unknownName();
        });
    }

    public static class SavepersonRetryListener implements RetryListener {

        @Override
        public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
            return true;

            //true가 되야 실행됨
        }

        @Override
        public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            log.info("CLOSE METHOD");
        }

        @Override
        public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            log.info("CLOSE METHOD");


        }
    }
}
