import featureSwitch from '../../feature/featureSwitch'

export const ADD_CONTACT = 'ADD_CONTACT'
export const LEGACY_SEARCH = 'LEGACY_SEARCH'
export const SHOW_OFFENDER_DETAILS = 'SHOW_OFFENDER_DETAILS'
export const ADD_NEW_OFFENDER = 'ADD_NEW_OFFENDER'

const recordSearchOutcome = (data) => {
  if (typeof gtag === 'function') {
    gtag('event', data.type, {
      'event_category': 'search',
      'event_label': 'Search Outcome: ' + (data.rankIndex ? data.type + (' (Rank: ' + data.rankIndex + ')') : ''),
      'value': data.rankIndex || 0
    })

    virtualPageLoad(data.type.replace('search-', ''))
  }
}

export const showOffenderDetails = (cookies, offenderId, rankIndex = {}) => dispatch => {
  recordSearchOutcome({ type: 'search-offender-details', rankIndex })

  if (featureSwitch.isEnabled(cookies, 'offenderSummary')) {
    window.location = window.offenderSummaryLink + offenderId
  } else {
    dispatch({ type: SHOW_OFFENDER_DETAILS, offenderId })
  }
}

export const addContact = (offenderId, rankIndex = {}) => dispatch => {
  recordSearchOutcome({ type: 'search-add-contact', rankIndex })
  dispatch({ type: ADD_CONTACT, offenderId })
}

export const legacySearch = () => dispatch => {
  recordSearchOutcome({ type: 'search-legacy-search' })
  dispatch({ type: LEGACY_SEARCH })
}

export const addNewOffender = () => dispatch => {
  recordSearchOutcome({ type: 'search-add-new-offender' })
  dispatch({ type: ADD_NEW_OFFENDER })
}
