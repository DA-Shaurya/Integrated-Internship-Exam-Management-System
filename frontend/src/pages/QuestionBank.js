import React, { useEffect, useState } from "react";
import API from "../services/api";

export default function QuestionBank() {
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    questionText: "",
    marks: 1,
    options: [
      { optionText: "", correct: false },
      { optionText: "", correct: false },
      { optionText: "", correct: false },
      { optionText: "", correct: false }
    ]
  });

  useEffect(() => {
    fetchQuestions();
  }, []);

  const fetchQuestions = () => {
    API.get("/questions/all") // Need to add this endpoint
      .then(res => setQuestions(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  const handleEdit = (q) => {
    setEditingId(q.questionId);
    setFormData(q);
  };

  const handleSave = async () => {
    try {
      if (editingId) {
        await API.put(`/questions/${editingId}`, formData);
      } else {
        await API.post("/questions", formData);
      }
      setEditingId(null);
      fetchQuestions();
    } catch (err) {
      alert("Failed to save question");
    }
  };

  if (loading) return <div className="flex-center"><div className="loader"></div></div>;

  return (
    <div className="container animate-fade-in">
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "2rem" }}>
        <div>
          <h1>Question Bank</h1>
          <p>Maintain the integrity of your exams.</p>
        </div>
        <button onClick={() => { setEditingId("new"); setFormData({ questionText: "", marks: 1, options: [{optionText:"",correct:false},{optionText:"",correct:false},{optionText:"",correct:false},{optionText:"",correct:false}] }) }}>
          + New Question
        </button>
      </div>

      <div className="grid" style={{ gridTemplateColumns: "1fr 350px" }}>
        <div className="glass-panel" style={{ padding: 0 }}>
          {questions.map(q => (
            <div key={q.questionId} className="list-item" style={{ padding: "1.5rem", borderBottom: "1px solid var(--border-glass)", cursor: "pointer" }} onClick={() => handleEdit(q)}>
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                <h4 style={{ margin: 0 }}>{q.questionText}</h4>
                <span className="badge primary">{q.marks} Marks</span>
              </div>
              <p style={{ fontSize: "0.8rem", color: "var(--text-muted)", marginTop: "0.5rem" }}>
                {q.options.length} Options • Correct: {q.options.find(o => o.correct)?.optionText}
              </p>
            </div>
          ))}
        </div>

        <div>
          {editingId && (
            <div className="glass-panel animate-slide-up" style={{ position: "sticky", top: "2rem" }}>
              <h3>{editingId === "new" ? "Add Question" : "Edit Question"}</h3>
              <div className="form-group">
                <label>Question Text</label>
                <textarea 
                  rows="3" 
                  value={formData.questionText}
                  onChange={(e) => setFormData({ ...formData, questionText: e.target.value })}
                />
              </div>
              
              <div className="form-group">
                <label>Marks</label>
                <input 
                  type="number" 
                  value={formData.marks}
                  onChange={(e) => setFormData({ ...formData, marks: parseInt(e.target.value) })}
                />
              </div>

              <div className="form-group">
                <label>Options (Select correct one)</label>
                {formData.options.map((opt, idx) => (
                  <div key={idx} style={{ display: "flex", gap: "0.5rem", marginBottom: "0.5rem" }}>
                    <input 
                      type="radio" 
                      name="correct-opt" 
                      checked={opt.correct}
                      onChange={() => {
                        const newOpts = formData.options.map((o, i) => ({ ...o, correct: i === idx }));
                        setFormData({ ...formData, options: newOpts });
                      }}
                    />
                    <input 
                      type="text" 
                      value={opt.optionText}
                      onChange={(e) => {
                        const newOpts = [...formData.options];
                        newOpts[idx].optionText = e.target.value;
                        setFormData({ ...formData, options: newOpts });
                      }}
                      placeholder={`Option ${idx + 1}`}
                    />
                  </div>
                ))}
              </div>

              <div style={{ display: "flex", gap: "1rem", marginTop: "1.5rem" }}>
                <button onClick={handleSave}>Save</button>
                <button className="secondary" onClick={() => setEditingId(null)}>Cancel</button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
