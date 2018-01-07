import { connect } from 'react-redux'
import offenderSearchResults from '../components/offenderSearchResults.jsx'

const mapStateToProps = state => {
    return {
        results: state.results
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