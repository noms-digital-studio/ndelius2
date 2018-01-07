import OffenderSearchResults from '../containers/offenderSearchResultsContainer.jsx';
import OffenderSearch from '../containers/offenderSearchContainer.jsx';
import GovUkPhaseBanner from './govukPhaseBanner.jsx';

export default () => (
    <div>
        <noscript>You need to enable JavaScript to run this app.</noscript>
        <div id="root">
            <main id="content">
                <GovUkPhaseBanner/>
                <div>
                    <div className="govuk-box-highlight blue">
                        <h1 className="heading-large no-margin-top margin-bottom medium">Search for an offender</h1>
                        <form className="padding-left-right"><OffenderSearch/></form>
                        <p className="bold margin-top medium no-margin-bottom">Can't find who you are looking for? <a className="clickable white">Add a new offender</a></p></div>
                </div>
                <div className="padded mobile-pad">
                    <OffenderSearchResults/>
                </div>
            </main>
        </div>
    </div>);

