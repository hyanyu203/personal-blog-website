import { apiFetch } from '~/utils/http'

export function subscribe(email: string) {
  return apiFetch<{ message: string }>('/subscriptions', {
    method: 'POST',
    body: JSON.stringify({ email })
  })
}

export function confirmSubscribe(token: string) {
  return apiFetch<{ message: string }>('/subscriptions/confirm', {
    method: 'POST',
    body: JSON.stringify({ token }),
    referrerPolicy: 'no-referrer'
  })
}

export function unsubscribe(token: string) {
  return apiFetch<{ message: string }>('/subscriptions/unsubscribe', {
    method: 'POST',
    body: JSON.stringify({ token }),
    referrerPolicy: 'no-referrer'
  })
}
