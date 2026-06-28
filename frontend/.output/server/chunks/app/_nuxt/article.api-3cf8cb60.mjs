import { a as apiFetch } from '../server.mjs';

function fetchArticles(page = 1, pageSize = 20, category, tag, keyword) {
  let url = `/articles?page=${page}&pageSize=${pageSize}`;
  if (category)
    url += `&category=${encodeURIComponent(category)}`;
  if (tag)
    url += `&tag=${encodeURIComponent(tag)}`;
  if (keyword)
    url += `&keyword=${encodeURIComponent(keyword)}`;
  return apiFetch(url);
}
function fetchArticleBySlug(slug) {
  return apiFetch(`/articles/slug/${slug}`);
}
function fetchArticleToc(id) {
  return apiFetch(`/articles/${id}/toc`);
}
function fetchRelatedArticles(id, limit = 5) {
  return apiFetch(`/articles/${id}/related?limit=${limit}`);
}

export { fetchArticleToc as a, fetchRelatedArticles as b, fetchArticles as c, fetchArticleBySlug as f };
//# sourceMappingURL=article.api-3cf8cb60.mjs.map
