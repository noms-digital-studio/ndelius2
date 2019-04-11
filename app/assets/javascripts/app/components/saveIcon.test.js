import { endSaveIcon, saveIconError, startSaveIcon } from './saveIcon'

jest.useFakeTimers()

describe('save icon component', () => {

  let $spinner
  let $saveIndicator

  beforeEach(() => {
    document.body.innerHTML =
      '<div id="save_indicator" class="moj-auto-save govuk-visually-hidden">' +
      '  <div class="moj-auto-save__spinner">' +
      '    <div class="moj-auto-save__spinner__double-bounce1"></div>' +
      '    <div class="moj-auto-save__spinner__double-bounce2"></div>' +
      '  </div>' +
      '  <span class="govuk-body-xs">Changes saved</span>' +
      '</div>'

    $spinner = document.querySelector('.moj-auto-save__spinner')
    $saveIndicator = document.getElementById('save_indicator')

    startSaveIcon()
  })

  it('should display the auto save icon', () => {
    expect($spinner.classList.contains('active')).toBeTruthy()
    expect($spinner.classList.contains('error')).toBeFalsy()
    expect($saveIndicator.classList.contains('govuk-visually-hidden')).toBeFalsy()
  })

  it('should change the auto save icon and then hide it after a delay', () => {
    endSaveIcon()
    expect($spinner.classList.contains('active')).toBeFalsy()
    expect($spinner.classList.contains('error')).toBeFalsy()
    expect($saveIndicator.classList.contains('govuk-visually-hidden')).toBeFalsy()

    jest.runAllTimers()

    expect($saveIndicator.classList.contains('govuk-visually-hidden')).toBeTruthy()
  })

  it('should show an error state', () => {
    saveIconError()
    expect($spinner.classList.contains('active')).toBeFalsy()
    expect($spinner.classList.contains('error')).toBeTruthy()
    expect($saveIndicator.classList.contains('govuk-visually-hidden')).toBeFalsy()
  })
})
