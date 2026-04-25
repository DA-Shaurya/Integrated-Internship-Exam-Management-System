import { useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../services/api";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async () => {
  try {
    setLoading(true);
    const res = await API.post("/auth/login", {
      email: email.trim().toLowerCase(),
      password: password.trim()
    });

    localStorage.setItem("token", res.data.token);
    localStorage.setItem("user", JSON.stringify(res.data));

    if (res.data.role === "ADMIN") {
      navigate("/admin-dashboard");
    } else {
      navigate("/internships");
    }

  } catch (err) {
    alert(err.response?.data?.message || "Invalid credentials");
  } finally {
    setLoading(false);
  }
};

  return (
    <div className="flex-center">
      <div className="glass-panel animate-fade-in" style={{ width: "100%", maxWidth: "400px" }}>
        <div style={{ textAlign: "center", marginBottom: "2rem" }}>
          <h2 style={{ color: "var(--primary-color)", fontSize: "2rem", marginBottom: "0.5rem" }}>IEMS Portal</h2>
          <p>Login to access internships & exams</p>
        </div>

        <div style={{ marginBottom: "1rem" }}>
          <label style={{ fontSize: "0.875rem", color: "var(--text-muted)", marginBottom: "0.5rem", display: "block" }}>Email Address</label>
          <input 
            type="email"
            placeholder="student@example.com" 
            onChange={e => setEmail(e.target.value)} 
          />
        </div>

        <div style={{ marginBottom: "2rem" }}>
          <label style={{ fontSize: "0.875rem", color: "var(--text-muted)", marginBottom: "0.5rem", display: "block" }}>Password</label>
          <input 
            type="password" 
            placeholder="••••••••" 
            onChange={e => setPassword(e.target.value)} 
          />
        </div>

        <button onClick={handleLogin} disabled={loading}>
          {loading ? "Logging in..." : "Login"}
        </button>

        <p style={{ textAlign: "center", marginTop: "1.5rem" }}>
          Don't have an account? <a href="/register" style={{ color: "var(--primary-color)", fontWeight: "600" }}>Register</a>
        </p>
      </div>
    </div>
  );
}