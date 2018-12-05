import { connect } from 'react-redux'
import offenderSummaryPage from '../components/offenderSummaryPage'
import { getOffenderDetails } from '../actions'

const mapStateToProps = state => ({
    fetching: state.offenderSummary.offenderDetails.fetching
})

export default connect(
    mapStateToProps,
    {getOffenderDetails}
)(offenderSummaryPage)