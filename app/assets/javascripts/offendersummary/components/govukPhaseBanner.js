import React from 'react'

const GovUkPhaseBanner = () => {
    return (
        <div className="govuk-phase-banner">
            <p className="govuk-phase-banner__content">
                <strong className="govuk-tag govuk-phase-banner__content__tag">ALPHA</strong>
                <span className="govuk-phase-banner__text">This is a new service – your <a href={ window.feedbackLink } title="Feedback: opens in a new window" className="govuk-link govuk-link--no-visited-state" target="_blank" rel="noopener">feedback</a> will help us to improve it.</span>
            </p>
        </div>
    )
}

export default GovUkPhaseBanner;