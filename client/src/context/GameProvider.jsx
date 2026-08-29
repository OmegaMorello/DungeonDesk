import {useCallback, useEffect, useRef, useState} from "react";
import {useGameSocket} from "../hooks/useGameSocket.js";
import {useAuth} from "./AuthContext.js";
import * as api from "../services/api.js";
import {GameContext} from "./GameContext.js";

const MAX_QUEUE_SIZE = 200; // GameState events cap, same as the server

export function GameProvider({children}) {
    const {user} = useAuth();

    const [feed, setFeed] = useState([]);
    const [sheets, setSheets] = useState([]);
    const [turnOrder, setTurnOrder] = useState([]);
    const [map, setMap] = useState(null);

    // handleEvent has empty dependencies, so it cannot see the current map: a ref can
    const mapRef = useRef(null);
    mapRef.current = map;
    const [campaignId, setCampaignId] = useState(null);
    const [campaignName, setCampaignName] = useState("");

    // The roster: the summaries carry the owner id, the name is in the campaign
    const [players, setPlayers] = useState([]);

    // Reloading sheets summary for game page visualization
    const reloadSheets = useCallback(() => {
        return api.getSheetsSummary().then(setSheets).catch(() => setSheets([]));
    }, []);

    const handleEvent = useCallback((event) => {
        switch (event.type) {
            // Treating chat and roll dice equally since they are being shown on the same chat list
            case "CHAT":
            case "DICE_ROLLED":
                setFeed((prev) => [...prev, event].slice(-MAX_QUEUE_SIZE));
                break;

            // Changing the values seen in the game page (player cards) real-time
            case "SHEET_CHANGED":
                setSheets((prev) =>
                    prev.map((s) =>
                        s.sheetId === event.sheetId
                            ? {...s, name: event.name, currentHp: event.currentHp, maxHp: event.maxHp}
                            : s
                    ));
                break;

            case "TOKEN_MOVED": {
                // If the token is new, the map must be read to know the token's sheet and type
                const known = mapRef.current?.tokenList?.some((t) => t.tokenId === event.tokenId);

                if (!known) {
                    api.getMap().then(setMap).catch(() => {
                    });
                    break;
                }

                setMap((prev) =>
                    !prev ? prev : {
                        ...prev,
                        tokenList: prev.tokenList.map((t) =>
                            t.tokenId === event.tokenId ? {...t, posX: event.posX, posY: event.posY} : t)
                    }
                );
                break;
            }

            // Deleting needs to read the whole map back to remove the token
            case "TOKEN_REMOVED":
                setMap((prev) =>
                    !prev ? prev : {
                        ...prev,
                        tokenList: prev.tokenList.filter((t) => t.tokenId !== event.tokenId)
                    }
                );
                break;

            // Reloading the map state but not the background unless it has been changed
            case "MAP_CHANGED":
                api.getMap()
                    .then((map) => setMap(event.backgroundChanged
                        ? {...map, backgroundVersion: Date.now()}
                        : map))
                    .catch(() => setMap(null));

                // Reloading the sheets summary to update the player cards after a deletion
                void reloadSheets();
                break;

            case "TURN_ORDER_CHANGED":
                setTurnOrder(event.sheetIds);
                break;

            default:
                break;
        }
    }, []);

    const {send, connected} = useGameSocket(handleEvent);


    useEffect(() => {
        void reloadSheets();

        api.getMap()
            .then(setMap)
            .catch(() => setMap(null));
    }, [reloadSheets]);

    // Getting the active session
    useEffect(() => {
        if (user.loginType !== "MASTER") {
            setCampaignId(user.campaignId ?? null);
            return;
        }

        api.getActiveSession()
            .then((session) => setCampaignId(session.campaignId))
            .catch(() => setCampaignId(null));
    }, [user]);

    // Getting the campaign info
    useEffect(() => {
        if (!campaignId) return;

        api.getCampaign(campaignId)
            .then((campaign) => {
                setCampaignName(campaign.name);
                setPlayers(campaign.players);
            })
            .catch(() => {
                setCampaignName("");
                setPlayers([]);
            });
    }, [campaignId]);

    const sendChatMessage = (text, recipient = null) => send({type: "CHAT", text, recipient});
    const rollDice = (diceType, hidden = false) => send({type: "ROLL_DICE", diceType, hidden});

    // Listing all the values needed in the game context to be used by children
    const value = {
        feed,
        sheets,
        turnOrder,
        map,
        setMap,
        campaignId,
        campaignName,
        setCampaignName,
        players,
        setPlayers,
        connected,
        sendChatMessage,
        rollDice,
        reloadSheets
    };

    return <GameContext.Provider value={value}>{children}</GameContext.Provider>;

}