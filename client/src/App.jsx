import {useState} from "react";
import {useAuth} from "./context/AuthContext";
import AuthPage from "./pages/AuthPage";
import CampaignsPage from "./pages/CampaignsPage";
import GamePage from "./pages/GamePage";
import {GameProvider} from "./context/GameProvider";
import './App.css'

// Single page application
// Deciding here who sees which page
export default function App() {
    const {user, loading} = useAuth();

    const [openSheetId, setOpenSheetId] = useState(null);

    const [atGamePage, setAtGamePage] = useState(false);

    if (loading) return <div className="app-loading">Loading…</div>;

    // If not logged in show the login page
    if (!user) return <AuthPage/>;

    const isMaster = user.loginType === "MASTER";

    // The master will see the campaign list at login
    // Starting a campaign session throws the master in the game screen
    if (isMaster && !atGamePage)
        return <CampaignsPage onStart={() => setAtGamePage(true)}/>;

    return (
        <GameProvider>
            <GamePage
                openSheetId={openSheetId}
                onOpenSheet={setOpenSheetId}
                onExit={() => setAtGamePage(false)}
            />
        </GameProvider>
    );
}

