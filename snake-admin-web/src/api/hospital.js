import request from '../utils/request'

// 医院 CRUD
export const hospitalPage = (params) => request.get('/admin/hospital/page', { params })
export const hospitalDetail = (id) => request.get(`/admin/hospital/${id}`)
export const addHospital = (data) => request.post('/admin/hospital', data)
export const updateHospital = (data) => request.put('/admin/hospital', data)
export const deleteHospital = (id) => request.delete(`/admin/hospital/${id}`)

// 血清库存 —— 单一来源
export const serumList = (hospitalId) => request.get(`/admin/hospital/${hospitalId}/serum`)
export const createSerum = (hospitalId, data) => request.post(`/admin/hospital/${hospitalId}/serum`, data)
export const updateSerum = (data) => request.put('/admin/hospital/serum', data)
export const deleteSerum = (id) => request.delete(`/admin/hospital/serum/${id}`)
