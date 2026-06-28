function userFacingError(error, fallback = "鍔犺浇澶辫触锛岃绋嶅悗閲嶈瘯") {
  if (process.env.NODE_ENV !== "production" && error instanceof Error && error.message) {
    return error.message;
  }
  return fallback;
}
export {
  userFacingError as u
};
//# sourceMappingURL=userErrorMessage-f95eddb5.js.map
