import { OFFENDER_DETAILS_LOAD_ERROR, RECEIVE_OFFENDER_DETAILS } from '../constants/ActionTypes'

const offenderDetails = (state = { fetching: true, offenderDetailsLoadError: false }, action) => {
  switch (action.type) {
    case RECEIVE_OFFENDER_DETAILS:
      return {
        ...state,
        fetching: false,
        offenderDetailsLoadError: false,
        ...action.details
      }
    case OFFENDER_DETAILS_LOAD_ERROR:
      return {
        ...state,
        fetching: false,
        offenderDetailsLoadError: true
      }
    default:
      return state
  }
}

export default offenderDetails
