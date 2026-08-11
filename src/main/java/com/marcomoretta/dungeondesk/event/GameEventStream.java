package com.marcomoretta.dungeondesk.event;

import org.springframework.stereotype.Component;

/**
 * Needed to have an injectable GameEvent Observable
 */
@Component
public class GameEventStream extends Observable<GameEvent>{
}
