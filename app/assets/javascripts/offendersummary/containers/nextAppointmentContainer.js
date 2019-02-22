import { connect } from 'react-redux'
import nextAppointment from '../components/nextAppointment'
import { getNextAppointment } from '../actions'

const mapStateToProps = state => ({
  appointment: state.offenderSummary.nextAppointment.appointment,
  noNextAppointment: state.offenderSummary.nextAppointment.noNextAppointment,
  fetching: state.offenderSummary.nextAppointment.fetching,
  error: state.offenderSummary.nextAppointment.loadError

})
export default connect(
  mapStateToProps,
  { getNextAppointment }
)(nextAppointment)
