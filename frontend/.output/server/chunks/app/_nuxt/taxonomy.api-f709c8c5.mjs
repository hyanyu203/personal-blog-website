import { a as apiFetch } from '../server.mjs';
import { c as fetchArticles } from './article.api-3cf8cb60.mjs';

function fetchArchives() {
  return apiFetch("/articles/archives");
}
function fetchCategory(slug) {
  return apiFetch(
    `/categories/${slug}`
  );
}
function fetchTag(slug) {
  return apiFetch(`/tags/${slug}`);
}
function fetchArticlesByCategory(slug, page = 1) {
  return fetchArticles(page, 20, slug, void 0, void 0);
}
function fetchArticlesByTag(slug, page = 1) {
  return fetchArticles(page, 20, void 0, slug, void 0);
}

export { fetchCategory as a, fetchArticlesByCategory as b, fetchTag as c, fetchArticlesByTag as d, fetchArchives as f };
//# sourceMappingURL=taxonomy.api-f709c8c5.mjs.map
