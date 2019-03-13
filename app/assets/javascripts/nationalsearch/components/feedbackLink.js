import React from 'react'
import PropTypes from 'prop-types'

const FeedbackLink = ({ children, tabIndex }) => (
  <a href={window.feedbackLink} title='Feedback: opens in a new window' tabIndex={tabIndex} className='govuk-link govuk-link--no-visited-state'
     target='_blank'>{children}</a>
)

FeedbackLink.propTypes = {
  children: PropTypes.node.isRequired,
  tabIndex: PropTypes.string
}

export default FeedbackLink
