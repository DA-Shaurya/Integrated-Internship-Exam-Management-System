import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import API from "../services/api";

export default function Register() {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
    cgpa: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");

    if (formData.password !== formData.confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    if (parseFloat(formData.cgpa) < 0 || parseFloat(formData.cgpa) > 10) {
      setError("CGPA must be between 0 and 10");
      return;
    }

    setLoading(true);
    try {
      await API.post("/auth/register", {
        name: formData.name,
        email: formData.email,
        password: formData.password,
        role: "STUDENT", // Default to student
      });
      alert("Account created — please log in.");
      navigate("/");
    } catch (err) {
      setError(err.response?.data?.message || "Registration failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex-center animate-fade-in">
      <div className="glass-panel" style={{ width: "100%", maxWidth: "450px" }}>
        <h1 style={{ textAlign: "center", marginBottom: "1rem" }}>Join IEMS</h1>
        <p style={{ textAlign: "center", marginBottom: "2rem" }}>Create your student account</p>

        {error && <div className="badge danger" style={{ width: "100%", marginBottom: "1rem", textAlign: "center" }}>{error}</div>}

        <form onSubmit={handleRegister}>
          <div className="form-group">
            <label>Full Name</label>
            <input
              type="text"
              required
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              placeholder="Enter your full name"
            />
          </div>

          <div className="form-group">
            <label>Email Address</label>
            <input
              type="email"
              required
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              placeholder="shaurya@example.com"
            />
          </div>

          <div className="grid" style={{ gridTemplateColumns: "1fr 1fr" }}>
            <div className="form-group">
              <label>Password</label>
              <input
                type="password"
                required
                minLength={8}
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>Confirm</label>
              <input
                type="password"
                required
                value={formData.confirmPassword}
                onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
              />
            </div>
          </div>

          <div className="form-group">
            <label>CGPA (0.0 - 10.0)</label>
            <input
              type="number"
              step="0.01"
              required
              value={formData.cgpa}
              onChange={(e) => setFormData({ ...formData, cgpa: e.target.value })}
              placeholder="e.g. 8.5"
            />
          </div>

          <button type="submit" disabled={loading}>
            {loading ? <div className="loader" style={{ width: "20px", height: "20px", borderSize: "2px" }}></div> : "Create Account"}
          </button>
        </form>

        <p style={{ textAlign: "center", marginTop: "1.5rem" }}>
          Already have an account? <Link to="/" style={{ color: "var(--primary-color)", fontWeight: "600" }}>Login</Link>
        </p>
      </div>
    </div>
  );
}
