import {connect} from 'react-redux'
import offenderAlerts from '../components/offenderAlerts'

const mapStateToProps = state => ({
    offenderConvictions: state.offenderSummary.offenderConvictions
})
export default connect(
    mapStateToProps,
    null
)(offenderAlerts)