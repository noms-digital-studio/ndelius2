import { combineReducers } from 'redux'
import search from './searchReducer'
import navigate from './navigateReducer'
import analytics from './analylticsReducer'

const app = combineReducers({
    search,
    navigate,
    analytics
})

export default app