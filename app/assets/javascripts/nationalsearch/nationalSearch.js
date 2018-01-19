import OffenderSearchPage from './components/offenderSearchPage';
import {Provider} from 'react-redux'
import {createStore} from 'redux'
import reducer from './reducers'

const store = createStore(reducer)

ReactDOM.render(
    <Provider store={store}>
        <OffenderSearchPage/>
    </Provider>,
    document.getElementById('content')
);


