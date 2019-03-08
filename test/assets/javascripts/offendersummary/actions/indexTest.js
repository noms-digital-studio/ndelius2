import { expect } from 'chai'
import { stub } from 'sinon'
import offender from '../api/offender'
import {
  getNextAppointment,
  getOffenderConvictions,
  getOffenderDetails,
  getOffenderPersonalCircumstances,
  getOffenderRegistrations,
  offenderSummaryClose,
  offenderSummaryViewPrevious,
  showMoreConvictions,
  transferInactiveOffender,
  viewOffenderAddresses,
  viewOffenderAliases,
  viewOffenderEvent,
  viewOffenderPersonalCircumstances,
  viewOffenderRegistrations
} from './index'

describe('offender summary action', () => {
  let dispatch

  beforeEach(() => {
    dispatch = stub()
    offender.getDetails = stub()
    offender.getRegistrations = stub()
    offender.getConvictions = stub()
    offender.getNextAppointment = stub()
    offender.getPersonalCircumstances = stub()
  })

  describe('on getOffenderDetails', () => {
    context('successful response', () => {
      beforeEach(() => {
        offender.getDetails.yields({ firstName: 'John' }, null)
        getOffenderDetails()(dispatch)
      })
      it('dispatches RECEIVE_OFFENDER_DETAILS with details', () => {
        expect(dispatch).to.be.calledWith({ type: 'RECEIVE_OFFENDER_DETAILS', details: { firstName: 'John' } })
      })
    })
    context('unsuccessful response', () => {
      beforeEach(() => {
        getOffenderDetails()(dispatch)
        offender.getDetails.callArgWith(1, 'Boom!')
      })
      it('dispatches OFFENDER_DETAILS_LOAD_ERROR with error', () => {
        expect(dispatch).to.be.calledWith({ type: 'OFFENDER_DETAILS_LOAD_ERROR', error: 'Boom!' })
      })
    })
  })
  describe('on getOffenderRegistrations', () => {
    context('successful response', () => {
      beforeEach(() => {
        offender.getRegistrations.yields([{ type: 'Bad' }], null)
        getOffenderRegistrations()(dispatch)
      })
      it('dispatches RECEIVE_OFFENDER_REGISTRATIONS with details', () => {
        expect(dispatch).to.be.calledWith({ type: 'RECEIVE_OFFENDER_REGISTRATIONS', registrations: [{ type: 'Bad' }] })
      })
    })
    context('unsuccessful response', () => {
      beforeEach(() => {
        getOffenderRegistrations()(dispatch)
        offender.getRegistrations.callArgWith(1, 'Boom!')
      })
      it('dispatches OFFENDER_REGISTRATIONS_LOAD_ERROR with error', () => {
        expect(dispatch).to.be.calledWith({ type: 'OFFENDER_REGISTRATIONS_LOAD_ERROR', error: 'Boom!' })
      })
    })
  })
  describe('on getOffenderConvictions', () => {
    context('successful response', () => {
      beforeEach(() => {
        offender.getConvictions.yields([{ type: 'Bad' }], null)
        getOffenderConvictions()(dispatch)
      })
      it('dispatches RECEIVE_OFFENDER_CONVICTIONS with details', () => {
        expect(dispatch).to.be.calledWith({ type: 'RECEIVE_OFFENDER_CONVICTIONS', convictions: [{ type: 'Bad' }] })
      })
    })
    context('unsuccessful response', () => {
      beforeEach(() => {
        getOffenderConvictions()(dispatch)
        offender.getConvictions.callArgWith(1, 'Boom!')
      })
      it('dispatches OFFENDER_CONVICTIONS_LOAD_ERROR with error', () => {
        expect(dispatch).to.be.calledWith({ type: 'OFFENDER_CONVICTIONS_LOAD_ERROR', error: 'Boom!' })
      })
    })
  })
  describe('on showMoreConvictions', () => {
    beforeEach(() => {
      showMoreConvictions()(dispatch)
    })
    it('dispatches INCREMENT_MAX_CONVICTIONS_VISIBLE with incrementBy of 10', () => {
      expect(dispatch).to.be.calledWith({ type: 'INCREMENT_MAX_CONVICTIONS_VISIBLE', incrementBy: 10 })
    })
  })
  describe('on getNextAppointment', () => {
    context('successful response', () => {
      beforeEach(() => {
        offender.getNextAppointment.yields({ appointmentId: 1 }, null)
        getNextAppointment()(dispatch)
      })
      it('dispatches RECEIVE_NEXT_APPOINTMENT with details', () => {
        expect(dispatch).to.be.calledWith({ type: 'RECEIVE_NEXT_APPOINTMENT', appointment: { appointmentId: 1 } })
      })
    })
    context('no data response', () => {
      beforeEach(() => {
        getNextAppointment()(dispatch)
        offender.getNextAppointment.callArg(1)
      })
      it('dispatches RECEIVE_NO_NEXT_APPOINTMENT', () => {
        expect(dispatch).to.be.calledWith({ type: 'RECEIVE_NO_NEXT_APPOINTMENT' })
      })
    })
    context('unsuccessful response', () => {
      beforeEach(() => {
        getNextAppointment()(dispatch)
        offender.getNextAppointment.callArgWith(2, 'Boom!')
      })
      it('dispatches NEXT_APPOINTMENT_LOAD_ERROR with error', () => {
        expect(dispatch).to.be.calledWith({ type: 'NEXT_APPOINTMENT_LOAD_ERROR', error: 'Boom!' })
      })
    })
  })
  describe('on getOffenderPersonalCircumstances', () => {
    context('successful response', () => {
      beforeEach(() => {
        offender.getPersonalCircumstances.yields([{ type: 'Bad' }], null)
        getOffenderPersonalCircumstances()(dispatch)
      })
      it('dispatches RECEIVE_OFFENDER_PERSONAL_CIRCUMSTANCES with details', () => {
        expect(dispatch).to.be.calledWith({
          type: 'RECEIVE_OFFENDER_PERSONAL_CIRCUMSTANCES',
          circumstances: [{ type: 'Bad' }]
        })
      })
    })
    context('unsuccessful response', () => {
      beforeEach(() => {
        getOffenderPersonalCircumstances()(dispatch)
        offender.getPersonalCircumstances.callArgWith(1, 'Boom!')
      })
      it('dispatches OFFENDER_PERSONAL_CIRCUMSTANCES_LOAD_ERROR with error', () => {
        expect(dispatch).to.be.calledWith({ type: 'OFFENDER_PERSONAL_CIRCUMSTANCES_LOAD_ERROR', error: 'Boom!' })
      })
    })
  })
  describe('on viewOffenderAliases', () => {
    beforeEach(() => {
      viewOffenderAliases(1234)(dispatch)
    })
    it('dispatches NAVIGATE_TO_VIEW_OFFENDER_ALIASES with offenderId', () => {
      expect(dispatch).to.be.calledWith({ type: 'NAVIGATE_TO_VIEW_OFFENDER_ALIASES', offenderId: 1234 })
    })
  })
  describe('on viewOffenderAddresses', () => {
    beforeEach(() => {
      viewOffenderAddresses(1234)(dispatch)
    })
    it('dispatches NAVIGATE_TO_VIEW_OFFENDER_ADDRESS_HISTORY with offenderId', () => {
      expect(dispatch).to.be.calledWith({ type: 'NAVIGATE_TO_VIEW_OFFENDER_ADDRESS_HISTORY', offenderId: 1234 })
    })
  })
  describe('on viewOffenderPersonalCircumstances', () => {
    beforeEach(() => {
      viewOffenderPersonalCircumstances(1234)(dispatch)
    })
    it('dispatches NAVIGATE_TO_VIEW_OFFENDER_PERSONAL_CIRCUMSTANCES with offenderId', () => {
      expect(dispatch).to.be.calledWith({ type: 'NAVIGATE_TO_VIEW_OFFENDER_PERSONAL_CIRCUMSTANCES', offenderId: 1234 })
    })
  })
  describe('on viewOffenderRegistrations', () => {
    beforeEach(() => {
      viewOffenderRegistrations(1234)(dispatch)
    })
    it('dispatches NAVIGATE_TO_VIEW_OFFENDER_REGISTRATIONS with offenderId', () => {
      expect(dispatch).to.be.calledWith({ type: 'NAVIGATE_TO_VIEW_OFFENDER_REGISTRATIONS', offenderId: 1234 })
    })
  })
  describe('on viewOffenderEvent', () => {
    beforeEach(() => {
      viewOffenderEvent(1234, 999)(dispatch)
    })
    it('dispatches NAVIGATE_TO_VIEW_OFFENDER_EVENT with offenderId and eventId', () => {
      expect(dispatch).to.be.calledWith({ type: 'NAVIGATE_TO_VIEW_OFFENDER_EVENT', offenderId: 1234, eventId: 999 })
    })
  })
  describe('on navigateToTransferInactiveOffender', () => {
    beforeEach(() => {
      transferInactiveOffender(1234)(dispatch)
    })
    it('dispatches NAVIGATE_TO_TRANSFER_INACTIVE_OFFENDER with offenderId', () => {
      expect(dispatch).to.be.calledWith({ type: 'NAVIGATE_TO_TRANSFER_INACTIVE_OFFENDER', offenderId: 1234 })
    })
  })
  describe('on offenderSummaryViewPrevious', () => {
    beforeEach(() => {
      offenderSummaryViewPrevious(1234)(dispatch)
    })
    it('dispatches NAVIGATE_TO_PREVIOUS_OFFENDER_SUMMARY with offenderId', () => {
      expect(dispatch).to.be.calledWith({ type: 'NAVIGATE_TO_PREVIOUS_OFFENDER_SUMMARY', offenderId: 1234 })
    })
  })
  describe('on offenderSummaryClose', () => {
    beforeEach(() => {
      offenderSummaryClose()(dispatch)
    })
    it('dispatches NAVIGATE_TO_CLOSE_OFFENDER_SUMMARY with offenderId', () => {
      expect(dispatch).to.be.calledWith({ type: 'NAVIGATE_TO_CLOSE_OFFENDER_SUMMARY' })
    })
  })
})
