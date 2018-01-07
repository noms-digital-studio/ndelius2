import OffenderSearchPage from './components/offenderSearchPage.jsx';
import { Provider } from 'react-redux'
import { createStore } from 'redux'
import reducer from './reducers/searchReducer'

let store = createStore(reducer)

ReactDOM.render(
    <Provider store={store}>
        <OffenderSearchPage/>
    </Provider>,
    document.getElementById('content')
);


