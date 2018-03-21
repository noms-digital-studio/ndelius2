export const ADD_CONTACT = 'ADD_CONTACT'
export const LEGACY_SEARCH = 'LEGACY_SEARCH'
export const SHOW_OFFENDER_DETAILS = 'SHOW_OFFENDER_DETAILS'
export const ADD_NEW_OFFENDER = 'ADD_NEW_OFFENDER'

const recordSearchOutcome = (data, callback) => {
    $.ajax({
        url: 'nationalSearch/recordSearchOutcome',
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        complete: callback
    })
}
export const showOffenderDetails = (offenderId, rankIndex, highlight = {}) => (
    dispatch => {
        recordSearchOutcome({ type: 'search-offender-details', rankIndex, fieldMatch: highlightToFieldMatch(highlight) })
        dispatch({type: SHOW_OFFENDER_DETAILS, offenderId})
    }
)

export const addContact = (offenderId, rankIndex, highlight = {}) => (
    dispatch => {
        recordSearchOutcome({ type: 'search-add-contact', rankIndex, fieldMatch: highlightToFieldMatch(highlight) })
        dispatch({type: ADD_CONTACT, offenderId})
    }
)

export const legacySearch = () => (
    dispatch => {
        recordSearchOutcome({ type: 'search-legacy-search'})
        dispatch({type: LEGACY_SEARCH})
    }
)

export const addNewOffender = () => (
    dispatch => {
        recordSearchOutcome({ type: 'search-add-new-offender'})
        dispatch({type: ADD_NEW_OFFENDER})
    }
)

export const addFeedback = (feedback, router) => (
    () => {
        recordSearchOutcome({ type: 'search-feedback', feedback}, () => router.goBack())
    }
)

const highlightToFieldMatch = highlight => Object.getOwnPropertyNames(highlight)

