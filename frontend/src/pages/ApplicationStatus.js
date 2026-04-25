import React, { useEffect, useState } from "react";
import API from "../services/api";

const STATUS_STEPS = ["APPLIED", "SHORTLISTED", "SELECTED"];

export default function ApplicationStatus() {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    API.get("/applications/my")
      .then((res) => setApplications(res.data))
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  const renderTimeline = (currentStatus) => {
    if (currentStatus === "REJECTED") {
      return (
        <div className="timeline rejected">
          <div className="step completed">APPLIED</div>
          <div className="connector completed"></div>
          <div className="step rejected">REJECTED</div>
        </div>
      );
    }

    return (
      <div className="timeline">
        {STATUS_STEPS.map((step, idx) => {
          const isCompleted = STATUS_STEPS.indexOf(currentStatus) >= idx;
          return (
            <React.Fragment key={step}>
              <div className={`step ${isCompleted ? "completed" : ""}`}>{step}</div>
              {idx < STATUS_STEPS.length - 1 && (
                <div className={`connector ${STATUS_STEPS.indexOf(currentStatus) > idx ? "completed" : ""}`}></div>
              )}
            </React.Fragment>
          );
        })}
      </div>
    );
  };

  if (loading) return <div className="flex-center"><div className="loader"></div></div>;

  return (
    <div className="container animate-fade-in">
      <div style={{ marginBottom: "2rem" }}>
        <h1>My Applications</h1>
        <p>Track your journey with top companies.</p>
      </div>

      <div className="grid">
        {applications.length === 0 ? (
          <div className="glass-panel" style={{ textAlign: "center" }}>
            <p>You haven't applied for any internships yet.</p>
          </div>
        ) : (
          applications.map((app) => (
            <div key={app.applicationId} className="glass-panel">
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: "1.5rem" }}>
                <div>
                  <h3 style={{ color: "var(--primary-color)", marginBottom: "0.25rem" }}>{app.internshipTitle}</h3>
                  <p style={{ margin: 0, fontWeight: "600", color: "var(--text-main)" }}>{app.companyName}</p>
                </div>
                <div className="badge success">₹{app.stipend.toLocaleString()} / mo</div>
              </div>

              {renderTimeline(app.status)}

              <div style={{ marginTop: "1.5rem", fontSize: "0.85rem", color: "var(--text-muted)", display: "flex", justifyContent: "space-between" }}>
                <span>Applied on: {new Date(app.appliedAt).toLocaleDateString()}</span>
                <span className={`badge ${app.status === 'REJECTED' ? 'danger' : 'success'}`}>{app.status}</span>
              </div>
            </div>
          ))
        )}
      </div>

      <style>{`
        .timeline {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin: 2rem 0;
          padding: 0 1rem;
        }
        .step {
          font-size: 0.7rem;
          font-weight: 700;
          color: var(--text-muted);
          background: var(--bg-dark);
          padding: 8px 12px;
          border-radius: 8px;
          border: 1px solid var(--border-glass);
          position: relative;
          z-index: 2;
        }
        .step.completed {
          color: white;
          background: var(--primary-color);
          border-color: var(--primary-color);
          box-shadow: 0 0 15px rgba(99, 102, 241, 0.4);
        }
        .step.rejected {
          color: white;
          background: var(--danger);
          border-color: var(--danger);
        }
        .connector {
          flex: 1;
          height: 2px;
          background: var(--border-glass);
          margin: 0 -5px;
          position: relative;
          z-index: 1;
        }
        .connector.completed {
          background: var(--primary-color);
        }
      `}</style>
    </div>
  );
}
