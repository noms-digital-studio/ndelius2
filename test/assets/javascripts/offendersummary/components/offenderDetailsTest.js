import { expect } from 'chai'
import { render } from 'enzyme'
import OffenderDetails from './offenderDetails'

describe('Offender Details component', () => {
  let wrapper
  let offenderDetails

  beforeEach(() => {
    offenderDetails = {
      gender: 'Male',
      offenderAliases: [
        {
          dateOfBirth: '1976-05-12',
          firstName: 'Johnny',
          surname: 'Wishbone',
          gender: 'Male'
        }
      ],
      contactDetails: {
        addresses: [{
          addressNumber: '5',
          buildingName: 'Sea View',
          county: 'Yorkshire',
          district: 'Nether Edge',
          from: '2018-06-22',
          noFixedAbode: false,
          notes: '',
          postcode: 'S10 1EQ',
          status: { code: 'M', description: 'Main' },
          streetName: 'High Street',
          telephoneNumber: '',
          town: 'Sheffield'
        }],
        emailAddresses: ['user@host.com'],
        phoneNumbers: [
          {
            number: '01753862474',
            type: 'TELEPHONE'
          },
          {
            number: '07777123456',
            type: 'MOBILE'
          }
        ]
      },
      otherIds: {
        niNumber: 'AB123456C'
      },
      middleNames: [
        'Andy',
        'Stephen'
      ],
      offenderProfile: {
        ethnicity: 'White British',
        nationality: 'British',
        offenderLanguages: {
          requiresInterpreter: true
        },
        disabilities: [
          {
            disabilityId: 2500072586,
            disabilityType: {
              code: 'LD',
              description: 'Learning Difficulties'
            },
            startDate: '2019-01-13'
          },
          {
            disabilityId: 2500073086,
            disabilityType: {
              code: 'SI',
              description: 'Speech Impairment'
            },
            startDate: '2019-01-23'
          },
          {
            disabilityId: 2500073087,
            disabilityType: {
              code: 'DD',
              description: 'Health Issues'
            },
            startDate: '2019-01-13',
            endDate: '2019-01-23'
          }
        ]
      }
    }
  })

  // OFFENDER DETAILS

  describe('Offender details', () => {
    context('when all offender details are recorded', () => {
      beforeEach(() => {
        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains alias flag and number', () => {
        expect(wrapper.text()).to.contain('AliasesYes (1)')
      })
      it('contains gender', () => {
        expect(wrapper.text()).to.contain('GenderMale')
      })
      it('contains middle namea', () => {
        expect(wrapper.text()).to.contain('Middle namesAndy, Stephen')
      })
      it('contains NI number', () => {
        expect(wrapper.text()).to.contain('NI NumberAB123456C')
      })
      it('contains nationality', () => {
        expect(wrapper.text()).to.contain('NationalityBritish')
      })
      it('contains ethnicity', () => {
        expect(wrapper.text()).to.contain('EthnicityWhite British')
      })
      it('contains interpreter requirement', () => {
        expect(wrapper.text()).to.contain('Interpreter requiredYes')
      })
      it('contains active disabilities', () => {
        expect(wrapper.find('.qa-disability').text()).to.equals('Learning Difficulties, Speech Impairment')
      })
    })

    context('when offender has no aliases recorded', () => {
      beforeEach(() => {
        offenderDetails.offenderAliases = []
        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains no alias flag', () => {
        expect(wrapper.text()).to.contain('AliasesNo')
      })
    })

    context('when offender has no middle names', () => {
      beforeEach(() => {
        offenderDetails.middleNames = []
        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains no middle name row', () => {
        expect(wrapper.text()).to.not.contain('Middle names')
      })
    })

    context('when offender has no gender recorded', () => {
      beforeEach(() => {
        offenderDetails.gender = ''
        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains unknown gender', () => {
        expect(wrapper.text()).to.contain('GenderUnknown')
      })
    })

    context('when offender has no NI Number recorded', () => {
      beforeEach(() => {
        offenderDetails.otherIds.niNumber = ''
        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains unknown NI Number', () => {
        expect(wrapper.text()).to.contain('NI NumberUnknown')
      })
    })

    context('when offender has no nationality recorded', () => {
      beforeEach(() => {
        offenderDetails.offenderProfile.nationality = ''
        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains unknown Nationality', () => {
        expect(wrapper.text()).to.contain('NationalityUnknown')
      })
    })

    context('when offender has no ethnicity recorded', () => {
      beforeEach(() => {
        offenderDetails.offenderProfile.ethnicity = ''
        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains unknown Ethnicity', () => {
        expect(wrapper.text()).to.contain('EthnicityUnknown')
      })
    })

    context('when offender has no interpreter requirement recorded', () => {
      beforeEach(() => {
        offenderDetails.offenderProfile.offenderLanguages.requiresInterpreter = void 0
        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains unknown interpreter requirement', () => {
        expect(wrapper.text()).to.contain('Interpreter requiredUnknown')
      })
    })

    context('when offender has interpreter requirement recorded as false', () => {
      beforeEach(() => {
        offenderDetails.offenderProfile.offenderLanguages.requiresInterpreter = false
        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains no interpreter requirement', () => {
        expect(wrapper.text()).to.contain('Interpreter requiredNo')
      })
    })
    context('when offender has no active disabilities recorded', () => {
      beforeEach(() => {
        offenderDetails.offenderProfile.disabilities = [{
          disabilityId: 2500073087,
          disabilityType: {
            code: 'DD',
            description: 'Health Issues'
          },
          startDate: '2019-01-13',
          endDate: '2019-01-23'
        }]
        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains no interpreter requirement', () => {
        expect(wrapper.text()).to.contain('Disability statusUnknown')
      })
    })
    context('when offender has no disabilities recorded', () => {
      beforeEach(() => {
        offenderDetails.offenderProfile.disabilities = []
        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains no interpreter requirement', () => {
        expect(wrapper.text()).to.contain('Disability statusUnknown')
      })
    })
  })

  // CONTACT DETAILS

  describe('Contact details section', () => {
    context('when all contact details are recorded', () => {
      beforeEach(() => {
        offenderDetails.contactDetails = {
          addresses: [{
            noFixedAbode: false
          }],
          emailAddresses: ['user@host.com'],
          phoneNumbers: [
            {
              number: '01753862474',
              type: 'TELEPHONE'
            },
            {
              number: '07777123456',
              type: 'MOBILE'
            }
          ]
        }

        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains telephone number', () => {
        expect(wrapper.text()).to.contain('Telephone01753862474')
      })
      it('contains email address', () => {
        expect(wrapper.text()).to.contain('Emailuser@host.com')
      })
      it('contains mobile number', () => {
        expect(wrapper.text()).to.contain('Mobile07777123456')
      })
    })

    context('when no email address is recorded', () => {
      beforeEach(() => {
        offenderDetails.contactDetails = {
          addresses: [{
            noFixedAbode: false
          }],
          emailAddresses: [],
          phoneNumbers: [
            {
              number: '01753862474',
              type: 'TELEPHONE'
            },
            {
              number: '07777123456',
              type: 'MOBILE'
            }
          ]
        }

        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('does not contain email address', () => {
        expect(wrapper.text()).to.contain('EmailUnknown')
      })
    })

    context('when no telephone number is recorded', () => {
      beforeEach(() => {
        offenderDetails.contactDetails = {
          addresses: [{
            noFixedAbode: false
          }],
          emailAddresses: ['user@host.com'],
          phoneNumbers: [
            {
              number: '07777123456',
              type: 'MOBILE'
            }
          ]
        }

        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('does not contain telephone number', () => {
        expect(wrapper.text()).to.contain('TelephoneUnknown')
      })
    })

    context('when no mobile number is recorded', () => {
      beforeEach(() => {
        offenderDetails.contactDetails = {
          addresses: [{
            noFixedAbode: false
          }],
          emailAddresses: ['user@host.com'],
          phoneNumbers: [
            {
              number: '01753862474',
              type: 'TELEPHONE'
            }
          ]
        }

        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('does not contain mobile number', () => {
        expect(wrapper.text()).to.contain('MobileUnknown')
      })
    })
  })

  // MAIN ADDRESS

  describe('Main address section', () => {
    context('when a main address is recorded', () => {
      beforeEach(() => {
        offenderDetails.contactDetails = {
          addresses: [{
            addressNumber: '5',
            buildingName: 'Sea View',
            county: 'Yorkshire',
            district: 'Nether Edge',
            from: '2018-06-22',
            noFixedAbode: false,
            notes: '',
            postcode: 'S10 1EQ',
            status: { code: 'M', description: 'Main' },
            streetName: 'High Street',
            telephoneNumber: '',
            town: 'Sheffield'
          }],
          emailAddresses: [],
          phoneNumbers: []
        }

        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains address number and building name', () => {
        expect(wrapper.text()).to.contain('Sea View')
      })
      it('contains street name and number', () => {
        expect(wrapper.text()).to.contain('5 High Street')
      })
      it('contains district name', () => {
        expect(wrapper.text()).to.contain('Nether Edge')
      })
      it('contains district and town', () => {
        expect(wrapper.text()).to.contain('Sheffield')
      })
      it('contains postcode', () => {
        expect(wrapper.text()).to.contain('S10 1EQ')
      })

      it('does not contain no fixed abode line', () => {
        expect(wrapper.text()).not.to.contain('No fixed abode')
      })
      it('does not contain no main address line', () => {
        expect(wrapper.text()).not.to.contain('No main address')
      })
    })

    context('when a main address is recorded but has no fixed abode', () => {
      beforeEach(() => {
        offenderDetails.contactDetails = {
          addresses: [{
            noFixedAbode: true,
            status: { code: 'M', description: 'Main' }
          }],
          emailAddresses: [],
          phoneNumbers: []
        }

        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains no fixed abode', () => {
        expect(wrapper.text()).to.contain('No fixed abode')
      })
    })

    context('when no main address is recorded', () => {
      beforeEach(() => {
        offenderDetails.contactDetails = {
          addresses: [{
            noFixedAbode: true
          }],
          emailAddresses: [],
          phoneNumbers: []
        }

        wrapper = render(<OffenderDetails offenderDetails={offenderDetails} />)
      })

      it('contains no main address', () => {
        expect(wrapper.text()).to.contain('No main address')
      })
    })
  })
})
