import { connect } from 'react-redux'
import { showOffenderDetails } from '../actions/navigate'
import restrictedOffenderSearchSummary from '../components/restrictedOffenderSearchSummary'
import { withCookies } from 'react-cookie'

export default withCookies(connect(() => ({}),
  (dispatch, ownProperties) => ({
    showOffenderDetails: (offenderId, rankIndex, highlight) => dispatch(showOffenderDetails(ownProperties.cookies, offenderId, rankIndex, highlight))
  })
)(restrictedOffenderSearchSummary))
