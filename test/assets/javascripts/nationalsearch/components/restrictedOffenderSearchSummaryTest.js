import RestrictedOffenderSearchSummary from './restrictedOffenderSearchSummary'
import { expect } from 'chai'
import { shallow } from 'enzyme'
import { stub } from 'sinon'
import { offender } from '../test-helper'

describe('RestrictedOffenderSearchSummary component', () => {
  describe('rendering', () => {
    it('should render crn', () => {
      const offenderSummary = offender({
        otherIds: {
          crn: 'X12343'
        }
      })

      const summary = shallow(<RestrictedOffenderSearchSummary
        offenderSummary={offenderSummary}
        showOffenderDetails={() => {}} />)

      expect(summary.text()).to.contain('X12343')
    })
    it('should render add contact link', () => {
      const summary = shallow(<RestrictedOffenderSearchSummary
        offenderSummary={offender()}
        showOffenderDetails={() => {}} />)

      expect(summary.find('Connect(AddContactLink)')).to.have.length(1)
    })
    describe('offender manager', () => {
      let summary

      context('no offender manager node', () => {
        beforeEach(() => {
          const offenderSummary = offender()
          delete offenderSummary.offenderManagers
          summary = shallow(<RestrictedOffenderSearchSummary offenderSummary={offenderSummary}
                                                             searchTerm={'Smith Fred'}
                                                             showOffenderDetails={() => {}} />)
        })

        it('renders provider label with no text', () => {
          expect(summary.find('#provider').text().trim()).to.equal('Provider:')
        })
        it('renders officer label with no text', () => {
          expect(summary.find('#officer').text().trim()).to.equal('Officer name:')
        })
      })
      context('no offender managers', () => {
        beforeEach(() => {
          const offenderSummary = offender({ offenderManagers: [] })
          summary = shallow(<RestrictedOffenderSearchSummary offenderSummary={offenderSummary}
                                                             searchTerm={'Smith Fred'}
                                                             showOffenderDetails={() => {}} />)
        })

        it('renders provider label with no text', () => {
          expect(summary.find('#provider').text().trim()).to.equal('Provider:')
        })
        it('renders officer label with no text', () => {
          expect(summary.find('#officer').text().trim()).to.equal('Officer name:')
        })
      })
      context('no active offender managers', () => {
        beforeEach(() => {
          const offenderSummary = offender({
            offenderManagers: [
              {
                'staff': {
                  'forenames': 'Annette',
                  'surname': 'Anld'
                },
                'probationArea': {
                  'code': 'C16',
                  'description': 'CPA Thames Valley'
                },
                'fromDate': '2018-02-16',
                'active': false
              }]
          })
          summary = shallow(<RestrictedOffenderSearchSummary offenderSummary={offenderSummary}
                                                             searchTerm={'Smith Fred'}
                                                             showOffenderDetails={() => {}} />)
        })

        it('renders provider label with no text', () => {
          expect(summary.find('#provider').text().trim()).to.equal('Provider:')
        })
        it('renders officer label with no text', () => {
          expect(summary.find('#officer').text().trim()).to.equal('Officer name:')
        })
      })
      context('with active offender managers', () => {
        beforeEach(() => {
          const offenderSummary = offender({
            offenderManagers: [
              {
                'staff': {
                  'forenames': 'Annette',
                  'surname': 'Anld'
                },
                'probationArea': {
                  'code': 'C16',
                  'description': 'CPA Thames Valley'
                },
                'fromDate': '2018-02-16',
                'active': true
              }]
          })
          summary = shallow(<RestrictedOffenderSearchSummary offenderSummary={offenderSummary}
                                                             searchTerm={'Smith Fred'}
                                                             showOffenderDetails={() => {}} />)
        })

        it('renders active provider', () => {
          expect(summary.find('#provider').text().trim()).to.equal('Provider: CPA Thames Valley')
        })
        it('renders active officer', () => {
          expect(summary.find('#officer').text().trim()).to.equal('Officer name: Anld, Annette')
        })
      })
      context('with active and inactive offender managers', () => {
        beforeEach(() => {
          const offenderSummary = offender({
            offenderManagers: [
              {
                'staff': {
                  'forenames': 'Booby',
                  'surname': 'Boat'
                },
                'probationArea': {
                  'code': 'C19',
                  'description': 'CPA Birmingham'
                },
                'fromDate': '2018-02-16',
                'active': false
              },
              {
                'staff': {
                  'forenames': 'Annette',
                  'surname': 'Anld'
                },
                'probationArea': {
                  'code': 'C16',
                  'description': 'CPA Thames Valley'
                },
                'fromDate': '2018-02-16',
                'active': true
              },
              {
                'staff': {
                  'forenames': 'Trevor',
                  'surname': 'Boots'
                },
                'probationArea': {
                  'code': 'C18',
                  'description': 'CPA Sheffield'
                },
                'fromDate': '2018-02-16',
                'active': false
              }
            ]
          })
          summary = shallow(<RestrictedOffenderSearchSummary offenderSummary={offenderSummary}
                                                             searchTerm={'Smith Fred'}
                                                             showOffenderDetails={() => {}} />)
        })

        it('renders active provider', () => {
          expect(summary.find('#provider').text().trim()).to.equal('Provider: CPA Thames Valley')
        })
        it('renders active officer', () => {
          expect(summary.find('#officer').text().trim()).to.equal('Officer name: Anld, Annette')
        })
      })
      context('with many active offender managers', () => {
        beforeEach(() => {
          const offenderSummary = offender({
            offenderManagers: [
              {
                'staff': {
                  'forenames': 'Annette',
                  'surname': 'Anld'
                },
                'probationArea': {
                  'code': 'C16',
                  'description': 'CPA Thames Valley'
                },
                'fromDate': '2018-02-16',
                'active': true
              },
              {
                'staff': {
                  'forenames': 'Booby',
                  'surname': 'Boat'
                },
                'probationArea': {
                  'code': 'C19',
                  'description': 'CPA Birmingham'
                },
                'fromDate': '2018-02-16',
                'active': true
              },
              {
                'staff': {
                  'forenames': 'Trevor',
                  'surname': 'Boots'
                },
                'probationArea': {
                  'code': 'C18',
                  'description': 'CPA Sheffield'
                },
                'fromDate': '2018-02-16',
                'active': true
              }
            ]
          })
          summary = shallow(<RestrictedOffenderSearchSummary offenderSummary={offenderSummary}
                                                             searchTerm={'Smith Fred'}
                                                             showOffenderDetails={() => {}} />)
        })

        it('renders first active provider', () => {
          expect(summary.find('#provider').text().trim()).to.equal('Provider: CPA Thames Valley')
        })
        it('renders first active officer', () => {
          expect(summary.find('#officer').text().trim()).to.equal('Officer name: Anld, Annette')
        })
      })
    })
  })
  context('link clicked', () => {
    it('showOffenderDetails callback function called with offenderId', () => {
      const showOffenderDetails = stub()
      const offenderSummary = offender({ offenderId: 123 })

      const summary = shallow(<RestrictedOffenderSearchSummary
        offenderSummary={offenderSummary}
        showOffenderDetails={showOffenderDetails} />)

      summary.find('a').simulate('click')

      expect(showOffenderDetails).to.be.calledWith(123)
    })
  })
})
