import { combineReducers } from 'redux'
import offenderDetails from './offenderDetails'
import offenderRegistrations from './offenderRegistrations'
import offenderConvictions from './offenderConvictions'

export default combineReducers({
    offenderDetails,
    offenderRegistrations,
    offenderConvictions
})