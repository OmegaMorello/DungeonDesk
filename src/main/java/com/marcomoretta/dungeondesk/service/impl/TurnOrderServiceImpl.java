package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.command.GameState;
import com.marcomoretta.dungeondesk.domain.entity.GenericSheet;
import com.marcomoretta.dungeondesk.service.SheetService;
import com.marcomoretta.dungeondesk.service.TurnOrderService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Serves the turn order controller
 */
@Service
public class TurnOrderServiceImpl implements TurnOrderService {

    private final SheetService sheetService;
    private final GameState gameState;
    private final Random random = new Random();

    public TurnOrderServiceImpl(SheetService sheetService, GameState gameState) {
        this.sheetService = sheetService;
        this.gameState = gameState;
    }

    @Override
    public List<Long> rollInitiative(AuthSession authSession) {
        List<GenericSheet> sheets = sheetService.getCampaignSheets(authSession);
        Map<Long, Integer> rolls = new HashMap<>();

        sheets.forEach(sheet -> rolls.put(sheet.getSheetId(), sheetRoll(sheet)));

        List<Long> order = sheets.stream()
                .sorted(Comparator.comparing((GenericSheet s) -> rolls.get(s.getSheetId())).reversed())
                .map(GenericSheet::getSheetId)
                .toList();

        gameState.setTurnOrder(order);

        return order;
    }

    @Override
    public List<Long> setOrder(List<Long> sheetIds, AuthSession authSession) {

        gameState.setTurnOrder(sheetIds);
        return sheetIds;
    }

    @Override
    public List<Long> getOrder() {
        return gameState.turnOrder();
    }

    // D&D initiative roll, always a D20 (1-20) + the initiative bonus (can be negative)
    private int sheetRoll(GenericSheet sheet) {
        return random.nextInt(20) + sheet.getInitiative() + 1;
    }
}
