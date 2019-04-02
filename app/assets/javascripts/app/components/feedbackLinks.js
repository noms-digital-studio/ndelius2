import { nodeListForEach } from '../utilities/nodeListForEach'
import { openPopupWindow } from '../helpers/popupHelper'

const initFeedbackLinks = () => {

  nodeListForEach(document.querySelectorAll('.js-feedback-link'), $element => {
    $element.addEventListener('click', event => {
      event.preventDefault()
      openPopupWindow(event.target.getAttribute('href'), 'feedbackForm', 250, 50)
    })
  })
}

export {
  initFeedbackLinks
}
