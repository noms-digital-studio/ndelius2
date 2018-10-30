export const ADD_CONTACT = 'ADD_CONTACT'
export const LEGACY_SEARCH = 'LEGACY_SEARCH'
export const SHOW_OFFENDER_DETAILS = 'SHOW_OFFENDER_DETAILS'
export const ADD_NEW_OFFENDER = 'ADD_NEW_OFFENDER'

export const showOffenderDetails = (offenderId) => (
    dispatch => {
        dispatch({type: SHOW_OFFENDER_DETAILS, offenderId})
    }
)

export const addContact = (offenderId) => (
    dispatch => {
        dispatch({type: ADD_CONTACT, offenderId})
    }
)

export const legacySearch = () => (
    dispatch => {
        dispatch({type: LEGACY_SEARCH})
    }
)

export const addNewOffender = () => (
    dispatch => {
        dispatch({type: ADD_NEW_OFFENDER})
    }
)


