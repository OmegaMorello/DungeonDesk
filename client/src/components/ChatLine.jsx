export default function ChatLine({event}) {
    // Using a switch case for scalability (i.e. future different events)
    switch (event.type) {
        case "DICE_ROLLED":
            return (
                <li className={`chat-roll ${event.hidden ? "is-hidden-roll" : ""}`}>
                    <strong>{event.senderName}</strong> rolls {event.diceType}: <b>{event.result}</b>
                </li>
            );

        case "CHAT":
            return (
                <li className="chat-message">
                    <strong>{event.senderName}</strong>
                    {event.recipientName && <em> -&gt; {event.recipientName}</em>}: {event.text}
                </li>
            );

        default:
            return null;
    }
}