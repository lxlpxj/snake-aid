import request from '../utils/request'

export const getLogPage = (params) => request.get('/admin/log/page', { params })
