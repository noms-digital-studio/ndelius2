import { combineReducers } from 'redux'
import search from './searchReducer'
import navigate from './navigateReducer'

const app = combineReducers({
    search,
    navigate
})

export default app