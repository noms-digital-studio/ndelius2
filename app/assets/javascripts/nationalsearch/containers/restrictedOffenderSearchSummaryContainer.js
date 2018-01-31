import { connect } from 'react-redux'
import {showOffenderDetails} from '../actions/navigate'
import restrictedOffenderSearchSummary from '../components/restrictedOffenderSearchSummary'

export default connect(
    () => ({}),
    dispatch => ({
        showOffenderDetails: (offenderId) => dispatch(showOffenderDetails(offenderId))
    })
)(restrictedOffenderSearchSummary)