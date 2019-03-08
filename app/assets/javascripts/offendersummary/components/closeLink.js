import React, { Component } from 'react'
import * as PropTypes from 'prop-types'

class CloseLink extends Component {
  render () {
    const { offenderSummaryClose } = this.props

    return (
      <p className='govuk-body govuk-!-margin-top-4'>
        <a className='govuk-link govuk-link--no-visited-state' href='javascript:void(0);' title='Close'
           onClick={() => offenderSummaryClose()}>Close</a>
      </p>
    )
  }
}

CloseLink.propTypes = {
  offenderSummaryClose: PropTypes.func
}

export default CloseLink
