import { connect } from 'react-redux'
import { search } from '../actions/search'
import offenderSearch from '../components/offenderSearch'

export default connect(
    state => ({
        searchTerm: state.search.searchTerm,
        probationAreasFilter: Object.getOwnPropertyNames(state.search.probationAreasFilter)
    }),
    dispatch => ({
        search: (searchTerm, probationAreasFilter) => dispatch(search(searchTerm, probationAreasFilter))
    })
)(offenderSearch)