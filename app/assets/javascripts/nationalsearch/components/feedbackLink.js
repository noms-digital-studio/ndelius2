import React from 'react'
import PropTypes from 'prop-types';

const FeedbackLink = ({ children, tabIndex }) => (
    <a href={ window.feedbackLink } tabIndex={tabIndex} className="clickable" target="_blank">{ children }</a>
);

FeedbackLink.propTypes = {
    children: PropTypes.node.isRequired,
    tabIndex: PropTypes.string
};

export default FeedbackLink;
