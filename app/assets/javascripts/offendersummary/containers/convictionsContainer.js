import { connect } from 'react-redux'
import convictions from '../components/convictions'
import { getOffenderConvictions, showMoreConvictions, viewOffenderEvent } from '../actions'

const mapStateToProps = state => ({
  convictions: state.offenderSummary.offenderConvictions.convictions,
  offenderId: state.offenderSummary.offenderDetails.offenderId,
  fetching: state.offenderSummary.offenderConvictions.fetching,
  error: state.offenderSummary.offenderConvictions.loadError,
  maxConvictionsVisible: state.offenderSummary.offenderConvictions.maxConvictionsVisible
})
export default connect(
  mapStateToProps,
  { getOffenderConvictions, showMoreConvictions, viewOffenderEvent }
)(convictions)
