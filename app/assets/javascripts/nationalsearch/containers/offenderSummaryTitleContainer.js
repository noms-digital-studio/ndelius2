import { connect } from 'react-redux'
import {showOffenderDetails} from '../actions/navigate'
import offenderSummaryTitle from '../components/offenderSummaryTitle'

export default connect(
    () => ({}),
    dispatch => ({
        showOffenderDetails: (offenderId, rankIndex) => dispatch(showOffenderDetails(offenderId, rankIndex))
    })
)(offenderSummaryTitle)