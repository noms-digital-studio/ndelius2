import React, {Component} from 'react';
import * as PropTypes from 'prop-types';
import ErrorMessage from './errorMessage'
import {dateFromISO} from '../../helpers/formatters'
import moment from 'moment'

class PersonalCircumstances extends Component {
    constructor(props) {
        super(props);
        this.details = null;
        this.setDetailsRef = element => this.details = element;
    }

    componentWillMount() {
        const { getOffenderPersonalCircumstances } = this.props;
        getOffenderPersonalCircumstances();
    }

    componentDidMount() {
        new window.GOVUKFrontend.Details(this.details).init();
    }


    render() {
        const {fetching, error, circumstances} = this.props;

        return (
            <details className="govuk-details govuk-!-margin-top-0 govuk-!-margin-bottom-0" role="group" ref={this.setDetailsRef}>
                <summary className="govuk-details__summary" role=" button" aria-controls="details-content-circumstances" aria-expanded="false">
                    <span className="govuk-details__summary-text">Personal circumstances</span>
                </summary>
                <div className="govuk-details__text moj-details__text--no-border" id="details-content-circumstances" aria-hidden="true">
                    {!fetching && !error &&
                    <div className="moj-inside-panel qa-offender-personal-circumstances">
                        {circumstances.length === 0 &&
                        <div><p className="govuk-body moj-!-text-align-center">No personal circumstance recorded</p></div>
                        }
                        {circumstances.length > 0 &&
                        <table className="govuk-table moj-table moj-table--split-rows" role="presentation">
                            <thead>
                            <tr>
                                <th>Type</th>
                                <th>Sub type</th>
                                <th>Date</th>
                            </tr>
                            </thead>
                            <tbody>
                            {circumstances.sort(circumstanceSorter).map(renderCircumstance)}
                            </tbody>
                        </table>
                        }
                    </div>
                    }
                    {!fetching && error &&
                    <ErrorMessage
                        message="Unfortunately, we cannot display you the offender's personal circumstances at the moment. Please try again later."/>
                    }

                </div>
            </details>
        )
    }
}

const renderCircumstance = circumstance => {
    return (
        <tr key={circumstance.personalCircumstanceId}>
            <td>{circumstance.personalCircumstanceType.description}</td>
            <td>{circumstance.personalCircumstanceSubType.description}</td>
            <td>{dateFromISO(circumstance.startDate)}</td>
        </tr>
    )
}

const circumstanceSorter = (first, second) => {
    return moment(second.startDate, 'YYYY-MM-DD').diff(moment(first.startDate, 'YYYY-MM-DD'))
}



PersonalCircumstances.propTypes = {
    getOffenderPersonalCircumstances: PropTypes.func,
    fetching: PropTypes.bool,
    error: PropTypes.bool,
    circumstances: PropTypes.arrayOf(
        PropTypes.shape({
            personalCircumstanceType: PropTypes.shape({
                code: PropTypes.string.isRequired,
                description: PropTypes.string.isRequired
            }).isRequired,
            personalCircumstanceSubType: PropTypes.shape({
                code: PropTypes.string.isRequired,
                description: PropTypes.string.isRequired
            }).isRequired,
            startDate: PropTypes.string.isRequired,
            personalCircumstanceId: PropTypes.number.isRequired
        }.isRequired
    ))

}

export default PersonalCircumstances