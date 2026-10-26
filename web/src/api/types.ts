export interface Canteen {
  id: number
  name: string
  address?: string
  openTime?: string
  closeTime?: string
  status: number
  sort?: number
}

export interface Stall {
  id: number
  canteenId: number
  name: string
  description?: string
  location?: string
  status: number
  sort?: number
}

export interface Category {
  id: number
  stallId: number
  type: number
  name: string
  sort: number
  status: number
}

export interface Dish {
  id: number
  categoryName?: string
  name: string
  categoryId: number
  price: number
  image?: string
  description?: string
  status: number
  flavorCount?: number
  flavors?: Array<Record<string, unknown>>
}

export interface PickupSlot {
  id: number
  stallId: number
  slotDate: string
  startTime: string
  endTime: string
  capacity: number
  booked: number
  remaining: number
  status: number
}

export interface ShoppingCart {
  id: number
  name: string
  stallId: number
  dishId?: number
  number: number
  amount: number
  image?: string
}

export interface Order {
  id: number
  number: string
  status: number
  orderTime: string
  payStatus: number
  amount: number
  pickupCode?: string
  stallId?: number
  slotId?: number
  orderDishes?: string
  remark?: string
  cancelReason?: string
  rejectionReason?: string
  orderDetailList?: Array<{ name: string; number: number; amount: number }>
}

export interface OrderSubmitResult {
  id: number
  orderNumber: string
  orderAmount: number
  orderTime: string
  pickupCode: string
  slotId: number
  slotDate: string
  startTime: string
  endTime: string
}

export interface Role {
  id: number
  code: string
  name: string
  description?: string
}

export interface Employee {
  id: number
  username: string
  name: string
  phone?: string
  sex?: string
  idNumber?: string
  status: number
  updateTime?: string
  roles?: Role[]
}

export interface Setmeal {
  id: number
  categoryId: number
  categoryName?: string
  name: string
  price: number
  status: number
  description?: string
  image?: string
  updateTime?: string
  dishCount?: number
  setmealDishes?: Array<{ dishId: number; name: string; price: number; copies: number }>
}

export interface Permission {
  id: number
  code: string
  name: string
  type: number
  parentId?: number
}

export interface OrderDetail {
  id: number
  name: string
  number: number
  amount: number
  dishId?: number
  setmealId?: number
  dishFlavor?: string
}
