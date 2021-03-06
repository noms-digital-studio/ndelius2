import {
  ADD_AREA_FILTER,
  CLEAR_RESULTS,
  NO_SAVED_SEARCH,
  REMOVE_AREA_FILTER,
  REQUEST_SEARCH,
  SAVED_SEARCH,
  SEARCH_RESULTS
} from '../actions/search'
import search from './searchReducer'
import { expect } from 'chai'
import { offender } from '../test-helper'

describe('searchReducer', () => {
  let state

  describe('when in default state', () => {
    beforeEach(() => {
      global.window = { probationAreas: { 'N03': 'NPS Wales', 'N02': 'NPS North East' } }
      state = search(undefined, { type: '"@@redux/INIT"' })
    })

    it('searchTerm will be blank', () => {
      expect(state.searchTerm).to.equal('')
    })
    it('resultsSearchTerm will be blank', () => {
      expect(state.resultsSearchTerm).to.equal('')
    })
    it('results will be empty', () => {
      expect(state.results).to.be.empty
    })
    it('suggestions will be empty', () => {
      expect(state.suggestions).to.be.empty
    })
    it('byProbationArea will be empty', () => {
      expect(state.byProbationArea).to.be.empty
    })
    it('probationAreasFilter will be empty', () => {
      expect(state.probationAreasFilter).to.eql({})
    })
    it('myProbationAreas will be populated from global window value', () => {
      expect(state.myProbationAreas).to.eql({ 'N03': 'NPS Wales', 'N02': 'NPS North East' })
    })
    it('total will be 0', () => {
      expect(state.total).to.equal(0)
    })
    it('pageNumber will be 1', () => {
      expect(state.pageNumber).to.equal(1)
    })
    it('resultsReceived will be false', () => {
      expect(state.resultsReceived).to.equal(false)
    })
    it('firstTimeIn will be true', () => {
      expect(state.firstTimeIn).to.equal(true)
    })
    it('showWelcomeBanner will be false', () => {
      expect(state.showWelcomeBanner).to.equal(false)
    })
    it('searchType will be `exact`', () => {
      expect(state.searchType).to.equal('exact')
    })
  })

  describe('when REQUEST_SEARCH action received', () => {
    beforeEach(() => {
      state = search({
        searchTerm: 'Mr Bean',
        resultsSearchTerm: 'Nobby',
        results: someResults(),
        resultsReceived: true,
        suggestions: someSuggestions(),
        byProbationArea: someByProbationArea(),
        total: 28,
        pageNumber: 3,
        firstTimeIn: true,
        showWelcomeBanner: true
      }, {
        type: REQUEST_SEARCH,
        searchTerm: 'John Smith'
      })
    })

    it('searchTerm is set to new value', () => {
      expect(state.searchTerm).to.equal('John Smith')
    })
    it('resultsSearchTerm is kept at existing value', () => {
      expect(state.resultsSearchTerm).to.equal('Nobby')
    })
    it('results is kept at existing value', () => {
      expect(state.results).to.eql(someResults())
    })
    it('suggestions is kept at existing value', () => {
      expect(state.suggestions).to.eql(someSuggestions())
    })
    it('byProbationArea is kept at existing value', () => {
      expect(state.byProbationArea).to.eql(someByProbationArea())
    })
    it('total is kept at existing value', () => {
      expect(state.total).to.equal(28)
    })
    it('pageNumber is kept at existing value', () => {
      expect(state.pageNumber).to.equal(3)
    })
    it('resultsReceived is kept at existing value', () => {
      expect(state.resultsReceived).to.equal(true)
    })
    it('firstTimeIn will be true', () => {
      expect(state.firstTimeIn).to.equal(true)
    })
    it('showWelcomeBanner will be true', () => {
      expect(state.showWelcomeBanner).to.equal(true)
    })
  })

  describe('when SEARCH_RESULTS action received', () => {
    context('when searchTerm matches from request action', () => {
      beforeEach(() => {
        state = search(
          {
            searchTerm: 'Mr Bean',
            resultsSearchTerm: 'Nobby',
            results: someResults(),
            resultsReceived: false,
            total: 28,
            pageNumber: 3,
            firstTimeIn: true,
            probationAreasFilter: ['N01']
          },
          { type: SEARCH_RESULTS, searchTerm: 'Mr Bean', results: emptyResults() })
      })
      it('results are replaced with new results', () => {
        expect(state.results).to.eql([])
      })
      it('resultsSearchTerm are replaced with new searchTerm', () => {
        expect(state.resultsSearchTerm).to.equal('Mr Bean')
      })
      it('resultsReceived is set to true', () => {
        expect(state.resultsReceived).to.equal(true)
      })
      it('firstTimeIn will be false', () => {
        expect(state.firstTimeIn).to.equal(false)
      })
      it('showWelcomeBanner will be false', () => {
        expect(state.showWelcomeBanner).to.equal(false)
      })
      it('probationAreasFilter remains unchanged', () => {
        expect(state.probationAreasFilter).to.eql(['N01'])
      })
    })

    context('when searchTerm partial matches from request action', () => {
      beforeEach(() => {
        state = search(
          { searchTerm: 'Mr Bean Bobby', resultsSearchTerm: 'Nobby', results: someResults(), total: 28, pageNumber: 3 },
          { type: SEARCH_RESULTS, searchTerm: 'Mr Bean', results: emptyResults() })
      })
      it('results are replaced with new results', () => {
        expect(state.results).to.eql([])
      })
      it('resultsSearchTerm are replaced with new searchTerm', () => {
        expect(state.resultsSearchTerm).to.equal('Mr Bean')
      })
      it('resultsReceived is set to true', () => {
        expect(state.resultsReceived).to.equal(true)
      })
    })

    context('when searchTerm does not match from request action', () => {
      beforeEach(() => {
        state = search(
          {
            searchTerm: 'Mr Fancy',
            resultsSearchTerm: 'Nobby',
            results: someResults(),
            resultsReceived: false,
            total: 28,
            pageNumber: 3
          },
          { type: SEARCH_RESULTS, searchTerm: 'Mr Bean', results: emptyResults() })
      })
      it('existing results are kept and new results discarded', () => {
        expect(state.results).to.eql(someResults())
      })
      it('resultsSearchTerm is kept at existing value', () => {
        expect(state.resultsSearchTerm).to.equal('Nobby')
      })
      it('total is kept at existing value', () => {
        expect(state.total).to.equal(28)
      })
      it('pageNumber is kept at existing value', () => {
        expect(state.pageNumber).to.equal(3)
      })
      it('resultsReceived is kept at existing value', () => {
        expect(state.resultsReceived).to.equal(false)
      })
    })

    context('when current searchTerm is blank but results received from previous request', () => {
      beforeEach(() => {
        state = search(
          { searchTerm: '', resultsSearchTerm: '', results: emptyResults(), total: 0, pageNumber: 1 },
          { type: SEARCH_RESULTS, searchTerm: 'Mr Bean', results: someResults() })
      })
      it('existing empty results are kept and new results discarded', () => {
        expect(state.results).to.eql(emptyResults())
      })
      it('resultsSearchTerm is kept at existing value', () => {
        expect(state.resultsSearchTerm).to.equal('')
      })
      it('total is kept at existing value', () => {
        expect(state.total).to.equal(0)
      })
      it('pageNumber is kept at existing value', () => {
        expect(state.pageNumber).to.equal(1)
      })
    })

    describe('search results copying', () => {
      beforeEach(() => {
        state = search(
          { searchTerm: 'Mr Bean', results: emptyResults(), byProbationArea: [], total: 0, pageNumber: 1 },
          {
            type: SEARCH_RESULTS,
            searchTerm: 'Mr Bean',
            pageNumber: 3,
            results: someResults({
              offenders: [
                {
                  offenderId: '99',
                  firstName: 'Rose',
                  surname: 'Blakesmith',
                  crn: 'DL999',
                  dateOfBirth: '19-07-1965',
                  risk: 'Red',
                  currentOffender: false,
                  gender: 'Female',
                  age: 23,
                  contactDetails: {
                    addresses: []
                  },
                  offenderAliases: []
                }
              ],
              total: 4,
              aggregations: {
                byProbationArea: someByProbationArea()
              }
            })
          })
      })
      it('pageNumber is copied', () => {
        expect(state.pageNumber).to.equal(3)
      })
      it('byProbationArea is copied', () => {
        expect(state.byProbationArea).to.eql(someByProbationArea())
      })
      it('total is copied', () => {
        expect(state.total).to.equal(4)
      })
      it('core attributes copied', () => {
        expect(state.results[0].offenderId).to.equal('99')
        expect(state.results[0].firstName).to.equal('Rose')
        expect(state.results[0].surname).to.equal('Blakesmith')
        expect(state.results[0].crn).to.equal('DL999')
        expect(state.results[0].dateOfBirth).to.equal('19-07-1965')
        expect(state.results[0].risk).to.equal('Red')
        expect(state.results[0].currentOffender).to.equal(false)
        expect(state.results[0].gender).to.equal('Female')
        expect(state.results[0].age).to.equal(23)
      })
    })

    describe('search rank index calculation', () => {
      context(('result is on first page'), () => {
        beforeEach(() => {
          state = search(
            { searchTerm: 'Mr Bean', results: emptyResults(), total: 0, pageNumber: 1 },
            {
              type: SEARCH_RESULTS,
              searchTerm: 'Mr Bean',
              pageNumber: 1,
              results: someResults({
                offenders: [
                  offender({ offenderId: '101' }),
                  offender({ offenderId: '102' }),
                  offender({ offenderId: '103' })
                ],
                total: 20
              })
            })
        })
        it('set rank index based on position and page', () => {
          expect(state.results[0].rankIndex).to.equal(1)
          expect(state.results[1].rankIndex).to.equal(2)
          expect(state.results[2].rankIndex).to.equal(3)
        })
      })

      context(('result is on second page'), () => {
        beforeEach(() => {
          state = search(
            { searchTerm: 'Mr Bean', results: emptyResults(), total: 0, pageNumber: 1 },
            {
              type: SEARCH_RESULTS,
              searchTerm: 'Mr Bean',
              pageNumber: 2,
              results: someResults({
                offenders: [
                  offender({ offenderId: '101' }),
                  offender({ offenderId: '102' }),
                  offender({ offenderId: '103' })
                ],
                total: 20
              })
            })
        })
        it('set rank index based on position and page', () => {
          expect(state.results[0].rankIndex).to.equal(11)
          expect(state.results[1].rankIndex).to.equal(12)
          expect(state.results[2].rankIndex).to.equal(13)
        })
      })
    })

    describe('address copying', () => {
      context('no contact details', () => {
        beforeEach(() => {
          const results = someResults()
          delete results.offenders[0].contactDetails
          state = search(
            { searchTerm: 'Mr Bean', results: someResults(), total: 0, pageNumber: 1 },
            { type: SEARCH_RESULTS, searchTerm: 'Mr Bean', results: results })
        })

        it('addresses are empty', () => {
          expect(state.results[0].addresses).to.have.lengthOf(0)
        })
      })

      context('with contact details', () => {
        beforeEach(() => {
          state = search(
            { searchTerm: 'Mr Bean sheffield', results: someResults(), total: 0, pageNumber: 1 },
            {
              type: SEARCH_RESULTS,
              searchTerm: 'Mr Bean sheffield',
              results: someSingleResultWithAddresses([
                {
                  buildingName: 'Sloppy Buildings',
                  addressNumber: '1',
                  streetName: 'High Street',
                  town: 'Sheffield',
                  county: 'South Yorkshire',
                  postcode: 'S1 2BX'
                },
                {
                  addressNumber: '1',
                  streetName: 'Sheffield Road',
                  town: 'Leeds',
                  postcode: 'LS1 2BX'
                },
                {
                  addressNumber: '1',
                  streetName: 'Ticky Street',
                  town: 'London',
                  postcode: 'SW11 2BX'

                }
              ])
            })
        })

        it('all addresses are copied', () => {
          expect(state.results[0].addresses).to.have.lengthOf(3)
        })
        it('all addresses attributes copied from address', () => {
          expect(state.results[0].addresses[0].buildingName).to.equal('Sloppy Buildings')
          expect(state.results[0].addresses[0].addressNumber).to.equal('1')
          expect(state.results[0].addresses[0].streetName).to.equal('High Street')
          expect(state.results[0].addresses[0].town).to.equal('Sheffield')
          expect(state.results[0].addresses[0].county).to.equal('South Yorkshire')
          expect(state.results[0].addresses[0].postcode).to.equal('S1 2BX')
        })
      })
    })

    describe('alias copying', () => {
      context('no aliases', () => {
        beforeEach(() => {
          const results = someResults()
          delete results.offenders[0].aliases
          state = search(
            { searchTerm: 'Mr Bean', results: someResults(), total: 0, pageNumber: 1 },
            { type: SEARCH_RESULTS, searchTerm: 'Mr Bean', results: results })
        })

        it('aliases are empty', () => {
          expect(state.results[0].aliases).to.have.lengthOf(0)
        })
      })

      context('aliases present', () => {
        beforeEach(() => {
          state = search(
            { searchTerm: 'bean', results: someResults(), total: 0, pageNumber: 1 },
            {
              type: SEARCH_RESULTS,
              searchTerm: 'bean',
              results: someSingleResultWithAliases([
                {
                  firstName: 'Bean',
                  surname: 'Bland'
                },
                {
                  firstName: 'Bobby',
                  surname: 'Bean'
                },
                {
                  firstName: 'Tommy',
                  surname: 'Tibbs'
                }
              ])
            })
        })

        it('all aliases copied', () => {
          expect(state.results[0].aliases).to.have.lengthOf(3)
        })
        it('all aliases attributes copied from alias', () => {
          expect(state.results[0].aliases[0].firstName).to.equal('Bean')
          expect(state.results[0].aliases[0].surname).to.equal('Bland')
        })
      })
    })

    describe('previous surname filtering', () => {
      context('previous surname not present in results', () => {
        beforeEach(() => {
          state = search(
            { searchTerm: 'trevor', results: someResults(), total: 0, pageNumber: 1 },
            { type: SEARCH_RESULTS, searchTerm: 'trevor', results: someSingleResultWithPreviousSurname() })
        })

        it('previous surname remains undefined', () => {
          expect(state.results[0].previousSurname).to.be.undefined
        })
      })

      context('previous surname in results', () => {
        beforeEach(() => {
          state = search(
            { searchTerm: 'trevor', results: someResults(), total: 0, pageNumber: 1 },
            { type: SEARCH_RESULTS, searchTerm: 'trevor', results: someSingleResultWithPreviousSurname('Trevor') })
        })

        it('previous surname is copied', () => {
          expect(state.results[0].previousSurname).to.equal('Trevor')
        })
      })
    })

    describe('suggestions merging', () => {
      context('no suggest node at all', () => {
        beforeEach(() => {
          state = search(
            { searchTerm: 'trevor', results: someResults(), total: 0, pageNumber: 1 },
            { type: SEARCH_RESULTS, searchTerm: 'trevor', results: someResultsWithSuggestions({ suggestions: {} }) })
        })
        it('suggestions are cleared', () => {
          expect(state.suggestions).to.be.empty
        })
      })

      context('no suggest terms at all', () => {
        beforeEach(() => {
          state = search(
            { searchTerm: 'trevor', results: someResults(), total: 0, pageNumber: 1 },
            {
              type: SEARCH_RESULTS,
              searchTerm: 'trevor',
              results: someResultsWithSuggestions({ suggestions: { suggest: {} } })
            })
        })
        it('suggestions are cleared', () => {
          expect(state.suggestions).to.be.empty
        })
      })

      context('no suggests have any suggestions', () => {
        beforeEach(() => {
          state = search(
            { searchTerm: 'trevor', results: someResults(), total: 0, pageNumber: 1 },
            {
              type: SEARCH_RESULTS,
              searchTerm: 'trevor',
              results: someResultsWithSuggestions({
                suggestions: {
                  suggest: {
                    firstName: [
                      {
                        text: 'smyth',
                        offset: 0,
                        length: 5,
                        options: []
                      },
                      {
                        text: 'gery',
                        offset: 6,
                        length: 4,
                        options: []
                      }
                    ],
                    surname: [
                      {
                        text: 'smyth',
                        offset: 0,
                        length: 5,
                        options: []
                      },
                      {
                        text: 'gery',
                        offset: 6,
                        length: 4,
                        options: []
                      }
                    ]
                  }

                }
              })
            })
        })
        it('suggestions are cleared', () => {
          expect(state.suggestions).to.be.empty
        })
      })

      context('one search term word has suggestions', () => {
        beforeEach(() => {
          state = search(
            { searchTerm: 'trevor', results: someResults(), total: 0, pageNumber: 1 },
            {
              type: SEARCH_RESULTS,
              searchTerm: 'trevor',
              results: someResultsWithSuggestions(
                {
                  suggestions: {
                    suggest: {
                      firstName: [
                        {
                          text: 'smyth',
                          offset: 0,
                          length: 5,
                          options: []
                        },
                        {
                          text: 'gery',
                          offset: 6,
                          length: 4,
                          options: []
                        }
                      ],
                      surname: [
                        {
                          text: 'smyth',
                          offset: 0,
                          length: 5,
                          options: [
                            {
                              'text': 'smith',
                              'score': 0.8,
                              'freq': 10
                            },
                            {
                              'text': 'smithy',
                              'score': 0.7,
                              'freq': 10
                            }
                          ]
                        },
                        {
                          text: 'gery',
                          offset: 6,
                          length: 4,
                          options: []
                        }
                      ]
                    }

                  }
                })
            })
        })
        it('suggestions are set search term word with score', () => {
          expect(state.suggestions).to.have.length(1)
          expect(state.suggestions).to.shallowDeepEqual(
            [{
              text: 'smyth',
              options: [
                {
                  'text': 'smith',
                  'score': 0.8
                },
                {
                  'text': 'smithy',
                  'score': 0.7
                }
              ]

            }]
          )
        })
      })
      context('many search term words have suggestions', () => {
        beforeEach(() => {
          state = search(
            { searchTerm: 'trevor', results: someResults(), total: 0, pageNumber: 1 },
            {
              type: SEARCH_RESULTS,
              searchTerm: 'trevor',
              results: someResultsWithSuggestions({
                suggestions: {
                  suggest: {
                    firstName: [
                      {
                        text: 'smyth',
                        offset: 0,
                        length: 5,
                        options: []
                      },
                      {
                        text: 'gery',
                        offset: 6,
                        length: 4,
                        options: [
                          {
                            'text': 'gary',
                            'score': 0.8,
                            'freq': 10
                          }
                        ]
                      }
                    ],
                    surname: [
                      {
                        text: 'smyth',
                        offset: 0,
                        length: 5,
                        options: [
                          {
                            'text': 'smith',
                            'score': 0.8,
                            'freq': 10
                          },
                          {
                            'text': 'smithy',
                            'score': 0.7,
                            'freq': 10
                          }
                        ]
                      },
                      {
                        text: 'gery',
                        offset: 6,
                        length: 4,
                        options: []
                      }
                    ]
                  }

                }
              })
            })
        })
        it('suggestions are set search term word with score', () => {
          expect(state.suggestions).to.have.length(2)
          expect(state.suggestions).to.shallowDeepEqual(
            [
              {
                text: 'gery',
                options: [
                  {
                    'text': 'gary',
                    'score': 0.8
                  }
                ]
              },
              {
                text: 'smyth',
                options: [
                  {
                    'text': 'smith',
                    'score': 0.8
                  },
                  {
                    'text': 'smithy',
                    'score': 0.7
                  }
                ]
              }
            ]
          )
        })
      })
    })
  })

  describe('when CLEAR_RESULTS action received', () => {
    beforeEach(() => {
      state = search({
        searchTerm: 'Mr Bean',
        resultsReceived: true,
        resultsSearchTerm: 'nobby',
        results: [{ aResult: {} }],
        suggestions: someSuggestions(),
        total: 28,
        pageNumber: 3,
        firstTimeIn: false
      }, { type: CLEAR_RESULTS })
    })

    it('searchTerm will be blank', () => {
      expect(state.searchTerm).to.equal('')
    })
    it('resultsSearchTerm will be blank', () => {
      expect(state.resultsSearchTerm).to.equal('')
    })
    it('results will be empty', () => {
      expect(state.results).to.be.empty
    })
    it('suggestions are cleared', () => {
      expect(state.suggestions).to.be.empty
    })
    it('total is cleared', () => {
      expect(state.total).to.equal(0)
    })
    it('pageNumber is reset', () => {
      expect(state.pageNumber).to.equal(1)
    })
    it('resultsReceived is set to false', () => {
      expect(state.resultsReceived).to.equal(false)
    })
    it('firstTimeIn will remain false', () => {
      expect(state.firstTimeIn).to.equal(false)
    })
  })

  describe('when NO_SAVED_SEARCH action received', () => {
    context('when first time in', () => {
      beforeEach(() => {
        state = search({
          searchTerm: '',
          resultsReceived: false,
          resultsSearchTerm: '',
          results: [],
          suggestions: someSuggestions(),
          total: 0,
          pageNumber: 1,
          firstTimeIn: true
        }, { type: NO_SAVED_SEARCH })
      })

      it('showWelcomeBanner will be true', () => {
        expect(state.showWelcomeBanner).to.equal(true)
      })
    })

    context('when no longer first time in', () => {
      beforeEach(() => {
        state = search({
          searchTerm: '',
          resultsReceived: false,
          resultsSearchTerm: '',
          results: [],
          suggestions: someSuggestions(),
          total: 0,
          pageNumber: 1,
          firstTimeIn: false
        }, { type: NO_SAVED_SEARCH })
      })

      it('showWelcomeBanner will be false', () => {
        expect(state.showWelcomeBanner).to.equal(false)
      })
    })
  })

  describe('when SAVED_SEARCH action received', () => {
    beforeEach(() => {
      state = search({
        searchTerm: '',
        resultsReceived: false,
        resultsSearchTerm: '',
        results: [],
        suggestions: someSuggestions(),
        total: 0,
        pageNumber: 1,
        firstTimeIn: false
      }, { type: SAVED_SEARCH, searchTerm: 'Mr Bean', probationAreasFilter: { 'N01': 'N01 Area' } })
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
        state = search({
          searchTerm: '',
          resultsReceived: false,
          resultsSearchTerm: '',
          results: [],
          suggestions: someSuggestions(),
          total: 0,
          pageNumber: 1,
          firstTimeIn: true,
          probationAreasFilter: {}
        }, { type: ADD_AREA_FILTER, probationAreaCode: 'N01', probationAreaDescription: 'N01 Area' })
      })

      it('area filter just contains new code', () => {
        expect(state.probationAreasFilter).to.eql({ 'N01': 'N01 Area' })
      })
    })

    context('when area code already in filter', () => {
      beforeEach(() => {
        state = search({
          searchTerm: '',
          resultsReceived: false,
          resultsSearchTerm: '',
          results: [],
          suggestions: someSuggestions(),
          total: 0,
          pageNumber: 1,
          firstTimeIn: true,
          probationAreasFilter: { 'N01': 'N01 Area' }
        }, { type: ADD_AREA_FILTER, probationAreaCode: 'N01', probationAreaDescription: 'N01 Area' })
      })

      it('area filter just contains existing code', () => {
        expect(state.probationAreasFilter).to.eql({ 'N01': 'N01 Area' })
      })
    })

    context('when filter set with other codes', () => {
      beforeEach(() => {
        state = search({
          searchTerm: '',
          resultsReceived: false,
          resultsSearchTerm: '',
          results: [],
          suggestions: someSuggestions(),
          total: 0,
          pageNumber: 1,
          firstTimeIn: true,
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
        state = search({
          searchTerm: '',
          resultsReceived: false,
          resultsSearchTerm: '',
          results: [],
          suggestions: someSuggestions(),
          total: 0,
          pageNumber: 1,
          firstTimeIn: true,
          probationAreasFilter: { 'N02': 'N02 Area' }
        }, { type: REMOVE_AREA_FILTER, probationAreaCode: 'N01' })
      })

      it('area filter remains unchanged', () => {
        expect(state.probationAreasFilter).to.eql({ 'N02': 'N02 Area' })
      })
    })

    context('when area code is only one in filter', () => {
      beforeEach(() => {
        state = search({
          searchTerm: '',
          resultsReceived: false,
          resultsSearchTerm: '',
          results: [],
          suggestions: someSuggestions(),
          total: 0,
          pageNumber: 1,
          firstTimeIn: true,
          probationAreasFilter: { 'N01': 'N01 Area' }
        }, { type: REMOVE_AREA_FILTER, probationAreaCode: 'N01' })
      })

      it('area filter becomes empty', () => {
        expect(state.probationAreasFilter).to.eql({})
      })
    })

    context('when area code is amongst others  in filter', () => {
      beforeEach(() => {
        state = search({
          searchTerm: '',
          resultsReceived: false,
          resultsSearchTerm: '',
          results: [],
          suggestions: someSuggestions(),
          total: 0,
          pageNumber: 1,
          firstTimeIn: true,
          probationAreasFilter: { 'N01': 'N01 Area', 'N02': 'N02 Area' }
        }, { type: REMOVE_AREA_FILTER, probationAreaCode: 'N01' })
      })

      it('area filter has all but the one to remove', () => {
        expect(state.probationAreasFilter).to.eql({ 'N02': 'N02 Area' })
      })
    })
  })
})

