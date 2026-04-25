import React, { useEffect, useState } from "react";
import API from "../services/api";

export default function ResultHistory() {
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    API.get("/results/my")
      .then((res) => setResults(res.data))
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  const stats = {
    taken: results.length,
    passed: results.filter(r => r.status === "PASS").length,
    avg: results.length > 0 ? (results.reduce((acc, r) => acc + r.percentage, 0) / results.length).toFixed(1) : 0
  };

  if (loading) return <div className="flex-center"><div className="loader"></div></div>;

  return (
    <div className="container animate-fade-in">
      <div style={{ marginBottom: "2rem" }}>
        <h1>Exam History</h1>
        <p>Review your academic performance and certifications.</p>
      </div>

      <div className="grid" style={{ gridTemplateColumns: "repeat(3, 1fr)", marginBottom: "2rem" }}>
        <div className="glass-panel" style={{ textAlign: "center" }}>
          <h2 style={{ margin: 0, color: "var(--primary-color)" }}>{stats.taken}</h2>
          <p style={{ margin: 0, fontSize: "0.85rem" }}>Exams Taken</p>
        </div>
        <div className="glass-panel" style={{ textAlign: "center" }}>
          <h2 style={{ margin: 0, color: "var(--success)" }}>{stats.passed}</h2>
          <p style={{ margin: 0, fontSize: "0.85rem" }}>Exams Passed</p>
        </div>
        <div className="glass-panel" style={{ textAlign: "center" }}>
          <h2 style={{ margin: 0, color: "var(--primary-color)" }}>{stats.avg}%</h2>
          <p style={{ margin: 0, fontSize: "0.85rem" }}>Avg. Score</p>
        </div>
      </div>

      <div className="glass-panel" style={{ padding: 0, overflow: "hidden" }}>
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead style={{ background: "rgba(255,255,255,0.05)" }}>
            <tr>
              <th style={{ padding: "1rem", textAlign: "left" }}>Exam</th>
              <th style={{ padding: "1rem", textAlign: "left" }}>Date</th>
              <th style={{ padding: "1rem", textAlign: "center" }}>Score</th>
              <th style={{ padding: "1rem", textAlign: "center" }}>Percentage</th>
              <th style={{ padding: "1rem", textAlign: "center" }}>Status</th>
            </tr>
          </thead>
          <tbody>
            {results.length === 0 ? (
              <tr>
                <td colSpan="5" style={{ padding: "2rem", textAlign: "center", color: "var(--text-muted)" }}>
                  No results found yet. Take an exam to see your history!
                </td>
              </tr>
            ) : (
              results.map((res) => (
                <tr key={res.resultId} style={{ borderTop: "1px solid var(--border-glass)" }}>
                  <td style={{ padding: "1rem", fontWeight: "600" }}>{res.examName}</td>
                  <td style={{ padding: "1rem" }}>{new Date(res.attemptDate).toLocaleDateString()}</td>
                  <td style={{ padding: "1rem", textAlign: "center" }}>{res.obtainedMarks} / {res.totalMarks}</td>
                  <td style={{ padding: "1rem", textAlign: "center" }}>{res.percentage.toFixed(1)}%</td>
                  <td style={{ padding: "1rem", textAlign: "center" }}>
                    <span className={`badge ${res.status === "PASS" ? "success" : "danger"}`}>
                      {res.status}
                    </span>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
