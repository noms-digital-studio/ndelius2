import {expect} from 'chai';
import {stub} from 'sinon';
import offender from '../api/offender'
import {getOffenderDetails, getOffenderRegistrations, getOffenderConvictions, showMoreConvictions} from './index'


describe('offender summary action', () => {
    let dispatch;

    beforeEach(() => {
        dispatch = stub()
        offender.getDetails = stub()
        offender.getRegistrations = stub()
        offender.getConvictions = stub()
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
    describe('on getOffenderConvictions', () => {
        context('successful response', () => {
            beforeEach(() => {
                offender.getConvictions.yields([{type: 'Bad'}], null)
                getOffenderConvictions()(dispatch)
            })
            it ('dispatches RECEIVE_OFFENDER_CONVICTIONS with details', () => {
                expect(dispatch).to.be.calledWith({type: 'RECEIVE_OFFENDER_CONVICTIONS', convictions: [{type: 'Bad'}]})
            })
        })
        context('unsuccessful response', () => {
            beforeEach(() => {
                getOffenderConvictions()(dispatch)
                offender.getConvictions.callArgWith(1, 'Boom!')
            })
            it ('dispatches OFFENDER_CONVICTIONS_LOAD_ERROR with error', () => {
                expect(dispatch).to.be.calledWith({type: 'OFFENDER_CONVICTIONS_LOAD_ERROR', error: 'Boom!'})
            })
        })
    })
    describe('on showMoreConvictions', () => {
        beforeEach(() => {
            showMoreConvictions()(dispatch)
        })
        it ('dispatches INCREMENT_MAX_CONVICTIONS_VISIBLE with incrementBy of 10', () => {
            expect(dispatch).to.be.calledWith({type: 'INCREMENT_MAX_CONVICTIONS_VISIBLE', incrementBy: 10})
        })
    })
})


