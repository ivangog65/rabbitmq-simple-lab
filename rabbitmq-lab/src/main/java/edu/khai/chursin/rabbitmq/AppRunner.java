package edu.khai.chursin.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.khai.chursin.rabbitmq.entity.Game;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.Scanner;

@EnableRabbit
@SpringBootApplication
@ComponentScan
@Import(RabbitConfiguration.class)
public class AppRunner implements CommandLineRunner {

    private static final String YOUR_TURN_MESSAGE = "\nYOUR TURN!(Example: \"1 2\" )\n";
    private static final String SEPARATOR = " ";
    private static final String CELL_IS_MARKED_MESSAGE = "This cell is marked. You pass your turn";

    @Autowired
    private AmqpAdmin admin;

    @Autowired
    private AmqpTemplate template;

    @Autowired
    private ObjectMapper mapper;

    private Game game;

    public static void main(String[] args) {
        SpringApplication.run(AppRunner.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        game = new Game();
    }

    public void makeStep() {

        Scanner scanner = new Scanner(System.in);

        game.print();

        if(game.checkDraw()) {
            game.finish();
            sendGame();
            purgeQueues();
            System.exit(0);
        }
        System.out.println(YOUR_TURN_MESSAGE);

        String[] choice;

        String line = scanner.nextLine();

        choice = line.split(SEPARATOR);

        int[] index = {Integer.valueOf(choice[0]), Integer.valueOf(choice[1])};

        if (!game.isCellFree(index)) {
            System.out.println(CELL_IS_MARKED_MESSAGE);
        } else {
            game.getCells()[index[0]][index[1]] = "\t\tO";
        }

        game.print();

        if (game.isFinished("O")) {
            game.finish(true);
            template.convertAndSend("queue1", "finished");
            System.exit(0);
        } else {
            sendGame();
        }
    }

    public void purgeQueues() {
        admin.purgeQueue("queue1", true);
        admin.purgeQueue("queue2", true);
    }

    private void sendGame() {
        try {
            String jsonGame = mapper.writeValueAsString(game);
            template.convertAndSend("queue1", jsonGame);
            System.out.println("Waiting for the opponent...");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "queue2")
    public void processQueue1(String message) throws IOException {
        if (!StringUtils.equals(message, "finished")) {
            this.game = mapper.readValue(message, Game.class);
            makeStep();
        } else {
            game.finish(false);
            purgeQueues();
            System.exit(0);
        }
    }
}