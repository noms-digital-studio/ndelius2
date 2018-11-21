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
        global.gtag = stub()
        global.virtualPageLoad = stub()
    })

    describe('on addContact', () => {
        beforeEach(() => {
            addContact(1234567, 2, {firstName: ["Josephina"], surname: ["Se"]})(dispatch)
        })
        it ('dispatches ADD_CONTACT', () => {
            expect(dispatch).to.be.calledWith({type: 'ADD_CONTACT', offenderId: 1234567})
        })
        it ('calls google analytics endpoint with event and rank', () => {
            expect(global.gtag).to.be.calledWith('event', 'search-add-contact', {
                'event_category': 'search',
                'event_label': 'Search Outcome: search-add-contact (Rank: 2)',
                'value': 2
            })
        })
        it ('calls google analytics virtual load', () => {
            expect(global.virtualPageLoad).to.be.calledWith('add-contact')
        })
    })
    describe('on addNewOffender', () => {
        beforeEach(() => {
            addNewOffender()(dispatch)
        })
        it ('dispatches ADD_NEW_OFFENDER', () => {
            expect(dispatch).to.be.calledWith({type: 'ADD_NEW_OFFENDER'})
        })
        it ('calls google analytics endpoint with event', () => {
            expect(global.gtag).to.be.calledWith('event', 'search-add-new-offender', {
                'event_category': 'search',
                'event_label': 'Search Outcome: ',
                'value': 0
            })
        })
        it ('calls google analytics virtual load', () => {
            expect(global.virtualPageLoad).to.be.calledWith('add-new-offender')
        })
    })
    describe('on showOffenderDetails', () => {
        beforeEach(() => {
            showOffenderDetails(1234567, 2, {firstName: ["Josephina"], surname: ["Se"]})(dispatch)
        })
        it ('dispatches SHOW_OFFENDER_DETAILS', () => {
            expect(dispatch).to.be.calledWith({type: 'SHOW_OFFENDER_DETAILS', offenderId: 1234567})
        })
        it ('calls google analytics endpoint with event and rank', () => {
            expect(global.gtag).to.be.calledWith('event', 'search-offender-details', {
                'event_category': 'search',
                'event_label': 'Search Outcome: search-offender-details (Rank: 2)',
                'value': 2
            })
        })
        it ('calls google analytics virtual load', () => {
            expect(global.virtualPageLoad).to.be.calledWith('offender-details')
        })
    })
    describe('on legacySearch', () => {
        beforeEach(() => {
            legacySearch()(dispatch)
        })
        it ('dispatches LEGACY_SEARCH', () => {
            expect(dispatch).to.be.calledWith({type: 'LEGACY_SEARCH'})
        })
        it ('calls google analytics endpoint with event', () => {
            expect(global.gtag).to.be.calledWith('event', 'search-legacy-search', {
                'event_category': 'search',
                'event_label': 'Search Outcome: ',
                'value': 0
            })
        })
        it ('calls google analytics virtual load', () => {
            expect(global.virtualPageLoad).to.be.calledWith('legacy-search')
        })
    })

})

