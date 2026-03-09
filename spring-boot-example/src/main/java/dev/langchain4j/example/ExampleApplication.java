package dev.langchain4j.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExampleApplication {

    public static void main(String[] args) {
        System.setProperty("langchain4j.http.clientBuilderFactory", "dev.langchain4j.http.client.spring.restclient.SpringRestClientBuilderFactory");
        SpringApplication.run(ExampleApplication.class, args);
    }
}
