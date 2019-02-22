import { connect } from 'react-redux'
import { legacySearch } from '../actions/navigate'
import legacySearchLink from '../components/legacySearchLink'

export default connect(() => ({}), dispatch => ({ legacySearch: () => dispatch(legacySearch()) }))(legacySearchLink)
