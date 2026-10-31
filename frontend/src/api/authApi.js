import axiosInstance from './axiosInstance.js';

/**
 * Auth API — maps directly to Spring Boot's /api/auth/* endpoints.
 */
const authApi = {
  /**
   * POST /api/auth/register
   * @param {{ username, email, password, firstName, lastName, phone?, address? }} data
   */
  register: (data) => axiosInstance.post('/auth/register', data),

  /**
   * POST /api/auth/login
   * @param {{ usernameOrEmail, password }} data
   */
  login: (data) => axiosInstance.post('/auth/login', data),

  /**
   * POST /api/auth/refresh
   * @param {string} refreshToken
   */
  refresh: (refreshToken) =>
    axiosInstance.post('/auth/refresh', { refreshToken }),

  /**
   * POST /api/auth/logout  (requires JWT in header)
   */
  logout: () => axiosInstance.post('/auth/logout'),
};

export default authApi;

