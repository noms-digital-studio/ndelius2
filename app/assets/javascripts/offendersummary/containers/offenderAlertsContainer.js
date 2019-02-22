import { connect } from 'react-redux'
import offenderAlerts from '../components/offenderAlerts'

const mapStateToProps = state => ({
  offenderConvictions: state.offenderSummary.offenderConvictions,
  registrations: state.offenderSummary.offenderRegistrations.registrations
})
export default connect(mapStateToProps, null)(offenderAlerts)
