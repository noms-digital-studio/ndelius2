import OffenderSearchSummary from './offenderSearchSummary';
import SearchResultsTitle from '../containers/searchResultsTitleContainer';
import PageSelection from '../containers/pageSelectionContainer';

import PropTypes from "prop-types";

const OffenderSearchResults = ({results}) => (
    <div className='padded mobile-pad'>
        <SearchResultsTitle/>

        <ul>
            {results.map(offenderSummary => (
                <OffenderSearchSummary {...offenderSummary} key={offenderSummary.offenderId}/>
            ))}
        </ul>
        <PageSelection/>
    </div>
);

OffenderSearchResults.propTypes = {
    results: PropTypes.array.isRequired
};


export default OffenderSearchResults;