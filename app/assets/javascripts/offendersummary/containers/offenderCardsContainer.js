import { connect } from 'react-redux'
import offenderCards from '../components/offenderCards'
import { findActiveOffenderManager } from '../../helpers/offenderManagerHelper'

const mapStateToProps = state => ({
  offenderConvictions: state.offenderSummary.offenderConvictions,
  offenderManager: state.offenderSummary.offenderDetails.offenderManagers && findActiveOffenderManager(state.offenderSummary.offenderDetails.offenderManagers)
})
export default connect(mapStateToProps, null)(offenderCards)
