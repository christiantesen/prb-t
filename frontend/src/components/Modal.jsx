export function Modal({ title, children, onClose }) {
  return (
    <div className="modal-back" role="presentation">
      <div className="modal" role="dialog" aria-modal="true" aria-labelledby="modal-title">
        <p className="eyebrow">Cines Bruma</p>
        <h2 id="modal-title">{title}</h2>
        {children}
        {onClose ? (
          <button className="primary" type="button" onClick={onClose}>
            Aceptar
          </button>
        ) : null}
      </div>
    </div>
  );
}
