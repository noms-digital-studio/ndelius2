import { connect } from 'react-redux'
import { search } from '../actions/search'
import offenderSearch from '../components/offenderSearch'

export default connect(
  state => ({
    searchTerm: state.search.searchTerm,
    probationAreasFilter: Object.getOwnPropertyNames(state.search.probationAreasFilter),
    searchType: state.search.searchType
  }),
  dispatch => ({
    search: (searchTerm, searchType, probationAreasFilter) =>
      dispatch(search(searchTerm, searchType, probationAreasFilter))
  })
)(offenderSearch)
