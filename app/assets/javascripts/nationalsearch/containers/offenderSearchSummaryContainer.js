import { connect } from 'react-redux'
import offenderSearchSummary from '../components/offenderSearchSummary'

export default connect(
    state => ({
        searchTerm: state.search.searchTerm
    }),
    () => ({})
)(offenderSearchSummary)