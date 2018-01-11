import PropTypes from 'prop-types'

const AddNewOffenderLink = ({addNewOffender}) => (
    <a className="clickable white" onClick={addNewOffender}>Add a new offender</a>
);

AddNewOffenderLink.propTypes = {
    addNewOffender: PropTypes.func.isRequired
}

export default AddNewOffenderLink
