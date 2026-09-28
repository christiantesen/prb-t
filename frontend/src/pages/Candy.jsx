import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getCandy } from "../api";
import { useSession } from "../state";

const money = new Intl.NumberFormat("es-PE", { style: "currency", currency: "PEN" });

export function CandyPage() {
  const navigate = useNavigate();
  const { cart, setQuantity } = useSession();
  const [products, setProducts] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    getCandy().then(setProducts).catch((reason) => setError(reason.message));
  }, []);

  const total = useMemo(() => {
    return products.reduce((sum, product) => sum + product.price * (cart[product.id] || 0), 0);
  }, [products, cart]);

  return (
    <section className="page">
      <div className="page-head">
        <p className="eyebrow">Dulcería</p>
        <h1>Para llevar a la sala</h1>
      </div>
      {error ? <p className="alert">{error}</p> : null}
      <div className="candy-grid">
        {products.map((product) => {
          const quantity = cart[product.id] || 0;
          return (
            <article className="card candy" key={product.id}>
              <div>
                <h2>{product.name}</h2>
                <p>{product.description}</p>
              </div>
              <div className="candy-buy">
                <strong>{money.format(product.price)}</strong>
                <div className="stepper">
                  <button type="button" onClick={() => setQuantity(product.id, quantity - 1)} aria-label="Quitar">-</button>
                  <span>{quantity}</span>
                  <button type="button" onClick={() => setQuantity(product.id, quantity + 1)} aria-label="Agregar">+</button>
                </div>
              </div>
            </article>
          );
        })}
      </div>
      <footer className="checkout-bar">
        <div>
          <span className="eyebrow">Total</span>
          <strong>{money.format(total)}</strong>
        </div>
        <button className="primary" type="button" disabled={total <= 0} onClick={() => navigate("/pago")}>
          Continuar
        </button>
      </footer>
    </section>
  );
}
