import {
  NEXT_APPOINTMENT_LOAD_ERROR,
  RECEIVE_NEXT_APPOINTMENT,
  RECEIVE_NO_NEXT_APPOINTMENT
} from '../constants/ActionTypes'
import nextAppointment from './nextAppointment'
import { expect } from 'chai'

describe('nextAppointmentReducer', () => {
  let state

  describe('when in default state', () => {
    beforeEach(() => {
      state = nextAppointment(undefined, { type: '"@@redux/INIT"' })
    })

    it('fetching is true', () => {
      expect(state.fetching).to.equal(true)
    })
    it('loadError error not set', () => {
      expect(state.loadError).to.equal(false)
    })
  })

  describe('when RECEIVE_NEXT_APPOINTMENT action received', () => {
    beforeEach(() => {
      state = nextAppointment({ fetching: true, loadError: true, noNextAppointment: true }, {
        type: RECEIVE_NEXT_APPOINTMENT,
        appointment: { appointmentId: 1 }
      })
    })
    it('details set', () => {
      expect(state.appointment).to.eql({ appointmentId: 1 })
    })
    it('fetching toggled off', () => {
      expect(state.fetching).to.equal(false)
    })
    it('noNextAppointment toggled off', () => {
      expect(state.noNextAppointment).to.equal(false)
    })
    it('loadError error is cleared', () => {
      expect(state.loadError).to.equal(false)
    })
  })

  describe('when NEXT_APPOINTMENT_LOAD_ERROR action received', () => {
    beforeEach(() => {
      state = nextAppointment({ fetching: true }, {
        type: NEXT_APPOINTMENT_LOAD_ERROR,
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

  describe('when RECEIVE_NO_NEXT_APPOINTMENT action received', () => {
    beforeEach(() => {
      state = nextAppointment({ noNextAppointment: false }, {
        type: RECEIVE_NO_NEXT_APPOINTMENT
      })
    })
    it('noNextAppointment set to true', () => {
      expect(state.noNextAppointment).to.equal(true)
    })
  })
})
