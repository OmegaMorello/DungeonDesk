// ====================================================
// PlayerJoinForm.jsx
// Player join: pick the name from the roster of the running session,
// then type the join code given by the Dungeon Master.
// ====================================================

import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import * as api from "../services/api";

export default function PlayerJoinForm() {
  const { loginPlayer } = useAuth();

  // Roster of the running session.
  // Empty list = no session open.
  const [players, setPlayers] = useState([]);
  const [loadingPlayers, setLoadingPlayers] = useState(true);

  const [form, setForm] = useState({ playerName: "", joinCode: "" });

  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  // --- Load the available names once, on mount --------------------
  useEffect(() => {
    api
      .getSessionPlayers()
      .then((list) => setPlayers(list ?? []))
      // A failure here is treated as "no session available"
      .catch(() => setPlayers([]))
      .finally(() => setLoadingPlayers(false));
  }, []);

  function handleChange(e) {
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      // The backend expects the same credentials shape for both strategies:
      // for a PLAYER, username is the roster name and secret is the join code.
      await loginPlayer({ username: form.playerName, secret: form.joinCode });
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  if (loadingPlayers) {
    return <p className="auth-tagline">Looking for an open session…</p>;
  }

  if (players.length === 0) {
    return (
      <p className="auth-tagline">
        No open session available. Ask the Dungeon Master to start one.
      </p>
    );
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <label className="auth-field">
        <span>Chi sei</span>
        <select
          name="playerName"
          value={form.playerName}
          onChange={handleChange}
          required
        >
          <option value="">Choose your character…</option>
          {players.map((p) => (
            <option key={p.name} value={p.name}>
              {p.name}
            </option>
          ))}
        </select>
      </label>

      <label className="auth-field">
        <span>Session Code</span>
        <input
          name="joinCode"
          type="text"
          autoComplete="off"
          placeholder="ABC123"
          value={form.joinCode}
          onChange={handleChange}
          required
        />
      </label>

      {error && (
        <p className="auth-error" role="alert">
          {error}
        </p>
      )}

      <button className="auth-submit" type="submit" disabled={submitting}>
        {submitting ? "Waiting…" : "Join Session"}
      </button>
    </form>
  );
}
