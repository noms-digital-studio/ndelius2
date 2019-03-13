import React from 'react'
import FeedbackLink from './feedbackLink'
import LegacySearchLink from '../containers/legacySearchLinkContainer'

const GovUkPhaseBanner = () => {
  return (
    <div className='govuk-phase-banner'>
      <p className='govuk-phase-banner__content'>
        <strong className='govuk-tag govuk-phase-banner__content__tag'>BETA</strong>
        <span className='govuk-phase-banner__text'>
          This is a new service – your <FeedbackLink tabIndex='1'>feedback</FeedbackLink> will help us to improve it.
          Access the <LegacySearchLink tabIndex='1'>previous search</LegacySearchLink> here.
        </span>
      </p>
    </div>
  )
}

export default GovUkPhaseBanner
