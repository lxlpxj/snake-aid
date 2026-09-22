import request from '../utils/request'

export const articlePage = (params) => request.get('/admin/article/page', { params })
export const articleList = articlePage
export const addArticle = (data) => request.post('/admin/article', data)
export const updateArticle = (data) => request.put('/admin/article', data)
export const deleteArticle = (id) => request.delete(`/admin/article/${id}`)
export const submitArticleAudit = (id) => request.post(`/admin/article/${id}/submit-audit`)
export const auditArticle = (id, data) => request.post(`/admin/article/${id}/audit`, data)
