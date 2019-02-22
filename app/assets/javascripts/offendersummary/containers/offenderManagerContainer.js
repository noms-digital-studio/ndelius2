import { connect } from 'react-redux'
import { findActiveOffenderManager } from '../../helpers/offenderManagerHelper'
import offenderManager from '../components/offenderManager'

const mapStateToProps = state => ({
  fetching: state.offenderSummary.offenderDetails.fetching,
  error: state.offenderSummary.offenderDetails.offenderDetailsLoadError,
  offenderManager: state.offenderSummary.offenderDetails.offenderManagers && findActiveOffenderManager(state.offenderSummary.offenderDetails.offenderManagers)
})

export default connect(mapStateToProps, null)(offenderManager)
