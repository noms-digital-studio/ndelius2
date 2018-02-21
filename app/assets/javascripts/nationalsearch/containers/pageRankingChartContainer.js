import {connect} from 'react-redux'
import analyticsBarChart from '../components/analyticsBarChart'
import {PAGE_SIZE} from '../actions/search'

export default connect(
    state => ({
        numberToCountData: byPageRanking(state.analytics.rankGrouping),
        fetching: state.analytics.fetching
    }),
    () => ({})
)(analyticsBarChart)

export const byPageRanking = rankGrouping => groupByPage(toPairList(rankGrouping))


const toPairList = rankGrouping => Object.getOwnPropertyNames(rankGrouping).map(name => ({
    rankIndex: name,
    count: rankGrouping[name]
}))

const groupByPage = (pairs) => (pairs.reduce((grouped, item) => {
        const key = toPage(item.rankIndex);
        grouped[key] = grouped[key] || 0;
        grouped[key] = grouped[key] + item.count
        return grouped;
    }, {})
)

const toPage = rankIndex => Math.floor( (rankIndex - 1) / PAGE_SIZE) + 1
