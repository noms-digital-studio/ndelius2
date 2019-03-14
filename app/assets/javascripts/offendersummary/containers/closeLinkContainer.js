import { connect } from 'react-redux'
import CloseLink from '../components/closeLink'
import { offenderSummaryClose } from '../actions'

const mapStateToProps = state => ({
  offenderId: state.offenderSummary.offenderDetails.offenderId
})
export default connect(mapStateToProps, { offenderSummaryClose })(CloseLink)
