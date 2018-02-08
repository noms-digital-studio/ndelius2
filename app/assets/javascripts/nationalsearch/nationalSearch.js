import OffenderSearchPage from './containers/offenderSearchPageContainer';
import FeedbackPage from './components/feedbackPage'
import {Provider} from 'react-redux'
import {createStore, applyMiddleware} from 'redux'
import reducer from './reducers'
import thunkMiddleware from 'redux-thunk'
import { Router, Route, hashHistory} from 'react-router'

const store = createStore(reducer, applyMiddleware(thunkMiddleware))

ReactDOM.render(
    <Provider store={store}>
        <Router history={hashHistory}>
            <Route path="/feedback" component={FeedbackPage}/>
            <Route path="/*" component={OffenderSearchPage}/>
        </Router>
    </Provider>,
    document.getElementById('content')
);
