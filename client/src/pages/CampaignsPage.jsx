import {useEffect, useState} from "react";
import {useAuth} from "../context/AuthContext.js";
import {coverFor} from "../assets/covers";
import CampaignsDialog from "../components/CampaignsDialog.jsx";
import * as api from "../services/api.js";
import "./CampaignsPage.css";

export default function CampaignsPage({onStart}) {
    const {user, logout} = useAuth();

    const [campaigns, setCampaigns] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selected, setSelected] = useState(null);
    const [joinCode, setJoinCode] = useState("");
    const [creating, setCreating] = useState(false);
    const [form, setForm] = useState({name: "", description: ""});
    const [managing, setManaging] = useState(null);

    // A previously open session may exist if the app is closed while playing
    const [activeSession, setActiveSession] = useState(null);

    const [error, setError] = useState(null);

    useEffect(() => {
        api.getCampaignList()
            .then(setCampaigns)
            .catch(() => setCampaigns([]))
            .finally(() => setLoading(false));

        api.getActiveSession()
            .then(setActiveSession)
            .catch(() => setActiveSession(null));

    }, []);


    async function handleCreate(e) {
        e.preventDefault();
        setError(null);

        try {
            const created = await api.createCampaign(form);

            setCampaigns((prev) => [...prev, created]);
            setSelected(created.campaignId);
            setCreating(false);
            setForm({name: "", description: ""});
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleDelete(campaignId) {
        await api.deleteCampaign(campaignId);

        setCampaigns((prev) => prev.filter((c) => c.campaignId !== campaignId));
        if (selected === campaignId) setSelected(null);
    }

    async function handleStart() {
        setError(null);

        try {
            await api.createSession(selected, {joinCode});
            onStart();
        } catch (err) {
            setError(err.message);
        }
    }

    // Only one session at a time is allowed. If a session is already open, it must be closed to open a new one
    async function handleCloseSession() {
        await api.closeSession(activeSession.sessionId);
        setActiveSession(null);
    }

    function handleCampaignUpdated(updated) {
        setCampaigns((prev) => prev.map((c) => (c.campaignId === updated.campaignId ? updated : c)));
        setManaging(updated);
    }

    if (loading) return <p className="campaigns-loading">Loading...</p>

    return (
        <div className="campaigns">
            <header className="campaigns-bar">
                <span className="campaigns-user">{user.displayName}</span>
                <button onClick={logout}>Logout</button>
            </header>

            {activeSession && (
                <div className="campaigns-open-session" role="alert">
                    <span>There is an open session on {activeSession.campaignName}</span>
                    <button onClick={handleCloseSession}>Close session</button>
                    <button onClick={onStart}>Continue</button>
                </div>
            )}

            <div className="campaigns-grid">
                <button className="campaign-new" onClick={() => setCreating(true)}>+</button>

                {campaigns.map((campaign) => (
                    <div key={campaign.campaignId} className="campaign-slot">
                        <button className={`campaign-card ${selected === campaign.campaignId ? "is-selected" : ""}`}
                                style={{backgroundImage: `url(${coverFor(campaign.campaignId)})`}}
                                onClick={() => setSelected(campaign.campaignId)}>
                            <span className="campaign-name">{campaign.name}</span>
                        </button>

                        <div className="campaign-actions">
                            <button onClick={() => setManaging(campaign)}>
                                Edit
                            </button>

                            <a href={`/api/v1/campaigns/${campaign.campaignId}/export`} download>
                                Export
                            </a>

                            <button onClick={() => handleDelete(campaign.campaignId)}>
                                Delete
                            </button>
                        </div>
                    </div>
                ))}
            </div>

            {creating && (
                <form className="campaign-form" onSubmit={handleCreate}>
                    <input
                        placeholder="Campaign name"
                        value={form.name}
                        onChange={(e) =>
                            setForm((f) =>
                                ({...f, name: e.target.value}))}
                    />
                    <input
                        placeholder="Description"
                        value={form.description}
                        onChange={(e) =>
                            setForm((f) =>
                                ({...f, description: e.target.value}))}
                    />
                    <button type="submit">Create</button>
                    <button type="button" onClick={() => setCreating(false)}>Cancel</button>
                </form>
            )}

            {managing && (
                <CampaignsDialog
                    campaign={managing}
                    onUpdated={handleCampaignUpdated}
                    onClose={() => setManaging(null)}
                />
            )}

            <div className="campaigns-start">
                <input
                    placeholder="Session code"
                    value={joinCode}
                    onChange={(e) => setJoinCode(e.target.value)}
                />
                <button
                    className="campaigns-start-button"
                    onClick={handleStart}
                    disabled={!selected || !joinCode.trim()}
                >
                    Start
                </button>
            </div>

            {error && <p className="campaigns-error" role="alert">{error}</p>}

        </div>
    )
}