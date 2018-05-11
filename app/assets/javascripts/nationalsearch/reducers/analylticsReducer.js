import {
    TIME_RANGE, 
    TODAY, 
    FILTER_COUNTS, 
    SATISFACTION_COUNTS, 
    UNIQUE_USER_VISITS, 
    ALL_VISITS, 
    ALL_SEARCHES, 
    RANK_GROUPING, 
    EVENT_OUTCOME, 
    DURATION_BETWEEN_START_END_SEARCH, 
    SEARCH_FIELD_MATCH, 
    CHANGE_YEAR,
    USER_AGENT_TYPE_COUNTS
} from '../actions/analytics'
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
    timeRange: TODAY,
    satisfactionCounts: {},
    yearNumber: String(new Date().getFullYear()),
    userAgentTypeCounts: {}
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

        case SATISFACTION_COUNTS:
            return {
                ...state,
                satisfactionCounts: action.satisfactionCounts,
                fetching: false
            }

        case CHANGE_YEAR:
            return {
                ...state,
                yearNumber: action.yearNumber,
            }
        case USER_AGENT_TYPE_COUNTS:
            return {
                ...state,
                userAgentTypeCounts: action.userAgentTypeCounts,
            }
            
        default:
            return state
    }
}

export default analytics

