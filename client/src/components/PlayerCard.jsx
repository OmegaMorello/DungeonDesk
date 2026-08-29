import {portraitFor} from "../assets/portraits";
import "./PlayerCard.css";

export default function PlayerCard({sheet, ownerName, isMine, onOpen}) {
    return (
        <button className={`player-card ${isMine ? "is-mine" : ""}`} onClick={onOpen}>
            {/* The badge names the owner, the outline says which one is the player's */}
            <span className={`player-owner ${isMine ? "is-mine" : ""} ${ownerName ? "" : "is-free"}`}>
                {ownerName ?? "Unassigned"}
            </span>

            <img src={portraitFor(sheet.sheetId)} alt="" className="player-portrait"/>

            <span className="player-name">{sheet.name}</span>
            <span className="player-class">{sheet.characterClass ?? sheet.species}</span>
            <span className="player-hp">HP: {sheet.currentHp}/{sheet.maxHp}</span>
        </button>
    );
}
