import React, { useEffect, useState } from "react";
import API from "../services/api";

export default function AdminDashboard() {
  const [stats, setStats] = useState({
    totalApplications: 0,
    activeInternships: 0,
    totalStudents: 0,
    pendingReviews: 0
  });
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);

  useEffect(() => {
    Promise.all([
      API.get("/admin/stats"),
      API.get(`/admin/applications?page=${page}&size=10`)
    ]).then(([statsRes, appsRes]) => {
      setStats(statsRes.data);
      setApplications(appsRes.data.content);
    }).catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, [page]);

  const updateStatus = async (id, newStatus) => {
    try {
      await API.put(`/applications/${id}/status`, { status: newStatus });
      setApplications(applications.map(app => 
        app.applicationId === id ? { ...app, status: newStatus } : app
      ));
    } catch (err) {
      alert("Failed to update status");
    }
  };

  if (loading) return <div className="flex-center"><div className="loader"></div></div>;

  return (
    <div className="container animate-fade-in">
      <div style={{ marginBottom: "2rem" }}>
        <h1>Admin Command Center</h1>
        <p>Monitor platform growth and manage applications.</p>
      </div>

      <div className="grid" style={{ gridTemplateColumns: "repeat(4, 1fr)", marginBottom: "3rem" }}>
        <div className="glass-panel kpi-card">
          <p>Total Apps</p>
          <h2>{stats.totalApplications}</h2>
        </div>
        <div className="glass-panel kpi-card">
          <p>Active Internships</p>
          <h2 style={{ color: "var(--success)" }}>{stats.activeInternships}</h2>
        </div>
        <div className="glass-panel kpi-card">
          <p>Students</p>
          <h2>{stats.totalStudents}</h2>
        </div>
        <div className="glass-panel kpi-card">
          <p>Pending Review</p>
          <h2 style={{ color: "var(--warning)" }}>{stats.pendingReviews}</h2>
        </div>
      </div>

      <div className="glass-panel" style={{ padding: 0 }}>
        <div style={{ padding: "1.5rem", borderBottom: "1px solid var(--border-glass)" }}>
          <h3 style={{ margin: 0 }}>Application Management</h3>
        </div>
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead style={{ background: "rgba(255,255,255,0.05)" }}>
            <tr>
              <th style={{ padding: "1rem", textAlign: "left" }}>Student</th>
              <th style={{ padding: "1rem", textAlign: "left" }}>Internship</th>
              <th style={{ padding: "1rem", textAlign: "center" }}>CGPA</th>
              <th style={{ padding: "1rem", textAlign: "center" }}>Status</th>
              <th style={{ padding: "1rem", textAlign: "right" }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {applications.map((app) => (
              <tr key={app.applicationId} style={{ borderTop: "1px solid var(--border-glass)" }}>
                <td style={{ padding: "1rem" }}>
                  <div style={{ fontWeight: "600" }}>{app.studentName}</div>
                  <div style={{ fontSize: "0.8rem", color: "var(--text-muted)" }}>{app.studentEmail}</div>
                </td>
                <td style={{ padding: "1rem" }}>
                  <div style={{ fontWeight: "600" }}>{app.internshipTitle}</div>
                  <div style={{ fontSize: "0.8rem", color: "var(--text-muted)" }}>{app.companyName}</div>
                </td>
                <td style={{ padding: "1rem", textAlign: "center" }}>{app.cgpaAtApply}</td>
                <td style={{ padding: "1rem", textAlign: "center" }}>
                  <span className={`badge ${app.status === 'APPLIED' ? 'warning' : app.status === 'REJECTED' ? 'danger' : 'success'}`}>
                    {app.status}
                  </span>
                </td>
                <td style={{ padding: "1rem", textAlign: "right" }}>
                  <select 
                    value={app.status} 
                    onChange={(e) => updateStatus(app.applicationId, e.target.value)}
                    className="status-select"
                  >
                    <option value="APPLIED">Applied</option>
                    <option value="SHORTLISTED">Shortlist</option>
                    <option value="REJECTED">Reject</option>
                    <option value="HIRED">Hire</option>
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <style>{`
        .kpi-card { text-align: center; }
        .kpi-card p { margin: 0; font-size: 0.85rem; color: var(--text-muted); }
        .kpi-card h2 { margin: 0.5rem 0 0; font-size: 2rem; }
        .status-select {
          background: var(--bg-dark);
          color: white;
          border: 1px solid var(--border-glass);
          padding: 4px 8px;
          border-radius: 4px;
          font-size: 0.8rem;
          outline: none;
        }
      `}</style>
    </div>
  );
}
