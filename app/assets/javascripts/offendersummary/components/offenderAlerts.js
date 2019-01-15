import React, { Fragment } from 'react';
import * as PropTypes from 'prop-types'

const OffenderAlerts = ({offenderConvictions }) => {

    let inBreach = false;

    if (offenderConvictions && offenderConvictions.convictions) {
        offenderConvictions.convictions.forEach((conviction) => {
            if (conviction.inBreach) {
                inBreach = true;
            }
        });
    }

    return(
        <Fragment>
            { inBreach && (
                <div className="qa-alert-breach moj-risk-alert moj-risk-alert--high moj-risk-alert--small govuk-!-margin-bottom-2" role="alert">breached conditions</div>
            )}
        </Fragment>
    );
};

OffenderAlerts.propTypes = {
    offenderConvictions: PropTypes.shape({
        convictions: PropTypes.arrayOf(PropTypes.shape({
            inBreach: PropTypes.bool.isRequired
        }))
    })
};

export default OffenderAlerts;
