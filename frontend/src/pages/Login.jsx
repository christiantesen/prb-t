import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { login, loginWithGoogle } from "../api";
import { Modal } from "../components/Modal";
import { useSession } from "../state";

export function LoginPage() {
  const navigate = useNavigate();
  const { setUser, logout } = useSession();
  const [email, setEmail] = useState("cliente@cine.com");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const [welcomeName, setWelcomeName] = useState("");
  const googleReady = useRef(false);
  const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;

  useEffect(() => {
    if (!googleClientId) return undefined;
    const timer = setInterval(() => {
      if (!window.google?.accounts?.id || googleReady.current) return;
      googleReady.current = true;
      clearInterval(timer);
      window.google.accounts.id.initialize({
        client_id: googleClientId,
        callback: async (response) => {
          setError("");
          try {
            const session = await loginWithGoogle(response.credential);
            setUser(session);
            setWelcomeName(session.fullName);
          } catch (reason) {
            setError(reason.message);
          }
        }
      });
      const slot = document.getElementById("google-btn");
      if (slot) {
        window.google.accounts.id.renderButton(slot, {
          theme: "filled_black",
          size: "large",
          text: "continue_with",
          shape: "pill",
          width: 320
        });
      }
    }, 200);
    return () => clearInterval(timer);
  }, [googleClientId, setUser]);

  async function onSubmit(event) {
    event.preventDefault();
    setBusy(true);
    setError("");
    try {
      const session = await login(email.trim(), password);
      setUser(session);
      setWelcomeName(session.fullName);
    } catch (reason) {
      setError(reason.message);
    } finally {
      setBusy(false);
    }
  }

  function enterAsGuest() {
    logout();
    navigate("/dulceria");
  }

  return (
    <section className="page narrow">
      <div className="page-head">
        <p className="eyebrow">Acceso</p>
        <h1>Entra a la función</h1>
      </div>
      <form className="card form" onSubmit={onSubmit}>
        <label>
          Correo
          <input type="email" value={email} onChange={(event) => setEmail(event.target.value)} required />
        </label>
        <label>
          Clave
          <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} required />
        </label>
        {error ? <p className="alert">{error}</p> : null}
        <div className="actions">
          <button className="primary" type="submit" disabled={busy}>
            {busy ? "Entrando..." : "Iniciar sesión"}
          </button>
          <button className="ghost-btn" type="button" onClick={enterAsGuest}>
            Invitado
          </button>
        </div>
        <p className="hint">Cuenta de prueba: cliente@cine.com / cine123</p>
      </form>
      {googleClientId ? (
        <div className="google-slot">
          <p className="hint">o</p>
          <div id="google-btn" />
        </div>
      ) : null}
      {welcomeName ? (
        <Modal title={`Bienvenido, ${welcomeName}`} onClose={() => navigate("/dulceria")} />
      ) : null}
    </section>
  );
}
