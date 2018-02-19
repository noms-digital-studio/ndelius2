import { connect } from 'react-redux'
import { fetchVisitCounts } from '../actions/analytics'
import analyticsPage from '../components/analyticsPage'

export default connect(
    state => ({
        currentTimeRange: state.analytics.timeRange
    }),
    dispatch => ({
        fetchVisitCounts: (timeRange) => dispatch(fetchVisitCounts(timeRange))
    }),
)(analyticsPage)