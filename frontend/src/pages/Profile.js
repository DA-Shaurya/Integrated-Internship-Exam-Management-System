import React, { useEffect, useState } from "react";
import API from "../services/api";

export default function Profile() {
  const [profile, setProfile] = useState({
    name: "",
    email: "",
    phone: "",
    cgpa: 0,
    skills: [],
    resumeFilename: ""
  });
  const [newSkill, setNewSkill] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    API.get("/students/profile")
      .then((res) => setProfile(res.data))
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  const handleSave = async () => {
    setSaving(true);
    try {
      await API.put("/students/profile", profile);
      alert("Profile updated successfully!");
    } catch (err) {
      alert("Failed to update profile");
    } finally {
      setSaving(false);
    }
  };

  const addSkill = () => {
    if (newSkill && !profile.skills.includes(newSkill)) {
      setProfile({ ...profile, skills: [...profile.skills, newSkill] });
      setNewSkill("");
    }
  };

  const removeSkill = (skill) => {
    setProfile({ ...profile, skills: profile.skills.filter(s => s !== skill) });
  };

  if (loading) return <div className="flex-center"><div className="loader"></div></div>;

  return (
    <div className="container animate-fade-in" style={{ maxWidth: "800px" }}>
      <div style={{ marginBottom: "2rem" }}>
        <h1>My Profile</h1>
        <p>Keep your professional information up to date.</p>
      </div>

      <div className="glass-panel">
        <div className="grid" style={{ gridTemplateColumns: "1fr 1fr", gap: "2rem" }}>
          <div>
            <div className="form-group">
              <label>Full Name</label>
              <input
                type="text"
                value={profile.name}
                onChange={(e) => setProfile({ ...profile, name: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>Email (Read-only)</label>
              <input type="email" value={profile.email} disabled />
            </div>
            <div className="form-group">
              <label>Phone Number</label>
              <input
                type="text"
                value={profile.phone}
                onChange={(e) => setProfile({ ...profile, phone: e.target.value })}
                placeholder="+91 98765 43210"
              />
            </div>
          </div>

          <div>
            <div className="form-group">
              <label>Current CGPA</label>
              <input
                type="number"
                step="0.01"
                value={profile.cgpa}
                onChange={(e) => setProfile({ ...profile, cgpa: parseFloat(e.target.value) })}
              />
            </div>
            
            <div className="form-group">
              <label>Skills</label>
              <div style={{ display: "flex", gap: "0.5rem", marginBottom: "1rem" }}>
                <input
                  type="text"
                  value={newSkill}
                  onChange={(e) => setNewSkill(e.target.value)}
                  placeholder="e.g. React"
                  onKeyPress={(e) => e.key === 'Enter' && addSkill()}
                />
                <button className="secondary" onClick={addSkill} style={{ width: "auto" }}>Add</button>
              </div>
              <div style={{ display: "flex", flexWrap: "wrap", gap: "0.5rem" }}>
                {profile.skills.map(skill => (
                  <div key={skill} className="badge primary" style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
                    {skill}
                    <span 
                      onClick={() => removeSkill(skill)} 
                      style={{ cursor: "pointer", fontSize: "1.2rem", lineHeight: 1 }}
                    >&times;</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>

        <div style={{ marginTop: "2rem", paddingTop: "2rem", borderTop: "1px solid var(--border-glass)" }}>
          <button onClick={handleSave} disabled={saving}>
            {saving ? "Saving Changes..." : "Save Profile"}
          </button>
        </div>
      </div>
    </div>
  );
}
