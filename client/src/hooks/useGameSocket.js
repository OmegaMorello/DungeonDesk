import {useEffect, useRef, useState} from "react";

/**
 * Opens a web socket connection handling events and returning payload and connection state
 *
 * @param onEvent Event to handle
 * @returns {{send: send, connected: boolean}}
 */
export function useGameSocket(onEvent) {
    const [connected, setConnected] = useState(false);

    // useRef avoids to re-render when object changes (instead of useState). Save { current: value }
    const socketRef = useRef(null);
    const handlerRef = useRef(onEvent);
    handlerRef.current = onEvent;

    useEffect(() => {
        let socket;
        let retryTimer;
        let closedByMe = false;

        function connect() {
            socket = new WebSocket(`ws://${window.location.host}/ws`); // Allows multiple sockets on the same machine
            socketRef.current = socket;

            socket.onopen = () => setConnected(true);

            socket.onmessage = (message) => {
                try {
                    handlerRef.current(JSON.parse(message.data));
                } catch {
                    // Do nothing, avoid to kill the connection
                }
            };

            socket.onclose = () => {
                setConnected(false);
                if (!closedByMe) retryTimer = setTimeout(connect, 2000); // Tries to reconnect on connection lost
            };
        }

        connect();

        return () => {
            closedByMe = true;
            clearTimeout(retryTimer);

            if (socket.readyState === WebSocket.OPEN) socket.close();
            else socket.addEventListener("open", () => socket.close());
        };
    }, []);

    function send(payload) {
        if (socketRef.current?.readyState === WebSocket.OPEN)
            socketRef.current.send(JSON.stringify(payload));
    }

    return {send, connected};
}