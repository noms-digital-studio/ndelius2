import {
  INCREMENT_MAX_CONVICTIONS_VISIBLE,
  OFFENDER_CONVICTIONS_LOAD_ERROR,
  RECEIVE_OFFENDER_CONVICTIONS
} from '../constants/ActionTypes'

const offenderConvictions = (state = {
  fetching: true,
  loadError: false,
  convictions: [],
  maxConvictionsVisible: 3
}, action) => {
  switch (action.type) {
    case RECEIVE_OFFENDER_CONVICTIONS:
      return {
        ...state,
        fetching: false,
        loadError: false,
        convictions: action.convictions
      }
    case OFFENDER_CONVICTIONS_LOAD_ERROR:
      return {
        ...state,
        fetching: false,
        loadError: true
      }
    case INCREMENT_MAX_CONVICTIONS_VISIBLE:
      return {
        ...state,
        maxConvictionsVisible: state.maxConvictionsVisible + action.incrementBy
      }

    default:
      return state
  }
}

export default offenderConvictions
