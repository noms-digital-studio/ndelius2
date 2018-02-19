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
            state = analytics({fetching: true}, {type: VISIT_COUNTS, uniqueUserVisits: 12, allVisits: 17, allSearches: 24})
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
