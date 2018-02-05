import { connect } from 'react-redux'
import offenderSearchPage from '../components/offenderSearchPage'

export default connect(
    state => ({
        firstTimeIn: state.search.firstTimeIn
    }),
    () => ({})
)(offenderSearchPage)