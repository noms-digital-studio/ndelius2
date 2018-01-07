import OffenderSearchSummary from './offenderSearchSummary.jsx';
import PropTypes from "prop-types";

const OffenderSearchResults = ({results}) => (
    <ul>
        {results.map((offenderSummary, index) => (
            <OffenderSearchSummary {...offenderSummary} key={index}/>
        ))}
    </ul>
);

OffenderSearchResults.propTypes = {
    results: PropTypes.array.isRequired
};


export default OffenderSearchResults;