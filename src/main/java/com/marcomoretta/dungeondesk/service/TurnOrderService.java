package com.marcomoretta.dungeondesk.service;

import com.marcomoretta.dungeondesk.auth.AuthSession;

import java.util.List;

/**
 * Interface for the turn order service
 */
public interface TurnOrderService {

    /**
     * Rolls the initiative dice for all the creatures (sheets) in the session
     *
     * @param authSession The actual session
     * @return A list containing the sheets ids turn order
     */
    List<Long> rollInitiative(AuthSession authSession);

    /**
     * Allows to set a custom initiative order
     *
     * @param sheetIds The list of sheet ids already ordered by turn
     * @param authSession The actual session
     * @return A list containing the sheets ids turn order
     */
    List<Long> setOrder(List<Long> sheetIds, AuthSession authSession);

    /**
     * Gets the turn order previously set
     *
     * @return A list containing the sheets ids turn order
     */
    List<Long> getOrder();
}
