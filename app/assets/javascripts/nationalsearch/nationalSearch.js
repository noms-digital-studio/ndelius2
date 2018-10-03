import OffenderSearchPage from './containers/offenderSearchPageContainer';

import HelpPage from './components/helpPage';
import AnalyticsPage from './containers/analyticsPageContainer'
import SatisfactionPage from './containers/satisfactionPageContainer'
import {Provider} from 'react-redux'
import {createStore, applyMiddleware} from 'redux'
import reducer from './reducers'
import thunkMiddleware from 'redux-thunk'
import { Router, Route, hashHistory} from 'react-router'

const store = createStore(reducer, applyMiddleware(thunkMiddleware))

ReactDOM.render(
    <Provider store={store}>
        <Router history={hashHistory}>
            <Route path="/help" component={HelpPage}/>
            <Route path="/analytics" component={AnalyticsPage}/>
            <Route path="/satisfaction" component={SatisfactionPage}/>
            <Route path="/*" component={OffenderSearchPage}/>
        </Router>
    </Provider>,
    document.getElementById('content')
);
