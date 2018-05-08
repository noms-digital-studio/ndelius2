import moment from 'moment'

export const VISIT_COUNTS = 'VISIT_COUNTS'
export const FILTER_COUNTS = 'FILTER_COUNTS'
export const FETCHING_VISIT_COUNTS = 'FETCHING_VISIT_COUNTS'
export const TIME_RANGE = 'TIME_RANGE'

export const LAST_HOUR = 'LAST_HOUR';
export const TODAY = 'TODAY';
export const THIS_WEEK = 'THIS_WEEK';
export const LAST_SEVEN_DAYS = 'LAST_SEVEN_DAYS';
export const LAST_THIRTY_DAYS = 'LAST_THIRTY_DAYS';
export const THIS_YEAR = 'THIS_YEAR';
export const ALL = 'ALL';

export const visitCounts = data => ({type: VISIT_COUNTS, ...data})
export const filterCounts = data => ({type: FILTER_COUNTS, filterCounts: {...data}})
export const changeTimeRange = timeRange => ({type: TIME_RANGE, timeRange})
export const fetchingVisitCounts = () => ({type: FETCHING_VISIT_COUNTS})

const fetchVisitCounts = timeRange => (
    dispatch => {
        dispatch(fetchingVisitCounts())
        $.getJSON(`analytics/visitCounts${timeRangeToDateParameters(timeRange)}`, data => {
            dispatch(visitCounts(data))
        });
        $.getJSON(`analytics/filterCounts${timeRangeToDateParameters(timeRange)}`, data => {
            dispatch(filterCounts(data))
        });

    }
)

const timeRangeToDateParameters = timeRange => {
    const from = timeRangeToISODateTime(moment().utc(), timeRange);

    return from ? `?from=${from}` : ''
}
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