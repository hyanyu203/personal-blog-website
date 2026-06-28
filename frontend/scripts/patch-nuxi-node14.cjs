const fs = require('fs')
const path = require('path')

function patchFile(file, replacements) {
  if (!fs.existsSync(file)) {
    return
  }
  const source = fs.readFileSync(file, 'utf8')
  let patched = source
  for (const [from, to] of replacements) {
    patched = patched.replace(from, to)
  }
  if (patched !== source) {
    fs.writeFileSync(file, patched)
    console.log(`patched ${path.relative(path.join(__dirname, '..'), file)}`)
  }
}

const nuxiCandidates = [
  path.join(__dirname, '..', 'node_modules', 'nuxt', 'node_modules', 'nuxi', 'bin', 'nuxi.mjs'),
  path.join(__dirname, '..', 'node_modules', 'nuxi', 'bin', 'nuxi.mjs')
]

for (const file of nuxiCandidates) {
  patchFile(file, [[
    'process.env.NODE_COMPILE_CACHE ||= directory',
    'process.env.NODE_COMPILE_CACHE || (process.env.NODE_COMPILE_CACHE = directory)'
  ]])
}

const nuxiTypecheckCandidates = [
  path.join(__dirname, '..', 'node_modules', 'nuxt', 'node_modules', 'nuxi', 'dist', 'chunks', 'typecheck.mjs'),
  path.join(__dirname, '..', 'node_modules', 'nuxi', 'dist', 'chunks', 'typecheck.mjs')
]

for (const file of nuxiTypecheckCandidates) {
  patchFile(file, [[
    'await execa("npx", "-p vue-tsc -p typescript vue-tsc --noEmit".split(" "), { stdio: "inherit", cwd: rootDir });',
    'await execa("tsc", ["--noEmit", "-p", ".nuxt/tsconfig.json"], { preferLocal: true, stdio: "inherit", cwd: rootDir });'
  ]])
}

const vuePluginCandidates = [
  path.join(__dirname, '..', 'node_modules', '@vitejs', 'plugin-vue', 'dist', 'index.mjs'),
  path.join(__dirname, '..', 'node_modules', '@nuxt', 'vite-builder', 'node_modules', '@vitejs', 'plugin-vue', 'dist', 'index.mjs')
]

for (const file of vuePluginCandidates) {
  patchFile(file, [[
    'const [filename, rawQuery] = id.split(`?`, 2);\n  const query = Object.fromEntries(new URLSearchParams(rawQuery));',
    'let [filename, rawQuery] = id.split(`?`, 2);\n  filename = filename.replace(/\\\\u([0-9a-fA-F]{4})/g, (_, code) => String.fromCharCode(parseInt(code, 16)));\n  const query = Object.fromEntries(new URLSearchParams(rawQuery));'
  ]])
}
