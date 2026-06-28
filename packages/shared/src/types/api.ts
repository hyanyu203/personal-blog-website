export interface PageResult<T> {
  items: T[]
  total: number
  page: number
  pageSize: number
}

export interface ApiEnvelope<T = unknown> {
  code: number
  message?: string
  data?: T
}

export class ApiError extends Error {
  readonly code: number

  constructor(code: number, message: string) {
    super(message)
    this.name = 'ApiError'
    this.code = code
  }
}

export function isApiError(error: unknown): error is ApiError {
  return error instanceof ApiError
}

export const UNAUTHORIZED_CODE = 40101
export const FORBIDDEN_CODE = 40301

export function computeHasMore(page: number, pageSize: number, total: number): boolean {
  return page * pageSize < total
}
