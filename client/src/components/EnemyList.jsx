import {useGame} from "../context/GameContext.js";
import {portraitFor} from "../assets/portraits";
import * as api from "../services/api.js";
import "./EnemyList.css";

export default function EnemyList({enemies, onOpenSheet}) {
    const {reloadSheets, setMap} = useGame();

    // Deleting the sheet takes its pawn away too thanks to the cascade on token.sheet
    async function handleRemove(sheetId) {
        await api.deleteSheet(sheetId);
        await reloadSheets();

        api.getMap().then(setMap).catch(() => {
        });
    }

    if (enemies.length === 0) return null;

    return (
        <div className="enemy-list">
            <h2 className="enemy-title">Enemies</h2>

            <div className="enemy-cards">
                {enemies.map((sheet) => (
                    <div key={sheet.sheetId} className="enemy-card">
                        <button
                            className="enemy-remove"
                            title="Remove this enemy"
                            onClick={() => handleRemove(sheet.sheetId)}
                        >
                            ×
                        </button>

                        <button className="enemy-open" onClick={() => onOpenSheet(sheet.sheetId)}>
                            <img src={portraitFor(sheet.sheetId)} alt="" className="enemy-portrait"/>

                            <span className="enemy-name">{sheet.name}</span>
                            <span className="enemy-type">{sheet.creatureType}</span>
                            <span className="enemy-hp">HP: {sheet.currentHp}/{sheet.maxHp}</span>
                        </button>
                    </div>
                ))}
            </div>
        </div>
    )
}
