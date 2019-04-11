import { formWithZeroJumpNumber } from '../utilities/formWithZeroJumpNumber'
import { promisifyXMLHttpRequest } from '../utilities/xhrPromisify'
import { isShortFormatReport } from '../helpers/locationHelper'
import { trackEvent } from '../../helpers/analyticsHelper'

const trackDraftClick = () => {
  trackEvent('click', `${ isShortFormatReport ? 'SFR' : 'PAROM1' } - View draft`, document.getElementsByTagName('h1')[0].textContent)
}

const initViewDraftLinks = () => {
  const $draftReportButton = document.getElementById('draftReport')

  if ($draftReportButton) {
    $draftReportButton.addEventListener('click', event => {
      event.preventDefault()
      const target = event.target.dataset.target
      const $form = document.getElementById('ndForm')

      trackDraftClick()

      promisifyXMLHttpRequest({
        method: 'POST',
        url: `${ $form.getAttribute('action') }/save`,
        body: formWithZeroJumpNumber($form)
      }).finally(() => {
        window.location = target
      })
    })
  }

  const $draftReportLink = document.querySelector('.js-view-draft')

  if ($draftReportLink) {
    $draftReportLink.addEventListener('click', () => {
      trackDraftClick()
    })
  }
}

export {
  initViewDraftLinks
}
