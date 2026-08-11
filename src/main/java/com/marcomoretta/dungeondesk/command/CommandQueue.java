package com.marcomoretta.dungeondesk.command;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

@Component
public class CommandQueue {
    private final Deque<Command> commands = new ArrayDeque<>();

    public synchronized void submit(Command command) {
        commands.addLast(command);
        notifyAll(); // Now there is only 1 consumer thread; keeping notifyAll() for future implementations.
    }

    public synchronized Command consume() throws InterruptedException {
        while (commands.isEmpty()) wait();
        return commands.pollFirst();
    }
}
