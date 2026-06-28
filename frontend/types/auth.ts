export interface AuthUser {
  id: number
  username: string
  displayName: string
  roles: string[]
  permissions?: string[]
}
