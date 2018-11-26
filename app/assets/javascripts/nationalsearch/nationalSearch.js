import React from 'react'
import ReactDOM from 'react-dom'
import OffenderSearchPage from './containers/offenderSearchPageContainer';

import HelpPage from './components/helpPage';
import AnalyticsPage from './containers/analyticsPageContainer'
import SatisfactionPage from './containers/satisfactionPageContainer'
import {Provider} from 'react-redux'
import {createStore, applyMiddleware} from 'redux'
import reducer from './reducers'
import thunkMiddleware from 'redux-thunk'
import {BrowserRouter as Router, Route} from 'react-router-dom'

const store = createStore(reducer, applyMiddleware(thunkMiddleware))

ReactDOM.render(
    <Provider store={store}>
        <Router>
            <div>
                <Route path="/help" component={HelpPage}/>
                <Route path="/analytics" component={AnalyticsPage}/>
                <Route path="/satisfaction" component={SatisfactionPage}/>
                <Route path="/*" component={OffenderSearchPage}/>
            </div>
        </Router>
    </Provider>,
    document.getElementById('content')
);
