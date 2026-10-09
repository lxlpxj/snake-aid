import request from '../utils/request'

export const stepPage = (params) => request.get('/admin/first-aid-step/page', { params })
export const stepList = (params) => request.get('/admin/first-aid-step/list', { params })
export const stepGet = (id) => request.get(`/admin/first-aid-step/${id}`)
export const stepCreate = (data) => request.post('/admin/first-aid-step', data)
export const stepUpdate = (data) => request.put('/admin/first-aid-step', data)
export const stepDelete = (id) => request.delete(`/admin/first-aid-step/${id}`)
export const stepToggle = (id) => request.post(`/admin/first-aid-step/${id}/toggle`)
