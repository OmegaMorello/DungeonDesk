import {useEffect, useState} from "react";
import {emptySheet, toSheetRequest} from "../services/sheetMapper.js";
import {useGame} from "../context/GameContext.js";
import {useAuth} from "../context/AuthContext.js";
import * as api from "../services/api.js";
import SheetView from "../components/SheetView.jsx";
import "./SheetPage.css";

export default function SheetPage({sheetId, sheetType, onBack}) {
    const {user} = useAuth();
    const {campaignId, reloadSheets, players} = useGame();

    const isNew = sheetId === null;
    const isMaster = user.loginType === "MASTER";

    const [sheet, setSheet] = useState(isNew ? emptySheet(sheetType) : null);
    const [editing, setEditing] = useState(isNew);

    const [error, setError] = useState(null);

    // Getting the sheet info
    useEffect(() => {
        if (isNew) return;
        api.getSheet(sheetId).then(setSheet).catch((e) => setError(e.message));
    }, [sheetId, isNew]);


    if (!sheet) return (
        <div className="sheet">
            <header className="sheet-bar">
                <button onClick={onBack}>Back</button>
            </header>

            {error
                ? <p className="sheet-error" role="alert">{error}</p>
                : <p className="sheet-loading">Loading...</p>}
        </div>
    );

    // A player may edit their own character only
    const canEdit = isMaster || (user.playerId != null && sheet.playerId === user.playerId);

    async function handleSave() {
        setError(null);
        try {
            const request = toSheetRequest(sheet, campaignId);

            if (isNew) {
                sheetType === "CHARACTER"
                    ? await api.createCharacterSheet(request)
                    : await api.createEnemySheet(request);

                await reloadSheets();
                onBack();
                return;
            }

            setSheet(await api.updateSheet(sheetId, request));

            // The summaries carry the owner
            await reloadSheets();
            setEditing(false);
        } catch (e) {
            setError(e.message);
        }
    }

    async function handleDelete() {
        await api.deleteSheet(sheetId);
        await reloadSheets();
        onBack();
    }


    return (
        <div className="sheet">
            <header className="sheet-bar">
                <button onClick={onBack}>Back</button>

                {canEdit && (
                    <button onClick={editing ? handleSave : () => setEditing(true)}>
                        {editing ? "Save" : "Edit"}
                    </button>
                )}

                {isMaster && !isNew && <button onClick={handleDelete}>Delete</button>}
            </header>

            {/* A banner, not a full page: the sheet and the back button stay reachable */}
            {error && <p className="sheet-error" role="alert">{error}</p>}

            <SheetView sheet={sheet} editing={editing} onChange={setSheet} isMaster={isMaster} players={players}/>
        </div>
    )

}