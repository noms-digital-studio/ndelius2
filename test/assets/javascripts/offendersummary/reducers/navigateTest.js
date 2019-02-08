import {
  NAVIGATE_TO_VIEW_OFFENDER_ALIASES,
  NAVIGATE_TO_VIEW_OFFENDER_ADDRESS_HISTORY,
  NAVIGATE_TO_VIEW_OFFENDER_PERSONAL_CIRCUMSTANCES,
  NAVIGATE_TO_VIEW_OFFENDER_REGISTRATIONS,
  NAVIGATE_TO_VIEW_OFFENDER_EVENT,
  NAVIGATE_TO_TRANSFER_INACTIVE_OFFENDER
} from '../constants/ActionTypes'
import navigate from './navigate'
import { expect } from 'chai'

describe('navigate reducer', () => {
  describe('when in default state', () => {
    it('should set default state to not close', () => {
      const state = navigate(undefined, { type: '"@@redux/INIT"' })
      expect(state.shouldClose).to.equal(false)
    })
  })
  describe('when NAVIGATE_TO_VIEW_OFFENDER_ALIASES action received', () => {
    let state

    beforeEach(() => {
      state = navigate({ shouldClose: false }, { type: NAVIGATE_TO_VIEW_OFFENDER_ALIASES, offenderId: '123' })
    })

    it('shouldClose is true', () => {
      expect(state.shouldClose).to.equal(true)
    })
    it('action is viewOffenderAliases', () => {
      expect(state.action).to.equal('viewOffenderAliases')
    })
    it('data is set to offenderId', () => {
      expect(state.data).to.equal('123')
    })
  })
  describe('when NAVIGATE_TO_VIEW_OFFENDER_ADDRESS_HISTORY action received', () => {
    let state

    beforeEach(() => {
      state = navigate({ shouldClose: false }, { type: NAVIGATE_TO_VIEW_OFFENDER_ADDRESS_HISTORY, offenderId: '123' })
    })

    it('shouldClose is true', () => {
      expect(state.shouldClose).to.equal(true)
    })
    it('action is viewOffenderAddresses', () => {
      expect(state.action).to.equal('viewOffenderAddresses')
    })
    it('data is set to offenderId', () => {
      expect(state.data).to.equal('123')
    })
  })
  describe('when NAVIGATE_TO_VIEW_OFFENDER_PERSONAL_CIRCUMSTANCES action received', () => {
    let state

    beforeEach(() => {
      state = navigate({ shouldClose: false }, { type: NAVIGATE_TO_VIEW_OFFENDER_PERSONAL_CIRCUMSTANCES, offenderId: '123' })
    })

    it('shouldClose is true', () => {
      expect(state.shouldClose).to.equal(true)
    })
    it('action is viewOffenderPersonalCircumstances', () => {
      expect(state.action).to.equal('viewOffenderPersonalCircumstances')
    })
    it('data is set to offenderId', () => {
      expect(state.data).to.equal('123')
    })
  })
  describe('when NAVIGATE_TO_VIEW_OFFENDER_REGISTRATIONS action received', () => {
    let state

    beforeEach(() => {
      state = navigate({ shouldClose: false }, { type: NAVIGATE_TO_VIEW_OFFENDER_REGISTRATIONS, offenderId: '123' })
    })

    it('shouldClose is true', () => {
      expect(state.shouldClose).to.equal(true)
    })
    it('action is viewOffenderRegistrations', () => {
      expect(state.action).to.equal('viewOffenderRegistrations')
    })
    it('data is set to offenderId', () => {
      expect(state.data).to.equal('123')
    })
  })
  describe('when NAVIGATE_TO_VIEW_OFFENDER_EVENT action received', () => {
    let state

    beforeEach(() => {
      state = navigate({ shouldClose: false }, { type: NAVIGATE_TO_VIEW_OFFENDER_EVENT, offenderId: '123', eventId: '999' })
    })

    it('shouldClose is true', () => {
      expect(state.shouldClose).to.equal(true)
    })
    it('action is viewEvent', () => {
      expect(state.action).to.equal('viewEvent')
    })
    it('data is set with offenderId', () => {
      expect(state.data.offenderId).to.equal('123')
    })
    it('data is set with eventId', () => {
      expect(state.data.eventId).to.equal('999')
    })
  })
  describe('when NAVIGATE_TO_TRANSFER_INACTIVE_OFFENDER action received', () => {
    let state

    beforeEach(() => {
      state = navigate({ shouldClose: false }, { type: NAVIGATE_TO_TRANSFER_INACTIVE_OFFENDER, offenderId: '123' })
    })

    it('shouldClose is true', () => {
      expect(state.shouldClose).to.equal(true)
    })
    it('action is transferInactiveOffender', () => {
      expect(state.action).to.equal('transferInactiveOffender')
    })
    it('data is set to offenderId', () => {
      expect(state.data).to.equal('123')
    })
  })
})
