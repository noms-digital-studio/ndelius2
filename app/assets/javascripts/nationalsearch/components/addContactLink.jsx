import PropTypes from 'prop-types'

const AddContactLink = ({offenderId, addContact}) => (
    <a className="clickable" onClick={() => addContact(offenderId)}>Add contact</a>
)

AddContactLink.propTypes = {
    offenderId: PropTypes.number.isRequired,
    addContact: PropTypes.func.isRequired
}


export default AddContactLink
