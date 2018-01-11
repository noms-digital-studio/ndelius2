import { connect } from 'react-redux'
import {showOffenderDetails} from '../actions/navigate'
import offenderSummaryTitle from '../components/offenderSummaryTitle'

export default connect(
    () => ({}),
    dispatch => ({
        showOffenderDetails: (offenderId) => dispatch(showOffenderDetails(offenderId))
    })
)(offenderSummaryTitle)