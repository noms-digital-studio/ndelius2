import { connect } from 'react-redux'
import searchResultsTitle from '../components/searchResultsTitle.jsx'

const mapStateToProps = state => {
    return {
        results: state.results,
        searchTerm: state.searchTerm
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