import PropTypes from 'prop-types'

const AddNewOffenderLink = ({addNewOffender, tabIndex}) => (
    <a tabIndex={tabIndex} href='#' className="clickable white" onClick={addNewOffender}>Add a new offender</a>
);

AddNewOffenderLink.propTypes = {
    addNewOffender: PropTypes.func.isRequired,
    tabIndex: PropTypes.string
}

export default AddNewOffenderLink
