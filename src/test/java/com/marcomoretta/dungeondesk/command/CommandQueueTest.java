package com.marcomoretta.dungeondesk.command;

import com.marcomoretta.dungeondesk.event.ChatEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CommandQueueTest {

    private CommandQueue commandQueue;

    @BeforeEach
    void setup() {
        // Arrange
        commandQueue = new CommandQueue();
    }

    @Test
    void consume() throws InterruptedException {
        // Arrange
        Command first = gameState -> new ChatEvent("DM", null, "first", Instant.now());
        Command second = gameState -> new ChatEvent("DM", null, "second", Instant.now());

        // Act
        commandQueue.submit(first);
        commandQueue.submit(second);

        // Assert
        assertEquals(first, commandQueue.consume());
        assertEquals(second, commandQueue.consume());
    }

    @Test
    void consume_waitUntilSomethingArrives() throws InterruptedException {
        // Arrange
        Command command = gameState -> new ChatEvent("DM", null, "new event", Instant.now());
        AtomicReference<Command> consumed = new AtomicReference<>();

        Thread consumer = new Thread(() -> {
            try {
                consumed.set(commandQueue.consume());
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        });

        // Act
        consumer.start();
        Thread.sleep(50);                 // Let the consumer reach the wait
        assertNull(consumed.get());             // Verify that it is still locked

        commandQueue.submit(command);
        consumer.join(1000);

        // Assert
        assertEquals(command, consumed.get());
    }

    @Test
    void submit_deliverEveryCommandUnderConcurrency() throws InterruptedException {
        // Arrange
        int producers = 10;
        int commandsPerProducer = 100;
        CountDownLatch ready = new CountDownLatch(producers);

        for (int p = 0; p < producers; p++) {
            new Thread(() -> {
                for (int i = 0; i < commandsPerProducer; i++)
                    commandQueue.submit(gameState -> null);
                ready.countDown();
            }).start();
        }

        // Act
        assertTrue(ready.await(5, TimeUnit.SECONDS));

        // Assert
        for (int i = 0; i < producers * commandsPerProducer; i++)
            assertNotNull(commandQueue.consume());
    }
}