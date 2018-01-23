import { connect } from 'react-redux'
import { addContact } from '../actions/navigate'
import addContactLink from '../components/addContactLink'

export default connect(
    () => ({}),
    dispatch => ({
        addContact: (offenderId) => dispatch(addContact(offenderId))
    }),
)(addContactLink)