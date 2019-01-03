import offender from '../api/offender'
import * as types from '../constants/ActionTypes'

const receiveOffenderDetails = details => ({
    type: types.RECEIVE_OFFENDER_DETAILS,
    details
})

const offenderDetailsLoadFailure = error => ({
    type: types.OFFENDER_DETAILS_LOAD_ERROR,
    error
})

const receiveOffenderRegistrations = registrations => ({
    type: types.RECEIVE_OFFENDER_REGISTRATIONS,
    registrations
})

const offenderRegistrationsLoadFailure = error => ({
    type: types.OFFENDER_REGISTRATIONS_LOAD_ERROR,
    error
})

export const getOffenderDetails = () => dispatch => {
    offender.getDetails(
        details => dispatch(receiveOffenderDetails(details)),
     error => dispatch(offenderDetailsLoadFailure(error)))
}
export const getOffenderRegistrations = () => dispatch => {
    offender.getRegistrations(
        details => dispatch(receiveOffenderRegistrations(details)),
     error => dispatch(offenderRegistrationsLoadFailure(error)))
}