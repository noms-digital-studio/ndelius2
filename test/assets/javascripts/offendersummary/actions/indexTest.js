import {expect} from 'chai';
import {stub} from 'sinon';
import offender from '../api/offender'
import {getOffenderDetails, getOffenderRegistrations, getOffenderConvictions, showMoreConvictions, getNextAppointment, getOffenderPersonalCircumstances} from './index'


describe('offender summary action', () => {
    let dispatch;

    beforeEach(() => {
        dispatch = stub()
        offender.getDetails = stub()
        offender.getRegistrations = stub()
        offender.getConvictions = stub()
        offender.getNextAppointment = stub()
        offender.getPersonalCircumstances = stub()
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
    describe('on getNextAppointment', () => {
        context('successful response', () => {
            beforeEach(() => {
                offender.getNextAppointment.yields({appointmentId: 1}, null)
                getNextAppointment()(dispatch)
            })
            it ('dispatches RECEIVE_NEXT_APPOINTMENT with details', () => {
                expect(dispatch).to.be.calledWith({type: 'RECEIVE_NEXT_APPOINTMENT', appointment: {appointmentId: 1}})
            })
        })
        context('no data response', () => {
            beforeEach(() => {
                getNextAppointment()(dispatch)
                offender.getNextAppointment.callArg(1)
            })
            it ('dispatches RECEIVE_NO_NEXT_APPOINTMENT', () => {
                expect(dispatch).to.be.calledWith({type: 'RECEIVE_NO_NEXT_APPOINTMENT'})
            })
        })
        context('unsuccessful response', () => {
            beforeEach(() => {
                getNextAppointment()(dispatch)
                offender.getNextAppointment.callArgWith(2, 'Boom!')
            })
            it ('dispatches NEXT_APPOINTMENT_LOAD_ERROR with error', () => {
                expect(dispatch).to.be.calledWith({type: 'NEXT_APPOINTMENT_LOAD_ERROR', error: 'Boom!'})
            })
        })
    })
    describe('on getOffenderPersonalCircumstances', () => {
        context('successful response', () => {
            beforeEach(() => {
                offender.getPersonalCircumstances.yields([{type: 'Bad'}], null)
                getOffenderPersonalCircumstances()(dispatch)
            })
            it ('dispatches RECEIVE_OFFENDER_PERSONAL_CIRCUMSTANCES with details', () => {
                expect(dispatch).to.be.calledWith({type: 'RECEIVE_OFFENDER_PERSONAL_CIRCUMSTANCES', circumstances: [{type: 'Bad'}]})
            })
        })
        context('unsuccessful response', () => {
            beforeEach(() => {
                getOffenderPersonalCircumstances()(dispatch)
                offender.getPersonalCircumstances.callArgWith(1, 'Boom!')
            })
            it ('dispatches OFFENDER_PERSONAL_CIRCUMSTANCES_LOAD_ERROR with error', () => {
                expect(dispatch).to.be.calledWith({type: 'OFFENDER_PERSONAL_CIRCUMSTANCES_LOAD_ERROR', error: 'Boom!'})
            })
        })
    })

})


