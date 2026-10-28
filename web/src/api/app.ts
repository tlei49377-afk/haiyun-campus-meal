import { del, get, post, put } from '../utils/request'
import type { Canteen, Category, Dish, Order, OrderSubmitResult, PickupSlot, ShoppingCart, Stall } from './types'

export const studentLogin = (data: { studentNo: string; password: string }) =>
  post<{ token: string; name: string; studentNo: string; id: number }>('/app/user/login', data)
export const studentRegister = (data: { studentNo: string; name: string; phone?: string; password: string }) =>
  post<{ token: string; name: string; studentNo: string; id: number }>('/app/user/register', data)
export const listAppCanteens = () => get<Canteen[]>('/app/canteen/list')
export const listAppStalls = (canteenId: number) => get<Stall[]>('/app/stall/list', { canteenId })
export const listAppCategories = (stallId: number) => get<Category[]>('/app/category/list', { stallId })
export const listAppDishes = (categoryId: number) => get<Dish[]>('/app/dish/list', { categoryId })
export const getAppDish = (id: number) => get<Dish>(`/app/dish/${id}`)
export const listAppSlots = (stallId: number, slotDate: string) => get<PickupSlot[]>('/app/slot/list', { stallId, slotDate })
export const addCart = (dishId: number) => post<null>('/app/shoppingCart/add', { dishId })
export const listCart = () => get<ShoppingCart[]>('/app/shoppingCart/list')
export const cleanCart = () => del<null>('/app/shoppingCart/clean')
export const submitOrder = (data: { slotId: number; payMethod: number; remark?: string }) => post<OrderSubmitResult>('/app/order/submit', data)
export const payOrder = (data: { orderNumber: string; payMethod: number }) => put<unknown>('/app/order/payment', data)
export const listMyOrders = (page = 1, pageSize = 20) => get<{ total: number; records: Order[] }>('/app/order/historyOrders', { page, pageSize })

export const cancelMyOrder = (id: number) => put<null>(`/app/order/cancel/${id}`)
export const repeatMyOrder = (id: number) => post<null>(`/app/order/repetition/${id}`)
export const getMyOrderDetail = (id: number) => get<Order>(`/app/order/orderDetail/${id}`)
