import {useState} from "react";
import * as api from "../services/api.js";
import "./PlayersManager.css";

export default function PlayersManager({campaign, onUpdated}) {
    const [name, setName] = useState("");

    // The player being renamed and the text typed so far
    const [editingId, setEditingId] = useState(null);
    const [draft, setDraft] = useState("");

    const [error, setError] = useState(null);

    async function handleAdd(e) {
        e.preventDefault();
        if (!name.trim()) return;

        setError(null);
        try {
            const updated = await api.addPlayer(campaign.campaignId, name);
            onUpdated(updated);          // the endpoint returns the whole campaign
            setName("");
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleRemove(playerId) {
        setError(null);
        try {
            onUpdated(await api.removePlayer(campaign.campaignId, playerId));
        } catch (err) {
            setError(err.message);
        }
    }

    function startRename(player) {
        setEditingId(player.playerId);
        setDraft(player.name);
        setError(null);
    }

    // Renaming keeps the id so the sheets assigned to the player stay assigned
    async function handleRename(e) {
        e.preventDefault();
        if (!draft.trim()) return;

        setError(null);
        try {
            onUpdated(await api.renamePlayer(campaign.campaignId, editingId, draft));
            setEditingId(null);
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <div className="players-manager">
            <h3>{campaign.name} players</h3>

            <ul>
                {campaign.players.map((player) => (
                    <li key={player.playerId}>
                        {editingId === player.playerId ? (
                            <form className="players-rename" onSubmit={handleRename}>
                                <input
                                    value={draft}
                                    onChange={(e) => setDraft(e.target.value)}
                                    autoFocus
                                />

                                <button type="submit" title="Save">✓</button>
                                <button type="button" title="Cancel"
                                        onClick={() => setEditingId(null)}>↩
                                </button>
                            </form>
                        ) : (
                            <>
                                {player.name}

                                <span className="players-actions">
                                    <button title="Rename" onClick={() => startRename(player)}>✎</button>
                                    <button className="players-remove" title="Remove"
                                            onClick={() => handleRemove(player.playerId)}>×
                                    </button>
                                </span>
                            </>
                        )}
                    </li>
                ))}
            </ul>

            <form onSubmit={handleAdd}>
                <input
                    value={name}
                    onChange={(e) =>
                        setName(e.target.value)} placeholder="Player name"
                />

                <button type="submit">Add</button>
            </form>

            {error && <p className="players-error">{error}</p>}
        </div>
    );
}
