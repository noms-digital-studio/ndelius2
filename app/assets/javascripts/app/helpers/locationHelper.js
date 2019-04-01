const SFR_LOCATION = 'shortFormatPreSentenceReport'
const PAROM1_LOCATION = 'paroleParom1Report'

const isShortFormatReport = () => {
  return window.location.pathname.indexOf(SFR_LOCATION) !== -1
}

const isParom1Report = () => {
  return window.location.pathname.indexOf(PAROM1_LOCATION) !== -1
}

export {
  isShortFormatReport,
  isParom1Report
}