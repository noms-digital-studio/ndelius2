import React, {Component, Fragment} from 'react';
import * as PropTypes from 'prop-types';
import Accordion from './shared/accordion';
import ErrorMessage from './errorMessage'
import {dateFromISO} from '../../helpers/formatters'

class Registrations extends Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        const { getOffenderRegistrations } = this.props;
        getOffenderRegistrations();
    }

    render() {
        const {fetching, error, registrations} = this.props;

        return (
            <Accordion label={`Registers and warnings (${registrations.length})`}>
                <Fragment>
                    {!fetching && !error &&
                    <div className="moj-inside-panel qa-offender-registrations">
                        {registrations.length === 0 &&
                        <div><p className="govuk-body moj-!-text-align-center">No data</p></div>
                        }
                        {registrations.length > 0 &&
                        <table className="govuk-table moj-table moj-table--split-rows" role="presentation">
                            <thead>
                                <tr>
                                    <th width="220">Type</th>
                                    <th width="130">Status</th>
                                    <th>Description</th>
                                    <th width="130">Date</th>
                                </tr>
                            </thead>
                            <tbody>
                            {registrations.sort(registrationSorter).map(renderRegistration)}
                            </tbody>
                        </table>
                        }
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
}

const registrationSorter = (first, second) => {
    const registerTypeCompare = first.register.description.localeCompare(second.register.description)
    const levelComparison = alertLevelComparison(first.riskColour, second.riskColour)
    const typeCompare = first.type.description.localeCompare(second.type.description)

    return registerTypeCompare === 0
        ? levelComparison === 0
            ? typeCompare
            : levelComparison
        : registerTypeCompare
}

const alertLevelComparison = (first, second) => {
    return toRiskNumber(second) - toRiskNumber(first)
}

const renderRegistration = registration => {
    return (
        <tr key={registration.registrationId}>
            <td><span className="govuk-body">{registration.register.description}</span></td>
            <td><span className={`moj-risk-tag ${alertLevelClass(registration)}`}>{alertLevelText(registration)}</span></td>
            <td>{registration.type.description}</td>
            <td>{dateFromISO(registration.startDate)}</td>
        </tr>

    );
}

const RED = 'Red'
const AMBER = 'Amber'
const GREEN = 'Green'

const toRiskNumber = riskColour => {
    switch (riskColour) {
        case RED:
            return 3;
        case AMBER:
            return 2;
        case GREEN:
            return 1;
        default:
            return 0
    }
}

const alertLevelText = registration => {
    switch (registration.riskColour) {
        case RED:
            return registration.type.description === 'Very High RoSH' ? 'very high' : 'high';
        case AMBER:
            return 'medium';
        case GREEN:
            return 'low';
        default:
            return 'warning'

    }
}

const alertLevelClass = registration => {
    switch (registration.riskColour) {
        case RED:
            return 'moj-risk-tag--high';
        case AMBER:
            return 'moj-risk-tag--medium';
        case  GREEN:
            return 'moj-risk-tag--low';
        default:
            return ''

    }
}

Registrations.propTypes = {
    getOffenderRegistrations: PropTypes.func,
    fetching: PropTypes.bool,
    error: PropTypes.bool,
    registrations: PropTypes.arrayOf(
        PropTypes.shape({
            register: PropTypes.shape({
                code: PropTypes.string.isRequired,
                description: PropTypes.string.isRequired
            }).isRequired,
            type: PropTypes.shape({
                code: PropTypes.string.isRequired,
                description: PropTypes.string.isRequired
            }).isRequired,
            startDate: PropTypes.string.isRequired,
            riskColour: PropTypes.string.isRequired,
            registrationId: PropTypes.number.isRequired


        }.isRequired
    ))

}

export default Registrations