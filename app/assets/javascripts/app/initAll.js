import { initProgressiveRadioButtons } from './components/progressiveRadioButtons'
import { initFeedbackLinks } from './components/feedbackLinks'
import { initExitLink } from './components/exitLink'
import { reorderErrorMessages } from './components/errorMessages'
import { initNavigation } from './components/reportNavigation'
import { initViewDraftLinks } from './components/viewDraftReport'
import { noBackPlease } from './utilities/noBackPlease'

const initAppAll = () => {
  initProgressiveRadioButtons()
  initFeedbackLinks()
  initExitLink()
  reorderErrorMessages()
  initNavigation()
  initViewDraftLinks()
  noBackPlease()
}

export {
  initAppAll
}
