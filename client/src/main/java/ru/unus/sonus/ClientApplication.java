package ru.unus.sonus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.unus.sonus.service.ClientCLI;

@SpringBootApplication
public class ClientApplication {
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ClientApplication.class, args);
        ClientCLI clientCLI = applicationContext.getBean(ClientCLI.class);
        clientCLI.start();
    }
}