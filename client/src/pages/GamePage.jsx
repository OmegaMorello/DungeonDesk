import {useAuth} from "../context/AuthContext.js";
import {useGame} from "../context/GameContext.js";
import {useState} from "react";
import SheetPage from "./SheetPage.jsx";
import AppHeader from "../components/AppHeader.jsx";
import PlayersBar from "../components/PlayersBar.jsx";
import MapBoard from "../components/MapBoard.jsx";
import MapTools from "../components/MapTools.jsx";
import EnemyList from "../components/EnemyList.jsx";
import DiceWidget from "../components/DiceWidget.jsx";
import TurnOrderPanel from "../components/TurnOrderPanel.jsx";
import ChatPanel from "../components/ChatPanel.jsx";
import TopMenu from "../components/TopMenu.jsx";
import NotesPanel from "../components/NotesPanel.jsx";
import JoinCodeDialog from "../components/JoinCodeDialog.jsx";
import ProfileDialog from "../components/ProfileDialog.jsx";
import CampaignsDialog from "../components/CampaignsDialog.jsx";
import * as api from "../services/api.js";
import "./GamePage.css";

export default function GamePage({openSheetId, onOpenSheet, onExit}) {
    const {user} = useAuth();
    const {sheets, map, connected, campaignId, campaignName, setCampaignName, setPlayers} = useGame();
    const [notesOpen, setNotesOpen] = useState(false);
    const [newSheetType, setNewSheetType] = useState(null);
    const [dialog, setDialog] = useState(null);
    const [campaign, setCampaign] = useState(null);

    async function handleEditCampaign() {
        setCampaign(await api.getCampaign(campaignId));
    }

    function handleCampaignUpdated(updated) {
        setCampaign(updated);
        setCampaignName(updated.name);   // keeps the title in sync
        setPlayers(updated.players);     // and the badges on the cards
    }

    const isMaster = user.loginType === "MASTER";
    const characters = sheets.filter((s) => s.sheetType === "CHARACTER");
    const enemies = sheets.filter((s) => s.sheetType === "ENEMY");

    if (newSheetType)
        return (
            <SheetPage
                sheetId={null}
                sheetType={newSheetType}
                onBack={() => setNewSheetType(null)}
            />
        );

    if (openSheetId)
        return <SheetPage sheetId={openSheetId} onBack={() => onOpenSheet(null)}/>;

    return (
        <div className="game">
            {/* Header and menu scroll with the page but stay pinned on top */}
            <div className="game-top">
                <AppHeader displayName={user.displayName} connected={connected}/>

                <TopMenu
                    onNewSheet={setNewSheetType}
                    onNotes={() => setNotesOpen(true)}
                    onProfile={() => setDialog("profile")}
                    onJoinCode={() => setDialog("join-code")}
                    onEditCampaign={handleEditCampaign}
                    onLeave={onExit}
                    onSessionClosed={onExit}
                />
            </div>

            <div className="game-body">
                <h1 className="game-title">{campaignName || "Session"}</h1>

                <div className="game-board">
                    <div className="game-main">
                        {/* Right above the map, so the party is always in sight */}
                        <PlayersBar characters={characters} onOpenSheet={onOpenSheet}/>

                        <div className="game-map">
                            <MapBoard map={map}/>
                        </div>

                        {/* The enemies on the table only visible by the master*/}
                        {isMaster && <EnemyList enemies={enemies} onOpenSheet={onOpenSheet}/>}

                        {/* Tools, dice and turn order under the map */}
                        <div className="game-tools">
                            {isMaster && <MapTools/>}
                            <DiceWidget isMaster={isMaster}/>
                            <TurnOrderPanel isMaster={isMaster}/>
                        </div>
                    </div>

                    <ChatPanel/>
                </div>
            </div>

            {notesOpen && <NotesPanel onClose={() => setNotesOpen(false)}/>}

            {dialog === "profile" && <ProfileDialog onClose={() => setDialog(null)}/>}
            {dialog === "join-code" && <JoinCodeDialog onClose={() => setDialog(null)}/>}

            {campaign && (
                <CampaignsDialog
                    campaign={campaign}
                    onUpdated={handleCampaignUpdated}
                    onClose={() => setCampaign(null)}
                />
            )}
        </div>
    )
}
