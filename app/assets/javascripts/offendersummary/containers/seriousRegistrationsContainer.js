import { connect } from 'react-redux'
import seriousRegistrations from '../components/seriousRegistrations'

const mapStateToProps = state => ({ registrations: state.offenderSummary.offenderRegistrations.registrations })
export default connect(mapStateToProps, null)(seriousRegistrations)
