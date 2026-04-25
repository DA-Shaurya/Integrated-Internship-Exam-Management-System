import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Internships from "./pages/Internships";
import ApplicationStatus from "./pages/ApplicationStatus";
import ResultHistory from "./pages/ResultHistory";
import Profile from "./pages/Profile";
import AdminDashboard from "./pages/AdminDashboard";
import QuestionBank from "./pages/QuestionBank";
import Exam from "./pages/Exam";
import Dashboard from "./pages/Dashboard";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/internships" element={<Internships />} />
        <Route path="/my-applications" element={<ApplicationStatus />} />
        <Route path="/my-results" element={<ResultHistory />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/admin-dashboard" element={<AdminDashboard />} />
        <Route path="/question-bank" element={<QuestionBank />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/exam" element={<Exam />} />
        {/* Fallback */}
        <Route path="*" element={<Internships />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;