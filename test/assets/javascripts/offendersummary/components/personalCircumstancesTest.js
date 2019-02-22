import PersonalCircumstances from './personalCircumstances'
import { expect } from 'chai'
import { shallow } from 'enzyme'
import { stub } from 'sinon'

describe('PersonalCircumstances component', () => {
  beforeEach(() => {
    global.window = {
      GOVUKFrontend: {
        Details: stub()
      }
    }
    global.document = {
      querySelector: stub()
    }
    global.window.GOVUKFrontend.Details.prototype.init = stub()
  })

  const aPersonalCircumstance = (appointment = {}) =>
    Object.assign({
      personalCircumstanceId: 1,
      offenderId: 1,
      personalCircumstanceType: {
        code: 'D',
        description: 'General Health'
      },
      personalCircumstanceSubType: {
        code: 'D01',
        description: 'Mental Health Concerns'
      },
      startDate: '2019-01-01',
      endDate: '2019-03-06',
      probationArea: {
        code: 'N07',
        description: 'NPS London'
      },
      evidenced: false
    }, appointment)

  context('on mount', () => {
    let getOffenderPersonalCircumstances
    beforeEach(() => {
      getOffenderPersonalCircumstances = stub()
      shallow(<PersonalCircumstances error={false} fetching
                                     getOffenderPersonalCircumstances={getOffenderPersonalCircumstances}
                                     viewOffenderPersonalCircumstances={stub()} offenderId={123} />)
    })

    it('personal circumstances are requested', () => {
      expect(getOffenderPersonalCircumstances).to.be.calledOnce
    })
    it('details section is initialised', () => {
      expect(global.window.GOVUKFrontend.Details.prototype.init).to.be.calledOnce
    })
  })

  describe('rendering PersonalCircumstances', () => {
    let wrapper

    context('when fetching', () => {
      beforeEach(() => {
        wrapper = shallow(<PersonalCircumstances fetching error={false} getOffenderPersonalCircumstances={stub()}
                                                 viewOffenderPersonalCircumstances={stub()} offenderId={123} />)
      })

      it('no main content is displayed', () => {
        expect(wrapper.find('.qa-offender-personal-circumstances').exists()).to.be.false
      })
    })
    context('when finished fetching', () => {
      beforeEach(() => {
        wrapper = shallow(<PersonalCircumstances fetching={false} error={false}
                                                 circumstances={[aPersonalCircumstance()]}
                                                 getOffenderPersonalCircumstances={stub()}
                                                 viewOffenderPersonalCircumstances={stub()} offenderId={123} />)
      })

      it('main content is displayed', () => {
        expect(wrapper.find('.qa-offender-personal-circumstances').exists()).to.be.true
      })

      context('with no data', () => {
        beforeEach(() => {
          wrapper = shallow(<PersonalCircumstances fetching={false} error={false} circumstances={[]}
                                                   getOffenderPersonalCircumstances={stub()}
                                                   viewOffenderPersonalCircumstances={stub()} offenderId={123} />)
        })
        it('renders a no data message', () => {
          expect(wrapper.find('.qa-no-pc-recorded-message').text()).to.equal('No personal circumstance recorded')
        })
      })
      context('with data', () => {
        beforeEach(() => {
          wrapper = shallow(<PersonalCircumstances fetching={false} error={false} circumstances={[
            aPersonalCircumstance({
              startDate: '2018-01-19',
              personalCircumstanceType: {
                description: 'Accommodation'
              },
              personalCircumstanceSubType: {
                description: 'No fixed abode'
              }
            })
          ]} getOffenderPersonalCircumstances={stub()} viewOffenderPersonalCircumstances={stub()}
                                                   offenderId={123} />)
        })
        it('renders type', () => {
          expect(wrapper.find('tbody tr td').at(0).text()).to.equal('Accommodation')
        })
        it('renders sub type', () => {
          expect(wrapper.find('tbody tr td').at(1).text()).to.equal('No fixed abode')
        })
        it('renders start date formatted', () => {
          expect(wrapper.find('tbody tr td').at(2).text()).to.equal('19/01/2018')
        })
      })
      context('with lots of data', () => {
        beforeEach(() => {
          wrapper = shallow(<PersonalCircumstances fetching={false} error={false} circumstances={[
            aPersonalCircumstance({ personalCircumstanceId: 1, startDate: '2018-01-19' }),
            aPersonalCircumstance({ personalCircumstanceId: 2, startDate: '2019-01-19' }),
            aPersonalCircumstance({ personalCircumstanceId: 3, startDate: '2017-01-19' }),
            aPersonalCircumstance({ personalCircumstanceId: 4, startDate: '2016-01-19' })
          ]} getOffenderPersonalCircumstances={stub()} viewOffenderPersonalCircumstances={stub()}
                                                   offenderId={123} />)
        })
        it('renders row for each circumstance', () => {
          expect(wrapper.find('tbody tr')).to.have.length(4)
        })

        it('is ordered by start date', () => {
          expect(wrapper.find('tbody tr').at(0).key()).to.equal('2')
          expect(wrapper.find('tbody tr').at(1).key()).to.equal('1')
          expect(wrapper.find('tbody tr').at(2).key()).to.equal('3')
          expect(wrapper.find('tbody tr').at(3).key()).to.equal('4')
        })
      })
    })
    context('when in error', () => {
      beforeEach(() => {
        wrapper = shallow(<PersonalCircumstances fetching={false} error circumstances={[]}
                                                 getOffenderPersonalCircumstances={stub()}
                                                 viewOffenderPersonalCircumstances={stub()} offenderId={123} />)
      })

      it('no main content is displayed', () => {
        expect(wrapper.find('.qa-offender-personal-circumstances').exists()).to.be.false
      })
      it('error is displayed', () => {
        expect(wrapper.find('ErrorMessage').exists()).to.be.true
      })
    })
  })
  describe('clicking view more personal circumstances link', () => {
    let wrapper
    let viewOffenderPersonalCircumstances

    beforeEach(() => {
      viewOffenderPersonalCircumstances = stub()
      wrapper = shallow(<PersonalCircumstances fetching={false} error={false}
                                               circumstances={[aPersonalCircumstance()]}
                                               getOffenderPersonalCircumstances={stub()}
                                               viewOffenderPersonalCircumstances={viewOffenderPersonalCircumstances}
                                               offenderId={123} />)
    })

    it('callback called with offenderId', () => {
      wrapper.find('a').simulate('click')
      expect(viewOffenderPersonalCircumstances).to.be.calledWith(123)
    })
  })
})
