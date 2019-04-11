const debounce = (func, wait = 500, immediate) => {
  let timeout
  return function () {
    const context = this
    const later = () => {
      timeout = null
      if (!immediate) {
        func.apply(context, arguments)
      }
    }
    const callNow = immediate && !timeout
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
    if (callNow) {
      func.apply(context, arguments)
    }
  }
}

export {
  debounce
}
