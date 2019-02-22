import { ADD_CONTACT, ADD_NEW_OFFENDER, LEGACY_SEARCH, SHOW_OFFENDER_DETAILS } from '../actions/navigate'
import navigate from './navigateReducer'
import { expect } from 'chai'

describe('navigateReducer', () => {
  describe('when in default state', () => {
    it('should set default state to not close', () => {
      const state = navigate(undefined, { type: '"@@redux/INIT"' })
      expect(state.shouldClose).to.equal(false)
    })
  })

  describe('when ADD_CONTACT action received', () => {
    let state

    beforeEach(() => {
      state = navigate({ shouldClose: false }, { type: ADD_CONTACT, offenderId: '123' })
    })

    it('shouldClose is true', () => {
      expect(state.shouldClose).to.equal(true)
    })
    it('action is addContact', () => {
      expect(state.action).to.equal('addContact')
    })
    it('data is set to offenderId', () => {
      expect(state.data).to.equal('123')
    })
  })

  describe('when SHOW_OFFENDER_DETAILS action received', () => {
    let state

    beforeEach(() => {
      state = navigate({ shouldClose: false }, { type: SHOW_OFFENDER_DETAILS, offenderId: '123' })
    })

    it('shouldClose is true', () => {
      expect(state.shouldClose).to.equal(true)
    })
    it('action is viewOffender', () => {
      expect(state.action).to.equal('viewOffender')
    })
    it('data is set to offenderId', () => {
      expect(state.data).to.equal('123')
    })
  })

  describe('when LEGACY_SEARCH action received', () => {
    let state

    beforeEach(() => {
      state = navigate({ shouldClose: false }, { type: LEGACY_SEARCH, offenderId: '123' })
    })

    it('shouldClose is true', () => {
      expect(state.shouldClose).to.equal(true)
    })
    it('action is toggleSearch', () => {
      expect(state.action).to.equal('toggleSearch')
    })
    it('data is empty', () => {
      expect(state.data).to.be.undefined
    })
  })

  describe('when ADD_NEW_OFFENDER action received', () => {
    let state

    beforeEach(() => {
      state = navigate({ shouldClose: false }, { type: ADD_NEW_OFFENDER, offenderId: '123' })
    })

    it('shouldClose is true', () => {
      expect(state.shouldClose).to.equal(true)
    })
    it('action is addOffender', () => {
      expect(state.action).to.equal('addOffender')
    })
    it('data is empty', () => {
      expect(state.data).to.be.undefined
    })
  })
})
