package com.marcomoretta.dungeondesk.command;

import com.marcomoretta.dungeondesk.event.ChatEvent;
import com.marcomoretta.dungeondesk.event.GameEventStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommandConsumerThreadTest {

    @Mock
    private GameEventStream gameEventStream;

    private CommandQueue commandQueue;
    private GameState gameState;
    private CommandConsumerThread consumerThread;

    @BeforeEach
    void setup() {
        // Arrange
        commandQueue = new CommandQueue();
        gameState = new GameState();
        consumerThread = new CommandConsumerThread(commandQueue, gameState, gameEventStream);
    }

    @AfterEach
    void tearDown() {
        consumerThread.stopConsumer();
    }

    @Test
    void run() {
        // Arrange
        ChatEvent event = new ChatEvent("DM", null, "Hello", Instant.now());

        // Act
        consumerThread.startConsumer();
        commandQueue.submit(gameState -> event);

        // Assert
        verify(gameEventStream, timeout(1000)).notifyObservers(event);
    }

    @Test
    void run_badCommand() {
        // Arrange
        ChatEvent event = new ChatEvent("DM", null, "After fail", Instant.now());

        // Act
        consumerThread.startConsumer();
        commandQueue.submit(gameState -> {
            throw new IllegalStateException("exception");
        });
        commandQueue.submit(gameState -> event);

        // Assert
        verify(gameEventStream, timeout(1000)).notifyObservers(event); // The commands still run after a failure
    }
}