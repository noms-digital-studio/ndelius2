import {
    fetchVisitCounts, timeRangeToISODateTime, fetchSatisfactionCounts, changeYear,
    ALL, LAST_HOUR, TODAY, THIS_WEEK, LAST_SEVEN_DAYS, LAST_THIRTY_DAYS, THIS_YEAR, SEARCH_TYPE_COUNTS
} from './analytics'
import {expect} from 'chai';
import {stub} from 'sinon';
import moment from 'moment'


describe('fetchVisitCounts action', () => {
    let dispatch;

    beforeEach(() => {
        dispatch = stub()
        global.$ = {
            getJSON: stub()
        }
    })

    describe('on fetch counts', () => {
        context('before responses', () => {
            beforeEach(() => {
                global.$.getJSON.yields({})
                fetchVisitCounts(ALL)(dispatch)
            })
            it ('calls filter endpoint with duration', () => {
                expect(global.$.getJSON).to.be.calledWith(`analytics/filterCounts`)
            })
            it ('calls uniqueUserVisits endpoint with duration', () => {
                expect(global.$.getJSON).to.be.calledWith(`analytics/uniqueUserVisits`)
            })
            it ('calls allVisits endpoint with duration', () => {
                expect(global.$.getJSON).to.be.calledWith(`analytics/allVisits`)
            })
            it ('calls allSearches endpoint with duration', () => {
                expect(global.$.getJSON).to.be.calledWith(`analytics/allSearches`)
            })
            it ('calls rankGrouping endpoint with duration', () => {
                expect(global.$.getJSON).to.be.calledWith(`analytics/rankGrouping`)
            })
            it ('calls durationBetweenStartEndSearch endpoint with duration', () => {
                expect(global.$.getJSON).to.be.calledWith(`analytics/durationBetweenStartEndSearch`)
            })
            it ('calls eventOutcome endpoint with duration', () => {
                expect(global.$.getJSON).to.be.calledWith(`analytics/eventOutcome`)
            })
            it ('calls searchFieldMatch endpoint with duration', () => {
                expect(global.$.getJSON).to.be.calledWith(`analytics/searchFieldMatch`)
            })
            it ('calls userAgentTypeCounts endpoint with duration', () => {
                expect(global.$.getJSON).to.be.calledWith(`analytics/userAgentTypeCounts`)
            })
            it ('calls searchTypeCounts endpoint with duration', () => {
                expect(global.$.getJSON).to.be.calledWith(`analytics/searchTypeCounts`)
            })
        })

        context('response from uniqueUserVisits', () => {
            beforeEach(() => {
                global.$.getJSON.withArgs('analytics/uniqueUserVisits').yields(10)
                fetchVisitCounts(ALL)(dispatch)
            })
            it ('dispatches UNIQUE_USER_VISITS with count data', () => {
                expect(dispatch).to.be.calledWith({type: 'UNIQUE_USER_VISITS', uniqueUserVisits: 10})
            })

        })
        context('response from allVisits', () => {
            beforeEach(() => {
                global.$.getJSON.withArgs('analytics/allVisits').yields(10)
                fetchVisitCounts(ALL)(dispatch)
            })
            it ('dispatches ALL_VISITS with count data', () => {
                expect(dispatch).to.be.calledWith({type: 'ALL_VISITS', allVisits: 10})
            })

        })
        context('response from allSearches', () => {
            beforeEach(() => {
                global.$.getJSON.withArgs('analytics/allSearches').yields(10)
                fetchVisitCounts(ALL)(dispatch)
            })
            it ('dispatches ALL_SEARCHES with count data', () => {
                expect(dispatch).to.be.calledWith({type: 'ALL_SEARCHES', allSearches: 10})
            })

        })
        context('response from rankGrouping', () => {
            beforeEach(() => {
                global.$.getJSON.withArgs('analytics/rankGrouping').yields({
                    "1": 10,
                    "2": 5,
                    "3": 1})
                fetchVisitCounts(ALL)(dispatch)
            })
            it ('dispatches RANK_GROUPING with count data', () => {
                expect(dispatch).to.be.calledWith({type: 'RANK_GROUPING', rankGrouping: {
                        "1": 10,
                        "2": 5,
                        "3": 1}})
            })

        })
        context('response from eventOutcome', () => {
            beforeEach(() => {
                global.$.getJSON.withArgs('analytics/eventOutcome').yields(
                    {"search-index": 10,
                    "search-request": 5,
                    "search-offender-details": 1,
                    "search-legacy-search": 5})
                fetchVisitCounts(ALL)(dispatch)
            })
            it ('dispatches EVENT_OUTCOME with count data', () => {
                expect(dispatch).to.be.calledWith({type: 'EVENT_OUTCOME', eventOutcome: {
                        "search-index": 10,
                        "search-request": 5,
                        "search-offender-details": 1,
                        "search-legacy-search": 5
                    }})
            })

        })
        context('response from durationBetweenStartEndSearch', () => {
            beforeEach(() => {
                global.$.getJSON.withArgs('analytics/durationBetweenStartEndSearch').yields({
                    "1": 10,
                    "2": 5,
                    "3": 1})
                fetchVisitCounts(ALL)(dispatch)
            })
            it ('dispatches DURATION_BETWEEN_START_END_SEARCH with count data', () => {
                expect(dispatch).to.be.calledWith({type: 'DURATION_BETWEEN_START_END_SEARCH', durationBetweenStartEndSearch: {
                        "1": 10,
                        "2": 5,
                        "3": 1}})
            })

        })
        context('response from searchFieldMatch', () => {
            beforeEach(() => {
                global.$.getJSON.withArgs('analytics/searchFieldMatch').yields(
                    {"otherIds.crn": 10,
                        "firstName": 5,
                        "surname": 1})
                fetchVisitCounts(ALL)(dispatch)
            })
            it ('dispatches SEARCH_FIELD_MATCH with count data', () => {
                expect(dispatch).to.be.calledWith({type: 'SEARCH_FIELD_MATCH', searchFieldMatch: {
                        "otherIds.crn": 10,
                        "firstName": 5,
                        "surname": 1
                    }})
            })

        })
        context('response from filterCounts', () => {
            beforeEach(() => {
                global.$.getJSON.withArgs('analytics/filterCounts').yields({someAnalytic: 10})
                fetchVisitCounts(ALL)(dispatch)
            })
            it ('dispatches FILTER_COUNTS with count data', () => {
                expect(dispatch).to.be.calledWith({type: 'FILTER_COUNTS', filterCounts: {someAnalytic: 10}})
            })

        })
        context('response from searchTypeCounts', () => {
            beforeEach(() => {
                global.$.getJSON.withArgs('analytics/searchTypeCounts').yields({someAnalytic: 8})
                fetchVisitCounts(ALL)(dispatch)
            })
            it ('dispatches SEARCH_TYPE_COUNTS with count data', () => {
                expect(dispatch).to.be.calledWith({type: 'SEARCH_TYPE_COUNTS', searchTypeCounts: {someAnalytic: 8}})
            })

        })
        context('response from userAgentTypeCounts', () => {
            beforeEach(() => {
                global.$.getJSON.withArgs('analytics/userAgentTypeCounts').yields({"Internet Explorer 8": 10})
                fetchVisitCounts(ALL)(dispatch)
            })
            it ('dispatches USER_AGENT_TYPE_COUNTS with count data', () => {
                expect(dispatch).to.be.calledWith({type: 'USER_AGENT_TYPE_COUNTS', userAgentTypeCounts: {"Internet Explorer 8": 10}})
            })

        })
    })

    describe('timeRangeToISODateTime', () => {
        let pretendNow;

        beforeEach(() => {
            pretendNow = moment.utc('2018-02-01 09:30:26') //Thursday
        })

        it('subtracts one hour for LAST_HOUR', () => {
            expect(timeRangeToISODateTime(pretendNow, LAST_HOUR)).to.equal('2018-02-01T08:30:26Z')
        })
        it('sets to midnight for TODAY', () => {
            expect(timeRangeToISODateTime(pretendNow, TODAY)).to.equal('2018-02-01T00:00:00Z')
        })
        it('sets to Monday midnight for THIS_WEEK', () => {
            expect(timeRangeToISODateTime(pretendNow, THIS_WEEK)).to.equal('2018-01-29T00:00:00Z')
        })
        it('sets to midnight 7 days ago for LAST_SEVEN_DAYS', () => {
            expect(timeRangeToISODateTime(pretendNow, LAST_SEVEN_DAYS)).to.equal('2018-01-25T00:00:00Z')
        })
        it('sets to midnight 30 days ago for LAST_THIRTY_DAYS', () => {
            expect(timeRangeToISODateTime(pretendNow, LAST_THIRTY_DAYS)).to.equal('2018-01-02T00:00:00Z')
        })
        it('sets to midnight beginning of year for THIS_YEAR', () => {
            expect(timeRangeToISODateTime(pretendNow, THIS_YEAR)).to.equal('2018-01-01T00:00:00Z')
        })
    })

})

