import {useAuth} from "../context/AuthContext.js";
import {useGame} from "../context/GameContext.js";
import * as api from "../services/api.js";
import TokenPawn from "./TokenPawn.jsx";
import "./MapBoard.css";


export default function MapBoard({map}) {
    const {user} = useAuth();
    const {setMap, sheets} = useGame();

    const isMaster = user.loginType === "MASTER";

    // Getting the sheet owner of the token
    function ownerOf(sheetId) {
        return sheets.find((s) => s.sheetId === sheetId)?.playerId ?? null;
    }

    if (!map) return <div className="map map-empty">There is no map</div>

    const style = {
        "--cols": map.gridColumns,
        "--rows": map.gridRows,
        backgroundImage: map.hasBackground
            ? `url(/api/v1/map/background?v=${map.backgroundVersion ?? 0})`
            : "none"
    };

    async function handleDrop(event, posX, posY) {
        event.preventDefault();
        const tokenId = Number(event.dataTransfer.getData("text/plain"));

        // Using prediction to set it before the server confirms the correct position
        setMap((prev) => ({
            ...prev,
            tokenList: prev.tokenList.map((t) =>
                t.tokenId === tokenId ? {...t, posX, posY} : t
            )
        }));

        try {
            await api.moveToken(tokenId, posX, posY);
        } catch {
            // If the token could not be moved, return it to its true location
            api.getMap().then(setMap);
        }
    }


    const cells = [];
    for (let y = 1; y <= map.gridRows; y++)
        for (let x = 1; x <= map.gridColumns; x++)
            cells.push(
                <div
                    key={`${x}-${y}`}
                    className="map-cell"
                    style={{gridColumn: x, gridRow: y}}
                    // Actually allows to drop a token on the cursor position. Default browser behaviour prevents it otherwise
                    onDragOver={(e) => e.preventDefault()}
                    onDrop={(e) => handleDrop(e, x, y)}
                />
            );

    return (
        <div className="map" style={style}>
            {cells}
            {map.tokenList.map((token) => {
                const isMine = user.playerId != null && ownerOf(token.sheetId) === user.playerId;

                return (
                    <TokenPawn
                        key={token.tokenId}
                        token={token}
                        isMine={isMine}
                        canMove={isMaster || isMine}
                        canRemove={isMaster}
                    />
                );
            })}
        </div>
    );
}


















