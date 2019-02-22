export const setIn = (object, property, value) => {
  const copyOf = Object.assign({}, object)
  copyOf[property] = value
  return copyOf
}

export const removeIn = (object, property) => {
  const copyOf = Object.assign({}, object)
  delete copyOf[property]
  return copyOf
}
