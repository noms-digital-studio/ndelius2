import { OFFENDER_DETAILS_LOAD_ERROR, RECEIVE_OFFENDER_DETAILS } from '../constants/ActionTypes'
import offenderDetails from './offenderDetails'
import { expect } from 'chai'

describe('offenderDetailsReducer', () => {
  let state

  describe('when in default state', () => {
    beforeEach(() => {
      state = offenderDetails(undefined, { type: '"@@redux/INIT"' })
    })

    it('fetching is true', () => {
      expect(state.fetching).to.equal(true)
    })
    it('offender error not set', () => {
      expect(state.offenderDetailsLoadError).to.equal(false)
    })
  })

  describe('when RECEIVE_OFFENDER_DETAILS action received', () => {
    beforeEach(() => {
      state = offenderDetails({ fetching: true, offenderDetailsLoadError: true }, {
        type: RECEIVE_OFFENDER_DETAILS,
        details: {
          firstName: 'John',
          surname: 'Smith'
        }
      })
    })
    it('details set', () => {
      expect(state.firstName).to.equal('John')
      expect(state.surname).to.equal('Smith')
    })
    it('fetching toggled off', () => {
      expect(state.fetching).to.equal(false)
    })
    it('offender error is cleared', () => {
      expect(state.offenderDetailsLoadError).to.equal(false)
    })
  })

  describe('when OFFENDER_DETAILS_LOAD_ERROR action received', () => {
    beforeEach(() => {
      state = offenderDetails({ fetching: true }, {
        type: OFFENDER_DETAILS_LOAD_ERROR,
        error: new Error('Boom!')
      })
    })
    it('offender error set', () => {
      expect(state.offenderDetailsLoadError).to.equal(true)
    })
    it('fetching toggled off', () => {
      expect(state.fetching).to.equal(false)
    })
  })
})
