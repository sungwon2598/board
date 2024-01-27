package ict.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BoardStarter {
    public static void main(String[] args) {
        SpringApplication.run(BoardStarter.class);
    }
}
