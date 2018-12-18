import React, { Fragment } from 'react';
import * as PropTypes from 'prop-types';
import Accordion from './shared/accordion';

const OffenderDetails = ({ offenderDetails }) => {

    let mainAddress;
    let telephoneNumber;
    let mobileNumber;

    if (offenderDetails.hasOwnProperty('contactDetails')) {
        if (offenderDetails.contactDetails.hasOwnProperty('addresses')) {
            mainAddress = offenderDetails.contactDetails.addresses.find((address) => {
                return address.status && address.status.code === 'M';
            });
        }
        if (offenderDetails.contactDetails.hasOwnProperty('phoneNumbers')) {
            telephoneNumber = offenderDetails.contactDetails.phoneNumbers.find((number) => {
                return number.type === 'TELEPHONE';
            });
            mobileNumber = offenderDetails.contactDetails.phoneNumbers.find((number) => {
                return number.type === 'MOBILE';
            });
        }
    }

    const requiresInterpreter = () => {
        if (offenderDetails.hasOwnProperty('offenderProfile') &&
            offenderDetails.offenderProfile.hasOwnProperty('offenderLanguages') &&
            typeof offenderDetails.offenderProfile.offenderLanguages.requiresInterpreter === 'boolean') {
            return offenderDetails.offenderProfile.offenderLanguages.requiresInterpreter ? 'Yes' : 'No';
        }
        return 'Unknown';
    };

    return (
        <Accordion label="Offender details" content={
            <Fragment>
                <table className="govuk-table moj-table moj-table--split-rows" role="presentation">
                    <tbody>
                    <tr>
                        <th width="50%">Aliases</th>
                        <td className="qa-aliases">{ offenderDetails.offenderAliases && offenderDetails.offenderAliases.length > 0 && 'Yes (' + offenderDetails.offenderAliases.length + ')' || 'No' }</td>
                        <td className="qa-aliases-link" style={ { textAlign: 'right' } }
                            width="100">{ offenderDetails.offenderAliases && offenderDetails.offenderAliases.length > 0 && (
                            <a href="javascript:void(0);">View all</a>) }</td>
                    </tr>
                    <tr>
                        <th>Gender</th>
                        <td className="qa-gender" colSpan="2">{ offenderDetails.gender || 'Unknown' }</td>
                    </tr>
                    <tr>
                        <th>NI Number</th>
                        <td className="qa-ni-number"
                            colSpan="2">{ offenderDetails.otherIds && offenderDetails.otherIds.niNumber || 'Unknown' }</td>
                    </tr>
                    <tr>
                        <th>Nationality</th>
                        <td className="qa-nationality"
                            colSpan="2">{ offenderDetails.offenderProfile && offenderDetails.offenderProfile.nationality || 'Unknown' }</td>
                    </tr>
                    <tr>
                        <th>Ethnicity</th>
                        <td className="qa-ethnicity"
                            colSpan="2">{ offenderDetails.offenderProfile && offenderDetails.offenderProfile.ethnicity || 'Unknown' }</td>
                    </tr>
                    <tr>
                        <th>Interpreter required</th>
                        <td className="qa-interpreter" colSpan="2">{ requiresInterpreter() }</td>
                    </tr>
                    <tr>
                        <th>Disability status</th>
                        <td className="qa-disability" colSpan="2"> --</td>
                    </tr>
                    </tbody>
                </table>

                <details className="govuk-details govuk-!-margin-top-0 govuk-!-margin-bottom-0" role="group">
                    <summary className="govuk-details__summary" role="button"
                             aria-controls="offender-details-contact-details"
                             aria-expanded="true"><span className="govuk-details__summary-text">Contact details</span>
                    </summary>
                    <div className="govuk-details__text moj-details__text--no-border" id="offender-details-contact-details"
                         aria-hidden="false">
                        <table className="govuk-table moj-table moj-table--split-rows" role="presentation">
                            <tbody>
                            <tr>
                                <th width="50%">Telephone</th>
                                <td className="qa-telephone">{ telephoneNumber && telephoneNumber.number || 'Unknown' }</td>
                            </tr>
                            <tr>
                                <th>Email</th>
                                <td className="qa-email">{ offenderDetails.contactDetails && offenderDetails.contactDetails.emailAddresses[0] || 'Unknown' }</td>
                            </tr>
                            <tr>
                                <th>Mobile</th>
                                <td className="qa-mobile">{ mobileNumber && mobileNumber.number || 'Unknown' }</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </details>

                <details className="govuk-details govuk-!-margin-top-0 govuk-!-margin-bottom-0" role="group">
                    <summary className="govuk-details__summary" role="button"
                             aria-controls="offender-details-main-address"
                             aria-expanded="true"><span className="govuk-details__summary-text">Main address</span>
                    </summary>
                    <div className="govuk-details__text moj-details__text--no-border" id="offender-details-main-address"
                         aria-hidden="false">
                        { mainAddress && !mainAddress.noFixedAbode && (
                            <table className="govuk-table moj-table moj-table--split-rows govuk-!-margin-bottom-2"
                                   role="presentation">
                                <thead>
                                <tr>
                                    <th>Number</th>
                                    <th>Street</th>
                                    <th>Town/City</th>
                                    <th>County</th>
                                    <th>Postcode</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td className="qa-main-address-1">{ mainAddress.addressNumber } { mainAddress.buildingName && (mainAddress.buildingName) }</td>
                                    <td className="qa-main-address-2">{ mainAddress.streetName }{ mainAddress.district && (', ' + mainAddress.district) }</td>
                                    <td className="qa-main-address-3">{ mainAddress.town }</td>
                                    <td className="qa-main-address-4">{ mainAddress.county }</td>
                                    <td className="qa-main-address-5">{ mainAddress.postcode }</td>
                                </tr>
                                </tbody>
                            </table>
                        ) }
                        { mainAddress && mainAddress.noFixedAbode && (
                            <p className="qa-main-address-nfa">No fixed abode</p>
                        ) }
                        { !mainAddress && (
                            <p className="qa-main-address-none">No main address</p>
                        ) }
                    </div>
                </details>
            </Fragment>
        }/>
    );
};

OffenderDetails.propTypes = {
    offenderDetails: PropTypes.shape({
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
                )
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
        })
    })
};

export default OffenderDetails;
