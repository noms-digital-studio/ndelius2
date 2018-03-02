import { connect } from 'react-redux'
import {showOffenderDetails} from '../actions/navigate'
import restrictedOffenderSearchSummary from '../components/restrictedOffenderSearchSummary'

export default connect(
    () => ({}),
    dispatch => ({
        showOffenderDetails: (offenderId, rankIndex, highlight) => dispatch(showOffenderDetails(offenderId, rankIndex, highlight))
    })
)(restrictedOffenderSearchSummary)