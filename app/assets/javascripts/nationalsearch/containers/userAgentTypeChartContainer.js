import { connect } from 'react-redux'
import analyticsBarChart from '../components/analyticsBarChart'

export default connect(
  state => ({
    numberToCountData: state.analytics.userAgentTypeCounts,
    fetching: state.analytics.fetching
  }), () => ({})
)(analyticsBarChart)
