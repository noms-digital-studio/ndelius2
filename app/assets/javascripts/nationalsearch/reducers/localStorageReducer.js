import {
    ADD_AREA_FILTER, CLEAR_RESULTS, REMOVE_AREA_FILTER, REQUEST_SEARCH, SAVED_SEARCH,
    SEARCH_RESULTS
} from '../actions/search'
import localforage from 'localforage'
import {setIn, removeIn} from '../../helpers/immutable'


const localStorage = (state = {searchTerm: '',  probationAreasFilter: {}, pageNumber: 1}, action) => {
    switch (action.type) {
        case REQUEST_SEARCH:
            return saveToLocalStorage({
                ...state,
                searchTerm: action.searchTerm,
            })
        case SEARCH_RESULTS:
            return saveToLocalStorage({
                ...state,
                pageNumber: action.pageNumber,
            })
        case SAVED_SEARCH:
            return {
                ...state,
                searchTerm: action.searchTerm,
                probationAreasFilter: action.probationAreasFilter
            };
        case CLEAR_RESULTS:
            removeFromLocalStorage()
            return {
                ...state,
                searchTerm: '',
                pageNumber: 1
            };
        case ADD_AREA_FILTER:
            return saveToLocalStorage({
                ...state,
                probationAreasFilter: setIn(state.probationAreasFilter, action.probationAreaCode, action.probationAreaDescription)
            })
        case REMOVE_AREA_FILTER:
            return saveToLocalStorage({
                ...state,
                probationAreasFilter: removeIn(state.probationAreasFilter, action.probationAreaCode)
            })
        default:
            return state
    }
};

const saveToLocalStorage = state =>  {
    localforage.setItem("nationalSearch", {
        when: Date.now(),
        what: state.searchTerm,
        page: state.pageNumber,
        filter: state.probationAreasFilter
    }).then(() => { }).catch(err => window.console && console.log(err))
    return state
}

const removeFromLocalStorage = () =>
    localforage.removeItem("nationalSearch")
    .then(() => { }).catch(err => window.console && console.log(err));

export default localStorage

