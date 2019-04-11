'use strict'

import { initFeedbackLinks } from './feedbackLinks'
import { openPopupWindow } from '../helpers/popupHelper'

jest.mock('../helpers/popupHelper')

describe('Feedback links component', () => {

  let $feedbackLinks

  beforeEach(() => {
    document.body.innerHTML = '<a href="some/test/url" class="js-feedback-link">feedback</a><a href="some/other/test/url" class="js-feedback-link">feedback</a>'
    initFeedbackLinks()

    $feedbackLinks = document.querySelectorAll('.js-feedback-link')
  })

  test('should call the openPopupWindow function with the correct parameters', () => {
    $feedbackLinks[0].click()
    expect(openPopupWindow).toHaveBeenCalledWith('some/test/url', 'feedbackForm', 250, 50)
  })

  test('should support multiple feedback links', () => {
    $feedbackLinks[1].click()
    expect(openPopupWindow).toHaveBeenCalledWith('some/other/test/url', 'feedbackForm', 250, 50)
  })
})
