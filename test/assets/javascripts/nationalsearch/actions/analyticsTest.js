import {fetchVisitCounts, timeRangeToISODateTime,
    ALL, LAST_HOUR, TODAY, THIS_WEEK, LAST_SEVEN_DAYS, LAST_THIRTY_DAYS, THIS_YEAR} from './analytics'
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
        beforeEach(() => {
            global.$.getJSON.yields({uniqueUserVisits: 10, allVisits: 100})
            fetchVisitCounts(ALL)(dispatch)
        })
        it ('dispatches FETCHING_VISIT_COUNTS', () => {
            expect(dispatch).to.be.calledWith({type: 'FETCHING_VISIT_COUNTS'})
        })
        it ('calls endpoint with duration', () => {
            expect(global.$.getJSON).to.be.calledWith(`nationalSearch/analytics/visitCounts`)
        })
        it ('dispatches VISIT_COUNTS with count data', () => {
            expect(dispatch).to.be.calledWith({type: 'VISIT_COUNTS', uniqueUserVisits: 10, allVisits: 100})
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

