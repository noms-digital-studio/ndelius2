import { connect } from 'react-redux'
import { search } from '../actions/search'
import offenderSearch from '../components/offenderSearch'

export default connect(
    state => ({
        searchTerm: state.search.searchTerm
    }),
    dispatch => ({
        search: (searchTerm) => search(dispatch, searchTerm)
    })
)(offenderSearch)