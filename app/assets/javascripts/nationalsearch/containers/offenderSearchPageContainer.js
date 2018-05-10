import { connect } from 'react-redux'
import offenderSearchPage from '../components/offenderSearchPage'
import {search, noSavedSearch, savedSearch} from "../actions/search";
import localforage from "localforage";

export default connect(
    state => ({
        firstTimeIn: state.search.firstTimeIn,
        showWelcomeBanner: state.search.showWelcomeBanner
    }),
    dispatch => ({
        reloadRecentSearch: () => localforage.getItem("nationalSearch").then(data => {

            if (data && data.when && ((Date.now() - data.when) / 1000 / 60 < window.recentSearchMinutes)) {
                dispatch(savedSearch(data.what, data.filter || {}))
                dispatch(search(data.what, probationAreaCodes(data.filter), data.page));
            } else {
                dispatch(noSavedSearch())
            }
        }).catch(err => {
            window.console && console.log(err)
            dispatch(noSavedSearch())
        })
    })
)(offenderSearchPage)

const probationAreaCodes = filter => Object.getOwnPropertyNames(filter || {})
