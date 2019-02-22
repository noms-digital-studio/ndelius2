import React from 'react'
import { expect } from 'chai'
import { shallow } from 'enzyme'
import OffenderCards from './offenderCards'
import { stub } from 'sinon'

describe('OffenderCards component', () => {
  let wrapper
  let offenderConvictions = []
  let offenderManager = {
    probationArea: {
      description: 'NPS North West'
    },
    staff: {
      forenames: 'John',
      surname: 'Smith'
    }
  }

  const aConviction = (conviction = {}) =>
    Object.assign({
      convictionId: 2500281204,
      inBreach: false,
      convictionDate: '2018-12-18',
      referralDate: '2018-12-18',
      offences: [
        {
          offenceId: 'M2500281204',
          mainOffence: true,
          detail: {
            code: '08902',
            description: 'Adulteration etc of milk products - 08902',
            mainCategoryCode: '089',
            mainCategoryDescription: 'Adulteration of food or drugs (Food Safety Act 1990)',
            mainCategoryAbbreviation: 'Adulteration of food or drugs (Food Safety Act 19',
            ogrsOffenceCategory: 'Other offence',
            subCategoryCode: '02',
            subCategoryDescription: 'Adulteration etc of milk products',
            form20Code: '12'
          },
          offenceDate: '2018-12-02T00:00:00',
          offenceCount: 1,
          offenderId: 2500099840,
          createdDatetime: '2018-12-18T10:53:33',
          lastUpdatedDatetime: '2018-12-18T10:53:33'
        }
      ],
      sentence: {
        description: 'ORA Community Order',
        originalLength: 18,
        originalLengthUnits: 'Months',
        defaultLength: 18,
        lengthInDays: 547
      },
      latestCourtAppearanceOutcome: {
        code: '329',
        description: 'ORA Community Order'
      }
    }, conviction)

  describe('Events card', () => {
    context('When the offender has no events', () => {
      beforeEach(() => {
        offenderConvictions = {
          convictions: []
        }

        wrapper = shallow(
          <OffenderCards offenderConvictions={offenderConvictions} offenderManager={offenderManager}
                         transferInactiveOffender={stub()} offenderId={123} />
        )
      })

      it('Should display no events', () => {
        expect(wrapper.find('.qa-card-events').text()).to.equal('0 events')
      })
    })

    context('When the offender has only inactive events', () => {
      beforeEach(() => {
        offenderConvictions = {
          convictions: [
            aConviction({ convictionId: 1, active: false }),
            aConviction({ convictionId: 2, active: false })
          ]
        }

        wrapper = shallow(
          <OffenderCards offenderConvictions={offenderConvictions} offenderManager={offenderManager}
                         transferInactiveOffender={stub()} offenderId={123} />
        )
      })

      it('Should display the number of events and active events', () => {
        expect(wrapper.find('.qa-card-events').text()).to.equal('2 events (0 active)')
      })

      it('Should not display any event data', () => {
        expect(wrapper.find('.qa-card-active-event').exists()).equal(false)
      })
    })

    context('When the offender has active and inactive events', () => {
      beforeEach(() => {
        offenderConvictions = {
          convictions: [
            aConviction({ convictionId: 1, active: true }),
            aConviction({
              convictionId: 2,
              active: true,
              referralDate: '2018-12-19',
              sentence: {
                description: 'ORA Community Order',
                originalLength: 12,
                originalLengthUnits: 'Months',
                defaultLength: 12,
                lengthInDays: 365
              }
            }),
            aConviction({ convictionId: 3, active: false })
          ]
        }

        wrapper = shallow(
          <OffenderCards offenderConvictions={offenderConvictions} offenderManager={offenderManager}
                         transferInactiveOffender={stub()} offenderId={123} />
        )
      })

      it('Should display the number of events and active events', () => {
        expect(wrapper.find('.qa-card-events').text()).to.equal('3 events (2 active)')
      })

      it('Should display the latest active event data', () => {
        expect(wrapper.find('.qa-card-active-event').exists()).equal(true)
      })

      it('Should display the latest active event description', () => {
        expect(wrapper.find('.qa-card-active-event').text()).to.equal('Last active event: ORA Community Order (12 Months)')
      })
    })
  })

  describe('Offender manager card', () => {
    context('When the offender has NO active events', () => {
      let transferInactiveOffender

      beforeEach(() => {
        offenderManager = {
          probationArea: {
            description: 'NPS North West'
          },
          staff: {
            forenames: 'John',
            surname: 'Smith'
          }
        }

        offenderConvictions = {
          convictions: [
            aConviction({ convictionId: 1, active: false })
          ]
        }

        transferInactiveOffender = stub()

        wrapper = shallow(
          <OffenderCards offenderConvictions={offenderConvictions} offenderManager={offenderManager}
                         transferInactiveOffender={transferInactiveOffender} offenderId={123} />
        )
      })

      it('Should display offender status as not current', () => {
        expect(wrapper.find('.qa-card-current-status').text()).to.equal('Not current')
      })

      it('Should display transfer in link', () => {
        expect(wrapper.find('a').text()).to.equal('Transfer in')
      })

      it('clicking link should call callback', () => {
        wrapper.find('a').simulate('click')
        expect(transferInactiveOffender).to.be.calledWith(123)
      })

      it('Should NOT display the provider details', () => {
        expect(wrapper.find('.qa-card-provider').exists()).equal(false)
      })

      it('Should NOT display the offender manager details', () => {
        expect(wrapper.find('.qa-card-offender-manager').exists()).equal(false)
      })
    })

    context('When the offender has active events but has no assigned offender manager details', () => {
      beforeEach(() => {
        offenderManager = {}

        offenderConvictions = {
          convictions: [
            aConviction({ convictionId: 1, active: true })
          ]
        }

        wrapper = shallow(
          <OffenderCards offenderConvictions={offenderConvictions} offenderManager={offenderManager}
                         transferInactiveOffender={stub()} offenderId={123} />
        )
      })

      it('Should display offender status as current offender', () => {
        expect(wrapper.find('.qa-card-current-status').text()).to.equal('Current offender')
      })

      it('Should NOT display transfer in link', () => {
        expect(wrapper.find('a').exists()).to.equal(false)
      })

      it('Should NOT display the provider details', () => {
        expect(wrapper.find('.qa-card-provider').exists()).equal(false)
      })

      it('Should NOT display the offender manager details', () => {
        expect(wrapper.find('.qa-card-offender-manager').exists()).equal(false)
      })
    })

    context('When the offender has active events and has an assigned offender manager', () => {
      beforeEach(() => {
        offenderManager = {
          probationArea: {
            description: 'NPS North West'
          },
          staff: {
            forenames: 'John',
            surname: 'Smith'
          }
        }

        offenderConvictions = {
          convictions: [
            aConviction({ convictionId: 1, active: true })
          ]
        }

        wrapper = shallow(
          <OffenderCards offenderConvictions={offenderConvictions} offenderManager={offenderManager}
                         transferInactiveOffender={stub()} offenderId={123} />
        )
      })

      it('Should display offender status as current offender', () => {
        expect(wrapper.find('.qa-card-current-status').text()).to.equal('Current offender')
      })

      it('Should NOT display transfer in link', () => {
        expect(wrapper.find('a').exists()).to.equal(false)
      })

      it('Should display the provider details', () => {
        expect(wrapper.find('.qa-card-provider').text()).to.equal('Provider: NPS North West')
      })

      it('Should NOT display the offender manager details', () => {
        expect(wrapper.find('.qa-card-offender-manager').text()).to.equal('Offender manager: Smith, John')
      })
    })
  })
})
