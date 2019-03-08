import React, { Component } from 'react'
import * as PropTypes from 'prop-types'

class GovUkPhaseBanner extends Component {
  render () {
    const { offenderId, offenderSummaryViewPrevious } = this.props

    return (
      <div className='govuk-phase-banner'>
        <p className='govuk-phase-banner__content'>
          <strong className='govuk-tag govuk-phase-banner__content__tag'>ALPHA</strong>
          <span className='govuk-phase-banner__text'>This is a new service – your
            <a href={window.feedbackLink} title='Feedback: opens in a new window'
               className='govuk-link govuk-link--no-visited-state' target='_blank' rel='noopener'>feedback</a> will help us to improve it.
            {' '}
            <a href="javascript:void(0);" title="View the previous version of the offender summary page here"
               className='govuk-link govuk-link--no-visited-state qa-view-previous'
               onClick={() => offenderSummaryViewPrevious(offenderId)}>View the previous version of the offender summary page here</a>.
          </span>
        </p>
      </div>
    )
  }
}

GovUkPhaseBanner.propTypes = {
  offenderId: PropTypes.number.isRequired,
  offenderSummaryViewPrevious: PropTypes.func
}

export default GovUkPhaseBanner
