import {useGame} from "../context/GameContext.js";
import * as api from "../services/api.js";
import {portraitFor} from "../assets/portraits";
import "./TokenPawn.css";

export default function TokenPawn({token, isMine, canMove, canRemove}) {
    const {setMap} = useGame();

    const style = {
        gridColumn: token.posX,
        gridRow: token.posY,
        backgroundImage: `url(${portraitFor(token.sheetId)})`,
    };

    async function handleRemove() {
        if (!canRemove) return;

        await api.deleteToken(token.tokenId);
        setMap((prev) => ({
            ...prev,
            tokenList: prev.tokenList.filter((t) => t.tokenId !== token.tokenId),
        }));
    }

    return (
        <div
            className={`token token-${token.tokenType.toLowerCase()} ${isMine ? "is-mine" : ""}`}
            style={style}
            draggable={canMove}
            onDragStart={(e) => e.dataTransfer.setData("text/plain", String(token.tokenId))}
            onDoubleClick={handleRemove}
            title={canRemove ? `${token.sheetName} — double click to remove` : token.sheetName}
        >
            <span className="token-name">{token.sheetName}</span>
        </div>
    );
}
