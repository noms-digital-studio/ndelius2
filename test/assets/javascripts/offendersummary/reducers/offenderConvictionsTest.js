import {RECEIVE_OFFENDER_CONVICTIONS, OFFENDER_CONVICTIONS_LOAD_ERROR, INCREMENT_MAX_CONVICTIONS_VISIBLE} from '../constants/ActionTypes'
import offenderConvictions  from './offenderConvictions'
import {expect} from 'chai';

describe("offenderConvictionsReducer", () => {
    let state;
    describe("when in default state", () => {
        beforeEach(() => {
            state = offenderConvictions(undefined, {type: '"@@redux/INIT"'})
        })

        it('fetching is true', () => {
            expect(state.fetching).to.equal(true)
        });
        it('offender error not set', () => {
            expect(state.loadError).to.equal(false)
        });
        it('maxConvictionsVisible defaults to 3', () => {
            expect(state.maxConvictionsVisible).to.equal(3)
        });
    })
    describe("when RECEIVE_OFFENDER_CONVICTIONS action received", () => {
        beforeEach(() => {
            state = offenderConvictions({fetching: true, loadError: true}, {
                type: RECEIVE_OFFENDER_CONVICTIONS,
                convictions: [{type: 'bad'}]
            })
        })
        it('details set', () => {
            expect(state.convictions).to.have.length(1)
        });
        it('fetching toggled off', () => {
            expect(state.fetching).to.equal(false)
        });
        it('offender error is cleared', () => {
            expect(state.loadError).to.equal(false)
        });

    })
    describe("when OFFENDER_CONVICTIONS_LOAD_ERROR action received", () => {
        beforeEach(() => {
            state = offenderConvictions({fetching: true}, {
                type: OFFENDER_CONVICTIONS_LOAD_ERROR,
                error: new Error('Boom!')
            })
        })
        it('offender error set', () => {
            expect(state.loadError).to.equal(true)
        });
        it('fetching toggled off', () => {
            expect(state.fetching).to.equal(false)
        });

    })
    describe("when INCREMENT_MAX_CONVICTIONS_VISIBLE action received", () => {
        beforeEach(() => {
            state = offenderConvictions({maxConvictionsVisible: 3}, {
                type: INCREMENT_MAX_CONVICTIONS_VISIBLE,
                incrementBy: 10
            })
        })
        it('maxConvictionsVisible incremented by count supplied', () => {
            expect(state.maxConvictionsVisible).to.equal(13)
        });
    })
})