const someResults = (results = {}) => (
  Object.assign({
    offenders: [{
      offenderId: '123',
      firstName: 'John',
      surname: 'Smith',
      contactDetails: {
        addresses: []
      },
      offenderAliases: []
    }],
    suggestions: {
      suggest: {
        firstName: [
          {
            text: 'smyth',
            offset: 0,
            length: 5,
            options: []
          },
          {
            text: 'gery',
            offset: 6,
            length: 4,
            options: [
              {
                text: 'gary',
                score: 0.75,
                freq: 1
              }
            ]
          }
        ],
        surname: [
          {
            text: 'smyth',
            offset: 0,
            length: 5,
            options: [
              {
                text: 'smith',
                score: 0.8,
                freq: 10
              }
            ]
          },
          {
            text: 'gery',
            offset: 6,
            length: 4,
            options: []
          }
        ]
      }
    },
    total: 1,
    aggregations: {
      byProbationArea: someByProbationArea()
    }
  }, results))

const someResultsWithSuggestions = ({ suggestions } = {}) => Object.assign(someResults(), { suggestions })
const emptyResults = () => ({
  offenders: [],
  total: 0,
  suggestions: {
    suggest: {
      firstName: [
        {
          text: 'smyth',
          offset: 0,
          length: 5,
          options: []
        },
        {
          text: 'gery',
          offset: 6,
          length: 4,
          options: []
        }
      ],
      surname: [
        {
          text: 'smyth',
          offset: 0,
          length: 5,
          options: []
        },
        {
          text: 'gery',
          offset: 6,
          length: 4,
          options: []
        }
      ]
    }
  },
  aggregations: {
    byProbationArea: []
  }

})

const someSingleResultWithAddresses = addresses => {
  const results = someResults()
  results.offenders = [results.offenders[0]]
  results.offenders[0].contactDetails.addresses = addresses
  return results
}

const someSingleResultWithAliases = aliases => {
  const results = someResults()
  results.offenders = [results.offenders[0]]
  results.offenders[0].offenderAliases = aliases
  return results
}

const someSingleResultWithPreviousSurname = previousSurname => {
  const results = someResults()
  results.offenders = [results.offenders[0]]
  if (previousSurname) {
    results.offenders[0].previousSurname = previousSurname
  } else {
    delete results.offenders[0].previousSurname
  }
  return results
}

const someSuggestions = () => [
  {
    smyth: [
      { text: 'smith', score: 0.8 },
      { text: 'smithy', score: 0.7 }
    ]
  },
  {
    jihnny: [
      { text: 'johny', score: 0.8 },
      { text: 'john', score: 0.6 }
    ]
  }
]
const someByProbationArea = () => [
  { code: 'N02', count: 2, description: 'NPS North East' },
  { code: 'N01', count: 1, description: 'NPS North West' },
  { code: 'N03', count: 1, description: 'NPS Wales' }
]
