import React from 'react'
import PropTypes from 'prop-types'
import AddContactLink from '../containers/addContactLinkContainer'
import { officer, provider } from '../../helpers/offenderManagerHelper'

const RestrictedOffenderSearchSummary = ({ offenderSummary, showOffenderDetails }) => (
  <li>
    <div className='offenderDetailsRow clearfix'>
      <div className='offenderImageContainer'>
        <img className='offenderImage' src='assets/images/NoPhoto@2x.png' />
      </div>
      <div role='group' className='panel panel-border-narrow offender-summary'>
        <p>
          <a className='heading-large no-underline offender-summary-title' tabIndex='1' title='Restricted access'
             onClick={() => showOffenderDetails(offenderSummary.offenderId, offenderSummary.rankIndex, {})}>
            <span>Restricted access</span>
          </a>
        </p>
        <p>
          <span className='bold'>CRN:&nbsp;</span>
          <span className='bold margin-right'>{offenderSummary.otherIds.crn}</span>
          <br />
          <span id='provider'>
            <span id='provider-label'>Provider:&nbsp;</span>
            <span className='margin-right' aria-labelledby='provider-label'>{provider(offenderSummary)}</span>
          </span>
          <br />
          <span id='officer'>
            <span id='officer-label'>Officer name:&nbsp;</span>
            <span className='margin-right' aria-labelledby='officer-label'>{officer(offenderSummary)}</span>
          </span>
        </p>
        <p>
          <AddContactLink tabIndex='1' offenderId={offenderSummary.offenderId}
                          rankIndex={offenderSummary.rankIndex} />
        </p>
      </div>
    </div>
  </li>
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
