import {useEffect, useState} from "react";
import {useAuth} from "../context/AuthContext.js";
import {useGame} from "../context/GameContext.js";
import * as api from "../services/api.js";
import "./NotesPanel.css";

export default function NotesPanel({onClose}) {
    const {user} = useAuth();
    const {campaignId, sessionId} = useGame();

    const [notes, setNotes] = useState([]);
    const [draft, setDraft] = useState({text: "", sharedWithPlayers: false});
    const [scope, setScope] = useState("campaign");

    const isMaster = user.loginType === "MASTER";

    useEffect(() => {
        if (scope === "campaign") {
            if (!campaignId) return;
            api.getCampaignNotes(campaignId).then(setNotes).catch(() => setNotes([]));
        } else {
            if (!sessionId) return;
            api.getSessionNotes(sessionId).then(setNotes).catch(() => setNotes([]));
        }
    }, [scope, campaignId, sessionId]);


    // The open tab decides the scope
    const isSessionScope = scope === "session" && sessionId !== null;

    async function handleCreate(e) {
        e.preventDefault();
        if (!draft.text.trim()) return;

        const created = await api.createNote(campaignId, {
            ...draft,
            sessionId: isSessionScope ? sessionId : null,
        });

        setNotes((prev) => [...prev, created]);
        setDraft({text: "", sharedWithPlayers: false});
    }

    // Sets visibility for the players
    async function handleToggleShared(note) {
        const updated = await api.updateNote(note.noteId, {
            text: note.text,
            sharedWithPlayers: !note.sharedWithPlayers,
        });

        setNotes((prev) => prev.map((n) => (n.noteId === updated.noteId ? updated : n)));
    }

    async function handleDelete(noteId) {
        await api.deleteNote(noteId);
        setNotes((prev) => prev.filter((n) => n.noteId !== noteId));
    }

    return (
        <aside className="notes-panel">
            <button className="notes-close" onClick={onClose}>✕</button>
            <h2>Note</h2>

            <nav className="notes-scope">
                <button className={scope === "campaign" ? "is-active" : ""} onClick={() => setScope("campaign")}>
                    Campaign
                </button>
                <button className={scope === "session" ? "is-active" : ""} onClick={() => setScope("session")}>
                    This session
                </button>
            </nav>

            {/* The campaign tab returns the session notes too */}
            <p className="notes-scope-hint">
                {scope === "campaign"
                    ? "Every note of the campaign, tonight's included"
                    : sessionId
                        ? "Only the notes taken during this session"
                        : "No running session: start one to take session notes"}
            </p>

            <ul>
                {notes.map((note) => (
                    <li key={note.noteId}>
                        {note.sessionId && <span className="notes-badge">Session</span>}

                        <p>{note.text}</p>

                        {isMaster && (
                            <div className="notes-actions">
                                <button onClick={() => handleToggleShared(note)}>
                                    {note.sharedWithPlayers ? "Shared" : "Private"}
                                </button>
                                <button onClick={() => handleDelete(note.noteId)}>Delete</button>
                            </div>
                        )}
                    </li>
                ))}
            </ul>

            {isMaster && (
                <form onSubmit={handleCreate}>
                    <textarea
                        value={draft.text}
                        onChange={(e) =>
                            setDraft((d) =>
                                ({...d, text: e.target.value}))}
                        placeholder={isSessionScope ? "New note for this session" : "New note for the campaign"}
                    />
                    <label>
                        <input
                            type="checkbox"
                            checked={draft.sharedWithPlayers}
                            onChange={(e) =>
                                setDraft((d) =>
                                    ({...d, sharedWithPlayers: e.target.checked}))}
                        />
                        Share with players
                    </label>
                    <button type="submit">Add</button>
                </form>
            )}
        </aside>
    );
}