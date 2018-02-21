import {connect} from 'react-redux'
import analyticsBarChart from '../components/analyticsBarChart'
import {PAGE_SIZE} from '../actions/search'

export default connect(
    state => ({
        numberToCountData: byTopPagesRanking(state.analytics.rankGrouping),
        fetching: state.analytics.fetching
    }),
    () => ({})
)(analyticsBarChart)

export const byTopPagesRanking = rankGrouping => Object.getOwnPropertyNames(rankGrouping).reduce((grouping, name) => {
    if (name  <= PAGE_SIZE * 2) {
        grouping[name] = rankGrouping[name]
    }
    return grouping
}, {})
