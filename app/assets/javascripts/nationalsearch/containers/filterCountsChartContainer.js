import {connect} from 'react-redux'
import analyticsPieChart from '../components/analyticsPieChart'

export default connect(
    state => ({
        numberToCountData: state.analytics.filterCounts,
        fetching: state.analytics.fetching,
        labelMapper
    }),
    () => ({})
)(analyticsPieChart)

export const labelMapper = data => Object.getOwnPropertyNames(data).map(eventType => eventTypeMap[eventType])

const eventTypeMap = {
    'hasUsedMyProvidersFilterCount': 'My providers',
    'hasUsedOtherProvidersFilterCount': 'Other providers',
    'hasUsedBothProvidersFilterCount': 'Both sets',
    'hasNotUsedFilterCount': 'No filter'
}