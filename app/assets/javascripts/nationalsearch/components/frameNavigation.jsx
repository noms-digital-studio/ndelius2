import PropTypes from 'prop-types'

const FrameNavigation = ({navigate}) => (
    <div>
        <p>{navigate.shouldClose}</p>
        <p>{navigate.nextPage}</p>
    </div>
);

FrameNavigation.propTypes = {
    navigate: PropTypes.shape({
        shouldClose: PropTypes.bool.isRequired,
        nextPage: PropTypes.string,
        parameters: PropTypes.array,
    })
};

export default FrameNavigation;