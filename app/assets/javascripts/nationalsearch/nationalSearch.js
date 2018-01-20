import OffenderSearchPage from './components/offenderSearchPage';
import {Provider} from 'react-redux'
import {createStore, applyMiddleware} from 'redux'
import reducer from './reducers'
import thunkMiddleware from 'redux-thunk'

const store = createStore(reducer, applyMiddleware(thunkMiddleware))

ReactDOM.render(
    <Provider store={store}>
        <OffenderSearchPage/>
    </Provider>,
    document.getElementById('content')
);