describe('fetchSatisfactionCounts action', () => {
    let dispatch;

    beforeEach(() => {
        dispatch = stub()
        global.$ = {
            getJSON: stub()
        }
    })

    describe('on fetch satisfaction counts', () => {
        beforeEach(() => {
            global.$.getJSON.yields(
                {
                    "satisfactionCounts": {
                        "Very satisfied": [],
                        "Satisfied": [],
                        "Very dissatisfied": [],
                        "Dissatisfied": [],
                        "Neither satisfied or dissatisfied": []
                    }
                }
            );
            fetchSatisfactionCounts()(dispatch)
        });

        it ('calls endpoint', () => {
            expect(global.$.getJSON).to.be.calledWith(`analytics/satisfaction`)
        });

        it ('dispatches SATISFACTION_COUNTS with count data', () => {
            expect(dispatch).to.be.calledWith({type: 'SATISFACTION_COUNTS',
                "satisfactionCounts": {
                "Very satisfied": [],
                    "Satisfied": [],
                    "Very dissatisfied": [],
                    "Dissatisfied": [],
                    "Neither satisfied or dissatisfied": []}
            })
        });
    })

    describe('change year', () => {
        it('dispatches CHANGE_YEAR with year number', () => {
            changeYear('2019')(dispatch);
            expect(dispatch).to.be.calledWith({ type: "CHANGE_YEAR", yearNumber: "2019" });
        })
    })

})
