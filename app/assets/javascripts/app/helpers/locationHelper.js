const SFR_LOCATION = 'shortFormatPreSentenceReport'

const isShortFormatReport = () => {
  return window.location.pathname.indexOf(SFR_LOCATION) !== -1
}

export {
  isShortFormatReport
}
