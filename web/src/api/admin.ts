import { del, get, post, put } from '../utils/request'
import type { Canteen, Dish, Employee, Order, OrderDetail, Permission, PickupSlot, Role, Stall } from './types'

export const adminLogin = (data: { username: string; password: string }) => post<{ token: string; userName: string; name: string; permissions: string[]; id: number }>('/admin/employee/login', data)

export const listCanteens = () => get<Canteen[]>('/admin/canteen/list')
export const saveCanteen = (data: Partial<Canteen>) => data.id ? put<null>('/admin/canteen', data) : post<null>('/admin/canteen', data)
export const listStalls = (canteenId?: number) => get<Stall[]>('/admin/stall/list', { canteenId })
export const saveStall = (data: Partial<Stall>) => data.id ? put<null>('/admin/stall', data) : post<null>('/admin/stall', data)
export const listSlots = (params: { stallId?: number; slotDate?: string }) => get<PickupSlot[]>('/admin/slot/list', params)
export const generateSlots = (data: Record<string, unknown>) => post<number>('/admin/slot/generate', data)
export const updateSlot = (data: Record<string, unknown>) => put<null>('/admin/slot', data)
export const deleteSlot = (id: number) => del<null>(`/admin/slot/${id}`)

export const listOrders = (params: Record<string, unknown>) => get<{ total: number; records: Order[] }>('/admin/order/conditionSearch', params)
export const orderStatistics = () => get<{ toBeConfirmed: number; making: number; readyForPickup: number }>('/admin/order/statistics')
export const confirmOrder = (id: number) => put<null>('/admin/order/confirm', { id })
export const readyOrder = (id: number) => put<null>(`/admin/order/ready/${id}`)
export const verifyPickup = (pickupCode: string) => post<{ message: string; orderNumber: string; pickupCode: string }>('/admin/order/pickup/verify', { pickupCode })
export const orderDetail = (id: number) => get<Order & { orderDetailList?: OrderDetail[] }>(`/admin/order/details/${id}`)
export const cancelOrder = (id: number, cancelReason: string) => put<null>('/admin/order/cancel', { id, cancelReason })
export const rejectOrder = (id: number, rejectionReason: string) => put<null>('/admin/order/rejection', { id, rejectionReason })

export const listCategoryPage = (type = 1) => get<{ total: number; records: Array<{ id: number; stallId: number; name: string; type: number; status: number; sort?: number; categoryName?: string }> }>('/admin/category/page', { page: 1, pageSize: 100, type })
export const saveCategory = (data: Record<string, unknown>) => data.id ? put<null>('/admin/category', data) : post<null>('/admin/category', data)
export const deleteCategory = (id: number) => del<null>(`/admin/category?id=${id}`)
export const toggleCategoryStatus = (id: number, status: number) => post<null>(`/admin/category/status/${status}?id=${id}`)

export const listDishs = (categoryId?: number) => get<Dish[]>('/admin/dish/list', { categoryId })
export const listDishPage = (params: { page: number; pageSize: number; categoryId?: number; name?: string; status?: number }) => get<{ total: number; records: Dish[] }>('/admin/dish/page', params)
export const getDish = (id: number) => get<Dish>(`/admin/dish/${id}`)
export const saveDish = (data: Record<string, unknown>) => data.id ? put<null>('/admin/dish', data) : post<null>('/admin/dish', data)
export const deleteDish = (id: number) => del<null>(`/admin/dish?ids=${id}`)
export const toggleDishStatus = (id: number, status: number) => post<null>(`/admin/dish/status/${status}?id=${id}`)

export const listEmployees = () => get<{ total: number; records: Employee[] }>('/admin/employee/page', { page: 1, pageSize: 50 })
export const saveEmployee = (data: Record<string, unknown>) => data.id ? put<null>('/admin/employee', data) : post<null>('/admin/employee', data)
export const toggleEmployeeStatus = (id: number, status: number) => post<null>(`/admin/employee/status/${status}?id=${id}`)

export const listRoles = () => get<Role[]>('/admin/role/list')
export const createRole = (data: Record<string, unknown>) => post<null>('/admin/role', data)
export const listPermissions = () => get<Permission[]>('/admin/permission/list')
export const listRolePermissions = (id: number) => get<number[]>(`/admin/role/${id}/permissions`)
export const assignRolePermissions = (id: number, permissionIds: number[]) => put<null>(`/admin/role/${id}/permissions`, { permissionIds })

export const turnoverReport = (begin: string, end: string) => get<{ dateList: string; turnoverList: string }>('/admin/report/turnoverStatistics', { begin, end })
export const ordersReport = (begin: string, end: string) => get<{ dateList: string; orderCountList: string; validOrderCountList: string; totalOrderCount: number; validOrderCount: number; orderCompletionRate: number }>('/admin/report/ordersStatistics', { begin, end })
export const userReport = (begin: string, end: string) => get<{ dateList: string; totalUserList: string; newUserList: string }>('/admin/report/userStatistics', { begin, end })
export const top10Report = (begin: string, end: string) => get<{ nameList: string; numberList: string }>('/admin/report/top10', { begin, end })

export const getBusinessData = () => get<{ turnover: number; validOrderCount: number; orderCompletionRate: number; unitPrice: number; newUsers: number }>('/admin/workspace/businessData')
export const getOrderOverview = () => get<{ waitingOrders: number; makingOrders: number; completedOrders: number; cancelledOrders: number; allOrders: number }>('/admin/workspace/overviewOrders')
export const getDishOverview = () => get<{ sold: number; discontinued: number }>('/admin/workspace/overviewDishes')
export const getSetmealOverview = () => get<{ sold: number; discontinued: number }>('/admin/workspace/overviewSetmeals')

// Backward-compatible names used by standalone category and combo pages.
export const categoryPage = (params: Record<string, unknown>) => get<{ total: number; records: Array<Record<string, unknown>> }>('/admin/category/page', params)
export const createCategory = (data: Record<string, unknown>) => post<null>('/admin/category', data)
export const updateCategory = (data: Record<string, unknown>) => put<null>('/admin/category', data)
export const setmealPage = (params: Record<string, unknown>) => get<{ total: number; records: Array<Record<string, unknown>> }>('/admin/setmeal/page', params)
export const getSetmeal = (id: number) => get<Record<string, unknown>>(`/admin/setmeal/${id}`)
export const createSetmeal = (data: Record<string, unknown>) => post<null>('/admin/setmeal', data)
export const updateSetmeal = (data: Record<string, unknown>) => put<null>('/admin/setmeal', data)
export const deleteSetmeals = (ids: number[]) => del<null>('/admin/setmeal', { ids: ids.join(',') })
export const toggleSetmealStatus = (id: number, status: number) => post<null>(`/admin/setmeal/status/${status}?id=${id}`)
