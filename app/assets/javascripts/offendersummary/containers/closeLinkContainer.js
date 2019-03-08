import { connect } from 'react-redux'
import CloseLink from '../components/closeLink'
import { offenderSummaryClose } from '../actions'

export default connect(null, { offenderSummaryClose })(CloseLink)
