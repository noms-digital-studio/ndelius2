import React from 'react'
import PropTypes from 'prop-types'

const FrameNavigation = ({navigate}) => {
    if (navigate.shouldClose) {
        const message = toMessage(navigate)
        parent.postMessage(message, '*');
        if ( top === self ) {
            if (window.console && console.log) {
                console.log('No nDelius iframe found but we would have sent:')
                console.log(message)
            }
        }
    }
    return (<span/>)
};

const toMessage = ({action, data = null}) =>
    JSON.stringify({
        action,
        data
    })

FrameNavigation.propTypes = {
    navigate: PropTypes.shape({
        shouldClose: PropTypes.bool.isRequired,
        action: PropTypes.string,
        parameters: PropTypes.object,
    })
};

export default FrameNavigation;