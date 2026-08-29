import {useEffect, useState} from "react";
import {useAuth} from "../context/AuthContext";
import * as api from "../services/api";

export default function PlayerJoinForm() {
    const {loginPlayer} = useAuth();

    // Roster of the running session.
    const [players, setPlayers] = useState([]);
    const [loadingPlayers, setLoadingPlayers] = useState(true);

    const [form, setForm] = useState({playerName: "", joinCode: ""});

    const [error, setError] = useState(null);
    const [submitting, setSubmitting] = useState(false);

    // Load the available names on mount
    useEffect(() => {
        api
            .getSessionPlayers()
            .then((list) => setPlayers(list ?? []))
            // A failure here is treated as "no session available"
            .catch(() => setPlayers([]))
            .finally(() => setLoadingPlayers(false));
    }, []);

    function handleChange(e) {
        setForm((f) => ({...f, [e.target.name]: e.target.value}));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError(null);
        setSubmitting(true);
        try {
            await loginPlayer({username: form.playerName, secret: form.joinCode});
        } catch (err) {
            setError(err.message);
        } finally {
            setSubmitting(false);
        }
    }

    if (loadingPlayers) {
        return <p className="auth-tagline">Looking for an open session…</p>;
    }

    // An empty roster means either no running session or a campaign with no players
    if (players.length === 0) {
        return (
            <p className="auth-tagline">
                No characters to join. Ask the Dungeon Master to start a session
                and to add you to the campaign.
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
                    <option value="">Select your name on the list</option>
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
