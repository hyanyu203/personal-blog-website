import { a as apiFetch } from '../server.mjs';

function fetchSnippets(page = 1, language) {
  let url = `/snippets?page=${page}&pageSize=20`;
  if (language)
    url += `&language=${encodeURIComponent(language)}`;
  return apiFetch(url);
}
function fetchSnippetBySlug(slug) {
  return apiFetch(`/snippets/${slug}`);
}

export { fetchSnippets as a, fetchSnippetBySlug as f };
//# sourceMappingURL=snippet.api-40fc8cb7.mjs.map
