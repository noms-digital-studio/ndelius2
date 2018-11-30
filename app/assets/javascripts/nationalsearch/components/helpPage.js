import React from 'react';
import {Link} from 'react-router-dom';
import GovUkPhaseBanner from './govukPhaseBanner';
import SearchHintBox from './searchHintBox';

const HelpPage = () => {
    return (<div>
        <div id="root">
            <main id="content">
                <GovUkPhaseBanner basicVersion={true}/>
                <div className="key-content">

                    <p className="text-secondary font-xxsmall margin-top medium"><Link to="nationalSearch"
                                                                                       className="back-link">Back to New
                        Search</Link></p>

                    <h1 className="heading-xlarge">Search tips</h1>

                    <ul>
                        <li className="list-bullet">
                            <h2 className="heading-medium no-margin-bottom no-margin-top">Refine the results by selecting ‘Yes’ to ‘Match all terms’</h2>
                            <p className="margin-top">This will search on all of the information you enter.</p>
                        </li>
                    </ul>

                    <ul>
                        <li className="list-bullet">
                            <h2 className="heading-medium no-margin-bottom no-margin-top">Expand the results by selecting ‘No’ to ‘Match all terms’</h2>
                            <p className="margin-top">This will search on any of the information you enter.</p>
                        </li>
                    </ul>

                    <ul>
                        <li className="list-bullet">
                            <h2 className="heading-medium no-margin-bottom no-margin-top">Search by name and date of birth at the same
                                time</h2>
                            <p className="margin-top no-margin-bottom">For example, <strong>"John Smith 23/06/1986"</strong> - the
                                results will be based on all those search terms.</p>

                            <SearchHintBox hint="John Smith 23/06/1986"/>
                        </li>
                    </ul>

                    <ul>
                        <li className="list-bullet">
                            <h2 className="heading-medium no-margin-bottom">Search by town, postcode at the same time as
                                name</h2>
                            <p className="margin-top no-margin-bottom">For example, <strong>"John Smith S1
                                1AB"</strong> or <strong>"John Smith Sheffield"</strong> and so on. The search box can
                                handle multiple search terms simultaneously.</p>

                            <span className="pull-left no-pull-mobile">
                                <SearchHintBox hint="John Smith S1 1AB"/>
                            </span>

                                    <p className="font-medium bold search-hint-middle pull-left no-pull-mobile">Or</p>

                                    <span className="pull-left no-pull-mobile">
                                <SearchHintBox hint="John Smith Sheffield"/>
                            </span>

                            <span className="clearfix"/>
                        </li>
                    </ul>

                    <ul>
                        <li className="list-bullet">
                            <h2 className="heading-medium no-margin-bottom margin-top medium">Include CRN, CRO, PNC,
                                National Insurance and NOMS numbers in your search</h2>
                            <p className="margin-top">Using unique numbers will achieve more accurate results.</p>

                            <SearchHintBox hint="X087946"/>
                        </li>
                    </ul>

                    <p>&nbsp;</p>

                    <Link to="nationalSearch" className="no-underline"><span className="icon prev-arrow"/><span
                        className="font-medium">Previous</span><br/><span
                        className="font-xxsmall underline">New Search</span></Link>

                </div>
            </main>
        </div>
    </div>);
};

export default HelpPage;