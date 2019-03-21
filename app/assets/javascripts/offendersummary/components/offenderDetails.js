import React, { Component, Fragment } from 'react'
import * as PropTypes from 'prop-types'
import Accordion from './shared/accordion'
import { standardOpenCloseElementTracking } from '../../helpers/analyticsHelper'

class OffenderDetails extends Component {
  constructor (props) {
    super(props)
    this.details = null
    this.setDetailsRef = element => { this.details = element }
  }

  componentDidMount () {
    new window.GOVUKFrontend.Details(this.details).init()
    standardOpenCloseElementTracking(document.querySelector('.js-analytics-contact-details'), 'Offender summary > Offender details', 'Contact details')
  }

  render () {
    const { offenderDetails, viewOffenderAliases, viewOffenderAddresses } = this.props
    let mainAddress
    let telephoneNumber
    let mobileNumber

    if (offenderDetails.hasOwnProperty('contactDetails')) {
      if (offenderDetails.contactDetails.hasOwnProperty('addresses')) {
        mainAddress = offenderDetails.contactDetails.addresses.find((address) => {
          return address.status && address.status.code === 'M'
        })
      }
      if (offenderDetails.contactDetails.hasOwnProperty('phoneNumbers')) {
        telephoneNumber = offenderDetails.contactDetails.phoneNumbers.find((number) => {
          return number.type === 'TELEPHONE'
        })
        mobileNumber = offenderDetails.contactDetails.phoneNumbers.find((number) => {
          return number.type === 'MOBILE'
        })
      }
    }

    const requiresInterpreter = () => {
      if (offenderDetails.hasOwnProperty('offenderProfile') &&
        offenderDetails.offenderProfile.hasOwnProperty('offenderLanguages') &&
        typeof offenderDetails.offenderProfile.offenderLanguages.requiresInterpreter === 'boolean') {
        return offenderDetails.offenderProfile.offenderLanguages.requiresInterpreter ? 'Yes' : 'No'
      }
      return 'Unknown'
    }

    const activeDisabilities = disability => !disability.endDate
    const disabilities = () => {
      if (offenderDetails.hasOwnProperty('offenderProfile') &&
        offenderDetails.offenderProfile.hasOwnProperty('disabilities') &&
        offenderDetails.offenderProfile.disabilities.filter(activeDisabilities).length > 0) {
        return offenderDetails.offenderProfile.disabilities.filter(activeDisabilities).map(disability => disability.disabilityType.description).join(', ')
      }
      return 'Unknown'
    }
    const middleNames = () => {
      return offenderDetails.middleNames && offenderDetails.middleNames.join(', ')
    }

    return (
      <Accordion label='Offender details' id='4'>
        <Fragment>
          <table className='govuk-table moj-table moj-table--split-rows'>
            <tbody>
            <tr>
              <th style={{ width: '50%' }}>Aliases</th>
              <td
                className='qa-aliases'>{(offenderDetails.offenderAliases && offenderDetails.offenderAliases.length > 0 && 'Yes (' + offenderDetails.offenderAliases.length + ')') || 'No'}</td>
              <td className='qa-aliases-link' style={{ textAlign: 'right', width: '100px' }}>
                {offenderDetails.offenderAliases && offenderDetails.offenderAliases.length > 0 && (
                  <a href='javascript:void(0);' className='qa-view-offender-aliases govuk-link govuk-link--no-visited-state'
                     onClick={() => viewOffenderAliases(offenderDetails.offenderId)}>View</a>)}
              </td>
            </tr>
            {middleNames() && (
              <tr>
                <th>Middle names</th>
                <td className='qa-middle-names' colSpan='2'>{middleNames()}</td>
              </tr>
            )
            }
            <tr>
              <th>Gender</th>
              <td className='qa-gender' colSpan='2'>{offenderDetails.gender || 'Unknown'}</td>
            </tr>
            <tr>
              <th>NI Number</th>
              <td className='qa-ni-number'
                  colSpan='2'>{(offenderDetails.otherIds && offenderDetails.otherIds.niNumber) || 'Unknown'}</td>
            </tr>
            <tr>
              <th>Nationality</th>
              <td className='qa-nationality'
                  colSpan='2'>{(offenderDetails.offenderProfile && offenderDetails.offenderProfile.nationality) || 'Unknown'}</td>
            </tr>
            <tr>
              <th>Ethnicity</th>
              <td className='qa-ethnicity'
                  colSpan='2'>{(offenderDetails.offenderProfile && offenderDetails.offenderProfile.ethnicity) || 'Unknown'}</td>
            </tr>
            <tr>
              <th>Interpreter required</th>
              <td className='qa-interpreter' colSpan='2'>{requiresInterpreter()}</td>
            </tr>
            <tr>
              <th>Disability status</th>
              <td className='qa-disability' colSpan='2'>{disabilities()}</td>
            </tr>
            </tbody>
          </table>

          <details className='govuk-details govuk-!-margin-top-0 govuk-!-margin-bottom-0' ref={this.setDetailsRef}>
            <summary className='govuk-details__summary js-analytics-contact-details'
                     aria-controls='offender-details-contact-details' aria-expanded='false'>
              <span className='govuk-details__summary-text'>Contact details</span>
            </summary>
            <div className='govuk-details__text moj-details__text--no-border' id='offender-details-contact-details'
                 aria-hidden='true'>
              <table className='govuk-table moj-table moj-table--split-rows'>
                <tbody>
                <tr>
                  <th style={{ width: '50%' }}>Telephone</th>
                  <td className='qa-telephone'>{(telephoneNumber && telephoneNumber.number) || 'Unknown'}</td>
                </tr>
                <tr>
                  <th>Email</th>
                  <td
                    className='qa-email'>{(offenderDetails.contactDetails && offenderDetails.contactDetails.emailAddresses && offenderDetails.contactDetails.emailAddresses[0]) || 'Unknown'}</td>
                </tr>
                <tr>
                  <th>Mobile</th>
                  <td className='qa-mobile'>{(mobileNumber && mobileNumber.number) || 'Unknown'}</td>
                </tr>
                <tr>
                  <th>Main address</th>
                  <td>
                    {mainAddress && !mainAddress.noFixedAbode && (
                      <Fragment>
                        {mainAddress.buildingName && (
                          <span className='qa-main-address-1'>{mainAddress.buildingName}<br /></span>)}
                        <span
                          className='qa-main-address-2'>{mainAddress.addressNumber && (mainAddress.addressNumber + ' ')}{mainAddress.streetName}</span><br />
                        {mainAddress.district && (
                          <span className='qa-main-address-3'>{mainAddress.district}<br /></span>)}
                        <span className='qa-main-address-4'>{mainAddress.town}</span><br />
                        <span className='qa-main-address-5'>{mainAddress.county}</span><br />
                        <span className='qa-main-address-6'>{mainAddress.postcode}</span>
                      </Fragment>
                    )}
                    {mainAddress && mainAddress.noFixedAbode && (
                      <span className='qa-main-address-nfa'>No fixed abode</span>
                    )}
                    {!mainAddress && (
                      <span className='qa-main-address-none'>No main address</span>
                    )}
                  </td>
                </tr>
                </tbody>
              </table>
              <p className='govuk-body app-align-right'>
                <a className='govuk-link govuk-link--no-visited-state qa-view-offender-addresses'
                   href='javascript:void(0);' onClick={() => viewOffenderAddresses(offenderDetails.offenderId)}>View
                  address history</a>
              </p>
            </div>
          </details>

        </Fragment>
      </Accordion>
    )
  }
}

