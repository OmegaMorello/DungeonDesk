package com.marcomoretta.dungeondesk.command;

import com.marcomoretta.dungeondesk.event.GameEventStream;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Single thread consuming the command queue and publishing the result events
 */
@Slf4j
@Component
public class CommandConsumerThread implements Runnable {
    private final CommandQueue commandQueue;
    private final GameState gameState;
    private final GameEventStream gameEventStream;
    private Thread thread;

    public CommandConsumerThread(CommandQueue commandQueue, GameState gameState, GameEventStream gameEventStream) {
        this.commandQueue = commandQueue;
        this.gameState = gameState;
        this.gameEventStream = gameEventStream;
    }

    /**
     * Starting when the bean is fully built
     */
    @PostConstruct
    public void startConsumer() {
        thread = new Thread(this, "command-consumer");
        thread.start();
    }

    /**
     * Interrupts the thread before destruction
     */
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
            } catch (InterruptedException interruptedException) { // When shut down
                Thread.currentThread().interrupt();
            } catch (Exception exception) { // When a command fails
                log.error("Command failed", exception);
            }
        }
    }
}
