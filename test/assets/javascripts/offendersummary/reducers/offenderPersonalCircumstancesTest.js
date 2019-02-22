import {
  OFFENDER_PERSONAL_CIRCUMSTANCES_LOAD_ERROR,
  RECEIVE_OFFENDER_PERSONAL_CIRCUMSTANCES
} from '../constants/ActionTypes'
import offenderPersonalCircumstances from './offenderPersonalCircumstances'
import { expect } from 'chai'

describe('offenderPersonalCircumstancesReducer', () => {
  let state

  describe('when in default state', () => {
    beforeEach(() => {
      state = offenderPersonalCircumstances(undefined, { type: '"@@redux/INIT"' })
    })

    it('fetching is true', () => {
      expect(state.fetching).to.equal(true)
    })
    it('load error not set', () => {
      expect(state.loadError).to.equal(false)
    })
  })

  describe('when RECEIVE_OFFENDER_PERSONAL_CIRCUMSTANCES action received', () => {
    beforeEach(() => {
      state = offenderPersonalCircumstances({ fetching: true, loadError: true }, {
        type: RECEIVE_OFFENDER_PERSONAL_CIRCUMSTANCES,
        circumstances: [{ type: 'bad' }]
      })
    })
    it('details set', () => {
      expect(state.circumstances).to.have.length(1)
    })
    it('fetching toggled off', () => {
      expect(state.fetching).to.equal(false)
    })
    it('load error is cleared', () => {
      expect(state.loadError).to.equal(false)
    })
  })

  describe('when OFFENDER_PERSONAL_CIRCUMSTANCES_LOAD_ERROR action received', () => {
    beforeEach(() => {
      state = offenderPersonalCircumstances({ fetching: true }, {
        type: OFFENDER_PERSONAL_CIRCUMSTANCES_LOAD_ERROR,
        error: new Error('Boom!')
      })
    })
    it('load error set', () => {
      expect(state.loadError).to.equal(true)
    })
    it('fetching toggled off', () => {
      expect(state.fetching).to.equal(false)
    })
  })
})
