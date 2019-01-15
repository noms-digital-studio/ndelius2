import React, {Fragment} from 'react'
import * as PropTypes from 'prop-types'
import ErrorMessage from './errorMessage'
import Accordion from './shared/accordion'
import NextAppointment from '../containers/nextAppointmentContainer'
import PersonalCircumstances from '../containers/personalCircumstancesContainer'
import {staff} from '../../helpers/offenderManagerHelper'
import {dateFromISO} from '../../helpers/formatters'

const OffenderManager = ({ fetching, error, offenderManager }) => {
    return(
        <Accordion label='Offender manager'>
            <Fragment>
                {!fetching && !error &&
                <div className="moj-inside-panel qa-offender-manager">
                    <table className="govuk-table moj-table moj-table--split-rows" role="presentation">
                        <tbody>
                        <tr>
                            <th>Provider</th>
                            <td>{offenderManager.probationArea.description}</td>
                        </tr>
                        <tr>
                            <th>Cluster</th>
                            <td>{offenderManager.team && offenderManager.team.borough && offenderManager.team.borough.description || 'Unknown'}</td>
                        </tr>
                        <tr>
                            <th>LDU and team</th>
                            <td>{offenderManager.team && offenderManager.team.district && offenderManager.team.district.description || 'Unknown'} &amp; {offenderManager.team &&  offenderManager.team.description || 'Unknown'}</td>
                        </tr>
                        <tr>
                            <th>Officer</th>
                            <td>{staff(offenderManager.staff)}</td>
                        </tr>
                        <tr>
                            <th>Team telephone</th>
                            <td>{offenderManager.team && offenderManager.team.telephone || 'Unknown'}</td>
                        </tr>
                        <tr>
                            <th>Date allocated</th>
                            <td>{offenderManager.fromDate && dateFromISO(offenderManager.fromDate) || 'Unknown'}</td>
                        </tr>
                        <tr>
                            <th>Reason for allocation</th>
                            <td>{offenderManager.allocationReason && offenderManager.allocationReason.description || 'Unknown'}</td>
                        </tr>
                        </tbody>
                    </table>
                    <PersonalCircumstances/>
                    <NextAppointment/>
                </div>
                }
                {!fetching && error &&
                <ErrorMessage
                    message="Unfortunately, we cannot display you the offender's registrations at the moment. Please try again later."/>
                }

            </Fragment>
        </Accordion>
    )
}


OffenderManager.propTypes = {
    fetching: PropTypes.bool,
    error: PropTypes.bool,
    offenderManager: PropTypes.shape({
        probationArea: PropTypes.shape({
            description: PropTypes.string
        }).isRequired,
        team: PropTypes.shape({
            description: PropTypes.string.isRequired,
            telephone: PropTypes.string,
            district: PropTypes.shape({
                description: PropTypes.string.isRequired,
            }).isRequired,
            borough: PropTypes.shape({
                description: PropTypes.string.isRequired,
            }).isRequired
        }),
        staff: PropTypes.shape({
            forenames: PropTypes.string,
            surname: PropTypes.string
        }),
        fromDate: PropTypes.string.isRequired,
        allocationReason: PropTypes.shape({
            description: PropTypes.string.isRequired
        })

    })
}

export default OffenderManager