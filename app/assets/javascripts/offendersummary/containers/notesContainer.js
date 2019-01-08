import { connect } from 'react-redux';
import notes from '../components/notes';

const mapStateToProps = state => ({
    notes: state.offenderSummary.offenderDetails && state.offenderSummary.offenderDetails.offenderProfile && state.offenderSummary.offenderDetails.offenderProfile.offenderDetails || '',
    fetching: state.offenderSummary.offenderDetails.fetching,
    error: state.offenderSummary.offenderDetails.offenderDetailsLoadError
});
export default connect(
    mapStateToProps,
    null
)(notes);