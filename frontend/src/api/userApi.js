import axiosInstance from './axiosInstance.js';

/**
 * User API — maps to Spring Boot's /api/users/* endpoints.
 */
const userApi = {
  /** GET /api/users/me  — returns the currently logged-in user's profile */
  getMyProfile: () => axiosInstance.get('/users/me'),

  /** GET /api/users/:id  — ADMIN only */
  getById: (id) => axiosInstance.get(`/users/${id}`),

  /** GET /api/users  — ADMIN only */
  getAll: () => axiosInstance.get('/users'),

  /** DELETE /api/users/:id  — ADMIN only */
  delete: (id) => axiosInstance.delete(`/users/${id}`),
};

export default userApi;

