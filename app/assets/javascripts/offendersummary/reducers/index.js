import { combineReducers } from 'redux'
import offenderDetails from './offenderDetails'
import offenderRegistrations from './offenderRegistrations'

export default combineReducers({
    offenderDetails,
    offenderRegistrations
})