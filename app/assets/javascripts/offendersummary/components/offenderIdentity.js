import React from 'react'
import * as PropTypes from 'prop-types'
import {dateFromISO} from '../../helpers/formatters'

const OffenderIdentity = ({ offenderDetails }) => {
    return(
        <table className="qa-offender-identity" role="presentation">
            <tbody>
            <tr>
                <td>
                    {offenderDetails.oneTimeNomisRef && <img alt={`Image of ${offenderDetails.firstName} ${offenderDetails.surname}`} className="offender-image" src={`offender/oneTimeNomisRef/${encodeURIComponent(offenderDetails.oneTimeNomisRef)}/image`}/>}
                    {!offenderDetails.oneTimeNomisRef && <img alt="No offender image available" className="offender-image" src='assets/images/NoPhoto@2x.png'/>}
                </td>
                <td>
                    <h1 className="govuk-heading-l govuk-!-margin-0 govuk-!-padding-0 qa-offender-name">{offenderDetails.surname}, {offenderDetails.firstName}</h1>
                    <p className="govuk-body govuk-!-margin-0 govuk-!-margin-top-2">Date of birth</p>
                    <p className="govuk-heading-m govuk-!-margin-0 govuk-!-padding-0 qa-offender-date-of-birth">{dateFromISO(offenderDetails.dateOfBirth)}</p>
                    <p className="govuk-body govuk-!-margin-0 govuk-!-margin-top-2">CRN</p>
                    <p className="govuk-heading-m govuk-!-margin-0 govuk-!-padding-0 qa-offender-crn">{offenderDetails.otherIds.crn}</p>
                </td>
            </tr>
            </tbody>
        </table>)
}

OffenderIdentity.propTypes = {
    offenderDetails: PropTypes.shape({
        firstName: PropTypes.string.isRequired,
        surname: PropTypes.string.isRequired,
        dateOfBirth: PropTypes.string.isRequired,
        otherIds : PropTypes.shape({
            crn: PropTypes.string.isRequired
        }),
        oneTimeNomisRef: PropTypes.string
    }).isRequired
}

export default OffenderIdentity