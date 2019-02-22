import { connect } from 'react-redux'
import offenderCards from '../components/offenderCards'
import { findActiveOffenderManager } from '../../helpers/offenderManagerHelper'
import { transferInactiveOffender } from '../actions'

const mapStateToProps = state => ({
  offenderConvictions: state.offenderSummary.offenderConvictions,
  offenderId: state.offenderSummary.offenderDetails.offenderId,
  offenderManager: state.offenderSummary.offenderDetails.offenderManagers && findActiveOffenderManager(state.offenderSummary.offenderDetails.offenderManagers)
})
export default connect(mapStateToProps, { transferInactiveOffender })(offenderCards)
