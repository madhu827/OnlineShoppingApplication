import axiosInstance from './axiosInstance.js';

/**
 * Product API — maps to Spring Boot's /api/products/* endpoints.
 * All Cassandra-backed operations.
 */
const productApi = {
  /** GET /api/products/active  — public */
  getAllActive: () => axiosInstance.get('/products/active'),

  /** GET /api/products/category/:category  — public */
  getByCategory: (category) =>
    axiosInstance.get(`/products/category/${category}`),

  /** GET /api/products/category/:category/:id  — public */
  getById: (category, id) =>
    axiosInstance.get(`/products/category/${category}/${id}`),

  /** GET /api/products/category/:category/brand/:brand  — public */
  getByBrand: (category, brand) =>
    axiosInstance.get(`/products/category/${category}/brand/${brand}`),

  /** POST /api/products  — ADMIN only */
  create: (data) => axiosInstance.post('/products', data),

  /** PUT /api/products/category/:category/:id  — ADMIN only */
  update: (category, id, data) =>
    axiosInstance.put(`/products/category/${category}/${id}`, data),

  /** DELETE /api/products/category/:category/:id  — ADMIN only */
  delete: (category, id) =>
    axiosInstance.delete(`/products/category/${category}/${id}`),
};

export default productApi;

