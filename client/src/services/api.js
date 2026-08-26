// All calls to the backend are done here

// Generic helper: does the fetch and parses the JSON response.
async function request(url, options = {}) {
    const res = await fetch(url, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...options.headers,
        },
    });

    if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.error || `Request failed: ${res.status}`);
    }
    if (res.status === 204) return null; // logout has no content
    return res.json();
}

// First test call: the health check.
export function getHealth() {
    return request("/api/v1/health");
}



// ---- App user --------------------------------------------------

// PUT /api/v1/appusers/update -> AppUserDto
export function updateUser({ username, secret }) {
    return request("/api/v1/appusers/update", {
        method: "PUT",
        body: JSON.stringify({ username, secret }),
    });
}



// ---- Auth ----------------------------------------------------

// POST /api/v1/auth/register -> { User Created }
export function register({username, secret}) {
    return request("/api/v1/auth/register", {
        method: "POST",
        body: JSON.stringify({username, secret}),
    });
}

// POST /api/v1/auth/login -> { auth session }
export function login({loginType, username, secret}) {
    // Creating a data variable to store the session data
    return request("/api/v1/auth/login", {
        method: "POST",
        body: JSON.stringify({loginType, username, secret}),
    });
}

// POST /api/v1/auth/logout -> { no content: ok }
export function logout() {
    return request("/api/v1/auth/logout", {method: "POST"});
}

// GET /api/v1/auth/me -> { login type, name, campaign id } | null
export function getMe() {
    return request("/api/v1/auth/me");
}

// GET /api/v1/auth/session/players -> [{ id, name }]
// Public: the roster of the running session.
// Returns an empty list when no session is open.
// NOTE: not implemented on the backend yet - the caller treats a failure
// as "no session available".
export function getSessionPlayers() {
    return request("/api/v1/auth/session/players");
}



// ---- Campaigns ----------------------------------------------------

// GET /api/v1/campaigns -> [CampaignDto]
export function getCampaignList() {
    return request("api/v1/campaigns");
}

// GET /api/v1/campaigns/:id -> CampaignDto
export function getCampaign(campaignId) {
    return request(`api/v1/campaigns/${campaignId}`);
}

// POST /api/v1/campaigns -> CampaignDto [201]
export function createCampaign(name, description) {
    return request("api/v1/campaigns", {
        method: "POST",
        body: JSON.stringify({ name, description })
    });
}

// PUT /api/v1/campaigns/:id -> CampaignDto
export function updateCampaign(campaignId, { name, description }) {
    return request(`api/v1/campaigns/${campaignId}`, {
        method: "PUT",
        body: JSON.stringify({ name, description })
    });
}

// DELETE /api/v1/campaigns/:id -> [204]
export function deleteCampaign(campaignId) {
    return request(`api/v1/campaigns/${campaignId}`, {
        method: "DELETE"
    });
}

// POST /api/v1/campaigns/:id/players -> CampaignDto
export function addPlayer(campaignId, name) {
    return request(`api/v1/campaigns/${campaignId}/players`, {
        method: "POST",
        body: JSON.stringify({ name })
    });
}

// DELETE /api/v1/campaigns/:id/players?playerId=... -> CampaignDto
export function removePlayer(campaignId, playerId) {
    return request(`api/v1/campaigns/${campaignId}/players?playerId=${playerId}`, {
        method: "DELETE"
    });
}



// ---- Game sessions ----------------------------------------------------

// POST /api/v1/campaigns/:id/sessions -> GameSessionDto
export function createSession(campaignId, { joinCode }) {
    return request(`api/v1/campaigns/${campaignId}/sessions`, {
        method: "POST",
        body: JSON.stringify(joinCode)
    });
}

// GET /api/v1/sessions/active -> GameSessionDto
export function getActiveSession() {
    return request(`api/v1/sessions/active`);
}

// PUT /api/v1/sessions/:id -> GameSessionDto
export function updateSession(sessionId, body) {
    return request(`api/v1/sessions/${sessionId}`, {
        method: "PUT",
        body: JSON.stringify(body)
    });
}

// PSOT /api/v1/sessions/:id/close -> GameSessionDto
export function closeSession(sessionId) {
    return request(`api/v1/sessions/${sessionId}/close`, {
        method: "POST"
    });
}



// ---- Map ----------------------------------------------------

// GET /api/v1/map -> MapStateDto
export function getMap() {
    return request("api/v1/map");
}

