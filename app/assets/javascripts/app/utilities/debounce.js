const debounce = (callback, wait = 500) => {
  let timeout = null
  return (...args) => {
    const next = () => callback(...args)
    clearTimeout(timeout)
    timeout = setTimeout(next, wait)
  }
}

export {
  debounce
}
