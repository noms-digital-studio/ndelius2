import React from 'react'
import ReactDOM from 'react-dom'
import { Provider } from 'react-redux'
import { applyMiddleware, createStore } from 'redux'
import thunkMiddleware from 'redux-thunk'
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'

import OffenderSearchPage from './nationalsearch/containers/offenderSearchPageContainer'
import HelpPage from './nationalsearch/components/helpPage'
import AnalyticsPage from './nationalsearch/containers/analyticsPageContainer'
import SatisfactionPage from './nationalsearch/containers/satisfactionPageContainer'

import OffenderSummaryPage from './offendersummary/containers/offenderSummaryPageContainer'
import FeatureSwitchPage from './feature/featureSwitchPage'

import reducer from './reducers'

const store = createStore(reducer, applyMiddleware(thunkMiddleware))

ReactDOM.render(
  <Provider store={ store }>
    <Router>
      <Switch>
        <Route path="(.*)/help" component={ HelpPage }/>
        <Route path="(.*)/analytics" component={ AnalyticsPage }/>
        <Route path="(.*)/satisfaction" component={ SatisfactionPage }/>
        <Route path="(.*)/offenderSummary" component={ OffenderSummaryPage }/>
        <Route path="(.*)/nationalSearch" component={ OffenderSearchPage }/>
        <Route path="(.*)/features" component={ () => <FeatureSwitchPage/> }/>
      </Switch>
    </Router>
  </Provider>,
  document.getElementById('content')
)
