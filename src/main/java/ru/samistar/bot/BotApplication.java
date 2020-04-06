package ru.samistar.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

import java.io.IOException;

@SpringBootApplication // анатация(метка) которой помечается главный класс спринга
@EnableScheduling // включает шидулеры(хрень которая вызывает повторение выполнения метода раз в указанное количество времени)
public class BotApplication{


	public static void main(String[] args) throws IOException {
        ApiContextInitializer.init();

        SpringApplication.run(BotApplication.class, args); // старт спринг приложения

    }
}
