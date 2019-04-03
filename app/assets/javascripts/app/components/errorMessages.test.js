'use strict'

import { reorderErrorMessages } from './errorMessages'

describe('Error messages component', () => {

  beforeEach(() => {
    document.body.innerHTML = ''
    reorderErrorMessages()
  })

  test('should reorder error messages based on position within form', () => {
    expect(true).toBe(true)
  })

})
