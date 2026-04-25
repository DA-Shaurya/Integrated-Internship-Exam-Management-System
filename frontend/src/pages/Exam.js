import { useEffect, useState, useCallback, useRef } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import API from "../services/api";

export default function Exam() {
  const [questions, setQuestions] = useState([]);
  const [selectedAnswers, setSelectedAnswers] = useState({});
  const [attemptId, setAttemptId] = useState(null);
  const [examDetails, setExamDetails] = useState(null);
  const [timeLeft, setTimeLeft] = useState(null);
  const [warnings, setWarnings] = useState(0);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const timerRef = useRef(null);

  const userRef = useRef(JSON.parse(localStorage.getItem("user")));
  const user = userRef.current;

  // ── Tab Switch Detection ─────────────────────────────────────────────────────
  useEffect(() => {
    const handleVisibilityChange = () => {
      if (document.hidden && !isSubmitting) {
        setWarnings(w => {
          const newWarnings = w + 1;
          API.post("/audit", {
            userId: user?.userId,
            action: "TAB_SWITCH",
            details: `User switched tab during exam. Warning count: ${newWarnings}`
          });
          
          if (newWarnings >= 3) {
            alert("Exam submitted automatically due to multiple tab switches.");
            submitExam();
          } else {
            alert(`Warning! Tab switching is not allowed. Warning ${newWarnings}/3`);
          }
          return newWarnings;
        });
      }
    };
    document.addEventListener("visibilitychange", handleVisibilityChange);
    return () => document.removeEventListener("visibilitychange", handleVisibilityChange);
  }, [user, isSubmitting]);

  // ── Timer Logic ─────────────────────────────────────────────────────────────
  useEffect(() => {
    if (timeLeft === null) return;
    
    if (timeLeft <= 0) {
      submitExam();
      return;
    }

    timerRef.current = setInterval(() => {
      setTimeLeft(t => t - 1);
    }, 1000);

    return () => clearInterval(timerRef.current);
  }, [timeLeft]);

  // ── Initialize Exam ─────────────────────────────────────────────────────────
  useEffect(() => {
    if (!user) {
        navigate("/");
        return;
    }

    const targetExamId = location.state?.examId;
    if (!targetExamId) {
        alert("No exam selected!");
        navigate("/dashboard");
        return;
    }

    const initExam = async () => {
        try {
            // Start or Resume Attempt
            const startRes = await API.post("/exam/start", {
                userId: user.userId,
                examId: targetExamId
            });
            
            const attempt = startRes.data;
            setAttemptId(attempt.attemptId);
            setExamDetails(attempt.exam);

            // Calculate remaining time based on backend startTime
            const startTime = new Date(attempt.startTime).getTime();
            const duration = attempt.exam?.durationMinutes || attempt.exam?.duration || 60;
            const durationMs = duration * 60 * 1000;
            const elapsedMs = Date.now() - startTime;
            const remainingSec = Math.max(0, Math.floor((durationMs - elapsedMs) / 1000));
            
            setTimeLeft(remainingSec);

            // Fetch Random/Shuffled Questions
            const qRes = await API.get(`/questions/exam/${targetExamId}`);
            setQuestions(qRes.data);
            
        } catch (err) {
            console.error("Initialization failed", err);
            alert("Could not start exam session. Please try again.");
            navigate("/dashboard");
        }
    };

    initExam();
  }, [navigate]); // Only on mount/navigate

  // ── Auto-Save ───────────────────────────────────────────────────────────────
  const saveAnswer = (questionId, optionId) => {
    setSelectedAnswers(prev => ({ ...prev, [questionId]: optionId }));
    if (!attemptId) return;
    API.post("/answers/save", {
      attemptId,
      questionId,
      selectedOption: optionId
    }).catch(err => console.error("Auto-save failed", err));
  };

  // ── Submit ──────────────────────────────────────────────────────────────────
  const submitExam = useCallback(async () => {
    if (isSubmitting) return;
    setIsSubmitting(true);
    clearInterval(timerRef.current);

    try {
      const res = await API.post("/exam/submit", { attemptId });
      alert(`Exam Submitted! Your Score: ${res.data?.obtainedMarks} / ${res.data?.totalMarks}`);
      navigate("/dashboard");
    } catch (err) {
      console.error(err);
      alert("Submission failed. Your progress is saved, please contact support if issues persist.");
      navigate("/dashboard");
    }
  }, [attemptId, navigate, isSubmitting]);

  const formatTime = (seconds) => {
    if (seconds === null) return "--:--";
    const m = Math.floor(seconds / 60).toString().padStart(2, '0');
    const s = (seconds % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  };

  if (!questions.length && !isSubmitting) {
    return (
      <div className="flex-center" style={{ flexDirection: 'column', gap: '1rem' }}>
        <div className="loader"></div>
        <p>Initializing secure exam session...</p>
      </div>
    );
  }

  return (
    <div className="exam-page">
      <nav className="navbar" style={{ position: 'sticky', top: 0, zIndex: 100 }}>
        <div className="nav-brand">IEMS Exam Module</div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
          <span className={`badge ${warnings > 0 ? 'danger' : 'success'}`}>
            Warnings: {warnings}/3
          </span>
          <div className={`timer ${timeLeft < 300 ? 'urgent' : ''}`} style={{ 
              fontSize: '1.5rem', 
              fontWeight: '700', 
              color: timeLeft < 300 ? '#ef4444' : '#10b981',
              fontFamily: 'monospace'
          }}>
            {formatTime(timeLeft)}
          </div>
        </div>
      </nav>

      <div className="container" style={{ maxWidth: "900px", padding: "2rem 1rem" }}>
        <div className="glass-panel" style={{ padding: "2.5rem" }}>
          <div style={{ marginBottom: "2.5rem" }}>
            <h1 style={{ fontSize: "1.8rem", marginBottom: "0.5rem" }}>{examDetails?.examName || "Exam"}</h1>
            <p style={{ color: "var(--text-dim)" }}>Please do not refresh the page or switch tabs.</p>
          </div>

          <div className="questions-list">
            {questions.map((q, index) => (
              <div key={q.questionId} className="question-card" style={{ 
                  marginBottom: "2.5rem", 
                  padding: "2rem", 
                  background: "rgba(255, 255, 255, 0.03)", 
                  borderRadius: "16px",
                  border: "1px solid rgba(255, 255, 255, 0.05)"
              }}>
                <div style={{ display: 'flex', gap: '1rem', marginBottom: '1.5rem' }}>
                  <span style={{ fontWeight: 'bold', color: 'var(--primary)' }}>{index + 1}.</span>
                  <h3 style={{ fontSize: "1.2rem", fontWeight: "500", lineHeight: "1.5" }}>
                    {q.questionText}
                  </h3>
                </div>

                <div className="options-grid" style={{ 
                    display: "grid", 
                    gridTemplateColumns: "1fr", 
                    gap: "1rem" 
                }}>
                  {q.options?.map(opt => (
                    <label key={opt.optionId} className="option-label" style={{ 
                        display: "flex", 
                        alignItems: "center", 
                        padding: "1rem 1.5rem", 
                        background: "rgba(255, 255, 255, 0.05)", 
                        borderRadius: "12px", 
                        cursor: "pointer", 
                        transition: "all 0.2s ease",
                        border: "1px solid transparent"
                    }}>
                      <input
                        type="radio"
                        name={"q" + q.questionId}
                        checked={selectedAnswers[q.questionId] === opt.optionId}
                        onChange={() => saveAnswer(q.questionId, opt.optionId)}
                        style={{ width: "20px", height: "20px", marginRight: "1.5rem", accentColor: "var(--primary)" }}
                      />
                      <span style={{ fontSize: "1.05rem" }}>{opt.optionText}</span>
                    </label>
                  ))}
                </div>
              </div>
            ))}
          </div>

          <div style={{ 
              display: "flex", 
              justifyContent: "space-between", 
              alignItems: "center",
              marginTop: "4rem",
              paddingTop: "2rem",
              borderTop: "1px solid rgba(255, 255, 255, 0.1)"
          }}>
            <p style={{ color: "var(--text-dim)", fontSize: "0.9rem" }}>
                Make sure you have answered all questions before submitting.
            </p>
            <button 
                onClick={submitExam} 
                disabled={isSubmitting}
                className="btn-primary"
                style={{ 
                    padding: "1rem 3rem", 
                    fontSize: "1.1rem",
                    boxShadow: "0 10px 15px -3px rgba(0, 0, 0, 0.1)"
                }}
            >
              {isSubmitting ? "Submitting..." : "Finish & Submit"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}