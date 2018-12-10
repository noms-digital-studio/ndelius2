import React from 'react';
import * as PropTypes from 'prop-types';

const OffenderDetails = ({ contactDetails }) => {

    const mainAddress = contactDetails.addresses.find((address) => {
        return address.status && address.status.code === 'M';
    });

    const telephoneNumber = contactDetails.phoneNumbers.find((number) => {
        return number.type && number.type === 'TELEPHONE';
    });

    const mobileNumber = contactDetails.phoneNumbers.find((number) => {
        return number.type && number.type === 'MOBILE';
    });

    return (
        <div>
            <details className="govuk-details govuk-!-margin-top-0 govuk-!-margin-bottom-0" role="group">
                <summary className="govuk-details__summary" role="button"
                         aria-controls="offender-details-main-address"
                         aria-expanded="true"><span className="govuk-details__summary-text">Contact details</span></summary>
                <div className="govuk-details__text moj-details__text--no-border" id="offender-details-main-address"
                     aria-hidden="false">
                    <table className="govuk-table moj-table moj-table--split-rows" role="presentation">
                        <tbody>
                        <tr>
                            <th width="50%">Telephone</th>
                            <td className="qa-telephone">{ telephoneNumber && telephoneNumber.number || 'Unknown' }</td>
                        </tr>
                        <tr>
                            <th>Email</th>
                            <td className="qa-email">{ contactDetails.emailAddresses[0] || 'Unknown' }</td>
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
                         aria-expanded="true"><span className="govuk-details__summary-text">Main address</span></summary>
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
        </div>
    );
};

OffenderDetails.propTypes = {
    contactDetails: PropTypes.shape({
        addresses: PropTypes.arrayOf(PropTypes.shape({
            addressNumber: PropTypes.string,
            buildingName: PropTypes.string,
            county: PropTypes.string,
            from: PropTypes.string,
            noFixedAbode: PropTypes.bool.isRequired,
            postcode: PropTypes.string,
            status: PropTypes.shape({
                code: PropTypes.string,
                description: PropTypes.string
            }),
            streetName: PropTypes.string,
            telephoneNumber: PropTypes.string,
            town: PropTypes.string
        })).isRequired,
        emailAddresses: PropTypes.arrayOf(PropTypes.string).isRequired,
        phoneNumbers: PropTypes.arrayOf(PropTypes.shape({
            number: PropTypes.string,
            type: PropTypes.string
        })).isRequired
    }).isRequired
};

export default OffenderDetails;
