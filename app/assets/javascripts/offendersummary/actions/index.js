import offender from '../api/offender'
import * as types from '../constants/ActionTypes'
import { trackEvent } from '../../helpers/analyticsHelper'

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

const receiveNextAppointment = appointment => ({
  type: types.RECEIVE_NEXT_APPOINTMENT,
  appointment
})

const nextAppointmentLoadFailure = error => ({
  type: types.NEXT_APPOINTMENT_LOAD_ERROR,
  error
})

const receiveNoNextAppointment = () => ({
  type: types.RECEIVE_NO_NEXT_APPOINTMENT
})

const receiveOffenderPersonalCircumstances = circumstances => ({
  type: types.RECEIVE_OFFENDER_PERSONAL_CIRCUMSTANCES,
  circumstances
})

const offenderPersonalCircumstancesLoadFailure = error => ({
  type: types.OFFENDER_PERSONAL_CIRCUMSTANCES_LOAD_ERROR,
  error
})

const navigateToViewOffenderAliases = offenderId => ({
  type: types.NAVIGATE_TO_VIEW_OFFENDER_ALIASES,
  offenderId
})

const navigateToViewOffenderAddresses = offenderId => ({
  type: types.NAVIGATE_TO_VIEW_OFFENDER_ADDRESS_HISTORY,
  offenderId
})

const navigateToViewOffenderPersonalCircumstances = offenderId => ({
  type: types.NAVIGATE_TO_VIEW_OFFENDER_PERSONAL_CIRCUMSTANCES,
  offenderId
})

const navigateToViewOffenderRegistrations = offenderId => ({
  type: types.NAVIGATE_TO_VIEW_OFFENDER_REGISTRATIONS,
  offenderId
})

const navigateToViewOffenderEvent = (offenderId, eventId) => ({
  type: types.NAVIGATE_TO_VIEW_OFFENDER_EVENT,
  offenderId,
  eventId
})

const navigateToTransferInactiveOffender = offenderId => ({
  type: types.NAVIGATE_TO_TRANSFER_INACTIVE_OFFENDER,
  offenderId
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
  trackEvent('show-more', 'Offender summary > Events', 'Show more events')
  dispatch(incrementMaxConvictionsVisibleCount(10))
}

export const getNextAppointment = () => dispatch => {
  offender.getNextAppointment(
    details => dispatch(receiveNextAppointment(details)),
    () => dispatch(receiveNoNextAppointment()),
    error => dispatch(nextAppointmentLoadFailure(error)))
}

export const getOffenderPersonalCircumstances = () => dispatch => {
  offender.getPersonalCircumstances(
    details => dispatch(receiveOffenderPersonalCircumstances(details)),
    error => dispatch(offenderPersonalCircumstancesLoadFailure(error)))
}

export const viewOffenderAliases = offenderId => dispatch => {
  trackEvent('delius-link', 'Offender summary > Offender details', 'View aliases')
  dispatch(navigateToViewOffenderAliases(offenderId))
}

export const viewOffenderAddresses = offenderId => dispatch => {
  trackEvent('delius-link', 'Offender summary > Offender details > Contact details', 'View address history')
  dispatch(navigateToViewOffenderAddresses(offenderId))
}

export const viewOffenderPersonalCircumstances = offenderId => dispatch => {
  trackEvent('delius-link', 'Offender summary > Offender manager > Personal Circumstances', 'View more personal circumstances')
  dispatch(navigateToViewOffenderPersonalCircumstances(offenderId))
}

export const viewOffenderRegistrations = offenderId => dispatch => {
  trackEvent('delius-link', 'Offender summary > Active registers and warnings', 'View more registers and warnings')
  dispatch(navigateToViewOffenderRegistrations(offenderId))
}

export const viewOffenderEvent = (offenderId, eventId) => dispatch => {
  trackEvent('delius-link', 'Offender summary > Events', 'View offender event')
  dispatch(navigateToViewOffenderEvent(offenderId, eventId))
}

export const transferInactiveOffender = offenderId => dispatch => {
  trackEvent('delius-link', 'Offender summary > Not current', 'Transfer in')
  dispatch(navigateToTransferInactiveOffender(offenderId))
}
