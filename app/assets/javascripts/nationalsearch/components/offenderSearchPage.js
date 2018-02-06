import OffenderSearchResults from '../containers/offenderSearchResultsContainer';
import OffenderSearch from '../containers/offenderSearchContainer';
import FrameNavigation from '../containers/frameNavigationContainer';
import AddNewOffenderLink from '../containers/addNewOffenderLinkContainer';
import Suggestions from '../containers/suggestionsContainer';
import GovUkPhaseBanner from './govukPhaseBanner';
import SearchFooter from "./searchFooter";
import PropTypes from "prop-types";

const OffenderSearchPage = ({firstTimeIn}) => (
    <div>
        <noscript>You need to enable JavaScript to run this app.</noscript>
        <div id="root">
            <main id="content">
                <GovUkPhaseBanner/>
                <div>
                    <div className="govuk-box-highlight blue">
                        <h1 className="heading-large no-margin-top margin-bottom medium">Search for an offender</h1>
                        <OffenderSearch/>
                        <Suggestions/>
                        <p className="bold margin-top medium no-margin-bottom">Can't find who you are looking for? <AddNewOffenderLink tabIndex="1"/></p></div>
                </div>
                <div className="padded mobile-pad">
                    {firstTimeIn && <SearchFooter/>}
                    {!firstTimeIn && <OffenderSearchResults/>}
                </div>
            </main>
        </div>
        <FrameNavigation/>
    </div>);

OffenderSearchPage.propTypes = {
    firstTimeIn: PropTypes.bool.isRequired
};

export default OffenderSearchPage;