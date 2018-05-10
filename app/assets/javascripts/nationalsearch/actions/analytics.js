import moment from 'moment'

export const FILTER_COUNTS = 'FILTER_COUNTS'
export const SATISFACTION_COUNTS = 'SATISFACTION_COUNTS'
export const TIME_RANGE = 'TIME_RANGE'
export const UNIQUE_USER_VISITS = 'UNIQUE_USER_VISITS'
export const ALL_VISITS = 'ALL_VISITS'
export const ALL_SEARCHES = 'ALL_SEARCHES'
export const RANK_GROUPING = 'RANK_GROUPING'
export const EVENT_OUTCOME = 'EVENT_OUTCOME'
export const DURATION_BETWEEN_START_END_SEARCH = 'DURATION_BETWEEN_START_END_SEARCH'
export const SEARCH_FIELD_MATCH = 'SEARCH_FIELD_MATCH'
export const CHANGE_YEAR = 'CHANGE_YEAR'

export const LAST_HOUR = 'LAST_HOUR';
export const TODAY = 'TODAY';
export const THIS_WEEK = 'THIS_WEEK';
export const LAST_SEVEN_DAYS = 'LAST_SEVEN_DAYS';
export const LAST_THIRTY_DAYS = 'LAST_THIRTY_DAYS';
export const THIS_YEAR = 'THIS_YEAR';
export const ALL = 'ALL';

export const filterCounts = data => ({type: FILTER_COUNTS, filterCounts: {...data}})
export const uniqueUserVisits = data => ({type: UNIQUE_USER_VISITS, uniqueUserVisits: data})
export const allVisits = data => ({type: ALL_VISITS, allVisits: data})
export const allSearches = data => ({type: ALL_SEARCHES, allSearches: data})
export const rankGrouping = data => ({type: RANK_GROUPING, rankGrouping: {...data}})
export const eventOutcome = data => ({type: EVENT_OUTCOME, eventOutcome: {...data}})
export const durationBetweenStartEndSearch = data => ({type: DURATION_BETWEEN_START_END_SEARCH, durationBetweenStartEndSearch: {...data}})
export const searchFieldMatch = data => ({type: SEARCH_FIELD_MATCH, searchFieldMatch: {...data}})
export const satisfactionCounts = data => ({type: SATISFACTION_COUNTS, ...data})
export const changeTimeRange = timeRange => ({type: TIME_RANGE, timeRange})
export const changingYear = yearNumber => ({type: CHANGE_YEAR, yearNumber})

const fetchVisitCounts = timeRange => (
    dispatch => {
        $.getJSON(`analytics/uniqueUserVisits${timeRangeToDateParameters(timeRange)}`, data => {
            dispatch(uniqueUserVisits(data))
        });
        $.getJSON(`analytics/allVisits${timeRangeToDateParameters(timeRange)}`, data => {
            dispatch(allVisits(data))
        });
        $.getJSON(`analytics/allSearches${timeRangeToDateParameters(timeRange)}`, data => {
            dispatch(allSearches(data))
        });
        $.getJSON(`analytics/rankGrouping${timeRangeToDateParameters(timeRange)}`, data => {
            dispatch(rankGrouping(data))
        });
        $.getJSON(`analytics/eventOutcome${timeRangeToDateParameters(timeRange)}`, data => {
            dispatch(eventOutcome(data))
        });
        $.getJSON(`analytics/durationBetweenStartEndSearch${timeRangeToDateParameters(timeRange)}`, data => {
            dispatch(durationBetweenStartEndSearch(data))
        });
        $.getJSON(`analytics/searchFieldMatch${timeRangeToDateParameters(timeRange)}`, data => {
            dispatch(searchFieldMatch(data))
        });
        $.getJSON(`analytics/filterCounts${timeRangeToDateParameters(timeRange)}`, data => {
            dispatch(filterCounts(data))
        });

    }
)

const fetchSatisfactionCounts = () => (
    dispatch => {
        $.getJSON(`analytics/satisfaction`, data => {
            dispatch(satisfactionCounts(data))
        });
    }
)

const timeRangeToDateParameters = timeRange => {
    const from = timeRangeToISODateTime(moment().utc(), timeRange);

    return from ? `?from=${from}` : ''
}

const changeYear = (year) => (
    dispatch => dispatch(changingYear(year))
)

const timeRangeToISODateTime = (now, timeRange) => {
    switch (timeRange) {
        case LAST_HOUR:
            return now.subtract(1, 'h').format();
        case TODAY:
            return now.hour(0).minute(0).second(0).format();
        case THIS_WEEK:
            return now.isoWeekday(1).hour(0).minute(0).second(0).format();
        case LAST_SEVEN_DAYS:
            return now.subtract(7, 'd').hour(0).minute(0).second(0).format();
        case LAST_THIRTY_DAYS:
            return now.subtract(30, 'd').hour(0).minute(0).second(0).format();
        case THIS_YEAR:
            return now.dayOfYear(1).hour(0).minute(0).second(0).format();
        default:
            return '';
    }

}

export {timeRangeToISODateTime}  // for testing
export {fetchVisitCounts}
export {fetchSatisfactionCounts}
export {changeYear}