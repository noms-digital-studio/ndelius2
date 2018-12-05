import offender from '../api/offender'
import * as types from '../constants/ActionTypes'

const receiveOffenderDetails = details => ({
    type: types.RECEIVE_OFFENDER_DETAILS,
    details
})

export const getOffenderDetails = () => dispatch => {
    offender.getDetails(details => {
        dispatch(receiveOffenderDetails(details))
    })
}