package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.domain.entity.CharacterSheet;
import com.marcomoretta.dungeondesk.domain.entity.EnemySheet;
import com.marcomoretta.dungeondesk.domain.entity.GenericSheet;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.domain.request.SheetRequest;
import com.marcomoretta.dungeondesk.exception.PlayerNotFoundException;
import com.marcomoretta.dungeondesk.exception.SheetNotFoundException;
import com.marcomoretta.dungeondesk.exception.SheetPermissionException;
import com.marcomoretta.dungeondesk.repository.PlayerRepository;
import com.marcomoretta.dungeondesk.repository.SheetRepository;
import com.marcomoretta.dungeondesk.service.AppUserService;
import com.marcomoretta.dungeondesk.service.CampaignService;
import com.marcomoretta.dungeondesk.service.GameSessionService;
import com.marcomoretta.dungeondesk.service.SheetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

/**
 * Serves the Sheet controller
 */
@Service
public class SheetServiceImpl implements SheetService {

    private final SheetRepository sheetRepository;
    private final PlayerRepository playerRepository;
    private final AppUserService appUserService;
    private final CampaignService campaignService;
    private final GameSessionService gameSessionService;

    public SheetServiceImpl(SheetRepository sheetRepository,
                            PlayerRepository playerRepository,
                            AppUserService appUserService,
                            CampaignService campaignService,
                            GameSessionService gameSessionService) {
        this.sheetRepository = sheetRepository;
        this.playerRepository = playerRepository;
        this.appUserService = appUserService;
        this.campaignService = campaignService;
        this.gameSessionService = gameSessionService;
    }

    @Override
    @Transactional
    public GenericSheet createCharacterSheet(SheetRequest request, AuthSession session) {
        CharacterSheet sheet = new CharacterSheet();
        applyCommon(sheet, request, session);
        applyCharacter(sheet, request, session);

        return sheetRepository.save(sheet);
    }

