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

const languageCoreFiles = [
  path.join(__dirname, '..', 'node_modules', '@vue', 'language-core', 'out', 'generators', 'template.js'),
  path.join(__dirname, '..', 'node_modules', '@vue', 'language-core', 'out', 'parsers', 'scriptSetupRanges.js'),
  path.join(__dirname, '..', 'node_modules', 'computeds', 'out', 'computeds', 'computedArray.js')
]

for (const file of languageCoreFiles) {
  patchFile(file, [
    ['tagOffsetsMap[tag] ??= [];', 'tagOffsetsMap[tag] = tagOffsetsMap[tag] == null ? [] : tagOffsetsMap[tag];'],
    ['tagOffsetsMap[node.tag] ??= [];', 'tagOffsetsMap[node.tag] = tagOffsetsMap[node.tag] == null ? [] : tagOffsetsMap[node.tag];'],
    ['statementRange ??= range;', 'statementRange = statementRange == null ? range : statementRange;'],
    ['array ??= [];', 'array = array == null ? [] : array;']
  ])
}
