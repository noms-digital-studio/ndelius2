import {
    VISIT_COUNTS,
    FETCHING_VISIT_COUNTS,
    TIME_RANGE,
    THIS_WEEK
} from '../actions/analytics'

const analytics = (state = {uniqueUserVisits: 0, allVisits: 0, allSearches: 0, rankGrouping: {}, fetching: false, timeRange: 'THIS_YEAR'}, action) => {
    switch (action.type) {
        case VISIT_COUNTS:
            return {
                ...state,
                uniqueUserVisits: action.uniqueUserVisits,
                allVisits: action.allVisits,
                allSearches: action.allSearches,
                rankGrouping: action.rankGrouping,
                fetching: false
            };
        case FETCHING_VISIT_COUNTS:
            return {
                ...state,
                fetching: true
            };
        case TIME_RANGE:
            return {
                ...state,
                timeRange: action.timeRange
            }
        default:
            return state
    }
};

export default analytics

