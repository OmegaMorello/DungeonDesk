// ============================================================
//  AuthPage.jsx
//  Shell of the authentication screen.
// ============================================================

import { useState } from "react";
import MasterAuthForm from "../components/MasterAuthForm";
import PlayerJoinForm from "../components/PlayerJoinForm";
import "./AuthPage.css";

export default function AuthPage() {
  // Who is authenticating: "master" or "player".
  const [mode, setMode] = useState("master");

  const isMaster = mode === "master";

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

        {isMaster ? <MasterAuthForm /> : <PlayerJoinForm />}

        <p className="auth-switch">
          <button
            type="button"
            className="auth-link"
            onClick={() => setMode(isMaster ? "player" : "master")}
          >
            {isMaster
              ? "Sei un giocatore? Unisciti a una sessione"
              : "Sei il Dungeon Master? Accedi"}
          </button>
        </p>
      </div>
    </div>
  );
}
