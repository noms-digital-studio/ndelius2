import {RECEIVE_OFFENDER_DETAILS} from '../constants/ActionTypes'
import offenderDetails  from './offenderDetails'
import {expect} from 'chai';

describe("offenderDetailsReducer", () => {
    let state;
    describe("when in default state", () => {
        beforeEach(() => {
            state = offenderDetails(undefined, {type: '"@@redux/INIT"'})
        })

        it('fetching is true', () => {
            expect(state.fetching).to.equal(true)
        });
    })
    describe("when RECEIVE_OFFENDER_DETAILS action received", () => {
        beforeEach(() => {
            state = offenderDetails({fetching: true}, {
                type: RECEIVE_OFFENDER_DETAILS,
                details: {
                    firstName: 'John',
                    surname: 'Smith'
                }
            })
        })
        it('details set', () => {
            expect(state.firstName).to.equal('John')
            expect(state.surname).to.equal('Smith')
        });
        it('fetching toggled off', () => {
            expect(state.fetching).to.equal(false)
        });

    })
})
