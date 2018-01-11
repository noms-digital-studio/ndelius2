import PropTypes from 'prop-types'

const FrameNavigation = ({navigate}) => (
    <span/>
);

FrameNavigation.propTypes = {
    navigate: PropTypes.shape({
        shouldClose: PropTypes.bool.isRequired,
        nextPage: PropTypes.string,
        parameters: PropTypes.array,
    })
};

export default FrameNavigation;