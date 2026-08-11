package com.marcomoretta.dungeondesk.command;

import com.marcomoretta.dungeondesk.event.GameEventStream;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommandConsumerThread implements Runnable{
    private final CommandQueue commandQueue;
    private final GameState gameState;
    private final GameEventStream gameEventStream;
    private Thread thread;

    public CommandConsumerThread(CommandQueue commandQueue, GameState gameState, GameEventStream gameEventStream) {
        this.commandQueue = commandQueue;
        this.gameState = gameState;
        this.gameEventStream = gameEventStream;
    }

    @PostConstruct
    public void startConsumer() {
        thread = new Thread(this, "command-consumer");
        thread.start();
    }

    @PreDestroy
    public void stopConsumer() {
        thread.interrupt();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Command command = commandQueue.consume();
                gameEventStream.notifyObservers(command.execute(gameState));
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            } catch (Exception exception) {
                log.error("Command failed", exception);
            }
        }
    }
}
