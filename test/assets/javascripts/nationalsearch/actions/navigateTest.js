import {addContact, addNewOffender, showOffenderDetails, legacySearch} from './navigate'
import {expect} from 'chai';
import {stub} from 'sinon';


describe('navigate action', () => {
    let dispatch;

    beforeEach(() => {
        dispatch = stub()
        global.$ = {
            ajax: stub()
        }
    })

    describe('on addContact', () => {
        beforeEach(() => {
            addContact(1234567)(dispatch)
        })
        it ('dispatches ADD_CONTACT', () => {
            expect(dispatch).to.be.calledWith({type: 'ADD_CONTACT', offenderId: 1234567})
        })
    })
    describe('on addNewOffender', () => {
        beforeEach(() => {
            addNewOffender()(dispatch)
        })
        it ('dispatches ADD_NEW_OFFENDER', () => {
            expect(dispatch).to.be.calledWith({type: 'ADD_NEW_OFFENDER'})
        })
    })
    describe('on showOffenderDetails', () => {
        beforeEach(() => {
            showOffenderDetails(1234567)(dispatch)
        })
        it ('dispatches SHOW_OFFENDER_DETAILS', () => {
            expect(dispatch).to.be.calledWith({type: 'SHOW_OFFENDER_DETAILS', offenderId: 1234567})
        })
    })
    describe('on legacySearch', () => {
        beforeEach(() => {
            legacySearch()(dispatch)
        })
        it ('dispatches LEGACY_SEARCH', () => {
            expect(dispatch).to.be.calledWith({type: 'LEGACY_SEARCH'})
        })
    })

})

