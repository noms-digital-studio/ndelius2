import React, { Component } from 'react';
import * as PropTypes from 'prop-types';
import GovUkPhaseBanner from './govukPhaseBanner';
import ErrorMessage from './errorMessage';
import OffenderIdentity from '../containers/offenderIdentityContainer';
import OffenderDetails from '../containers/offenderDetailsContainer';
import Registrations from '../containers/registrationsContainer';
import Convictions from '../containers/convictionsContainer';
import Notes from '../containers/notesContainer';

class OffenderSummaryPage extends Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        const { getOffenderDetails } = this.props;
        getOffenderDetails();
    }

    componentDidUpdate() {
        window.GOVUKFrontend.initAll();
    }

    render() {
        const { fetching, error } = this.props;
        return (
            <div>
                <GovUkPhaseBanner/>
                { !fetching && !error &&
                <div>

                    <div className="govuk-grid-row govuk-!-margin-top-3">
                        <div className="govuk-grid-column-one-half">
                            <OffenderIdentity/>
                        </div>
                        <div className="govuk-grid-column-one-half">&nbsp;</div>
                    </div>

                    <Registrations/>
                    <Convictions/>
                    <OffenderDetails/>
                    <Notes/>

                </div>
                }
                { !fetching && error &&
                <ErrorMessage
                    message="Unfortunately, we cannot display you the offender's information at the moment. Please try again later."/>
                }
            </div>
        );
    }
}

OffenderSummaryPage.propTypes = {
    getOffenderDetails: PropTypes.func,
    fetching: PropTypes.bool,
    error: PropTypes.bool
};

export default OffenderSummaryPage;