import { connect } from 'react-redux'
import offenderSearchPage from '../components/offenderSearchPage'
import {search, noSavedSearch} from "../actions/search";
import localforage from "localforage";

export default connect(
    state => ({
        firstTimeIn: state.search.firstTimeIn,
        showWelcomeBanner: state.search.showWelcomeBanner
    }),
    dispatch => ({
        reloadRecentSearch: () => localforage.getItem("nationalSearch").then(data => {

            if (data && data.when && ((Date.now() - data.when) / 1000 / 60 < window.recentSearchMinutes)) {

                dispatch(search(data.what, data.page));
            } else {
                dispatch(noSavedSearch())
            }
        }).catch(err => {
            window.console && console.log(err)
            dispatch(noSavedSearch())
        })
    })
)(offenderSearchPage)
