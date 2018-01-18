import { connect } from 'react-redux'
import offenderSearchResults from '../components/offenderSearchResults'

const mapStateToProps = state => {
    return {
        results: state.search.results
    }
}

const mapDispatchToProps = dispatch => {
    return {}
}

const offenderSearchResultsContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(offenderSearchResults);

export default offenderSearchResultsContainer