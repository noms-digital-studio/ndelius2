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
import { updateFormData } from './helpers/formDataHelper'

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
  updateFormData()
}

export {
  initAppAll
}
