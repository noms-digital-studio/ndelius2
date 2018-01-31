import {connect} from 'react-redux'
import suggestions from '../components/suggestions'
import {search} from '../actions/search'

export default connect(
    state => ({
        suggestions: state.search.suggestions,
        searchTerm: state.search.resultsSearchTerm
    }),
    dispatch => ({
        search: (searchTerm) => dispatch(search(searchTerm))
    })
)(suggestions)