import {useEffect, useState} from "react";
import {useGame} from "../context/GameContext.js";
import {toSheetRequest} from "../services/sheetMapper.js";
import * as api from "../services/api.js";
import "./MapTools.css";

// Rejects empty fields and anything outside the grid limits the server accepts
function isValidSize(value) {
    return Number.isInteger(value) && value >= 1 && value <= 50;
}

export default function MapTools() {
    const {map, setMap, campaignId, sheets, reloadSheets} = useGame();

    // Kept as text while typing, will validate later
    const [grid, setGrid] = useState({gridRows: "8", gridColumns: "12"});

    // The whole library of the master
    const [library, setLibrary] = useState([]);
    const [libraryId, setLibraryId] = useState("");

    const [sheetId, setSheetId] = useState("");
    const [tokenType, setTokenType] = useState("NPC");

    const [error, setError] = useState(null);

    useEffect(() => {
        api.getOwnedSheets().then(setLibrary).catch(() => setLibrary([]));
    }, []);

    // One pawn per creature
    const placed = new Set((map?.tokenList ?? []).map((token) => token.sheetId));

    // A playing character pawn stands for a character sheet, a non-playing one for an enemy
    const creatures = sheets
        .filter((sheet) => tokenType === "PC" ? sheet.sheetType === "CHARACTER" : sheet.sheetType === "ENEMY")
        .filter((sheet) => !placed.has(sheet.sheetId));

    // Enemies of the other campaigns so they can be reused
    const bestiary = library
        .filter((sheet) => sheet.sheetType === "ENEMY")
        .filter((sheet) => !sheets.some((own) => own.sheetId === sheet.sheetId));

    async function handleCreateOrResize(e) {
        e.preventDefault();
        setError(null);

        const gridRows = Number(grid.gridRows);
        const gridColumns = Number(grid.gridColumns);

        // Validated on submit so an empty field stays empty while typing
        if (!isValidSize(gridRows) || !isValidSize(gridColumns)) {
            setError("Rows and columns must be whole numbers between 1 and 50");
            return;
        }

        try {
            await api.createOrResizeMap({campaignId, gridRows, gridColumns});

            // Read it back to avoid reloading
            setMap(await api.getMap());
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleUpload(e) {
        const file = e.target.files[0];
        if (!file) return;

        setError(null);

        try {
            await api.uploadMapBackground(file);

            // The image URL never changes so a timestamp is needed for the browser to know the image file has changed
            setMap({...(await api.getMap()), backgroundVersion: Date.now()});
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleAddToken(e) {
        e.preventDefault();
        setError(null);

        try {
            const token = await api.addToken({
                sheetId: Number(sheetId),
                tokenType,
                posX: 1,
                posY: 1,
            });

            setMap((prev) => ({...prev, tokenList: [...prev.tokenList, token]}));
            setSheetId("");
        } catch (err) {
            setError(err.message);
        }
    }

    // Can copy an enemy sheet from a different campaign and use it on the current one
    async function handleAddFromLibrary(e) {
        e.preventDefault();
        setError(null);

        try {
            const original = library.find((sheet) => sheet.sheetId === Number(libraryId));
            const copy = await api.createEnemySheet(toSheetRequest(original, campaignId));

            await reloadSheets();

            const token = await api.addToken({
                sheetId: copy.sheetId,
                tokenType: "NPC",
                posX: 1,
                posY: 1,
            });

            setMap((prev) => ({...prev, tokenList: [...prev.tokenList, token]}));
            setLibraryId("");
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <div className="map-tools">
            <form onSubmit={handleCreateOrResize}>
                <input
                    type="number" min="1" max="50"
                    value={grid.gridColumns}
                    onChange={(e) =>
                        setGrid((g) =>
                            ({...g, gridColumns: e.target.value}))}
                />
                X
                <input
                    type="number" min="1" max="50"
                    value={grid.gridRows}
                    onChange={(e) =>
                        setGrid((g) =>
                            ({...g, gridRows: e.target.value}))}
                />
                <button type="submit">{map ? "Resize" : "Create map"}</button>
            </form>

            {map && (
                <>
                    <label className="map-tools-upload">
                        Background
                        <input type="file" accept="image/png,image/jpeg" onChange={handleUpload}/>
                    </label>

                    <form className="map-tools-token" onSubmit={handleAddToken}>
                        <div className="map-tools-fields">
                            <select
                                value={tokenType}
                                onChange={(e) => {
                                    setTokenType(e.target.value);
                                    setSheetId("");     // the list below changes
                                }}>

                                <option value="PC">Playing Character</option>
                                <option value="NPC">Non-Playing Character</option>
                            </select>

                            <select
                                value={sheetId}
                                onChange={(e) =>
                                    setSheetId(e.target.value)} required>

                                <option value="">Choose a creature</option>

                                {creatures.map((sheet) => (
                                    <option key={sheet.sheetId} value={sheet.sheetId}>{sheet.name}</option>
                                ))}
                            </select>
                        </div>

                        <button type="submit">Add token</button>
                    </form>

                    {bestiary.length > 0 && (
                        <form className="map-tools-token" onSubmit={handleAddFromLibrary}>
                            <div className="map-tools-fields">
                                <span className="map-tools-label">From the bestiary</span>

                                <select
                                    value={libraryId}
                                    onChange={(e) =>
                                        setLibraryId(e.target.value)} required>

                                    <option value="">Choose an enemy</option>

                                    {bestiary.map((sheet) => (
                                        <option key={sheet.sheetId} value={sheet.sheetId}>{sheet.name}</option>
                                    ))}
                                </select>
                            </div>

                            <button type="submit">Copy and add</button>
                        </form>
                    )}
                </>
            )}

            {error && <p className="map-tools-error">{error}</p>}
        </div>
    );
}