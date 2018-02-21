import {FETCHING_VISIT_COUNTS, VISIT_COUNTS, TIME_RANGE} from '../actions/analytics'
import analytics  from './analylticsReducer'
import {expect} from 'chai';

describe("analylticsReducer", () => {
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
        it('fetching is false', () => {
            expect(state.fetching).to.equal(false)
        });
    })
    describe("when FETCHING_VISIT_COUNTS action received", () => {
        beforeEach(() => {
            state = analytics({fetching: false}, {type: FETCHING_VISIT_COUNTS})
        })

        it('fetching is true', () => {
            expect(state.fetching).to.equal(true)
        });
    })
    describe("when VISIT_COUNTS action received", () => {
        beforeEach(() => {
            state = analytics({fetching: true}, {
                type: VISIT_COUNTS,
                uniqueUserVisits: 12,
                allVisits: 17,
                allSearches: 24,
                rankGrouping: {
                    "1": 10,
                    "2": 5,
                    "3": 1},
                eventOutcome: {
                    "search-add-new-offender": 2,
                    "search-request": 1,
                    "search-offender-details": 1,
                    "search-index": 4,
                    "search-add-contact": 1}
            })
        })

        it('fetching is false', () => {
            expect(state.fetching).to.equal(false)
        });
        it('uniqueUserVisits is set', () => {
            expect(state.uniqueUserVisits).to.equal(12)
        });
        it('allVisits is set', () => {
            expect(state.allVisits).to.equal(17)
        });
        it('allSearches is set', () => {
            expect(state.allSearches).to.equal(24)
        });
        it('rankGrouping is set', () => {
            expect(state.rankGrouping).to.eql({"1": 10, "2": 5, "3": 1})
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
    describe("when TIME_RANGE action received", () => {
        beforeEach(() => {
            state = analytics({fetching: false, timeRange: 'LAST_THIRTY_DAYS'}, {type: TIME_RANGE, timeRange: 'LAST_SEVEN_DAYS'})
        })

        it('updates timeRange', () => {
            expect(state.timeRange).to.equal('LAST_SEVEN_DAYS')
        });
    })
})
