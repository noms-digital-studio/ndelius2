import { connect } from 'react-redux'
import searchResultsTitle from '../components/searchResultsTitle.jsx'
import { PAGE_SIZE } from '../actions/search'

const mapStateToProps = state => {
    return {
        pageNumber: state.search.pageNumber,
        total: state.search.total,
        pageSize: PAGE_SIZE,
        searchTerm: state.search.searchTerm
    }
}

const mapDispatchToProps = dispatch => {
    return {}
}

const searchResultsTitleContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(searchResultsTitle);

export default searchResultsTitleContainer