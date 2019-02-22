import { OFFENDER_REGISTRATIONS_LOAD_ERROR, RECEIVE_OFFENDER_REGISTRATIONS } from '../constants/ActionTypes'

const offenderRegistrations = (state = { fetching: true, loadError: false, registrations: [] }, action) => {
  switch (action.type) {
    case RECEIVE_OFFENDER_REGISTRATIONS:
      return {
        ...state,
        fetching: false,
        loadError: false,
        registrations: action.registrations
      }
    case OFFENDER_REGISTRATIONS_LOAD_ERROR:
      return {
        ...state,
        fetching: false,
        loadError: true
      }
    default:
      return state
  }
}

export default offenderRegistrations
