import PropTypes from 'prop-types'

const OffenderSearchSummary = ({ mistake, suggestions}) => (
    <li>
        <span>{mistake}</span>
        <ul>
            {suggestions.map((suggestion, index) => (
                <li key={index}>{suggestion}</li>
            ))}
        </ul>
    </li>
)

OffenderSearchSummary.propTypes = {
    mistake: PropTypes.string.isRequired,
    suggestions: PropTypes.array.isRequired
};


export default OffenderSearchSummary;