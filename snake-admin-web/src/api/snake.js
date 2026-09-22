import request from '../utils/request'

export const snakePage = (params) => request.get('/admin/snake/page', { params })
export const snakeList = snakePage  // 别名
export const snakeDetail = (id) => request.get(`/admin/snake/${id}`)
export const addSnake = (data) => request.post('/admin/snake', data)
export const updateSnake = (data) => request.put('/admin/snake', data)
export const deleteSnake = (id) => request.delete(`/admin/snake/${id}`)
export const submitSnakeAudit = (id) => request.post(`/admin/snake/${id}/submit-audit`)
export const auditSnake = (id, data) => request.post(`/admin/snake/${id}/audit`, data)
