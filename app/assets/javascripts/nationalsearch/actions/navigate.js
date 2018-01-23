export const ADD_CONTACT = 'ADD_CONTACT'
export const LEGACY_SEARCH = 'LEGACY_SEARCH'
export const SHOW_OFFENDER_DETAILS = 'SHOW_OFFENDER_DETAILS'
export const ADD_NEW_OFFENDER = 'ADD_NEW_OFFENDER'


export const addContact = offenderId => ({type: ADD_CONTACT, offenderId})
export const legacySearch = () => ({type: LEGACY_SEARCH})
export const showOffenderDetails = offenderId => ({type: SHOW_OFFENDER_DETAILS, offenderId})
export const addNewOffender = () => ({type: ADD_NEW_OFFENDER})

