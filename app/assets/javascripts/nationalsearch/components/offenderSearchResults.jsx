import OffenderSearchSummary from './offenderSearchSummary.jsx';
import SearchResultsTitle from '../containers/searchResultsTitleContainer.jsx';

import PropTypes from "prop-types";

const OffenderSearchResults = ({results}) => (
    <div className='padded mobile-pad'>
        <SearchResultsTitle/>
        <ul>
            {results.map((offenderSummary) => (
                <OffenderSearchSummary {...offenderSummary} key={offenderSummary.offenderId}/>
            ))}
        </ul>
    </div>
);

OffenderSearchResults.propTypes = {
    results: PropTypes.array.isRequired
};


export default OffenderSearchResults;