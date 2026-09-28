import { Link, NavLink } from "react-router-dom";
import { useSession } from "../state";

export function Layout({ children }) {
  const { user, logout } = useSession();
  return (
    <div className="shell">
      <header className="topbar">
        <Link to="/" className="brand">
          <span>Cines</span> Bruma
        </Link>
        <nav>
          <NavLink to="/" end>Home</NavLink>
          <NavLink to="/dulceria">Dulcería</NavLink>
          <NavLink to="/login">Login</NavLink>
        </nav>
        {user ? (
          <button className="session" type="button" onClick={logout}>
            {user.fullName} · Salir
          </button>
        ) : (
          <span className="session ghost">Invitado</span>
        )}
      </header>
      <main>{children}</main>
    </div>
  );
}
