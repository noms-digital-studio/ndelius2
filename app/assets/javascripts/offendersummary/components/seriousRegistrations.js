import React, { Fragment } from 'react'
import * as PropTypes from 'prop-types'

const SeriousRegistrations = ({ registrations }) => {

    return (
        <Fragment>
            {hasAnySeriousRegistrations(registrations) &&
              <div className="govuk-warning-text moj-warning-text moj-warning-text--critical govuk-!-margin-bottom-3 qa-offender-serious-registrations">
                <span aria-hidden="true" className="govuk-warning-text__icon">!</span>
                <strong className="govuk-warning-text__text"><span className="govuk-warning-text__assistive">Warning</span>This offender has serious registrations</strong>
              </div>
            }
        </Fragment>
    )
}

const hasAnySeriousRegistrations = registrations => registrations.find(registration => registration.warnUser)

SeriousRegistrations.propTypes = {
    registrations: PropTypes.arrayOf(PropTypes.shape({
      warnUser: PropTypes.bool.isRequired
    }))
}

export default SeriousRegistrations;
