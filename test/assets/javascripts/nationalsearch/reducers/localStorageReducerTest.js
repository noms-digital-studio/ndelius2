import {
  ADD_AREA_FILTER,
  CLEAR_RESULTS,
  REMOVE_AREA_FILTER,
  REQUEST_SEARCH,
  SAVED_SEARCH,
  SEARCH_RESULTS,
  SEARCH_TYPE_CHANGED
} from '../actions/search'
import localStorageReducer from './localStorageReducer'
import { expect } from 'chai'

describe('localStorageReducer', () => {
  let state

  describe('when in default state', () => {
    beforeEach(() => {
      state = localStorageReducer(undefined, { type: '"@@redux/INIT"' })
      global.window = {}
    })

    it('searchTerm will be blank', () => {
      expect(state.searchTerm).to.equal('')
    })
    it('probationAreasFilter will be empty', () => {
      expect(state.probationAreasFilter).to.eql({})
    })
    it('pageNumber will be 1', () => {
      expect(state.pageNumber).to.equal(1)
    })
    it('searchType will be `broad`', () => {
      expect(state.searchType).to.equal('broad')
    })
  })

  describe('when REQUEST_SEARCH action received', () => {
    beforeEach(() => {
      state = localStorageReducer({ searchTerm: '', probationAreasFilter: {}, pageNumber: 1 }, {
        type: REQUEST_SEARCH,
        searchTerm: 'John Smith'
      })
    })

    it('searchTerm is set to new value', () => {
      expect(state.searchTerm).to.equal('John Smith')
    })
  })

  describe('when SEARCH_RESULTS action received', () => {
    beforeEach(() => {
      state = localStorageReducer({ searchTerm: '', probationAreasFilter: {}, pageNumber: 1 }, {
        type: SEARCH_RESULTS,
        pageNumber: 6
      })
    })

    it('pageNumber is set to new value', () => {
      expect(state.pageNumber).to.equal(6)
    })
  })

  describe('when CLEAR_RESULTS action received', () => {
    beforeEach(() => {
      state = localStorageReducer({
        searchTerm: 'Mr Bean',
        probationAreasFilter: { 'N01': 'N01 Area' },
        pageNumber: 3
      }, { type: CLEAR_RESULTS })
    })

    it('searchTerm will be blank', () => {
      expect(state.searchTerm).to.equal('')
    })
    it('pageNumber is reset', () => {
      expect(state.pageNumber).to.equal(1)
    })
    it('probationAreasFilter remains unchanged', () => {
      expect(state.probationAreasFilter).to.eql({ 'N01': 'N01 Area' })
    })
  })

  describe('when SAVED_SEARCH action received', () => {
    beforeEach(() => {
      state = localStorageReducer({ searchTerm: '', probationAreasFilter: {}, pageNumber: 1 }, {
        type: SAVED_SEARCH,
        searchTerm: 'Mr Bean',
        probationAreasFilter: { 'N01': 'N01 Area' }
      })
    })

    it('area filter just contains saved filter', () => {
      expect(state.probationAreasFilter).to.eql({ 'N01': 'N01 Area' })
    })
    it('searchTerm is set', () => {
      expect(state.searchTerm).to.equal('Mr Bean')
    })
  })

  describe('when ADD_AREA_FILTER action received', () => {
    context('when no filter set yet', () => {
      beforeEach(() => {
        state = localStorageReducer({ searchTerm: '', pageNumber: 1, probationAreasFilter: {} }, {
          type: ADD_AREA_FILTER,
          probationAreaCode: 'N01',
          probationAreaDescription: 'N01 Area'
        })
      })

      it('area filter just contains new code', () => {
        expect(state.probationAreasFilter).to.eql({ 'N01': 'N01 Area' })
      })
    })

    context('when area code already in filter', () => {
      beforeEach(() => {
        state = localStorageReducer({
          searchTerm: '',
          pageNumber: 1,
          probationAreasFilter: { 'N01': 'N01 Area' }
        }, { type: ADD_AREA_FILTER, probationAreaCode: 'N01', probationAreaDescription: 'N01 Area' })
      })

      it('area filter just contains existing code', () => {
        expect(state.probationAreasFilter).to.eql({ 'N01': 'N01 Area' })
      })
    })

    context('when filter set with other codes', () => {
      beforeEach(() => {
        state = localStorageReducer({
          searchTerm: '',
          pageNumber: 1,
          probationAreasFilter: { 'N01': 'N01 Area' }
        }, { type: ADD_AREA_FILTER, probationAreaCode: 'N02', probationAreaDescription: 'N02 Area' })
      })

      it('area filter just contains existing code and new code', () => {
        expect(state.probationAreasFilter).to.eql({ 'N01': 'N01 Area', 'N02': 'N02 Area' })
      })
    })
  })

  describe('when REMOVE_AREA_FILTER action received', () => {
    context('when code to remove not in filter', () => {
      beforeEach(() => {
        state = localStorageReducer({
          searchTerm: '',
          pageNumber: 1,
          probationAreasFilter: { 'N02': 'N02 Area' }
        }, { type: REMOVE_AREA_FILTER, probationAreaCode: 'N01' })
      })

      it('area filter remains unchanged', () => {
        expect(state.probationAreasFilter).to.eql({ 'N02': 'N02 Area' })
      })
    })

    context('when area code is only one in filter', () => {
      beforeEach(() => {
        state = localStorageReducer({
          searchTerm: '',
          pageNumber: 1,
          probationAreasFilter: { 'N01': 'N01 Area' }
        }, { type: REMOVE_AREA_FILTER, probationAreaCode: 'N01' })
      })

      it('area filter becomes empty', () => {
        expect(state.probationAreasFilter).to.eql({})
      })
    })

    context('when area code is amongst others  in filter', () => {
      beforeEach(() => {
        state = localStorageReducer({
          searchTerm: '',
          pageNumber: 1,
          probationAreasFilter: { 'N01': 'N01 Area', 'N02': 'N02 Area' }
        }, { type: REMOVE_AREA_FILTER, probationAreaCode: 'N01' })
      })

      it('area filter has all but the one to remove', () => {
        expect(state.probationAreasFilter).to.eql({ 'N02': 'N02 Area' })
      })
    })
  })

  describe('when SEARCH_TYPE_CHANGED action received', () => {
    it('searchType will be changed to `exact`', () => {
      state = localStorageReducer({
          searchTerm: 'Mr Bean',
          searchType: 'broad',
          probationAreasFilter: { 'N01': 'N01 Area' },
          pageNumber: 3
        },
        { type: SEARCH_TYPE_CHANGED, searchType: 'exact' })

      expect(state.searchType).to.equal('exact')
    })

    it('searchType will be changed to `broad`', () => {
      state = localStorageReducer({
          searchTerm: 'Mr Bean',
          searchType: 'exact',
          probationAreasFilter: { 'N01': 'N01 Area' },
          pageNumber: 3
        },
        { type: SEARCH_TYPE_CHANGED, searchType: 'broad' })

      expect(state.searchType).to.equal('broad')
    })
  })
})
