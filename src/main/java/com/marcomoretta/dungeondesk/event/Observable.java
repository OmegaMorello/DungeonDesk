package com.marcomoretta.dungeondesk.event;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Generic observable class
 *
 * @param <E> The event type published to the observers
 */
@Slf4j
public class Observable<E> {
    private final List<Observer<E>> observers = new CopyOnWriteArrayList<>();

    /**
     * Attach an observer to the notification list
     *
     * @param observer The observer to be added
     */
    public void attach(Observer<E> observer) {
        observers.add(observer);
    }

    /**
     * Detach an observer from the notification list
     *
     * @param observer The observer to be removed
     */
    public void detach(Observer<E> observer) {
        observers.remove(observer);
    }

    /**
     * Invocation of the event to be observed.
     * The try-catch instruction is needed to prevent a failing observer to
     * block the notification to the remaining observers in the notification list
     *
     * @param event The event to notify
     */
    public void notifyObservers(E event) {
        for (Observer<E> observer : observers) {
            try {
                observer.onEvent(event);
            } catch (Exception e) {
                log.warn("Observer {} failed handling {}", observer.getClass().getSimpleName(), event, e);
            }
        }
    }
}
