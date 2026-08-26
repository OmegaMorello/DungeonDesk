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
export function register({ username, secret }) {
  return request("/api/v1/auth/register", {
    method: "POST",
    body: JSON.stringify({ username, secret }),
  });
}

// POST /api/v1/auth/login -> { auth session }
export function login({ loginType, username, secret }) {
  // Creating a data variable to store the session data
  return request("/api/v1/auth/login", {
    method: "POST",
    body: JSON.stringify({ loginType, username, secret }),
  });
}

// POST /api/v1/auth/logout -> { no content: ok }
export function logout() {
  return request("/api/v1/auth/logout", { method: "POST" });
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
