import { Navigate, Route, Routes } from "react-router-dom";
import { Layout } from "./components/Layout";
import { CandyPage } from "./pages/Candy";
import { HomePage } from "./pages/Home";
import { LoginPage } from "./pages/Login";
import { PaymentPage } from "./pages/Payment";

export function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/dulceria" element={<CandyPage />} />
        <Route path="/pago" element={<PaymentPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Layout>
  );
}
