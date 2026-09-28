import { createContext, useContext, useMemo, useState } from "react";

const SessionContext = createContext(null);
const USER_KEY = "bruma-user";
const CART_KEY = "bruma-cart";

function readJson(key, fallback) {
  try {
    const raw = sessionStorage.getItem(key);
    return raw ? JSON.parse(raw) : fallback;
  } catch {
    return fallback;
  }
}

export function SessionProvider({ children }) {
  const [user, setUserState] = useState(() => readJson(USER_KEY, null));
  const [cart, setCartState] = useState(() => readJson(CART_KEY, {}));

  const api = useMemo(() => {
    function setUser(next) {
      setUserState(next);
      if (next) sessionStorage.setItem(USER_KEY, JSON.stringify(next));
      else sessionStorage.removeItem(USER_KEY);
    }

    function setCart(next) {
      setCartState(next);
      sessionStorage.setItem(CART_KEY, JSON.stringify(next));
    }

    return {
      user,
      cart,
      setUser,
      logout() {
        setUser(null);
      },
      setQuantity(productId, quantity) {
        const next = { ...cart };
        if (quantity <= 0) delete next[productId];
        else next[productId] = quantity;
        setCart(next);
      },
      clearCart() {
        setCart({});
      }
    };
  }, [user, cart]);

  return <SessionContext.Provider value={api}>{children}</SessionContext.Provider>;
}

export function useSession() {
  return useContext(SessionContext);
}
