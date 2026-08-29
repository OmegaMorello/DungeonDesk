import {useEffect, useState} from "react";
import MasterAuthForm from "../components/MasterAuthForm";
import PlayerJoinForm from "../components/PlayerJoinForm";
import * as api from "../services/api.js";
import "./AuthPage.css";

export default function AuthPage() {
    // Who is authenticating: "master" or "player".
    const [mode, setMode] = useState("master");

    const [serverUp, setServerUp] = useState(null);

    const isMaster = mode === "master";

    // Getting the server status
    useEffect(() => {
        api.getHealth().then(() => setServerUp(true)).catch(() => setServerUp(false));
    }, []);


    return (
        <div className="auth">
            <div className="auth-card">
                <div className="auth-brand">
                    <div className="auth-logo" aria-hidden="true">
                        🐉
                    </div>
                    <h1 className="auth-title">DungeonDesk</h1>
                    <p className="auth-tagline">Ready your dice</p>
                </div>

                {isMaster ? <MasterAuthForm/> : <PlayerJoinForm/>}

                <p className="auth-switch">
                    <button
                        type="button"
                        className="auth-link"
                        onClick={() => setMode(isMaster ? "player" : "master")}
                    >
                        {isMaster
                            ? "Are you a player? Join a session"
                            : "Are you the Dungeon Master? Login"}
                    </button>
                </p>
            </div>
            {serverUp === false && (
                <p className="auth-error" role="alert">Server not reachable</p>
            )}
        </div>


    );
}
