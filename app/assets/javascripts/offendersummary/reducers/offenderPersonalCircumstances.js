import {
  OFFENDER_PERSONAL_CIRCUMSTANCES_LOAD_ERROR,
  RECEIVE_OFFENDER_PERSONAL_CIRCUMSTANCES
} from '../constants/ActionTypes'

const offenderPersonalCircumstances = (state = { fetching: true, loadError: false, circumstances: [] }, action) => {
  switch (action.type) {
    case RECEIVE_OFFENDER_PERSONAL_CIRCUMSTANCES:
      return {
        ...state,
        fetching: false,
        loadError: false,
        circumstances: action.circumstances
      }
    case OFFENDER_PERSONAL_CIRCUMSTANCES_LOAD_ERROR:
      return {
        ...state,
        fetching: false,
        loadError: true
      }
    default:
      return state
  }
}

export default offenderPersonalCircumstances
