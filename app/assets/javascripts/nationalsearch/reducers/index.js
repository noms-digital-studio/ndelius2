import { combineReducers } from 'redux'
import search from './searchReducer'
import navigate from './navigateReducer'
import analytics from './analylticsReducer'
import localStorage from './localStorageReducer'

const app = combineReducers({
    search,
    navigate,
    analytics,
    localStorage
})

export default app