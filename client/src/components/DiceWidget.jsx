import {useGame} from "../context/GameContext.js";
import {useState} from "react";
import "./DiceWidget.css";

const DICE = ["D4", "D6", "D8", "D10", "D12", "D20", "D100"]

export default function DiceWidget({isMaster}) {
    const {rollDice} = useGame();
    const [hidden, setHidden] = useState(false);

    return (
        <div className="dice">
            <div className="dice-row">
                {DICE.map((d) => (
                    <button key={d} onClick={() => rollDice(d, hidden)}>{d}</button>
                ))}
            </div>

            {/*Hiding the hidden checkbox for the players (server only allows the Master to use it anyway)*/}
            {isMaster && (
                <label className="dice-hidden">
                    <input type="checkbox" checked={hidden} onChange={(e) => setHidden(e.target.checked)}/>
                    Hidden roll
                </label>
            )}

        </div>
    )
}