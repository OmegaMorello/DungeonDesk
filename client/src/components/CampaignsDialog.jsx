import * as api from "../services/api.js";
import "./Modal.css";
import Modal from "./Modal.jsx";
import {useState} from "react";
import PlayersManager from "./PlayersManager.jsx";

export default function CampaignsDialog({campaign, onUpdated, onClose}) {
    const [form, setForm] = useState({
        name: campaign.name,
        description: campaign.description ?? "",
    });

    const [error, setError] = useState(null);

    async function handleSave(event) {
        event.preventDefault();
        setError(null);

        try {
            onUpdated(await api.updateCampaign(campaign.campaignId, form));
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <Modal title={`Campaign ${campaign.name}`} onClose={onClose}>
            <form onSubmit={handleSave}>
                <label className="modal-field">
                    <span>Nome</span>
                    <input
                        value={form.name}
                        onChange={(e) =>
                            setForm((f) =>
                                ({...f, name: e.target.value}))}
                        required
                    />
                </label>

                <label className="modal-field">
                    <span>Description</span>
                    <textarea
                        value={form.description}
                        onChange={(e) =>
                            setForm((f) =>
                                ({...f, description: e.target.value}))}
                    />
                </label>

                <button type="submit">Save</button>
            </form>

            <PlayersManager campaign={campaign} onUpdated={onUpdated}/>

            {error && <p className="modal-error">{error}</p>}
        </Modal>
    );
}