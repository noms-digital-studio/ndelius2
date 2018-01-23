import { connect } from 'react-redux'
import { addNewOffender } from '../actions/navigate'
import addNewOffenderLink from '../components/addNewOffenderLink'

export default connect(
    () => ({}),
    dispatch => ({
        addNewOffender: () => dispatch(addNewOffender())
    }),
)(addNewOffenderLink)