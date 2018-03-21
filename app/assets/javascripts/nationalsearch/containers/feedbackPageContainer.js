import { connect } from 'react-redux'
import { addFeedback } from '../actions/navigate'
import feedbackPage from '../components/feedbackPage'

export default connect(
    () => ({}),
    dispatch => ({
        addFeedback: (feedback, router) => dispatch(addFeedback(feedback, router))
    }),
)(feedbackPage)