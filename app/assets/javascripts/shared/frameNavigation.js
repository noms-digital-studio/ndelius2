import React from 'react'
import * as PropTypes from 'prop-types'

const FrameNavigation = ({ navigate }) => {
  if (navigate.shouldClose) {
    const message = toMessage(navigate)
    // eslint-disable-next-line no-undef
    parent.postMessage(message, '*')
    // eslint-disable-next-line no-undef
    if (top === self) {
      if (window.console && console.log) {
        console.log('No nDelius iframe found but we would have sent:')
        console.log(message)
      }
    }
  }
  return (<span />)
}

const toMessage = ({ action, data = null }) =>
  JSON.stringify({
    action,
    data
  })

FrameNavigation.propTypes = {
  navigate: PropTypes.shape({
    shouldClose: PropTypes.bool.isRequired,
    action: PropTypes.string,
    parameters: PropTypes.object
  })
}

export default FrameNavigation
