import { connect } from 'react-redux'
import searchResultsTitle from '../components/searchResultsTitle'
import { PAGE_SIZE } from '../actions/search'

export default connect(
    state => ({
        pageNumber: state.search.pageNumber,
        total: state.search.total,
        pageSize: PAGE_SIZE,
        resultsReceived: state.search.resultsReceived
    }),
    () => ({})
)(searchResultsTitle)