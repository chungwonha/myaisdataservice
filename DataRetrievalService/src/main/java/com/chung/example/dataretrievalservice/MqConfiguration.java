package com.chung.example.dataretrievalservice;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfiguration {

    @Bean
    Queue fileQueue() {
        return new Queue("fileQueue", false);
    }

    @Bean
    Queue dbQueue() {
        return new Queue("dbQueue", false);
    }

    @Bean
    Queue aisDbQueue() {
        return new Queue("aisdbqueue", false);
    }

    @Bean
    FanoutExchange exchange() {
        return new FanoutExchange("myflow-fanout-exchange");
    }

    @Bean
    TopicExchange topicExchange(){return new TopicExchange("aisData-topic-exchange");}

    @Bean
    Binding fileBinding(Queue fileQueue, FanoutExchange exchange) {
        return BindingBuilder.bind(fileQueue).to(exchange);
    }

    @Bean
    Binding dbBinding(Queue dbQueue, FanoutExchange exchange) {
        return BindingBuilder.bind(dbQueue).to(exchange);
    }

    @Bean
    Binding aisDataBinding(Queue aisDbQueue, TopicExchange topicExchange){
        return BindingBuilder.bind(aisDbQueue).to(topicExchange).with("aisdata.*");
    }

}
