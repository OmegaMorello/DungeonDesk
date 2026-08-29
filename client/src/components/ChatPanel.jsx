import {useAuth} from "../context/AuthContext.js";
import {useGame} from "../context/GameContext.js";
import {useState} from "react";
import ChatLine from "./ChatLine.jsx";
import "./ChatPanel.css";

export default function ChatPanel() {
    const {user} = useAuth();
    const {feed, players, sendChatMessage} = useGame();

    const [channel, setChannel] = useState(null);
    const [text, setText] = useState("");

    const isMaster = user.loginType === "MASTER";

    // Player names used to understand who sends and receives a message
    const channels = isMaster ? players.map((p) => p.name) : [];

    // A player receives only what is meant for them
    // The master can also send direct messages
    const visible = isMaster
        ? feed.filter((e) =>
            channel === null
                ? !e.recipientName
                : e.recipientName === channel || e.senderName === channel)
        : feed;

    function handleSubmit(e) {
        e.preventDefault();
        if (!text.trim()) return;

        sendChatMessage(text, channel);
        setText("");
    }

    return (
        <aside className="chat-panel">
            <h2 className="chat-title">Chat</h2>

            <nav className="chat-channels">
                <button
                    className={channel === null ? "is-active" : ""}
                    onClick={() => setChannel(null)}
                >
                    Group
                </button>

                {channels.map((name) => (
                    <button
                        key={name}
                        className={channel === name ? "is-active" : ""}
                        onClick={() => setChannel(name)}
                    >
                        {name}
                    </button>
                ))}
            </nav>

            <ul className="chat-feed">
                {visible.map((event, index) => (
                    <ChatLine key={index} event={event}/>
                ))}
            </ul>

            <form className="chat-form" onSubmit={handleSubmit}>
                <input
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    placeholder={channel ? `To ${channel}` : "To everyone"}
                />
                <button type="submit">Send</button>
            </form>

        </aside>
    )
}

