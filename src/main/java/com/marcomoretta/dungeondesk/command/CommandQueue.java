package com.marcomoretta.dungeondesk.command;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Shared queue of commands between clients' threads
 */
@Component
public class CommandQueue {
    private final Deque<Command> commands = new ArrayDeque<>();

    /**
     * Submits a command and notifies consumers
     *
     * @param command The command to be consumed
     */
    public synchronized void submit(Command command) {
        commands.addLast(command);
        notifyAll(); // Keeping notifyAll() even if there is a single consumer
    }

    /**
     * Consumes the oldest command on the list
     *
     * @return The command to be executed
     * @throws InterruptedException When the consumer is shut down
     */
    public synchronized Command consume() throws InterruptedException {
        while (commands.isEmpty()) wait();
        return commands.pollFirst();
    }
}
