import PropTypes from 'prop-types';

const FeedbackLink = ({ children }) => (
    <a href={ window.feedbackLink } className="clickable" target="_blank">{ children }</a>
);

FeedbackLink.propTypes = {
    children: PropTypes.node.isRequired
};

export default FeedbackLink;
