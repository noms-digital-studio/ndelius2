import {TIME_RANGE, TODAY, FILTER_COUNTS, UNIQUE_USER_VISITS, ALL_VISITS, ALL_SEARCHES, RANK_GROUPING, EVENT_OUTCOME, DURATION_BETWEEN_START_END_SEARCH, SEARCH_FIELD_MATCH} from '../actions/analytics'

const analytics = (state = {
    uniqueUserVisits: 0,
    allVisits: 0,
    allSearches: 0,
    rankGrouping: {},
    eventOutcome: {},
    durationBetweenStartEndSearch: {},
    searchCount: {},
    searchFieldMatch: {},
    filterCounts: {},
    fetching: false,
    timeRange: TODAY
}, action) => {
    switch (action.type) {
        case FILTER_COUNTS:
            return {
                ...state,
                filterCounts: action.filterCounts
            }

        case UNIQUE_USER_VISITS:
            return {
                ...state,
                uniqueUserVisits: action.uniqueUserVisits
            }

        case ALL_VISITS:
            return {
                ...state,
                allVisits: action.allVisits
            }

        case ALL_SEARCHES:
            return {
                ...state,
                allSearches: action.allSearches
            }

        case RANK_GROUPING:
            return {
                ...state,
                rankGrouping: action.rankGrouping
            }

        case EVENT_OUTCOME:
            return {
                ...state,
                eventOutcome: action.eventOutcome
            }

        case DURATION_BETWEEN_START_END_SEARCH:
            return {
                ...state,
                durationBetweenStartEndSearch: action.durationBetweenStartEndSearch
            }

        case SEARCH_FIELD_MATCH:
            return {
                ...state,
                searchFieldMatch: action.searchFieldMatch
            }

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

