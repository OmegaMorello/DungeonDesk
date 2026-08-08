// All calls to the backend live here, so components never write URLs directly.

// Generic helper: does the fetch and parses the JSON response.
// Instead of using cookies, I use the custom token header for auth
async function request(url, options = {}) {
  const token = localStorage.getItem("token");

  const res = await fetch(url, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(token && { Authorization: `Bearer ${token}` }),
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
  return request("/api/health");
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
export async function login({ loginType, username, secret }) {
  // Creating a data variable to store the session data
  const data = await request("/api/v1/auth/login", {
    method: "POST",
    body: JSON.stringify({ loginType, username, secret }),
  });
  localStorage.setItem("token", data.token); // Save the token in the session storage
  return data;
}

// POST /api/v1/auth/logout -> { no content: ok }
export async function logout() {
  try {
    await request("/api/v1/auth/logout", { method: "POST" });
  } finally {
    localStorage.removeItem("token"); // Remove the token from the session storage
  }
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
