import { connect } from 'react-redux'
import frameNavigation from '../components/frameNavigation'

export default connect(
    state => ({
        navigate: state.navigate
    }),
    () => ({})
)(frameNavigation)