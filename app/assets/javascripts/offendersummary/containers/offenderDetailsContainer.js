import { connect } from 'react-redux'
import offenderDetails from '../components/offenderDetails'
import { viewOffenderAddresses, viewOffenderAliases } from '../actions'

const mapStateToProps = state => ({
  offenderDetails: state.offenderSummary.offenderDetails
})
export default connect(mapStateToProps, { viewOffenderAliases, viewOffenderAddresses })(offenderDetails)
