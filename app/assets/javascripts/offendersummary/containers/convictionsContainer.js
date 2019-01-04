import { connect } from 'react-redux';
import convictions from '../components/convictions';
import { getOffenderConvictions, showMoreConvictions } from '../actions'

const mapStateToProps = state => ({
    convictions: state.offenderSummary.offenderConvictions.convictions,
    fetching: state.offenderSummary.offenderConvictions.fetching,
    error: state.offenderSummary.offenderConvictions.loadError,
    maxConvictionsVisible: state.offenderSummary.offenderConvictions.maxConvictionsVisible

});
export default connect(
    mapStateToProps,
    {getOffenderConvictions, showMoreConvictions}
)(convictions);