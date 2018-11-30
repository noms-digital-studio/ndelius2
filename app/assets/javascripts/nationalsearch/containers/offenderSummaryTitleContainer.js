import { connect } from 'react-redux'
import {showOffenderDetails} from '../actions/navigate'
import offenderSummaryTitle from '../components/offenderSummaryTitle'
import { withCookies } from 'react-cookie';

export default withCookies(connect(
    () => ({}),
    (dispatch, ownProperties) => ({
        showOffenderDetails: (offenderId, rankIndex, highlight) => dispatch(showOffenderDetails(ownProperties.cookies, offenderId, rankIndex, highlight))
    })
)(offenderSummaryTitle))