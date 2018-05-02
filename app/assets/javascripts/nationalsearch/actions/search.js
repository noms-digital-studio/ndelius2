export const REQUEST_SEARCH = 'REQUEST_SEARCH'
export const SEARCH_RESULTS = 'SEARCH_RESULTS'
export const CLEAR_RESULTS = 'CLEAR_RESULTS'
export const NO_SAVED_SEARCH = 'NO_SAVED_SEARCH'
export const SAVED_SEARCH = 'SAVED_SEARCH'
export const ADD_AREA_FILTER = 'ADD_AREA_FILTER'
export const REMOVE_AREA_FILTER = 'REMOVE_AREA_FILTER'

export const PAGE_SIZE = 10;

const requestSearch = (searchTerm) => ({
        type: REQUEST_SEARCH,
        searchTerm
    })

const searchResults = (searchTerm, results, pageNumber) => ({
        type: SEARCH_RESULTS,
        searchTerm,
        results,
        pageNumber
    })

const clearResults = () => ({type: CLEAR_RESULTS})
const addAreaFilter = (probationAreaCode, probationAreaDescription) => ({type: ADD_AREA_FILTER, probationAreaCode, probationAreaDescription})
const removeAreaFilter = probationAreaCode => ({type: REMOVE_AREA_FILTER, probationAreaCode})

const performSearch = _.debounce((dispatch, searchTerm, probationAreasFilter, pageNumber) => {
    const encodedSearchTerm = encodeURIComponent(searchTerm)
    const toAreaFilter = () => probationAreasFilter.join(',')

    $.getJSON(`searchOffender/${encodedSearchTerm}?pageSize=${PAGE_SIZE}&pageNumber=${pageNumber}&areasFilter=${toAreaFilter()}`, data => {
        dispatch(searchResults(searchTerm, data, pageNumber))
    });
}, 500);



const search = (searchTerm, probationAreasFilter = [], pageNumber = 1) => (
    dispatch => {
        if (searchTerm === '') {
            dispatch(clearResults())
        } else {
            dispatch(requestSearch(searchTerm));
            performSearch(dispatch, searchTerm, probationAreasFilter, pageNumber);
        }
    }
)

const noSavedSearch = () => ({type: NO_SAVED_SEARCH})
const savedSearch = (searchTerm, probationAreasFilter) => ({type: SAVED_SEARCH, searchTerm, probationAreasFilter})

export {search, noSavedSearch, savedSearch, addAreaFilter, removeAreaFilter}