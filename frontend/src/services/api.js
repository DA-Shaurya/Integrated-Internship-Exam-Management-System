import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8081/api",
  withCredentials: true,  // ← Required: sends HttpOnly refresh cookie automatically
});

// ── Helper: get access token from localStorage ───────────────────────────────
const getAccessToken = () => {
  return localStorage.getItem("token");
};

// ── Request interceptor: attach JWT Bearer token ─────────────────────────────
API.interceptors.request.use(
  (config) => {
    const token = getAccessToken();
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ── Track if a refresh is already in progress (prevent multiple parallel refresh calls)
let isRefreshing = false;
let refreshSubscribers = [];

const subscribeTokenRefresh = (cb) => refreshSubscribers.push(cb);
const onRefreshed = (newToken) => {
  refreshSubscribers.forEach((cb) => cb(newToken));
  refreshSubscribers = [];
};

// ── Response interceptor: 401 → try refresh → retry original request ─────────
API.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // 401 on any endpoint except auth endpoints → try to refresh
    if (
      error.response?.status === 401 &&
      !originalRequest._retry &&
      !originalRequest.url.includes("/auth/")
    ) {
      if (isRefreshing) {
        // Another request already triggered refresh — queue this one
        return new Promise((resolve) => {
          subscribeTokenRefresh((newToken) => {
            originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
            resolve(API(originalRequest));
          });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const res = await API.post("/auth/refresh");
        const newToken = res.data.token;

        // Update stored token
        localStorage.setItem("token", newToken);

        // Notify all queued requests
        onRefreshed(newToken);
        isRefreshing = false;

        // Retry the original failed request with new token
        originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
        return API(originalRequest);
      } catch (refreshError) {
        // Refresh failed — session is dead, force logout
        isRefreshing = false;
        refreshSubscribers = [];
        localStorage.removeItem("user");
        window.location.href = "/";
        return Promise.reject(refreshError);
      }
    }

    // 429 — rate limited
    if (error.response?.status === 429) {
      error.message = error.response.data?.message || "Too many attempts. Please wait.";
    }

    return Promise.reject(error);
  }
);

export default API;