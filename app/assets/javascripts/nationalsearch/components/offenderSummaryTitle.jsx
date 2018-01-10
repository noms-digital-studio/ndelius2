import PropTypes from 'prop-types'

const OffenderSummaryTitle = (offenderSummary) => (
    <a className='clickable heading-large no-underline'>
        <span>{offenderSummary.surname}</span>
        <span>,&nbsp;</span>
        <span>{offenderSummary.firstName}</span>
        <span>&nbsp;-&nbsp;</span>
        <span>{offenderSummary.dateOfBirth}</span>
    </a>
)

OffenderSummaryTitle.propTypes = {
    firstName: PropTypes.string.isRequired,
    surname: PropTypes.string.isRequired,
    dateOfBirth: PropTypes.string.isRequired
};


export default OffenderSummaryTitle;