import { connect } from 'react-redux'
import { PAGE_SIZE, search } from '../actions/search'
import pageSelection from '../components/pageSelection'

export default connect(
  state => ({
    searchTerm: state.search.resultsSearchTerm,
    probationAreasFilter: Object.getOwnPropertyNames(state.search.probationAreasFilter),
    pageSize: PAGE_SIZE,
    total: state.search.total,
    pageNumber: state.search.pageNumber,
    searchType: state.search.searchType
  }),
  dispatch => ({
    gotoPage: (searchTerm, searchType, probationAreasFilter, pageNumber) => dispatch(search(searchTerm, searchType, probationAreasFilter, pageNumber))
  })
)(pageSelection)
