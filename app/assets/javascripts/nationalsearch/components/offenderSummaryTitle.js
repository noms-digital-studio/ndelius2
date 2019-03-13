import React from 'react'
import PropTypes from 'prop-types'
import MT from '../containers/markableTextContainer'
import moment from 'moment'

const OffenderSummaryTitle = ({ showOffenderDetails, offenderId, rankIndex, firstName, surname, dateOfBirth, highlight, tabIndex }) => (
  <a tabIndex={tabIndex} href='javascript:' title={`View offender record for ${surname}, ${firstName}`}
     className='govuk-link govuk-heading-m govuk-link--no-visited-state'
     onClick={() => showOffenderDetails(offenderId, rankIndex, highlight)}>
    <span><MT text={surname} highlight={highlight} highlightFieldName='surname' /></span>
    <span>, </span>
    <span><MT text={firstName} highlight={highlight} highlightFieldName='firstName' allowSingleCharacter /></span>
    <span> - </span>
    <span><MT text={moment(dateOfBirth, 'YYYY-MM-DD').format('DD/MM/YYYY')} isDate={true} highlight={highlight}
              highlightFieldName='dateOfBirth' /></span>
  </a>
)

OffenderSummaryTitle.propTypes = {
  showOffenderDetails: PropTypes.func.isRequired,
  offenderId: PropTypes.number.isRequired,
  rankIndex: PropTypes.number.isRequired,
  firstName: PropTypes.string.isRequired,
  surname: PropTypes.string.isRequired,
  dateOfBirth: PropTypes.string.isRequired,
  highlight: PropTypes.object,
  tabIndex: PropTypes.string
}

export default OffenderSummaryTitle
