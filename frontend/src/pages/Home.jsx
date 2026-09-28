import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getPremieres } from "../api";

export function HomePage() {
  const navigate = useNavigate();
  const [films, setFilms] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    getPremieres().then(setFilms).catch((reason) => setError(reason.message));
  }, []);

  return (
    <section className="page">
      <div className="page-head">
        <p className="eyebrow">Cartelera</p>
        <h1>Esta semana en sala</h1>
      </div>
      {error ? <p className="alert">{error}</p> : null}
      {!error && films.length === 0 ? <p className="muted">Cargando estrenos...</p> : null}
      <div className="billboard">
        {films.map((film) => (
          <article className="feature" key={film.id}>
            <button
              className="poster-hit"
              type="button"
              onClick={() => navigate("/login")}
              aria-label={`Elegir ${film.title}`}
            >
              <img src={film.imageUrl} alt="" />
            </button>
            <div>
              <h2>{film.title}</h2>
              <p>{film.synopsis}</p>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
