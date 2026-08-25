package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.event.GameEventStream;
import com.marcomoretta.dungeondesk.event.TurnOrderChangedEvent;
import com.marcomoretta.dungeondesk.service.TurnOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * Class that exposes REST APIs for the initiative turn order
 */
@RestController
@RequestMapping("/api/v1/turn-order")
public class TurnOrderController {
    private final TurnOrderService turnOrderService;
    private final GameEventStream gameEventStream;

    public TurnOrderController(TurnOrderService turnOrderService, GameEventStream gameEventStream) {
        this.turnOrderService = turnOrderService;
        this.gameEventStream = gameEventStream;
    }


    /**
     * Requests the current order (if any)
     *
     * @param authSession The actual session
     * @return The turn-ordered list of sheet ids [200 - OK]
     */
    @GetMapping
    public ResponseEntity<List<Long>> getOrder(
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        return ResponseEntity.ok(turnOrderService.getOrder());
    }


    /**
     * Requests a new initiative roll
     *
     * @param authSession The actual session
     * @return The turn-ordered list of sheet ids [200 - OK]
     */
    @PostMapping("/roll")
    public ResponseEntity<List<Long>> rollInitiative(
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();
        List<Long> order = turnOrderService.rollInitiative(authSession);
        notifyInitiative(order, authSession);

        return ResponseEntity.ok(order);
    }


    /**
     * Request to set a turn order decided by the DM
     *
     * @param sheetIds    The custom turn-ordered list of sheet ids
     * @param authSession The actual session
     * @return The turn-ordered list of sheet ids [200 - OK]
     */
    @PutMapping
    public ResponseEntity<List<Long>> setOrder(
            @RequestBody List<Long> sheetIds,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();
        List<Long> order = turnOrderService.setOrder(sheetIds, authSession);
        notifyInitiative(order, authSession);

        return ResponseEntity.ok(order);
    }


    /**
     * Publishes the new turn order to every client
     *
     * @param order The turn-ordered list of sheet ids
     * @param authSession The actual session
     */
    private void notifyInitiative(List<Long> order, AuthSession authSession) {
        gameEventStream.notifyObservers(
                new TurnOrderChangedEvent(
                        authSession.displayName(),
                        order,
                        Instant.now())
        );
    }
}
