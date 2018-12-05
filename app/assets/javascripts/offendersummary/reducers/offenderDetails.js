import { RECEIVE_OFFENDER_DETAILS } from '../constants/ActionTypes'


const offenderDetails = (state = {fetching: true}, action) => {
    switch (action.type) {
        case RECEIVE_OFFENDER_DETAILS:
            return {
                ...state,
                fetching: false,
                ...action.details
            }
        default:
            return state
    }
}

export default offenderDetails