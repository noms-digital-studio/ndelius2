import { connect } from 'react-redux'
import { changeYear, fetchSatisfactionCounts } from '../actions/analytics'
import satisfactionPage from '../components/satisfactionPage'

export default connect(
  state => ({
    currentTimeRange: state.analytics.timeRange,
    satisfactionCounts: state.analytics.satisfactionCounts,
    yearNumber: state.analytics.yearNumber
  }),
  dispatch => ({
    fetchSatisfactionCounts: () => dispatch(fetchSatisfactionCounts()),
    changeYear: yearNumber => dispatch(changeYear(yearNumber))
  })
)(satisfactionPage)
