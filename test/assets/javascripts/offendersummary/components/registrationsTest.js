import Registrations from './registrations'
import { expect } from 'chai'
import { shallow } from 'enzyme'
import { stub } from 'sinon'

describe('Registrations component', () => {
  let wrapper
  const someRegistrations = () => [aRegistration(), aRegistration()]

  const aRegistration = (registration = {}) =>
    Object.assign({
      registrationId: 1,
      offenderId: 1,
      register: {
        code: '1',
        description: 'Public Protection'
      },
      type: {
        code: 'REG1',
        description: 'Risk to Known Adult'
      },
      riskColour: 'Amber',
      startDate: '2018-12-11',
      nextReviewDate: '2019-06-11',
      reviewPeriodMonths: 6,
      notes: 'some notes',
      registeringTeam: {
        code: 'N07N07',
        description: 'CHT'
      },
      registeringOfficer: {
        forenames: 'Unallocated',
        surname: 'Staff'
      },
      registeringProbationArea: {
        code: 'N07',
        description: 'NPS London'
      },
      warnUser: false,
      active: true
    }, registration)

  context('on mount', () => {
    it('offender registrations are requested', () => {
      const getOffenderRegistrations = stub()
      shallow(<Registrations registrations={[]} error={false} fetching
                             getOffenderRegistrations={getOffenderRegistrations} viewOffenderRegistrations={stub()}
                             offenderId={123} />)

      expect(getOffenderRegistrations).to.be.calledOnce
    })
  })

  describe('registration rendering', () => {
    context('no registrations', () => {
      beforeEach(() => {
        wrapper = shallow(<Registrations registrations={[]} error={false} fetching={false}
                                         getOffenderRegistrations={stub()} viewOffenderRegistrations={stub()}
                                         offenderId={123} />)
      })

      it('contains registration count of zero', () => {
        expect(wrapper.find({ label: 'Active registers and warnings (0)' }).exists()).to.be.true
      })

      it('contains no data text', () => {
        expect(wrapper.find('.qa-no-registrations-message').text()).to.equal('No active registers and warnings recorded')
      })
    })

    context('some registrations', () => {
      beforeEach(() => {
        wrapper = shallow(<Registrations registrations={someRegistrations()} error={false} fetching={false}
                                         getOffenderRegistrations={stub()} viewOffenderRegistrations={stub()}
                                         offenderId={123} />)
      })

      it('contains registration count', () => {
        expect(wrapper.find({ label: 'Active registers and warnings (2)' }).exists()).to.be.true
      })
      it('contains row for each registration', () => {
        expect(wrapper.find('tbody tr')).to.have.length(2)
      })
    })

    context('a high rosh registration', () => {
      beforeEach(() => {
        wrapper = shallow(<Registrations registrations={[aRegistration({
          type: { description: 'Very High RoSH' },
          register: { description: 'RoSH' },
          startDate: '2018-12-11',
          riskColour: 'Red'
        })]} error={false} fetching={false} getOffenderRegistrations={stub()}
                                         viewOffenderRegistrations={stub()} offenderId={123} />)
      })

      it('contains register type', () => {
        expect(wrapper.find('tbody tr td').at(0).text()).to.equal('RoSH')
      })
      it('maps status text using rosh type', () => {
        expect(wrapper.find('tbody tr td').at(1).text()).to.equal('very high')
      })
      it('maps css class using risk colour', () => {
        expect(wrapper.find('tbody tr .moj-risk-tag--high').exists()).to.be.true
      })
      it('contains register description', () => {
        expect(wrapper.find('tbody tr td').at(2).text()).to.equal('Very High RoSH')
      })
      it('contains formatted start date', () => {
        expect(wrapper.find('tbody tr td').at(3).text()).to.equal('11/12/2018')
      })
    })
    context('a low rosh registration', () => {
      beforeEach(() => {
        wrapper = shallow(<Registrations registrations={[aRegistration({
          type: { description: 'Low RoSH' },
          register: { description: 'RoSH' },
          startDate: '2018-12-11',
          riskColour: 'Green'
        })]} error={false} fetching={false} getOffenderRegistrations={stub()}
                                         viewOffenderRegistrations={stub()} offenderId={123} />)
      })

      it('contains register type', () => {
        expect(wrapper.find('tbody tr td').at(0).text()).to.equal('RoSH')
      })
      it('maps status text using risk colour', () => {
        expect(wrapper.find('tbody tr td').at(1).text()).to.equal('low')
      })
      it('maps css class using risk colour', () => {
        expect(wrapper.find('tbody tr .moj-risk-tag--low').exists()).to.be.true
      })
      it('contains register description', () => {
        expect(wrapper.find('tbody tr td').at(2).text()).to.equal('Low RoSH')
      })
      it('contains formatted start date', () => {
        expect(wrapper.find('tbody tr td').at(3).text()).to.equal('11/12/2018')
      })
    })

    describe('sorting', () => {
      beforeEach(() => {
        wrapper = shallow(<Registrations registrations={[
          aRegistration({
            registrationId: 1,
            type: { description: 'AA' },
            register: { description: 'MM' },
            riskColour: 'Green'
          }),
          aRegistration({
            registrationId: 2,
            type: { description: 'AA' },
            register: { description: 'DD' },
            riskColour: 'Green'
          }),
          aRegistration({
            registrationId: 3,
            type: { description: 'BB' },
            register: { description: 'DD' },
            riskColour: 'Red'
          }),
          aRegistration({
            registrationId: 4,
            type: { description: 'AA' },
            register: { description: 'DD' },
            riskColour: 'Red'
          })
        ]} error={false} fetching={false} getOffenderRegistrations={stub()} viewOffenderRegistrations={stub()}
                                         offenderId={123} />)
      })

      it('is ordered by register type, risk colour and alert type', () => {
        expect(wrapper.find('tbody tr').at(0).key()).to.equal('4')
        expect(wrapper.find('tbody tr').at(1).key()).to.equal('3')
        expect(wrapper.find('tbody tr').at(2).key()).to.equal('2')
        expect(wrapper.find('tbody tr').at(3).key()).to.equal('1')
      })
    })

    describe('clicking View more registers and warnings link', () => {
      let viewOffenderRegistrations
      beforeEach(() => {
        viewOffenderRegistrations = stub()
        wrapper = shallow(<Registrations registrations={someRegistrations()} error={false} fetching={false}
                                         getOffenderRegistrations={stub()}
                                         viewOffenderRegistrations={viewOffenderRegistrations} offenderId={123} />)
      })

      it('callback called with offenderId', () => {
        wrapper.find('a').simulate('click')
        expect(viewOffenderRegistrations).to.be.calledWith(123)
      })
    })
  })
})
