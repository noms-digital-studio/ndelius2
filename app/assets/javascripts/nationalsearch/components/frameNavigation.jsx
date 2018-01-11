import PropTypes from 'prop-types'

const FrameNavigation = ({navigate}) => {
    if (navigate.shouldClose) {
        const message = toMessage(navigate)
        parent.postMessage(message, '*');
        if ( top === self ) {
            if (console && console.log) {
                console.log('No nDelius iframe found but we would have sent:')
                console.log(message)
            }
        }
    }
    return (<span/>)
};

function toMessage({nextPage, parameters = {}}) {
    return JSON.stringify({
        navigate: {
            nextPage,
            parameters
        }
    })
}

FrameNavigation.propTypes = {
    navigate: PropTypes.shape({
        shouldClose: PropTypes.bool.isRequired,
        nextPage: PropTypes.string,
        parameters: PropTypes.object,
    })
};

export default FrameNavigation;