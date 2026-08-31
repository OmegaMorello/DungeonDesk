import SheetField from "./SheetField.jsx";
import "./SheetView.css";

const ABILITIES = ["strength", "dexterity", "constitution", "intelligence", "wisdom", "charisma"];
const ABILITY_LABELS = {
    strength: "Strength",
    dexterity: "Dexterity",
    constitution: "Constitution",
    intelligence: "Intelligence",
    wisdom: "Wisdom",
    charisma: "Charisma"
};

export default function SheetView({sheet, editing, onChange, isMaster, players}) {

    const set = editing
        ? (field) => (value) => onChange({...sheet, [field]: value})
        : () => undefined;

    const isCharacter = sheet.sheetType === "CHARACTER";

    // A sheet still being created has no id and no server computed values
    const isSaved = Boolean(sheet.sheetId);


    return (
        <>
            <section className="sheet-section">
                <h2>Character</h2>
                <div className="sheet-grid">
                    <SheetField label="Name" value={sheet.name} onChange={set("name")}/>

                    {isCharacter ? (
                        <>
                            <SheetField label="Class" value={sheet.characterClass} onChange={set("characterClass")}/>
                            <SheetField label="Species" value={sheet.species} onChange={set("species")}/>
                            <SheetField label="Level" type="number" value={sheet.level} onChange={set("level")}/>
                            <SheetField label="EXP points" type="number" value={sheet.experiencePoints}
                                        onChange={set("experiencePoints")}/>

                            {/*The master can change the player the sheet belongs to*/}
                            {isMaster && (
                                <label className="sheet-field">
                                    <span>Player</span>
                                    <select
                                        value={sheet.playerId ?? ""}
                                        disabled={!editing}
                                        onChange={(e) => set("playerId")(e.target.value ? Number(e.target.value) : null)}
                                    >
                                        <option value="">Not assigned</option>
                                        {players.map((player) => (
                                            <option key={player.playerId} value={player.playerId}>{player.name}</option>
                                        ))}
                                    </select>
                                </label>
                            )}
                        </>
                    ) : (
                        <>
                            <SheetField label="Creature type" value={sheet.creatureType}
                                        onChange={set("creatureType")}/>
                            <SheetField label="Challenge rating" value={sheet.challengeRating}
                                        onChange={set("challengeRating")}/>
                            <SheetField label="EXP reward" type="number" value={sheet.experienceReward}
                                        onChange={set("experienceReward")}/>
                        </>
                    )}


                </div>
            </section>

            <section className="sheet-section">
                <h2>Stats</h2>
                <div className="sheet-grid">
                    <SheetField label="Armor class" type="number" value={sheet.armorClass}
                                onChange={set("armorClass")}/>
                    <SheetField label="Current hp" type="number" value={sheet.currentHp} onChange={set("currentHp")}/>
                    <SheetField label="Max hp" type="number" value={sheet.maxHp} onChange={set("maxHp")}/>
                    <SheetField label="Speed" type="number" value={sheet.speed} onChange={set("speed")}/>
                    <SheetField label="Proficiency bonus" type="number" value={sheet.proficiencyBonus}
                                onChange={set("proficiencyBonus")}/>

                    <SheetField label="Initiative" value={sheet.initiative} derived/>
                    <SheetField label="Passive perception" value={sheet.passivePerception} derived/>
                </div>
            </section>

            <section className="sheet-section">
                <h2>Abilities</h2>
                <div className="sheet-grid">
                    {ABILITIES.map((ability) => (
                        <SheetField
                            key={ability}
                            label={ABILITY_LABELS[ability]}
                            type="number"
                            value={sheet[ability]}
                            onChange={set(ability)}
                        />
                    ))}
                </div>
            </section>

            {/* Derived values and attacks come from the server */}
            {isSaved ? (
                <>
                    <section className="sheet-section">
                        <h2>Modifiers</h2>
                        <div className="sheet-grid">
                            {Object.entries(sheet.abilityModifiers ?? {}).map(([ability, value]) => (
                                <SheetField key={ability} label={ability} value={formatSigned(value)} derived/>
                            ))}
                        </div>
                    </section>

                    <section className="sheet-section">
                        <h2>Saving throws</h2>
                        <div className="sheet-grid">
                            {Object.entries(sheet.savingThrows ?? {}).map(([ability, value]) => (
                                <SheetField key={ability} label={ability} value={formatSigned(value)} derived/>
                            ))}
                        </div>
                    </section>

                    <section className="sheet-section">
                        <h2>Skills</h2>
                        <div className="sheet-grid">
                            {Object.entries(sheet.skillModifiers ?? {}).map(([skill, value]) => (
                                <SheetField key={skill} label={skill} value={formatSigned(value)} derived/>
                            ))}
                        </div>
                    </section>

                    <section className="sheet-section">
                        <h2>Attacks</h2>
                        <ul className="sheet-list">
                            {(sheet.attacks ?? []).map((attack, index) => (
                                <li key={index}>
                                    {attack.name} — {formatSigned(attack.attackBonus)} · {attack.damageDie} {formatSigned(attack.damageModifier)} {attack.damageType}
                                </li>
                            ))}
                        </ul>
                    </section>
                </>
            ) : (
                <p className="sheet-hint">
                    Modifiers, saving throws and skills will appear once the sheet is saved.
                </p>
            )}

            <section className="sheet-section">
                <h2>Notes</h2>
                <label className="sheet-field">
                    <textarea
                        value={sheet.notes ?? ""}
                        readOnly={!editing}
                        onChange={(e) => onChange({...sheet, notes: e.target.value})}
                    />
                </label>
            </section>
        </>
    );
}

function formatSigned(value) {
    if (value === null || value === undefined) return "";
    return value >= 0 ? `+${value}` : String(value);
}