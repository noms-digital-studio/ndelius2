import { connect } from 'react-redux'
import { changeTimeRange } from '../actions/analytics'
import analyticsTimeRangeLink from '../components/analyticsTimeRangeLink'

export default connect(
    state => ({
        currentTimeRange: state.analytics.timeRange
    }),
    dispatch => ({
        changeTimeRange: (timeRange) => dispatch(changeTimeRange(timeRange))
    }),
)(analyticsTimeRangeLink)