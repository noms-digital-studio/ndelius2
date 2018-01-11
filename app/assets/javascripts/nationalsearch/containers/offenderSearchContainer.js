import { connect } from 'react-redux'
import { search } from '../actions/search'
import offenderSearch from '../components/offenderSearch.jsx'

const mapStateToProps = (state) => {
    return {
        searchTerm: state.searchTerm
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        onClick: (searchTerm) => {
            search(dispatch, searchTerm)
        }
    }
};

const offenderSearchContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(offenderSearch);

export default offenderSearchContainer