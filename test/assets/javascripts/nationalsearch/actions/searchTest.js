import {search} from './search'
import {expect} from 'chai';
import {stub} from 'sinon';


describe('search action', () => {
    let dispatch;

    beforeEach(() => {
        dispatch = stub()
        global.$ = {
            getJSON: stub()
        }
    })

    describe('on search', () => {
        context('when blank search term', () => {
            it ('sends a CLEAR_RESULTS', () => {
                search('')(dispatch)
                expect(dispatch).to.be.calledWith({type: 'CLEAR_RESULTS'})
            })
        })
        context('with just a search term', () => {
            beforeEach(() => {
                global.$.getJSON.yields({offenders: []})
                search('Mr Bean', 'broad')(dispatch)
            })
            it ('dispatches REQUEST_SEARCH with searchTerm', () => {
                expect(dispatch).to.be.calledWith({type: 'REQUEST_SEARCH', searchTerm: 'Mr Bean'})
            })
            it ('dispatches SEARCH_RESULTS with searchTerm and results', () => {
                expect(dispatch).to.be.calledWith({type: 'SEARCH_RESULTS', results: {offenders: []}, searchTerm: 'Mr Bean', pageNumber: 1})
            })
            it('calls ajax with pagesNumber, searchTerm and pageSize', () => {
                expect(global.$.getJSON).to.be.calledWith('searchOffender/Mr%20Bean?pageSize=10&pageNumber=1&areasFilter=&searchType=broad')
            })
        })
        context('with a search term, page number and area filter', () => {
            beforeEach(() => {
                global.$.getJSON.yields({offenders: []})
                search('Mr Bean', 'broad', ['N01', 'N02'], 3)(dispatch)
            })
            it ('dispatches REQUEST_SEARCH with searchTerm', () => {
                expect(dispatch).to.be.calledWith({type: 'REQUEST_SEARCH', searchTerm: 'Mr Bean'})
            })
            it ('dispatches SEARCH_RESULTS with searchTerm and results', () => {
                expect(dispatch).to.be.calledWith({type: 'SEARCH_RESULTS', results: {offenders: []}, searchTerm: 'Mr Bean', pageNumber: 3})
            })
            it('calls ajax with pagesNumber, searchTerm and pageSize', () => {
                expect(global.$.getJSON).to.be.calledWith('searchOffender/Mr%20Bean?pageSize=10&pageNumber=3&areasFilter=N01,N02&searchType=broad')
            })
        })
    })

})

