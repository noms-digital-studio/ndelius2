import { connect } from 'react-redux'
import analyticsPieChart from '../components/analyticsPieChart'

export default connect(
  state => ({
    numberToCountData: state.analytics.searchFieldMatch,
    fetching: state.analytics.fetching,
    labelMapper
  }), () => ({})
)(analyticsPieChart)

export const labelMapper = data => Object.getOwnPropertyNames(data).map(name => {
  const names = name.split('.')
  if (names[0] === 'offenderAliases') {
    return names[names.length - 1] + ' (alias)'
  }
  return names[names.length - 1]
})
