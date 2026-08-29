import {useGame} from "../context/GameContext.js";
import {useEffect, useState} from "react";
import * as api from "../services/api.js"
import "./TurnOrderPanel.css";

export default function TurnOrderPanel({isMaster}) {
    const {sheets, turnOrder} = useGame();

    // What the master typed, ready to be set
    const [draft, setDraft] = useState({});

    // A new order arrived
    useEffect(() => {
        setDraft({});
    }, [turnOrder]);

    // filter(Boolean) drops the ids of sheets deleted while the fight is running
    const ordered = turnOrder
        .map((id) => sheets.find((s) => s.sheetId === id))
        .filter(Boolean);

    // Untouched fields show their current position
    function positionOf(sheet, index) {
        return draft[sheet.sheetId] ?? String(index + 1);
    }

    async function apply() {
        const ids = ordered
            .map((sheet, index) => ({sheet, position: Number(positionOf(sheet, index))}))
            .sort((a, b) => a.position - b.position)
            .map((entry) => entry.sheet.sheetId);

        await api.setTurnOrder(ids);
    }

    return (
        <div className="turn-order">
            <h2 className="turn-order-title">Turn order</h2>

            {ordered.length === 0 && <p className="turn-order-empty">No fight running</p>}

            <ol>
                {ordered.map((sheet, index) => (
                    <li key={sheet.sheetId}>
                        {isMaster ? (
                            <input
                                type="number"
                                min="1"
                                value={positionOf(sheet, index)}
                                onChange={(e) =>
                                    setDraft((d) => ({...d, [sheet.sheetId]: e.target.value}))}
                            />
                        ) : (
                            <span className="turn-order-position">{index + 1}</span>
                        )}

                        {sheet.name}
                    </li>
                ))}
            </ol>

            {/* Players read the order. Only the master can change it */}
            {isMaster && (
                <div className="turn-order-actions">
                    <button onClick={() => api.rollInitiative()}>Roll initiative</button>
                    {ordered.length > 0 && <button onClick={apply}>Apply</button>}
                </div>
            )}
        </div>
    )
}
