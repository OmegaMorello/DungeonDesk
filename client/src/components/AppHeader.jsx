import "./AppHeader.css";

export default function AppHeader({displayName, connected}) {
    return (
        <header className="app-header">
            <div className="app-header-user">
                <div className="app-header-avatar" aria-hidden="true">🐉</div>
                <span className="app-header-name">{displayName}</span>
            </div>

            {/* Green when the socket is open, red when disconnected */}
            <span className={`app-header-status ${connected ? "is-connected" : ""}`}>
                {connected ? "Connected" : "Disconnected"}
            </span>
        </header>
    )
}
