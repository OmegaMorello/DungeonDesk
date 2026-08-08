import { useAuth } from "./context/AuthContext";
import AuthPage from "./pages/AuthPage";

import './App.css'

function App() {
  const { user, logout, loading } = useAuth();

  if (loading) return <div className="app-loading">Caricamento…</div>;
  if (!user) return <AuthPage />;

  return (
    <div className="App">
      <h1>Hello, {user.displayName}!</h1>
      <button onClick={logout}>Logout</button>
    </div>
  )
}

export default App
