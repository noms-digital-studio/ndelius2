import { combineReducers } from 'redux'
import offenderDetails from './offenderDetails'
import offenderRegistrations from './offenderRegistrations'
import offenderConvictions from './offenderConvictions'
import nextAppointment from './nextAppointment'
import offenderPersonalCircumstances from './offenderPersonalCircumstances'
import navigate from './navigate'

export default combineReducers({
    offenderDetails,
    offenderRegistrations,
    offenderConvictions,
    nextAppointment,
    offenderPersonalCircumstances,
    navigate
})