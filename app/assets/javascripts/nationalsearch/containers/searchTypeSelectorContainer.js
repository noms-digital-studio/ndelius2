import { connect } from 'react-redux'
import searchTypeSelector from '../components/searchTypeSelector'
import {searchTypeChanged, search} from '../actions/search'

export default connect(
    state => ({
        searchType: state.search.searchType,
        searchTerm: state.search.resultsSearchTerm,
        probationAreasFilter: Object.getOwnPropertyNames(state.search.probationAreasFilter)
    }),
    dispatch => ({
        searchTypeChanged: searchType => dispatch(searchTypeChanged(searchType)),
        search: (searchTerm, searchType, probationAreasFilter) =>
            dispatch(search(searchTerm, searchType, probationAreasFilter))
    })
)(searchTypeSelector)