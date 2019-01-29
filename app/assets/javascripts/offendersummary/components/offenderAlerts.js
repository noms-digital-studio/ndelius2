import React, { Fragment } from 'react';
import * as PropTypes from 'prop-types';

const OffenderAlerts = ({ offenderConvictions, registrations }) => {

    let roshAlertLevelClass;
    let roshAlertLevelText;
    let inBreach = false;

    if (offenderConvictions && offenderConvictions.convictions) {
        offenderConvictions.convictions.forEach((conviction) => {
            if (conviction.active && conviction.inBreach) {
                inBreach = true;
            }
        });
    }

    if (registrations) {
        const roshRegistration = registrations.find(registration => {
            return registration.register && registration.register.description && registration.register.description === 'RoSH';
        });

        if (roshRegistration && roshRegistration.riskColour) {
            switch (roshRegistration.riskColour) {
                case 'Red':
                    roshAlertLevelClass = 'moj-risk-alert--high';
                    roshAlertLevelText = roshRegistration.type.description === 'Very High RoSH' ? 'very high' : 'high';
                    break;
                case 'Amber':
                    roshAlertLevelClass = 'moj-risk-alert--medium';
                    roshAlertLevelText = 'medium';
                    break;
                case 'Green':
                    roshAlertLevelClass = 'moj-risk-alert--low';
                    roshAlertLevelText = 'low';
            }
        }
    }

    return (
        <Fragment>
            { roshAlertLevelClass && (
                <div className={ `qa-alert-rosh moj-risk-alert app-risk-alert app-float-right govuk-!-margin-bottom-2 ${roshAlertLevelClass}` } role="alert">{ roshAlertLevelText } risk of serious harm</div>
            ) }
            { inBreach && (
                <div className="qa-alert-breach moj-risk-alert app-risk-alert app-float-right moj-risk-alert--high moj-risk-alert--small govuk-!-margin-bottom-2" role="alert">breached conditions</div>
            ) }
        </Fragment>
    );
};

OffenderAlerts.propTypes = {
    offenderConvictions: PropTypes.shape({
        convictions: PropTypes.arrayOf(PropTypes.shape({
            active: PropTypes.bool.isRequired,
            inBreach: PropTypes.bool.isRequired
        }))
    }),
    registrations: PropTypes.arrayOf(PropTypes.shape({
        register: PropTypes.shape({
            description: PropTypes.string.isRequired
        }).isRequired,
        type: PropTypes.shape({
            description: PropTypes.string.isRequired
        }).isRequired,
        riskColour: PropTypes.string.isRequired
    }))
};

export default OffenderAlerts;
