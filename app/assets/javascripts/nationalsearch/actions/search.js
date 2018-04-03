import localforage from 'localforage'

export const REQUEST_SEARCH = 'REQUEST_SEARCH'
export const SEARCH_RESULTS = 'SEARCH_RESULTS'
export const CLEAR_RESULTS = 'CLEAR_RESULTS'

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

const performSearch = _.debounce((dispatch, searchTerm, pageNumber) => {
    const encodedSearchTerm = encodeURIComponent(searchTerm)

    localforage.setItem("nationalSearch", {
        when: Date.now(),
        what: searchTerm,
        page: pageNumber
    }).then(() => { }).catch(err => window.console && console.log(err));

    $.getJSON(`searchOffender/${encodedSearchTerm}?pageSize=${PAGE_SIZE}&pageNumber=${pageNumber}`, data => {
        dispatch(searchResults(searchTerm, data, pageNumber))
    });
}, 500);



const search = (searchTerm, pageNumber = 1) => (
    dispatch => {
        if (searchTerm === '') {
            localforage.removeItem("nationalSearch").then(() => { }).catch(err => window.console && console.log(err));
            dispatch(clearResults())
        } else {
            dispatch(requestSearch(searchTerm));
            performSearch(dispatch, searchTerm, pageNumber);
        }
    }
)

export {search}