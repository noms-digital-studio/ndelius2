import {
  ALL_SEARCHES,
  ALL_VISITS,
  CHANGE_YEAR,
  DURATION_BETWEEN_START_END_SEARCH,
  EVENT_OUTCOME,
  FILTER_COUNTS,
  RANK_GROUPING,
  SATISFACTION_COUNTS,
  SEARCH_FIELD_MATCH,
  SEARCH_TYPE_COUNTS,
  TIME_RANGE,
  TODAY,
  UNIQUE_USER_VISITS,
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
  userAgentTypeCounts: {},
  searchTypeCounts: {}
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
        yearNumber: action.yearNumber
      }
    case USER_AGENT_TYPE_COUNTS:
      return {
        ...state,
        userAgentTypeCounts: action.userAgentTypeCounts
      }

    case SEARCH_TYPE_COUNTS:
      return {
        ...state,
        searchTypeCounts: action.searchTypeCounts
      }

    default:
      return state
  }
}

export default analytics
