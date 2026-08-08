// ====================================================
// MasterAuthForm.jsx
// Dungeon Master login and registration.
// Owns its own form state; talks to the server through useAuth().
// ====================================================

import { useState } from "react";
import { useAuth } from "../context/AuthContext";

export default function MasterAuthForm() {
  const { loginMaster, register } = useAuth();

  // Which action is showing: "login" or "register".
  const [action, setAction] = useState("login");

  // One state object for all fields keeps the handlers short.
  const [form, setForm] = useState({ username: "", secret: "" });

  // Last error message to show the user (null = no error).
  const [error, setError] = useState(null);

  // True while a request is in flight: disables the button so the
  // user can't submit twice (double account / double login).
  const [submitting, setSubmitting] = useState(false);

  const isLogin = action === "login";

  // Generic change handler: updates the field matching the input "name".
  function handleChange(e) {
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }));
  }

  // Switch tab: clear the error and the secret, so a password typed for
  // one action is never carried over into the other.
  function switchAction(next) {
    setAction(next);
    setError(null);
    setForm((f) => ({ ...f, secret: "" }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      if (isLogin) {
        await loginMaster(form);
      } else {
        // register() auto-logs-in on success (see AuthProvider).
        await register(form);
      }
      // On success there is nothing else to do here: `user` in the context
      // changed, so App re-renders and shows the application.
    } catch (err) {
      setError(err.message); // e.g. "Invalid credentials"
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <>
      <div className="auth-tabs">
        <button
          type="button"
          aria-pressed={isLogin}
          className={`auth-tab ${isLogin ? "is-active" : ""}`}
          onClick={() => switchAction("login")}
        >
          Login
        </button>
        <button
          type="button"
          aria-pressed={!isLogin}
          className={`auth-tab ${!isLogin ? "is-active" : ""}`}
          onClick={() => switchAction("register")}
        >
          Register
        </button>
      </div>

      <form className="auth-form" onSubmit={handleSubmit}>
        <label className="auth-field">
          <span>Username</span>
          <input
            name="username"
            type="text"
            autoComplete="username"
            placeholder="Mario Rossi"
            value={form.username}
            onChange={handleChange}
            required
          />
        </label>

        <label className="auth-field">
          <span>Password</span>
          <input
            name="secret"
            type="password"
            autoComplete={isLogin ? "current-password" : "new-password"}
            placeholder="••••••••"
            value={form.secret}
            onChange={handleChange}
            minLength={isLogin ? undefined : 8}
            required
          />
          {!isLogin && (
            <small className="auth-hint">At least 8 characters.</small>
          )}
        </label>

        {error && (
          <p className="auth-error" role="alert">
            {error}
          </p>
        )}

        <button className="auth-submit" type="submit" disabled={submitting}>
          {submitting ? "Waiting…" : isLogin ? "Login" : "Create account"}
        </button>
      </form>

      <p className="auth-switch">
        {isLogin ? "Don't have an account? " : "Already have an account? "}
        <button
          type="button"
          className="auth-link"
          onClick={() => switchAction(isLogin ? "register" : "login")}
        >
          {isLogin ? "Register" : "Login"}
        </button>
      </p>
    </>
  );
}
