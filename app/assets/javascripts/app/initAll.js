import { initProgressiveRadioButtons } from './components/progressiveRadioButtons'
import { initFeedbackLinks } from './components/feedbackLinks'
import { initExitLink } from './components/exitLink'
import { reorderErrorMessages } from './components/errorMessages'
import { initReportNavigation } from './components/reportNavigation'
import { initViewDraftLinks } from './components/viewDraftReport'
import { noBackPlease } from './utilities/noBackPlease'
import { initSummaryAnalytics } from './components/summary'
import { initTextAreas } from './components/textAreas'
import { initInputs } from './components/input'
import { RICH_TEXT } from './helpers/formDataHelper'

const initAppAll = () => {
  initTextAreas()
  initInputs()
  initProgressiveRadioButtons()
  initFeedbackLinks()
  initExitLink()
  reorderErrorMessages()
  initReportNavigation()
  initViewDraftLinks()
  initSummaryAnalytics()
  noBackPlease()

  /**
   * @FIXME: This could/should be part of the PDF Generator?
   */
  const $form = document.getElementById('ndForm')
  const formData = new FormData($form)
  formData.forEach(($item, $key) => {
    if ($item.indexOf('<p>') !== -1 && $item.indexOf(RICH_TEXT) === -1) {
      document.getElementById($key).value = RICH_TEXT + $item
    }
  })
}

export {
  initAppAll
}
