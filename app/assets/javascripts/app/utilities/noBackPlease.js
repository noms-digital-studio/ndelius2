const noBackPlease = () => {
  if (typeof (window) === 'undefined') {
    throw new Error('window is undefined')
  }

  const _hash = '!'
  window.location.href += '#'

  window.setTimeout(() => {
    window.location.href += '!'
  }, 50)

  window.onhashchange = () => {
    if (window.location.hash !== _hash) {
      window.location.hash = _hash
    }
  }
}

export {
  noBackPlease
}
