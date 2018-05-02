import {connect} from 'react-redux'
import suggestions from '../components/suggestions'
import {search} from '../actions/search'

export default connect(
    state => ({
        suggestions: state.search.suggestions,
        searchTerm: state.search.resultsSearchTerm,
        probationAreasFilter: Object.getOwnPropertyNames(state.search.probationAreasFilter)
    }),
    dispatch => ({
        search: (searchTerm, probationAreasFilter) => dispatch(search(searchTerm, probationAreasFilter))
    })
)(suggestions)