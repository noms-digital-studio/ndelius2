import React from 'react'
import PropTypes from 'prop-types'
import AddContactLink from '../containers/addContactLinkContainer'
import { officer, provider } from '../../helpers/offenderManagerHelper'

const RestrictedOffenderSearchSummary = ({ offenderSummary, showOffenderDetails }) => (
  <div className='govuk-grid-row qa-offender-details-row'>
    <div className='govuk-grid-column-one-quarter govuk-!-margin-0 moj-!-text-align-center'>
      <img className='offenderImage' src='assets/images/NoPhoto@2x.png' />
    </div>
    <div role='group' className='govuk-grid-column-three-quarters govuk-!-margin-0'>
      <p>
        <a className='govuk-link govuk-heading-m govuk-link--no-visited-state' tabIndex='1' title='Restricted access'
           onClick={() => showOffenderDetails(offenderSummary.offenderId, offenderSummary.rankIndex, {})}>
          <span>Restricted access</span>
        </a>
      </p>
      <p>
        <span className='govuk-body govuk-!-font-weight-bold'>CRN:&nbsp;</span>
        <span className='govuk-body govuk-!-font-weight-bold govuk-!-margin-right-1'>{offenderSummary.otherIds.crn}</span>
        <br />
        <span id='provider'>
            <span id='provider-label'>Provider:&nbsp;</span>
            <span className='govuk-!-margin-right-1' aria-labelledby='provider-label'>{provider(offenderSummary)}</span>
          </span>
        <br />
        <span id='officer'>
            <span id='officer-label'>Officer name:&nbsp;</span>
            <span className='govuk-!-margin-right-1' aria-labelledby='officer-label'>{officer(offenderSummary)}</span>
          </span>
      </p>
      <p>
        <AddContactLink tabIndex='1' offenderId={offenderSummary.offenderId} rankIndex={offenderSummary.rankIndex} />
      </p>
    </div>
  </div>
)

RestrictedOffenderSearchSummary.propTypes = {
  showOffenderDetails: PropTypes.func.isRequired,
  offenderSummary: PropTypes.shape({
    rankIndex: PropTypes.number.isRequired,
    offenderId: PropTypes.number.isRequired,
    otherIds: PropTypes.shape({
      crn: PropTypes.string.isRequired
    }).isRequired
  }).isRequired
}

export default RestrictedOffenderSearchSummary