// POST /api/v1/map -> MapStateDto
export function createOrResizeMap({ campaignId, gridRows, gridColumns }) {
    return request("api/v1/map", {
        method: "POST",
        body: JSON.stringify({ campaignId, gridRows, gridColumns })
    });
}

// POST /api/v1/map/tokens -> TokenDto [201]
export function addToken({ sheetId, tokenType, posX, posY }) {
    return request("api/v1/map/tokens", {
        method: "POST",
        body: JSON.stringify({ sheetId, tokenType, posX, posY })
    });
}

// PUT /api/v1/map/tokens/:id -> TokenDto
export function moveToken(tokenId, posX, posY) {
    return request(`api/v1/map/tokens/${tokenId}`, {
        method: "PUT",
        body: JSON.stringify({ posX, posY })
    });
}

// DELETE /api/v1/map/tokens/:id -> [204]
export function deleteToken(tokenId) {
    return request(`api/v1/map/tokens/${tokenId}`, {
        method: "DELETE"
    });
}

// PUT /api/v1/map/background -> [204]
export async function uploadMapBackground(mapFile) {
    const form = new FormData();
    form.append("mapFile", mapFile);

    const res = await fetch("/api/v1/map/background", {
        method: "POST",
        body: form
    });

    if (!res.ok) throw new Error("Upload failed");
}



// ---- Notes ----------------------------------------------------

// POST /api/v1/campaigns/:id/notes -> NoteDto
export function createNote(campaignId, note) {
    return request(`api/v1/campaigns/${campaignId}/notes`, {
        method: "POST",
        body: JSON.stringify(note)
    });
}

// GET /api/v1/campaigns/:id/notes -> [NoteDto]
export function getCampaignNotes(campaignId) {
    return request(`api/v1/campaigns/${campaignId}/notes`);
}

// GET /api/v1/sessions/:id/notes -> [NoteDto]
export function getSessionNotes(campaignId) {
    return request(`api/v1/sessions/${campaignId}/notes`);
}

// PUT /api/v1/notes/:id -> NoteDto
export function updateNote(noteId, note) {
    return request(`api/v1/notes/${noteId}`, {
        method: "PUT",
        body: JSON.stringify(note)
    });
}

// DELETE /api/v1/ntoes/:id -> [204]
export function deleteNote(noteId) {
    return request(`api/v1/notes/${noteId}`, {
        method: "POST"
    });
}




// ---- Sheets ----------------------------------------------------

// GET /api/v1/sheets/session -> [SheetSummaryDto]
export function getSheetsSummary() {
    return request("api/v1/sheets/session");
}

// GET /api/v1/sheets -> [SheetDto]
export function getOwnedSheets() {
    return request("api/v1/sheets/");
}

// GET /api/v1/sheets/:id -> [SheetDto]
export function getSheet(sheetId) {
  return request(`api/v1/sheets/${sheetId}`);
}

// POST /api/v1/sheets/character -> SheetDto [201]
export function createCharacterSheet(sheetRequest) {
    return request("api/v1/sheets/characters", {
        method: "POST",
        body: JSON.stringify(sheetRequest)
    });
}

// POST /api/v1/sheets/character -> SheetDto [201]
export function createEnemySheet(sheetRequest) {
    return request("api/v1/sheets/enemies", {
        method: "POST",
        body: JSON.stringify(sheetRequest)
    });
}

// PUT /api/v1/sheets/:id -> SheetDto
export function updateSheet(sheetId, sheetRequest) {
    return request(`api/v1/sheets/${sheetId}`, {
        method: "PUT",
        body: JSON.stringify(sheetRequest)
    });
}

// DELETE /api/v1/sheets/:id -> [204]
export function deleteSheet(sheetId) {
    return request(`api/v1/sheets/${sheetId}`, {
        method: "DELETE"
    });
}



// ---- Turn order ----------------------------------------------------

// GET /api/v1/turn-order -> [sheetId]
export function getTurnOrder() {
    return request("api/v1/turn-order");
}

// POST /api/v1/turn-order/roll -> [sheetId]
export function rollInitiative() {
    return request("api/v1/turn-order", {
        method: "POST"
    });
}

// PUT /api/v1/turn-order -> [sheetId]
export function setTurnOrder(sheetIds) {
    return request("api/v1/turn-order", {
        method: "PUT",
        body: JSON.stringify(sheetIds)
    });
}
















