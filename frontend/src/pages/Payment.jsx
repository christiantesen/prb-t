import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { checkout, getCandy } from "../api";
import { Modal } from "../components/Modal";
import { useSession } from "../state";

const money = new Intl.NumberFormat("es-PE", { style: "currency", currency: "PEN" });

export function PaymentPage() {
  const { user, cart, clearCart } = useSession();
  const [products, setProducts] = useState([]);
  const [email, setEmail] = useState(user?.email || "");
  const [fullName, setFullName] = useState(user?.fullName || "");
  const [documentType, setDocumentType] = useState("DNI");
  const [documentNumber, setDocumentNumber] = useState("");
  const [cardNumber, setCardNumber] = useState("");
  const [expirationDate, setExpirationDate] = useState("");
  const [cvv, setCvv] = useState("");
  const [simulateRejection, setSimulateRejection] = useState(false);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const [result, setResult] = useState(null);

  useEffect(() => {
    getCandy().then(setProducts).catch((reason) => setError(reason.message));
  }, []);

  const lines = useMemo(
    () => products.filter((product) => (cart[product.id] || 0) > 0).map((product) => ({
      ...product,
      quantity: cart[product.id]
    })),
    [products, cart]
  );
  const total = lines.reduce((sum, line) => sum + line.price * line.quantity, 0);

  function fillTestCard() {
    setCardNumber("4907840000000005");
    setExpirationDate("05/27");
    setCvv("777");
    if (!documentNumber) setDocumentNumber("12345678");
  }

  async function onSubmit(event) {
    event.preventDefault();
    setError("");
    const digits = cardNumber.replace(/\s+/g, "");
    if (!/^\d{16}$/.test(digits)) {
      setError("La tarjeta debe tener 16 dígitos");
      return;
    }
    if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(expirationDate)) {
      setError("La expiración debe tener el formato MM/AA");
      return;
    }
    if (!/^\d{3,4}$/.test(cvv)) {
      setError("El CVV debe tener 3 o 4 dígitos");
      return;
    }
    if (documentType === "DNI" && !/^\d{8}$/.test(documentNumber.trim())) {
      setError("El DNI debe tener 8 dígitos");
      return;
    }
    setBusy(true);
    try {
      const response = await checkout({
        items: lines.map((line) => ({ productId: line.id, quantity: line.quantity })),
        email: email.trim(),
        fullName: fullName.trim(),
        documentType,
        documentNumber: documentNumber.trim(),
        cardNumber: digits,
        expirationDate,
        cvv,
        simulateRejection
      }, user?.token);
      clearCart();
      setResult(response);
    } catch (reason) {
      setError(reason.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <section className="page">
      <div className="page-head">
        <p className="eyebrow">Pago</p>
        <h1>Confirma la compra</h1>
      </div>
      {products.length === 0 && Object.values(cart).some((qty) => qty > 0) ? (
        <p className="muted">Cargando el pedido...</p>
      ) : lines.length === 0 && !result ? (
        <p className="muted">El carrito está vacío. <Link to="/dulceria">Volver a dulcería</Link></p>
      ) : (
        <div className="pay-layout">
          <form className="card form" onSubmit={onSubmit}>
            <label>
              Número de tarjeta
              <input inputMode="numeric" maxLength={19} value={cardNumber} onChange={(event) => setCardNumber(event.target.value)} required />
            </label>
            <div className="split">
              <label>
                Expiración
                <input placeholder="MM/AA" value={expirationDate} onChange={(event) => setExpirationDate(event.target.value)} required />
              </label>
              <label>
                CVV
                <input inputMode="numeric" maxLength={4} value={cvv} onChange={(event) => setCvv(event.target.value)} required />
              </label>
            </div>
            <label>
              Correo electrónico
              <input type="email" value={email} onChange={(event) => setEmail(event.target.value)} required />
            </label>
            <label>
              Nombre
              <input value={fullName} onChange={(event) => setFullName(event.target.value)} required />
            </label>
            <div className="split">
              <label>
                Tipo de documento
                <select value={documentType} onChange={(event) => setDocumentType(event.target.value)}>
                  <option value="DNI">DNI</option>
                  <option value="CE">CE</option>
                  <option value="PAS">Pasaporte</option>
                </select>
              </label>
              <label>
                Número de documento
                <input value={documentNumber} onChange={(event) => setDocumentNumber(event.target.value)} required />
              </label>
            </div>
            <label className="check">
              <input type="checkbox" checked={simulateRejection} onChange={(event) => setSimulateRejection(event.target.checked)} />
              Simular rechazo de PayU
            </label>
            {error ? <p className="alert">{error}</p> : null}
            <div className="actions">
              <button className="primary" type="submit" disabled={busy || lines.length === 0}>
                {busy ? "Pagando..." : "Pagar"}
              </button>
              <button className="ghost-btn" type="button" onClick={fillTestCard}>
                Tarjeta de prueba
              </button>
            </div>
            <p className="hint">Tarjeta de prueba PayU: Visa 4907840000000005, CVV 777 y expiración 05/27. El nombre del cliente se guarda aparte; el sandbox aprueba con el tarjetahabiente APPROVED.</p>
          </form>
          <aside className="card summary">
            <h2>Tu pedido</h2>
            <ul>
              {lines.map((line) => (
                <li key={line.id}>
                  <span>{line.quantity} × {line.name}</span>
                  <span>{money.format(line.price * line.quantity)}</span>
                </li>
              ))}
            </ul>
            <p className="total"><span>Total</span><strong>{money.format(total)}</strong></p>
          </aside>
        </div>
      )}
      {result ? (
        <Modal title="Compra correcta" onClose={() => { window.location.assign("/"); }}>
          <p>Código de respuesta {result.code}.</p>
          <p>Transacción {result.transactionId}</p>
          <p className="hint">Operación {result.operationDate}</p>
        </Modal>
      ) : null}
    </section>
  );
}
