import {useEffect, useState} from "react";
import * as api from "../services/api.js";
import Modal from "./Modal.jsx";

export default function JoinCodeDialog({onClose}) {
    const [session, setSession] = useState(null);
    const [joinCode, setJoinCode] = useState("");
    const [message, setMessage] = useState(null);

    // Where the players have to point their browser, read from the server itself
    const [server, setServer] = useState(null);

    useEffect(() => {
        api.getActiveSession()
            .then((active) => {
                setSession(active);
                setJoinCode(active.joinCode);
            })
            .catch(() => setMessage("No active session"));

    }, []);

    async function handleSubmit(event) {
        event.preventDefault();

        try {
            setSession(await api.updateSession(session.sessionId, {joinCode}));
            setMessage("Join Code updated");
        } catch (err) {
            setMessage(err.message);
        }
    }

    return (
        <Modal title="Join Code" onClose={onClose}>
            {session && (
                <form onSubmit={handleSubmit}>
                    <label className="modal-field">
                        <span>Code to share with the players</span>
                        <input value={joinCode} onChange={(e) => setJoinCode(e.target.value)} required/>
                    </label>

                    <button type="submit">Save</button>
                </form>
            )}

            {message && <p className="modal-error">{message}</p>}
        </Modal>
    );
}