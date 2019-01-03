import {expect} from 'chai';
import {stub} from 'sinon';
import offender from '../api/offender'
import {getOffenderDetails, getOffenderRegistrations} from './index'


describe('offender summary action', () => {
    let dispatch;

    beforeEach(() => {
        dispatch = stub()
        offender.getDetails = stub()
        offender.getRegistrations = stub()
    })

    describe('on getOffenderDetails', () => {
        context('successful response', () => {
            beforeEach(() => {
                offender.getDetails.yields({firstName: 'John'}, null)
                getOffenderDetails()(dispatch)
            })
            it ('dispatches RECEIVE_OFFENDER_DETAILS with details', () => {
                expect(dispatch).to.be.calledWith({type: 'RECEIVE_OFFENDER_DETAILS', details: {firstName: 'John'}})
            })
        })
        context('unsuccessful response', () => {
            beforeEach(() => {
                getOffenderDetails()(dispatch)
                offender.getDetails.callArgWith(1, 'Boom!')
            })
            it ('dispatches OFFENDER_DETAILS_LOAD_ERROR with error', () => {
                expect(dispatch).to.be.calledWith({type: 'OFFENDER_DETAILS_LOAD_ERROR', error: 'Boom!'})
            })
        })
    })
    describe('on getOffenderRegistrations', () => {
        context('successful response', () => {
            beforeEach(() => {
                offender.getRegistrations.yields([{type: 'Bad'}], null)
                getOffenderRegistrations()(dispatch)
            })
            it ('dispatches RECEIVE_OFFENDER_REGISTRATIONS with details', () => {
                expect(dispatch).to.be.calledWith({type: 'RECEIVE_OFFENDER_REGISTRATIONS', registrations: [{type: 'Bad'}]})
            })
        })
        context('unsuccessful response', () => {
            beforeEach(() => {
                getOffenderRegistrations()(dispatch)
                offender.getRegistrations.callArgWith(1, 'Boom!')
            })
            it ('dispatches OFFENDER_REGISTRATIONS_LOAD_ERROR with error', () => {
                expect(dispatch).to.be.calledWith({type: 'OFFENDER_REGISTRATIONS_LOAD_ERROR', error: 'Boom!'})
            })
        })
    })
})