    @Override
    @Transactional
    public GenericSheet createEnemySheet(SheetRequest request, AuthSession session) {
        EnemySheet sheet = new EnemySheet();
        applyCommon(sheet, request, session);
        applyEnemy(sheet, request);

        return sheetRepository.save(sheet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenericSheet> getCampaignSheets(AuthSession session) {
        Long campaignId = session.loginType() == LoginType.MASTER
                ? gameSessionService.getActiveSession()
                .map((gameSession -> gameSession.getCampaign().getCampaignId()))
                .orElse(null)
                : session.campaignId();

        if (campaignId == null) return List.of();

        List<GenericSheet> sheetList = sheetRepository.findByCampaign_CampaignIdOrderByNameAsc(campaignId);

        if (session.loginType() == LoginType.MASTER) return sheetList;

        return sheetList.stream().filter(CharacterSheet.class::isInstance).toList();

    }

    @Override
    @Transactional(readOnly = true)
    public GenericSheet getSheet(Long sheetId, AuthSession session) {
        GenericSheet sheet = findSheet(sheetId);
        checkCanRead(sheet, session);
        return sheet;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenericSheet> getOwnedSheets(AuthSession session) {
        return sheetRepository.findByOwner_UserIdOrderByNameAsc(session.userId());
    }

    @Override
    @Transactional
    public GenericSheet updateSheet(SheetRequest request, AuthSession session) {
        GenericSheet sheet = findSheet(request.sheetId());
        checkCanWrite(sheet, session);

        applyCommon(sheet, request, session);

        // The type is fixed at creation: the concrete class decides which block applies
        if (sheet instanceof CharacterSheet character) applyCharacter(character, request, session);
        if (sheet instanceof EnemySheet enemy) applyEnemy(enemy, request);

        return sheetRepository.save(sheet);
    }

    @Override
    @Transactional
    public void deleteSheet(Long sheetId, AuthSession session) {
        GenericSheet sheet = findSheet(sheetId);
        checkIsOwner(sheet, session);
        sheetRepository.delete(sheet);
    }

    // ----- Helpers -----

    private GenericSheet findSheet(Long sheetId) {
        return sheetRepository.findById(sheetId)
                .orElseThrow(() -> new SheetNotFoundException("Sheet not found: " + sheetId));
    }

    private void checkIsOwner(GenericSheet sheet, AuthSession session) {
        if (!sheet.getOwner().getUserId().equals(session.userId()))
            throw new SheetPermissionException("Only the owner of the sheet can manage it");
    }

    // A master reads their own library, a player only the players sheets in its own campaign
    private void checkCanRead(GenericSheet sheet, AuthSession session) {
        if (session.loginType() == LoginType.MASTER) {
            checkIsOwner(sheet, session);
        } else if (session.loginType() == LoginType.PLAYER && sheet instanceof CharacterSheet characterSheet) {
            checkIsInSameCampaign(characterSheet, session);
        } else throw new SheetPermissionException("You do not have the permission to read this sheet!");
    }

    // Checks if the player is in the same campaign of the requested sheet
    private void checkIsInSameCampaign(CharacterSheet characterSheet, AuthSession session) {
        if (characterSheet.getPlayer() != null
                && !characterSheet.getPlayer().getCampaign().getCampaignId().equals(session.campaignId()))

            throw new SheetPermissionException("You cannot read sheets from other campaigns");
    }

    // A player edits their own sheet during play
    private void checkCanWrite(GenericSheet sheet, AuthSession session) {
        if (session.loginType() == LoginType.MASTER) {
            checkIsOwner(sheet, session);
            return;
        }
        if (!isOwnCharacterSheet(sheet, session))
            throw new SheetPermissionException("You can only edit your own character sheet");
    }

    private boolean isOwnCharacterSheet(GenericSheet sheet, AuthSession session) {
        return sheet instanceof CharacterSheet character
                && character.getPlayer() != null
                && character.getPlayer().getPlayerId().equals(session.playerId());
    }

    private void applyCommon(GenericSheet sheet, SheetRequest request, AuthSession session) {
        if (sheet.getOwner() == null) sheet.setOwner(appUserService.getUser(session.userId()));
        if (sheet.getCampaign() == null) sheet.setCampaign(campaignService.getCampaign(request.campaignId()));

        sheet.setName(request.name());
        sheet.setArmorClass(request.armorClass());
        sheet.setMaxHp(request.maxHp());
        sheet.setCurrentHp(request.currentHp());
        sheet.setSpeed(request.speed());

        sheet.setStrength(request.strength());
        sheet.setDexterity(request.dexterity());
        sheet.setConstitution(request.constitution());
        sheet.setIntelligence(request.intelligence());
        sheet.setWisdom(request.wisdom());
        sheet.setCharisma(request.charisma());

        sheet.setProficiencyBonus(request.proficiencyBonus());

        sheet.setSkillProficiencies(new HashSet<>(request.skillProficiencies()));
        sheet.setSkillExpertise(new HashSet<>(request.skillExpertise()));
        sheet.setSavingThrowProficiencies(new HashSet<>(request.savingThrowProficiencies()));

        sheet.setSpellcastingAbility(request.spellcastingAbility());
        sheet.setSpellSlots(new HashSet<>(request.spellSlots()));
        sheet.setAttacks(new HashSet<>(request.attacks()));

        sheet.setNotes(request.notes());
    }

    private void applyCharacter(CharacterSheet sheet, SheetRequest request, AuthSession session) {
        // Only a master reassigns a sheet: a player editing their own cannot hand it over
        if (session.loginType() == LoginType.MASTER)
            sheet.setPlayer(resolvePlayer(request.playerId()));
        sheet.setLevel(orZero(request.level()));
        sheet.setCharacterClass(request.characterClass());
        sheet.setSpecies(request.species());
        sheet.setExperiencePoints(orZero(request.experiencePoints()));
        sheet.setHitDiceSize(orZero(request.hitDiceSize()));
        sheet.setHitDiceRemaining(orZero(request.hitDiceRemaining()));
        sheet.setDeathSaveSuccesses(orZero(request.deathSaveSuccesses()));
        sheet.setDeathSaveFailures(orZero(request.deathSaveFailures()));
        sheet.setExhaustion(orZero(request.exhaustion()));
        sheet.setInspiration(Boolean.TRUE.equals(request.inspiration()));
    }

    private void applyEnemy(EnemySheet sheet, SheetRequest request) {
        sheet.setChallengeRating(request.challengeRating());
        sheet.setCreatureType(request.creatureType());
        sheet.setExperienceReward(orZero(request.experienceReward()));
    }

    private Player resolvePlayer(Long playerId) {
        if (playerId == null) return null;
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found: " + playerId));
    }

    private int orZero(Integer value) {
        return value == null ? 0 : value;
    }
}
