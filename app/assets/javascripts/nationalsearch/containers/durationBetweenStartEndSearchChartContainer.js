import {connect} from 'react-redux'
import analyticsLineChart from '../components/analyticsLineChart'

export default connect(
    state => ({
        numberToCountData: state.analytics.durationBetweenStartEndSearch,
        fetching: state.analytics.fetching
    }),
    () => ({})
)(analyticsLineChart)

