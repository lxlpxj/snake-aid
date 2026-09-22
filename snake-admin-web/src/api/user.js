import request from '../utils/request'

// 登录
export const login = (data) => request.post('/admin/auth/login', data)

// 后台用户管理（仅 SYS_ADMIN）
export const getUserList = (params) => request.get('/admin/user/page', { params })
export const userPage = (params) => request.get('/admin/user/page', { params })
export const addUser = (data) => request.post('/admin/user', data)
export const createUser = addUser
export const updateUser = (data) => request.put('/admin/user', data)
export const updateUserStatus = (id, status) => request.put(`/admin/user/${id}/status`, null, { params: { status } })
export const deleteUser = (id) => request.delete(`/admin/user/${id}`)
export const resetUserPwd = (id) => request.put(`/admin/user/${id}/reset-password`)
