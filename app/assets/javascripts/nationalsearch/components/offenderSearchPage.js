import React from 'react'
import {Link} from 'react-router-dom'

import OffenderSearchResults from '../containers/offenderSearchResultsContainer';
import OffenderSearch from '../containers/offenderSearchContainer';
import FrameNavigation from '../containers/frameNavigationContainer';
import AddNewOffenderLink from '../containers/addNewOffenderLinkContainer';
import Suggestions from '../containers/suggestionsContainer';
import GovUkPhaseBanner from './govukPhaseBanner';
import SearchFooter from './searchFooter';
import PropTypes from 'prop-types';

const OffenderSearchPage = ({firstTimeIn, showWelcomeBanner, reloadRecentSearch}) => {

    if (firstTimeIn) {

        if (typeof gtag === 'function') {
            virtualPageLoad('')
        }

        reloadRecentSearch();
    }

    return (
        <div>
            <div id="root">
                <main id="content">
                    <GovUkPhaseBanner/>
                    <div className="govuk-box-highlight blue">
                        <div className="key-content search relative">

                            <div className="search">

                                <h1 className="heading-large margin-bottom medium no-margin-top">Search for an offender</h1>

                                <div className="national-search-add">
                                    <AddNewOffenderLink tabIndex="3"/>
                                </div>

                                <OffenderSearch/>

                                <div className="grid-row">
                                    <div className="column-two-thirds">
                                        <Suggestions/>
                                    </div>
                                </div>

                            </div>

                            <div className="national-search-help">
                                <Link tabIndex="2" to="help" className="bold clickable white">Tips for getting better results</Link>
                            </div>

                        </div>
                    </div>
                    <div>
                        {showWelcomeBanner && <SearchFooter/>}
                        {!showWelcomeBanner && <OffenderSearchResults/>}
                    </div>
                </main>
            </div>
            <FrameNavigation/>
        </div>
    );
};

OffenderSearchPage.propTypes = {
    firstTimeIn: PropTypes.bool.isRequired,
    showWelcomeBanner: PropTypes.bool.isRequired,
    reloadRecentSearch: PropTypes.func.isRequired
};

export default OffenderSearchPage;