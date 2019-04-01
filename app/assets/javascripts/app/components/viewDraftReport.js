import { formWithZeroJumpNumber } from '../utilities/formWithZeroJumpNumber'
import { promisifyXMLHttpRequest } from '../utilities/xhrPromisify'

const initViewDraftLink = () => {
  const $draftReportElement = document.getElementById('draftReport')
  if ($draftReportElement) {
    $draftReportElement.addEventListener('click', event => {
      event.preventDefault()
      const target = event.target.dataset.target
      const $form = document.getElementById('ndForm')

      promisifyXMLHttpRequest({
        method: 'POST',
        url: `${ $form.getAttribute('action') }/save`,
        body: formWithZeroJumpNumber($form)
      }).finally(() => {
        window.location = target
      })
    })
  }
}

export {
  initViewDraftLink
}
