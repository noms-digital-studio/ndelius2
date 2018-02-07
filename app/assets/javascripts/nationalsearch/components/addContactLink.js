import PropTypes from 'prop-types'

const AddContactLink = ({offenderId, surname, firstName, addContact, tabIndex}) => (
    <span>
        <a tabIndex={tabIndex} href='javascript:' className="clickable" aria-label={`Add contact to ${firstName} ${surname}`} onClick={() => addContact(offenderId)}>Add contact</a>
    </span>
)

AddContactLink.propTypes = {
    offenderId: PropTypes.number.isRequired,
    addContact: PropTypes.func.isRequired,
    tabIndex: PropTypes.string,
    surname: PropTypes.string,
    firstName: PropTypes.string
}


export default AddContactLink
