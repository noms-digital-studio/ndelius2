import PropTypes from 'prop-types'

const OffenderSearchSummary = (offenderSummary) => (
    <li>
        <span>{offenderSummary['FIRST_NAME']}</span>
        <span>{offenderSummary['SURNAME']}</span>
        <span>{offenderSummary['CRN']}</span>
    </li>
)

OffenderSearchSummary.propTypes = {
    mistake: PropTypes.string.isRequired,
    suggestions: PropTypes.array.isRequired
};


export default OffenderSearchSummary;