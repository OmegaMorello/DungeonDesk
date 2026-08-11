package com.marcomoretta.dungeondesk.event;

import com.marcomoretta.dungeondesk.command.DiceType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObservableTest {

    @Test
    void observableNotifies_observerReceives() {
        // Arrange
        Observable<GameEvent> observable = new Observable<>();
        TestObserver observer = new TestObserver();
        observable.attach(observer);

        ChatEvent event = new ChatEvent("Omega", "Roll you dice!", Instant.now());

        // Act
        observable.notifyObservers(event);

        // Assert
        assertEquals(1, observer.gameEventList.size());
        assertEquals(event, observer.gameEventList.getFirst());
    }

    @Test
    void moreObservers() {
        // Arrange
        Observable<GameEvent> observable = new Observable<>();
        TestObserver observer1 = new TestObserver();
        TestObserver observer2 = new TestObserver();
        observable.attach(observer1);
        observable.attach(observer2);

        DiceRolledEvent event = new DiceRolledEvent("Omega", DiceType.D20,18,false, Instant.now());

        // Act
        observable.notifyObservers(event);

        // Assert
        assertEquals(1, observer1.gameEventList.size());
        assertEquals(1, observer2.gameEventList.size());
        assertEquals(event, observer1.gameEventList.getFirst());
        assertEquals(event, observer2.gameEventList.getFirst());
    }

    @Test
    void moreEventsInvoked() {
        // Arrange
        Observable<GameEvent> observable = new Observable<>();
        TestObserver observer = new TestObserver();
        observable.attach(observer);

        SheetChangedEvent event1 = new SheetChangedEvent("Omega", 1L, 15, Instant.now());
        TokenMovedEvent event2 = new TokenMovedEvent("Omega", 1L,5,10, Instant.now());

        // Act
        observable.notifyObservers(event1);
        observable.notifyObservers(event2);

        // Assert
        assertEquals(2, observer.gameEventList.size());
        assertEquals(event1, observer.gameEventList.getFirst());
        assertEquals(event2, observer.gameEventList.getLast());
    }

    @Test
    void failingObserver() {
        // Arrange
        Observable<GameEvent> observable = new Observable<>();
        TestFailingObserver failingObserver = new TestFailingObserver();
        TestObserver observer = new TestObserver();
        observable.attach(failingObserver);
        observable.attach(observer);

        ChatEvent event = new ChatEvent("Omega", "Anybody there?", Instant.now());

        // Act
        observable.notifyObservers(event);

        // Assert
        assertEquals(1, observer.gameEventList.size());
        assertEquals(event, observer.gameEventList.getFirst());
    }

    @Test
    void detachedObserver_doesNotReceive() {
        // Arrange
        Observable<GameEvent> observable = new Observable<>();
        TestObserver observer = new TestObserver();
        observable.attach(observer);
        observable.detach(observer);

        ChatEvent event = new ChatEvent("Omega", "Anybody there?", Instant.now());

        // Act and Assert
        assertDoesNotThrow(() -> observable.notifyObservers(event));
    }



    private static class TestObserver implements Observer<GameEvent> {
        private final List<GameEvent> gameEventList = new ArrayList<>();

        @Override
        public void onEvent(GameEvent event) {
            gameEventList.add(event);
        }
    }

    private static class TestFailingObserver implements Observer<GameEvent> {
        @Override
        public void onEvent(GameEvent event) {
            throw new RuntimeException("Deaf, cannot listen to event: " + event.toString());
        }
    }

}