const initExitLink = () => {
  const $exitElement = document.getElementById('exitLink')
  if ($exitElement) {
    $exitElement.addEventListener('click', (event) => {
      event.preventDefault()
      document.getElementById('jumpNumber').value = 0
      document.getElementById('ndForm').submit()
    })
  }
}

export {
  initExitLink
}
