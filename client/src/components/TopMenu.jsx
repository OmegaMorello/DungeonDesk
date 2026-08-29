import {useAuth} from "../context/AuthContext.js";
import * as api from "../services/api.js";
import "./TopMenu.css";

export default function TopMenu({
                                    onNewSheet,
                                    onNotes,
                                    onSessionClosed,
                                    onProfile,
                                    onJoinCode,
                                    onEditCampaign,
                                    onLeave
                                }) {

    const {user, logout} = useAuth();

    const isMaster = user.loginType === "MASTER";

    async function handleCloseSession() {
        const session = await api.getActiveSession();
        await api.closeSession(session.sessionId);
        onSessionClosed();
    }

    return (
        <nav className="top-menu">
            {isMaster && (
                <>
                    <button onClick={onLeave}>Campaigns</button>
                    <button onClick={onEditCampaign}>Edit campaign</button>
                    <button onClick={() => onNewSheet("CHARACTER")}>New character</button>
                    <button onClick={() => onNewSheet("ENEMY")}>New enemy</button>
                    <button onClick={onNotes}>Notes</button>
                    <button onClick={onJoinCode}>Join code</button>
                    <button onClick={onProfile}>Profile</button>
                    <button onClick={handleCloseSession}>End session</button>
                </>
            )}

            <button onClick={logout}>Logout</button>
        </nav>
    );
}
