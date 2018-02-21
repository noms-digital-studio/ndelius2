import {connect} from 'react-redux'
import analyticsPieChart from '../components/analyticsPieChart'

export default connect(
    state => ({
        numberToCountData: onlySearchEvents(state.analytics.eventOutcome),
        fetching: state.analytics.fetching,
        labelMapper
    }),
    () => ({})
)(analyticsPieChart)

export const labelMapper = data => Object.getOwnPropertyNames(data).map(eventType => eventTypeMap[eventType])

export const onlySearchEvents = eventOutcome => Object.getOwnPropertyNames(eventOutcome).reduce((outcomes, name) => {
    if (Object.getOwnPropertyNames(eventTypeMap).indexOf(name) !== -1) {
        outcomes[name] = eventOutcome[name]
    }
    return outcomes
}, {})

const eventTypeMap = {
    'search-index': 'No search',
    'search-request': 'Search abandoned',
    'search-results': 'Search abandoned',
    'search-offender-details': 'Offender selected',
    'search-add-contact': 'Offender add contact',
    'search-legacy-search': 'Old search',
}