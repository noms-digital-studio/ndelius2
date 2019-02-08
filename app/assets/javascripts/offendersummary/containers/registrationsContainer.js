import { connect } from 'react-redux'
import registrations from '../components/registrations'
import { getOffenderRegistrations, viewOffenderRegistrations } from '../actions'

const mapStateToProps = state => ({
  registrations: state.offenderSummary.offenderRegistrations.registrations,
  offenderId: state.offenderSummary.offenderDetails.offenderId,
  fetching: state.offenderSummary.offenderRegistrations.fetching,
  error: state.offenderSummary.offenderRegistrations.loadError

})
export default connect(
  mapStateToProps,
  { getOffenderRegistrations, viewOffenderRegistrations }
)(registrations)
