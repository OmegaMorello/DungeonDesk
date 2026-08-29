import {createContext, useContext} from "react";

// The context of the game, storing values needed by children
export const GameContext = createContext(null);
export const useGame = () => useContext(GameContext);