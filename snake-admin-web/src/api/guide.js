import request from '../utils/request'

export const guidePage = (params) => request.get('/admin/guide/page', { params })
export const guideList = guidePage  // 别名：View 调 guideList()
export const addGuide = (data) => request.post('/admin/guide', data)
export const updateGuide = (data) => request.put('/admin/guide', data)
export const deleteGuide = (id) => request.delete(`/admin/guide/${id}`)
export const submitGuideAudit = (id) => request.post(`/admin/guide/${id}/submit-audit`)
export const auditGuide = (id, data) => request.post(`/admin/guide/${id}/audit`, data)
