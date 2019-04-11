import { initInputs } from './input'
import { autoSaveProgress } from '../helpers/saveProgressHelper'

jest.mock('../helpers/saveProgressHelper')
jest.useFakeTimers()

describe('input component', () => {

  let $input

  function triggerEvent ($element, type) {
    const event = new KeyboardEvent(type)
    $element.dispatchEvent(event)
  }

  beforeEach(() => {
    document.body.innerHTML =
      '<label for="text-input">Text input</label>' +
      '<input id="text-input" type="text" />' +
      '<label for="text-input">Hidden text input</label>' +
      '<input id="hidden-input" type="hidden" />' +
      '<formgroup>' +
      '  <label for="radio-input">Radio input</label>' +
      '  <input id="radio-input" type="radio" />' +
      '</formgroup>' +
      '<formgroup>' +
      '  <label for="checkbox-input">Radio input</label>' +
      '  <input id="checkbox-input" type="checkbox" />' +
      '</formgroup>'

    initInputs()
  })

  afterEach(() => {
    autoSaveProgress.mockClear()
  })

  describe('text input', () => {

    beforeEach(() => {
      $input = document.getElementById('text-input')
    })

    it('should auto save progress on blur', () => {
      triggerEvent($input, 'blur')
      expect(autoSaveProgress).toBeCalled()
    })

    it('should auto save progress on keyup', () => {
      triggerEvent($input, 'keyup')
      jest.runAllTimers()
      expect(autoSaveProgress).toBeCalled()
    })
  })

  describe('hidden input', () => {

    beforeEach(() => {
      $input = document.getElementById('hidden-input')
    })

    it('should NOT auto save progress on blur', () => {
      triggerEvent($input, 'blur')
      expect(autoSaveProgress).not.toBeCalled()
    })

    it('should NOT auto save progress on keyup', () => {
      triggerEvent($input, 'keyup')
      jest.runAllTimers()
      expect(autoSaveProgress).not.toBeCalled()
    })
  })

  describe('radio input', () => {
    it('should auto save progress on click', () => {
      $input = document.getElementById('radio-input')
      $input.click()
      expect(autoSaveProgress).toBeCalled()
    })
  })

  describe('checkbox input', () => {
    it('should auto save progress on click', () => {
      $input = document.getElementById('checkbox-input')
      $input.click()
      expect(autoSaveProgress).toBeCalled()
    })
  })
})
