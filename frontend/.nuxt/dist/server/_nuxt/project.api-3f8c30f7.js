import { a as apiFetch } from "../server.mjs";
function fetchProjects() {
  return apiFetch("/projects");
}
function fetchProjectDetail(owner, repo) {
  return apiFetch(`/projects/${owner}/${repo}`);
}
export {
  fetchProjects as a,
  fetchProjectDetail as f
};
//# sourceMappingURL=project.api-3f8c30f7.js.map
