import React from 'react'
import * as PropTypes from 'prop-types'

const ErrorMessage = ({ message }) => {
  return (
    <div aria-labelledby='error-summary-title' className='govuk-error-summary' role='alert' tabIndex='-1'>
      <h2 className='govuk-error-summary__title' id='error-summary-title'>There is a problem </h2>
      <div className='govuk-error-summary__body'>
        <ul className='govuk-list govuk-error-summary__list'>
          <li>{message}</li>
        </ul>
      </div>
    </div>
  )
}

ErrorMessage.propTypes = {
  message: PropTypes.string.isRequired
}

export default ErrorMessage
