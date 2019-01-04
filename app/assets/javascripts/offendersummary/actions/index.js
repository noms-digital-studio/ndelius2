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

const receiveOffenderConvictions = convictions => ({
    type: types.RECEIVE_OFFENDER_CONVICTIONS,
    convictions
})

const offenderConvictionsLoadFailure = error => ({
    type: types.OFFENDER_CONVICTIONS_LOAD_ERROR,
    error
})

const incrementMaxConvictionsVisibleCount = incrementBy => ({
    type: types.INCREMENT_MAX_CONVICTIONS_VISIBLE,
    incrementBy
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

export const getOffenderConvictions = () => dispatch => {
    offender.getConvictions(
        details => dispatch(receiveOffenderConvictions(details)),
        error => dispatch(offenderConvictionsLoadFailure(error)))
}

export const showMoreConvictions = () => dispatch => {
    dispatch(incrementMaxConvictionsVisibleCount(10))
}