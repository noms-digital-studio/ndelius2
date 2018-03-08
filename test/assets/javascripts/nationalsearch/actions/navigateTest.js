import {addContact, addNewOffender, showOffenderDetails, legacySearch, addFeedback} from './navigate'
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
            addContact(1234567, 2, {firstName: ["Josephina"], surname: ["Se"]})(dispatch)
        })
        it ('dispatches ADD_CONTACT', () => {
            expect(dispatch).to.be.calledWith({type: 'ADD_CONTACT', offenderId: 1234567})
        })
        it ('calls endpoint with rankIndex, type and fieldMatch', () => {
            expect(global.$.ajax).to.be.calledWith({
                url: 'nationalSearch/recordSearchOutcome',
                type: 'POST',
                data: JSON.stringify({type: 'search-add-contact', rankIndex: 2, fieldMatch: ["firstName", "surname"]}),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: null
            })
        })
    })
    describe('on addNewOffender', () => {
        beforeEach(() => {
            addNewOffender()(dispatch)
        })
        it ('dispatches ADD_NEW_OFFENDER', () => {
            expect(dispatch).to.be.calledWith({type: 'ADD_NEW_OFFENDER'})
        })
        it ('calls endpoint with type', () => {
            expect(global.$.ajax).to.be.calledWith({
                url: 'nationalSearch/recordSearchOutcome',
                type: 'POST',
                data: JSON.stringify({type: 'search-add-new-offender'}),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: null
            })
        })
    })
    describe('on showOffenderDetails', () => {
        beforeEach(() => {
            showOffenderDetails(1234567, 2, {firstName: ["Josephina"], surname: ["Se"]})(dispatch)
        })
        it ('dispatches SHOW_OFFENDER_DETAILS', () => {
            expect(dispatch).to.be.calledWith({type: 'SHOW_OFFENDER_DETAILS', offenderId: 1234567})
        })
        it ('calls endpoint with rankIndex, type and fieldMatch', () => {
            expect(global.$.ajax).to.be.calledWith({
                url: 'nationalSearch/recordSearchOutcome',
                type: 'POST',
                data: JSON.stringify({type: 'search-offender-details', rankIndex: 2, fieldMatch: ["firstName", "surname"]}),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: null
            })
        })
    })
    describe('on legacySearch', () => {
        beforeEach(() => {
            legacySearch()(dispatch)
        })
        it ('dispatches LEGACY_SEARCH', () => {
            expect(dispatch).to.be.calledWith({type: 'LEGACY_SEARCH'})
        })
        it ('calls endpoint with type', () => {
            expect(global.$.ajax).to.be.calledWith({
                url: 'nationalSearch/recordSearchOutcome',
                type: 'POST',
                data: JSON.stringify({type: 'search-legacy-search'}),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: null
            })
        })
    })
    describe('on addFeedback', () => {
        beforeEach(() => {
            addFeedback({anything: "some feedback"})()
        })
        it ('calls endpoint with type', () => {
            expect(global.$.ajax).to.be.calledWith({
                url: 'nationalSearch/recordSearchOutcome',
                type: 'POST',
                data: JSON.stringify({type: 'search-feedback', feedback: {anything: "some feedback"}}),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: null
            })
        })
    })

})

