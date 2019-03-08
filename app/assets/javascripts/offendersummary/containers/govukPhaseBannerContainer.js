import { connect } from 'react-redux'
import GovUkPhaseBanner from '../components/govukPhaseBanner'
import { offenderSummaryViewPrevious } from '../actions'

const mapStateToProps = state => ({
  offenderId: state.offenderSummary.offenderDetails.offenderId
})
export default connect(mapStateToProps, { offenderSummaryViewPrevious })(GovUkPhaseBanner)
