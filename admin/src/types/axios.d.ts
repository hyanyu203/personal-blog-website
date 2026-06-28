import 'axios'

declare module 'axios' {
  export interface AxiosInstance {
    get<T = unknown>(url: string, config?: import('axios').AxiosRequestConfig): Promise<T>
    delete<T = unknown>(url: string, config?: import('axios').AxiosRequestConfig): Promise<T>
    post<T = unknown>(url: string, data?: unknown, config?: import('axios').AxiosRequestConfig): Promise<T>
    patch<T = unknown>(url: string, data?: unknown, config?: import('axios').AxiosRequestConfig): Promise<T>
  }
}

export {}
