import { nodeListForEach } from '../utilities/nodeListForEach'
import { trackEvent } from '../../helpers/analyticsHelper'
import { isShortFormatReport } from '../helpers/locationHelper'

const initSummaryAnalytics = () => {

  function getLabel ($element) {
    const parent = $element.parentElement
    let label

    if (parent) {
      if (parent.querySelector('.govuk-caption-xl')) {
        label = parent.querySelector('.govuk-caption-xl').textContent
      } else if (parent.parentElement && parent.parentElement.querySelector('span:not(.govuk-details__summary-text)')) {
        label = parent.parentElement.querySelector('span:not(.govuk-details__summary-text)').textContent
      } else if (parent.parentElement && parent.parentElement.parentElement && parent.parentElement.parentElement.parentElement && parent.parentElement.parentElement.parentElement.querySelector('legend > span')) {
        label = parent.parentElement.parentElement.parentElement.querySelector('legend > span').textContent
      } else if (parent.parentElement && parent.parentElement.parentElement && parent.parentElement.parentElement.parentElement && parent.parentElement.parentElement.parentElement.querySelector('legend')) {
        label = parent.parentElement.parentElement.parentElement.querySelector('legend').textContent
      }
    }
    return label || 'Unknown field'
  }

  nodeListForEach(document.querySelectorAll('summary'), $element => {

    $element.addEventListener('click', event => {
      const details = event.target.parentElement
      const isOpen = details.dataset.open
      const label = getLabel(details)

      console.info('Label:', label.trim())

      isOpen ? delete details.dataset.open : details.dataset.open = true

      trackEvent(isOpen ? 'close' : 'open', `${ isShortFormatReport() ? 'SFR' : 'PAROM1' } - What to include`, `${ document.querySelector('h1').textContent } > ${ label.trim() }`)
    })
  })
}

export {
  initSummaryAnalytics
}
