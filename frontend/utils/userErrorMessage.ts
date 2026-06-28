export function userFacingError(error: unknown, fallback = '鍔犺浇澶辫触锛岃绋嶅悗閲嶈瘯'): string {
  if (process.env.NODE_ENV !== 'production' && error instanceof Error && error.message) {
    return error.message
  }
  return fallback
}
