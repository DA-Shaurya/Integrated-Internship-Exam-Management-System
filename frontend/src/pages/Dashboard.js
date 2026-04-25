import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AdminDashboard from "./AdminDashboard";
import Internships from "./Internships";

export default function Dashboard() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user"));

  useEffect(() => {
    if (!user) {
      navigate("/");
    }
  }, [user, navigate]);

  if (!user) return null;

  // SaaS RBAC: Route to different dashboards based on role
  if (user.role === "ADMIN") {
    return <AdminDashboard />;
  }

  return <Internships />;
}
