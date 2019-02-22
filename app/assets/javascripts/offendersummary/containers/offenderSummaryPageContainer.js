import { connect } from 'react-redux'
import offenderSummaryPage from '../components/offenderSummaryPage'
import { getOffenderDetails } from '../actions'

const mapStateToProps = state => ({
  fetching: state.offenderSummary.offenderDetails.fetching,
  error: state.offenderSummary.offenderDetails.offenderDetailsLoadError,
  childrenFetching: state.offenderSummary.offenderConvictions.fetching || state.offenderSummary.offenderRegistrations.fetching
})

export default connect(mapStateToProps, { getOffenderDetails })(offenderSummaryPage)
