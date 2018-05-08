import {Component} from 'react'
import UniqueVisitsCounts from '../containers/uniqueVisitsCountContainer';
import AllVisitsCounts from '../containers/allVisitsCountContainer';
import AllSearchesCounts from '../containers/allSearchesCountContainer';
import TimeRangeLink from '../containers/analyticsTimeRangeLinkContainer';
import TopPagesRankingChart from '../containers/topPagesRankingChartContainer';
import PageRankingChart from '../containers/pageRankingChartContainer';
import SearchOutcomeChart from '../containers/searchOutcomeChartContainer';
import DurationBetweenStartEndSearchChart from '../containers/durationBetweenStartEndSearchChartContainer';
import FilterCountsChart from '../containers/filterCountsChartContainer';
import SearchFieldMatchChart from '../containers/searchFieldMatchChartContainer';
import GovUkPhaseBanner from './govukPhaseBanner';
import {LAST_HOUR, TODAY, THIS_WEEK, LAST_SEVEN_DAYS, LAST_THIRTY_DAYS, THIS_YEAR, ALL} from '../actions/analytics'
import PropTypes from 'prop-types'

class AnalyticsPage extends Component {
    constructor(props) {
        super(props);
    }
    componentWillMount() {
        fetch(this.props)
        const interval = setInterval(() => fetch(this.props), 30000)
        if (interval.unref) {interval.unref()} // when running in mocha/node unref so test doesn't hang
    }
    componentWillReceiveProps(nextProps) {
        fetch(nextProps)
    }
    onClickRefresh() {
        fetch(this.props)
    }

    render() {
        return (
            <div>
                <GovUkPhaseBanner basicVersion={true}/>
                <h1 className="heading-xlarge no-margin-bottom">National Search Analytics</h1>
                <div className="grid-row margin-top">
                    <div className="column-two-thirds">
                        <UniqueVisitsCounts description='Unique visits'/>
                        <AllVisitsCounts description='All visits'/>
                        <AllSearchesCounts description='All searches'/>
                        <TopPagesRankingChart description='Offender details clicks - ranking within top 2 pages' label='Clicks per rank within page'/>
                        <PageRankingChart description='Offender details clicks - ranking across pages' label='Clicks per page'/>
                        <SearchOutcomeChart description='Search visit outcome' label='Outcome'/>
                        <DurationBetweenStartEndSearchChart description='Duration to find offender' label='From search to clicking offender' xAxesLabel='Up to minutes' />
                        <SearchFieldMatchChart description='Top field matches' label='Search term'/>
                        <FilterCountsChart description='Final search in session filter' label='Filter usage'/>
                    </div>
                    <div className="column-one-third">
                        <NavigationPanel/>
                        <input className="button margin-top" type="button" value="Refresh" onClick={() => this.onClickRefresh()}/>
                    </div>
                </div>
            </div>)
    }
}

const fetch = props => {
    const {fetchVisitCounts, currentTimeRange} = props;
    fetchVisitCounts(currentTimeRange)
}

const NavigationPanel = () => (
            <nav className="js-stick-at-top-when-scrolling">
                <div className="nav-header"/>
                <h3 className="heading-medium no-margin-top no-margin-bottom">Analytics for</h3>
                <TimeRangeLink timeRange={LAST_HOUR}>Last hour</TimeRangeLink><br/>
                <TimeRangeLink timeRange={TODAY}>Today</TimeRangeLink><br/>
                <TimeRangeLink timeRange={THIS_WEEK}>This week</TimeRangeLink><br/>
                <TimeRangeLink timeRange={LAST_SEVEN_DAYS}>Last 7 days</TimeRangeLink><br/>
                <TimeRangeLink timeRange={LAST_THIRTY_DAYS}>Last 30 days</TimeRangeLink><br/>
                <TimeRangeLink timeRange={THIS_YEAR}>This year</TimeRangeLink><br/>
                <TimeRangeLink timeRange={ALL}>All time</TimeRangeLink><br/>
            </nav>
        )

AnalyticsPage.propTypes = {
    fetchVisitCounts: PropTypes.func.isRequired,
    currentTimeRange: PropTypes.string.isRequired
}


export default AnalyticsPage;