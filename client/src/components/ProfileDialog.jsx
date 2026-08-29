import {useAuth} from "../context/AuthContext.js";
import {useState} from "react";
import Modal from "./Modal.jsx";
import * as api from "../services/api.js";
import "./Modal.css";

export default function ProfileDialog({onClose}) {
    const {user} = useAuth();

    // The server asks for the current secret before writing
    const [form, setForm] = useState({username: user.displayName, currentSecret: "", newSecret: ""});
    const [message, setMessage] = useState(null);

    async function handleSubmit(event) {
        event.preventDefault();

        try {
            await api.updateUser(form);
            setMessage("Profile updated");
        } catch (err) {
            setMessage(err.message);
        }
    }

    return (
        <Modal title="Profile management" onClose={onClose}>
            <form onSubmit={handleSubmit}>
                <label className="modal-field">
                    <span>Username</span>
                    <input
                        value={form.username}
                        onChange={(e) => setForm((f) => ({...f, username: e.target.value}))}
                        required
                    />
                </label>

                <label className="modal-field">
                    <span>Current Password</span>
                    <input
                        type="password"
                        autoComplete="current-password"
                        value={form.currentSecret}
                        onChange={(e) => setForm((f) => ({...f, currentSecret: e.target.value}))}
                        required
                    />
                </label>

                <label className="modal-field">
                    <span>New Password</span>
                    <input
                        type="password"
                        autoComplete="new-password"
                        minLength={8}
                        value={form.newSecret}
                        onChange={(e) => setForm((f) => ({...f, newSecret: e.target.value}))}
                        required
                    />
                </label>

                <button type="submit">Save</button>
            </form>

            {message && <p className="modal-error">{message}</p>}
        </Modal>
    );
}