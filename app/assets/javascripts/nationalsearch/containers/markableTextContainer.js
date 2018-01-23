import { connect } from 'react-redux'
import markableText from '../components/markableText'

export default connect(
    state => ({
        searchTerm: state.search.searchTerm
    }),
    () => ({})
)(markableText)