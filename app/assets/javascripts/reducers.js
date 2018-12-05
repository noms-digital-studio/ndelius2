import { combineReducers } from 'redux'
import search from './nationalsearch/reducers/searchReducer'
import navigate from './nationalsearch/reducers/navigateReducer'
import analytics from './nationalsearch/reducers/analylticsReducer'
import localStorage from './nationalsearch/reducers/localStorageReducer'
import offenderSummary from './offendersummary/reducers'


const app = combineReducers({
    search,
    navigate,
    analytics,
    localStorage,
    offenderSummary
})

export default app