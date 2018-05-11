const flatMap = (a, cb) => [].concat(...a.map(cb))

const range = (count) => [...Array(count)].map((v, i) => i + 1)

export {flatMap, range}