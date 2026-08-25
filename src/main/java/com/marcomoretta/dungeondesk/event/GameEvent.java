package com.marcomoretta.dungeondesk.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Marker interface for every event published to the observers
 * JsonTypeInfo and JsonSubTypes are needed to build a custom json response based on the type of event:
 * In this case a json response would be like {"type":"TOKEN_MOVED", "senderName":"...", ...}
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChatEvent.class, name = GameEvent.CHAT),
        @JsonSubTypes.Type(value = DiceRolledEvent.class, name = GameEvent.DICE_ROLLED),
        @JsonSubTypes.Type(value = SheetChangedEvent.class, name = GameEvent.SHEET_CHANGED),
        @JsonSubTypes.Type(value = TokenMovedEvent.class, name = GameEvent.TOKEN_MOVED),
        @JsonSubTypes.Type(value = TurnOrderChangedEvent.class, name = GameEvent.TURN_ORDER_CHANGED)
}
)
public interface GameEvent {
    String CHAT = "CHAT";
    String DICE_ROLLED = "DICE_ROLLED";
    String SHEET_CHANGED = "SHEET_CHANGED";
    String TOKEN_MOVED = "TOKEN_MOVED";
    String TURN_ORDER_CHANGED = "TURN_ORDER_CHANGED";
}

