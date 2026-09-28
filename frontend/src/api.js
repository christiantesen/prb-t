async function read(path, options) {
  const response = await fetch(path, options);
  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(data.message || "No se pudo completar la operacion");
  }
  return data;
}

export function getPremieres() {
  return read("/api/premieres");
}

export function getCandy() {
  return read("/api/candy");
}

export function login(email, password) {
  return read("/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password })
  });
}

export function loginWithGoogle(idToken) {
  return read("/api/auth/google", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ idToken })
  });
}

export function checkout(payload, token) {
  const headers = { "Content-Type": "application/json" };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  return read("/api/checkout", {
    method: "POST",
    headers,
    body: JSON.stringify(payload)
  });
}
