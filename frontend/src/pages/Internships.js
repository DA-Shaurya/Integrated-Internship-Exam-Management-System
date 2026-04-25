import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import API from "../services/api";

export default function Internships() {
  const [data, setData] = useState([]);
  const [showExams, setShowExams] = useState(false);
  const [exams, setExams] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const user = JSON.parse(localStorage.getItem("user"));

  useEffect(() => {
    setLoading(true);
    API.get("/internships")
      .then(res => {
        setData(res.data.content || res.data);
      })
      .catch(err => console.error("Internships failed", err))
      .finally(() => setLoading(false));

    API.get("/exam/list")
      .then(res => setExams(res.data))
      .catch(err => console.error("Exams fetch failed", err));
  }, []);

  const apply = async (internshipId) => {
    try {
      await API.post("/applications/apply", {
        studentId: user?.userId,
        internshipId: internshipId
      });
      alert("Applied successfully");
    } catch (err) {
      alert("Already applied or error occurred.");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("user");
    localStorage.removeItem("token");
    navigate("/");
  };

  return (
    <div>
      <nav className="navbar">
        <div className="nav-brand">IEMS Portal</div>
        <div className="nav-links">
          <button className={`secondary ${!showExams ? 'active' : ''}`} onClick={() => setShowExams(false)}>Internships</button>
          <button className={`secondary ${showExams ? 'active' : ''}`} onClick={() => setShowExams(true)}>My Exams</button>
          <Link to="/my-applications"><button className="secondary">My Applications</button></Link>
          <Link to="/my-results"><button className="secondary">My Results</button></Link>
          <Link to="/profile"><button className="secondary">Profile</button></Link>
          <button className="secondary" onClick={handleLogout} style={{ color: 'var(--danger)' }}>Logout</button>
        </div>
      </nav>

      <div className="container animate-fade-in">
        {!showExams ? (
          <>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
              <h2>Available Internships</h2>
            </div>

            <div className="grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))' }}>
              {data.map(i => (
                <div key={i.internshipId} className="glass-panel" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column' }}>
                  <div style={{ flex: 1 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                      <h3 style={{ margin: 0, fontSize: '1.25rem', color: 'var(--primary-color)' }}>{i.role}</h3>
                      <span className="badge" style={{ background: 'rgba(99, 102, 241, 0.2)', color: 'var(--primary-color)' }}>
                        ₹{i.stipend}/mo
                      </span>
                    </div>
                    
                    <div style={{ marginBottom: '1.5rem' }}>
                      <p style={{ margin: '0 0 0.5rem 0', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        🏢 {i.companyName || "Unknown Company"}
                      </p>
                      <p style={{ margin: '0', fontSize: '0.875rem' }}>
                        📍 {i.location || "N/A"}
                      </p>
                      <p style={{ margin: '0', fontSize: '0.875rem' }}>
                        📅 Deadline: {i.deadline || "Open"}
                      </p>
                    </div>
                  </div>

                  <button onClick={() => apply(i.internshipId)} style={{ margin: 0 }}>
                    Apply Now
                  </button>
                </div>
              ))}
            </div>
          </>
        ) : (
          <>
            <div style={{ marginBottom: '2rem' }}>
              <h2>Available Exams</h2>
              <p>Complete these to improve your profile.</p>
            </div>
            <div className="grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))' }}>
              {exams.map(e => (
                <div key={e.examId} className="glass-panel" style={{ padding: '1.5rem' }}>
                  <h3 style={{ color: 'var(--primary-color)' }}>{e.examName}</h3>
                  <p>⏱ Duration: {e.durationMinutes || e.duration || 0} mins</p>
                  <button onClick={() => navigate("/exam", { state: { examId: e.examId } })} style={{ marginTop: '1rem' }}>
                    Start Exam
                  </button>
                </div>
              ))}
            </div>
          </>
        )}
      </div>
    </div>
  );
}