import {
  NEXT_APPOINTMENT_LOAD_ERROR,
  RECEIVE_NEXT_APPOINTMENT,
  RECEIVE_NO_NEXT_APPOINTMENT
} from '../constants/ActionTypes'

const nextAppointment = (state = { fetching: true, loadError: false, noNextAppointment: false }, action) => {
  switch (action.type) {
    case RECEIVE_NEXT_APPOINTMENT:
      return {
        ...state,
        fetching: false,
        loadError: false,
        noNextAppointment: false,
        appointment: action.appointment
      }
    case RECEIVE_NO_NEXT_APPOINTMENT:
      return {
        ...state,
        fetching: false,
        loadError: false,
        noNextAppointment: true
      }
    case NEXT_APPOINTMENT_LOAD_ERROR:
      return {
        ...state,
        fetching: false,
        loadError: true
      }
    default:
      return state
  }
}

export default nextAppointment
