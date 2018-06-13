import {
    FILTER_COUNTS,
    TIME_RANGE,
    TODAY,
    UNIQUE_USER_VISITS,
    ALL_VISITS,
    ALL_SEARCHES,
    RANK_GROUPING,
    EVENT_OUTCOME,
    DURATION_BETWEEN_START_END_SEARCH,
    SEARCH_FIELD_MATCH,
    SATISFACTION_COUNTS,
    CHANGE_YEAR,
    USER_AGENT_TYPE_COUNTS,
    SEARCH_TYPE_COUNTS} from '../actions/analytics'
import analytics  from './analylticsReducer'
import {expect} from 'chai';

describe("analyticsReducer", () => {
    let state;
    describe("when in default state", () => {
        beforeEach(() => {
            state = analytics(undefined, {type: '"@@redux/INIT"'})
        })

        it('counts are all set to zero', () => {
            expect(state.uniqueUserVisits).to.equal(0)
            expect(state.allVisits).to.equal(0)
            expect(state.allSearches).to.equal(0)
        });
        it('rankGrouping is set to empty object', () => {
            expect(state.rankGrouping).to.eql({})
        })
        it('eventOutcome is set to empty object', () => {
            expect(state.eventOutcome).to.eql({})
        })
        it('durationBetweenStartEndSearch is set to empty object', () => {
            expect(state.durationBetweenStartEndSearch).to.eql({})
        })
        it('searchCount is set to empty object', () => {
            expect(state.searchCount).to.eql({})
        })
        it('searchFieldMatch is set to empty object', () => {
            expect(state.searchFieldMatch).to.eql({})
        })
        it('fetching is false', () => {
            expect(state.fetching).to.equal(false)
        });
        it('timeRange is TODAY', () => {
            expect(state.timeRange).to.equal(TODAY)
        });
        it('filterCounts is set to empty object', () => {
            expect(state.filterCounts).to.eql({})
        })
        it('satisfactionCounts is set to empty object', () => {
            expect(state.satisfactionCounts).to.eql({})
        })
        it('yearNumber starts off at current year', () => {
            expect(state.yearNumber).to.equal(String(new Date().getFullYear()))
        })
        it('userAgentTypeCounts is set to empty object', () => {
            expect(state.userAgentTypeCounts).to.eql({})
        })
        it('searchTypeCounts is set to empty object', () => {
            expect(state.searchTypeCounts).to.eql({})
        })
    })
    describe("when UNIQUE_USER_VISITS action received", () => {
        beforeEach(() => {
            state = analytics({}, {
                type: UNIQUE_USER_VISITS,
                uniqueUserVisits: 12
            })
        })
        it('uniqueUserVisits is set', () => {
            expect(state.uniqueUserVisits).to.equal(12)
        });

    })
    describe("when ALL_VISITS action received", () => {
        beforeEach(() => {
            state = analytics({}, {
                type: ALL_VISITS,
                allVisits: 17
            })
        })
        it('allVisits is set', () => {
            expect(state.allVisits).to.equal(17)
        });

    })
    describe("when ALL_SEARCHES action received", () => {
        beforeEach(() => {
            state = analytics({}, {
                type: ALL_SEARCHES,
                allSearches: 24
            })
        })
        it('allSearches is set', () => {
            expect(state.allSearches).to.equal(24)
        });

    })
    describe("when RANK_GROUPING action received", () => {
        beforeEach(() => {
            state = analytics({}, {
                type: RANK_GROUPING,
                rankGrouping: {
                    "1": 10,
                    "2": 5,
                    "3": 1}
            })
        })
        it('rankGrouping is set', () => {
            expect(state.rankGrouping).to.eql({"1": 10, "2": 5, "3": 1})
        })

    })
    describe("when EVENT_OUTCOME action received", () => {
        beforeEach(() => {
            state = analytics({}, {
                type: EVENT_OUTCOME,
                eventOutcome: {
                    "search-add-new-offender": 2,
                    "search-request": 1,
                    "search-offender-details": 1,
                    "search-index": 4,
                    "search-add-contact": 1}
            })
        })
        it('eventOutcome is set', () => {
            expect(state.eventOutcome).to.eql({
                "search-add-new-offender": 2,
                "search-request": 1,
                "search-offender-details": 1,
                "search-index": 4,
                "search-add-contact": 1}
            )
        })

    })
    describe("when DURATION_BETWEEN_START_END_SEARCH action received", () => {
        beforeEach(() => {
            state = analytics({}, {
                type: DURATION_BETWEEN_START_END_SEARCH,
                durationBetweenStartEndSearch: {
                    "1": 100,
                    "2": 20,
                    "3": 5
                }
            })
        })
        it('durationBetweenStartEndSearch is set', () => {
            expect(state.durationBetweenStartEndSearch).to.eql({
                "1": 100,
                "2": 20,
                "3": 5})
        })

    })
    describe("when SEARCH_FIELD_MATCH action received", () => {
        beforeEach(() => {
            state = analytics({}, {
                type: SEARCH_FIELD_MATCH,
                searchFieldMatch: {
                    "otherIds.crn": 3,
                    "firstName": 5,
                    "surname": 6
                }
            })
        })
        it('searchFieldMatch is set', () => {
            expect(state.searchFieldMatch).to.eql({
                    "otherIds.crn": 3,
                    "firstName": 5,
                    "surname": 6
                }
            )
        })
    })
    describe("when FILTER_COUNTS action received", () => {
        beforeEach(() => {
            state = analytics({fetching: true}, {
                type: FILTER_COUNTS,
                filterCounts: {
                "hasUsedMyProvidersFilterCount": 2,
                "hasUsedOtherProvidersFilterCount": 1,
                "hasUsedBothProvidersFilterCount": 1,
                "hasNotUsedFilterCount": 4}}
            )
        })

        it('filterCounts is set', () => {
            expect(state.filterCounts).to.eql({
                "hasUsedMyProvidersFilterCount": 2,
                "hasUsedOtherProvidersFilterCount": 1,
                "hasUsedBothProvidersFilterCount": 1,
                "hasNotUsedFilterCount": 4})

        })

    })
    describe("when SEARCH_TYPE_COUNTS action received", () => {
        beforeEach(() => {
            state = analytics({fetching: true}, {
                type: SEARCH_TYPE_COUNTS,
                searchTypeCounts: {
                "broad": 2,
                "exact": 1}}
                )
        })

        it('searchTypeCounts is set', () => {
            expect(state.searchTypeCounts).to.eql({
                "broad": 2,
                "exact": 1})
        })

    })
    describe("when TIME_RANGE action received", () => {
        beforeEach(() => {
            state = analytics({fetching: false, timeRange: 'LAST_THIRTY_DAYS'}, {type: TIME_RANGE, timeRange: 'LAST_SEVEN_DAYS'})
        })

        it('updates timeRange', () => {
            expect(state.timeRange).to.equal('LAST_SEVEN_DAYS')
        });
    })
    describe("when SATISFACTION_COUNTS action received", () => {
        beforeEach(() => {
            state = analytics({}, {
                type: SATISFACTION_COUNTS,
                satisfactionCounts: {foo: 'bar'}
            })
        })
        it('satisfactionCounts is set', () => {
            expect(state.satisfactionCounts).to.eql({foo: 'bar'})
        });
    })
    describe("when SATISFACTION_COUNTS action received", () => {
        beforeEach(() => {
            state = analytics({}, {
                type: CHANGE_YEAR,
                yearNumber: '2019'
            })
        })
        it('satisfactionCounts is set', () => {
            expect(state.yearNumber).to.equal('2019')
        });
    })
    describe("when USER_AGENT_TYPE_COUNTS action received", () => {
        beforeEach(() => {
            state = analytics({}, {
                type: USER_AGENT_TYPE_COUNTS,
                userAgentTypeCounts: {
                    "Internet Explorer 8": 10,
                    "Internet Explorer 11": 16
                }
            })
        })
        it('userAgentTypeCounts is set', () => {
            expect(state.userAgentTypeCounts).to.eql({
                "Internet Explorer 8": 10,
                "Internet Explorer 11": 16
                }
            )
        })
    })
})
