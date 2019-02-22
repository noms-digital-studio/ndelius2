import { connect } from 'react-redux'
import offenderSearchPage from '../components/offenderSearchPage'
import { noSavedSearch, savedSearch, search, searchTypeChanged } from '../actions/search'
import localforage from 'localforage'

export default connect(
  state => ({
    firstTimeIn: state.search.firstTimeIn,
    showWelcomeBanner: state.search.showWelcomeBanner,
    searchType: state.search.searchType
  }),
  dispatch => ({
    reloadRecentSearch: () => localforage.getItem('nationalSearch').then(data => {
      if (data && data.when && ((Date.now() - data.when) / 1000 / 60 < window.recentSearchMinutes)) {
        dispatch(savedSearch(data.what, data.filter || {}))
        dispatch(search(data.what, data.type || 'broad', probationAreaCodes(data.filter), data.page))
        dispatch(searchTypeChanged(data.type || 'broad'))
      } else {
        dispatch(noSavedSearch())
      }
    }).catch(err => {
      window.console && console.log(err)
      dispatch(noSavedSearch())
    })
  })
)(offenderSearchPage)

const probationAreaCodes = filter => Object.getOwnPropertyNames(filter || {})
