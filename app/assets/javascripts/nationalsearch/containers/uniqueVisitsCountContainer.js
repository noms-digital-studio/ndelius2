import {connect} from 'react-redux'
import analyticsCount from '../components/analyticsCount'

export default connect(
    state => ({
        count: state.analytics.uniqueUserVisits,
        fetching: state.analytics.fetching
    }),
    () => ({})
)(analyticsCount)