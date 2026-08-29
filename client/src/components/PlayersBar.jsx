import {useAuth} from "../context/AuthContext.js";
import {useGame} from "../context/GameContext.js";
import PlayerCard from "./PlayerCard.jsx";
import "./PlayersBar.css";

export default function PlayersBar({characters, onOpenSheet}) {
    const {user} = useAuth();
    const {players} = useGame();

    // Getting the name of the player to be shown on the card
    function nameOf(playerId) {
        return players.find((p) => p.playerId === playerId)?.name ?? null;
    }

    return (
        <div className="players-bar">
            {characters.map((sheet) =>
                <PlayerCard
                    key={sheet.sheetId}
                    sheet={sheet}
                    ownerName={nameOf(sheet.playerId)}
                    isMine={user.playerId != null && sheet.playerId === user.playerId}
                    onOpen={() => onOpenSheet(sheet.sheetId)}
                />
            )}
        </div>
    );
}