OffenderDetails.propTypes = {
  viewOffenderAliases: PropTypes.func.isRequired,
  viewOffenderAddresses: PropTypes.func.isRequired,
  offenderDetails: PropTypes.shape({
    offenderId: PropTypes.number.isRequired,
    offenderAliases: PropTypes.arrayOf(PropTypes.shape({
        dateOfBirth: PropTypes.string,
        firstName: PropTypes.string,
        surname: PropTypes.string,
        gender: PropTypes.string
      }
    )),
    offenderProfile: PropTypes.shape({
        ethnicity: PropTypes.string,
        nationality: PropTypes.string,
        offenderLanguages: PropTypes.shape({
            requiresInterpreter: PropTypes.bool
          }
        ),
        disabilities: PropTypes.arrayOf(PropTypes.shape({
          disabilityId: PropTypes.number,
          disabilityType: PropTypes.shape({
            description: PropTypes.string
          }),
          startDate: PropTypes.string,
          endDate: PropTypes.string
        }))
      }
    ),
    gender: PropTypes.string,
    otherIds: PropTypes.shape({
      niNumber: PropTypes.string
    }),
    contactDetails: PropTypes.shape({
      addresses: PropTypes.arrayOf(PropTypes.shape({
        addressNumber: PropTypes.string,
        buildingName: PropTypes.string,
        county: PropTypes.string,
        from: PropTypes.string,
        noFixedAbode: PropTypes.bool,
        postcode: PropTypes.string,
        status: PropTypes.shape({
          code: PropTypes.string,
          description: PropTypes.string
        }),
        streetName: PropTypes.string,
        telephoneNumber: PropTypes.string,
        town: PropTypes.string
      })),
      emailAddresses: PropTypes.arrayOf(PropTypes.string),
      phoneNumbers: PropTypes.arrayOf(PropTypes.shape({
        number: PropTypes.string,
        type: PropTypes.string
      }))
    }),
    middleNames: PropTypes.arrayOf(PropTypes.string)
  })
}

export default OffenderDetails
