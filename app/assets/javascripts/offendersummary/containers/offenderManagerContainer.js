import { connect } from 'react-redux';
import offenderManager from '../components/offenderManager';

const mapStateToProps = state => ({
    fetching: state.offenderSummary.offenderDetails.fetching,
    error: state.offenderSummary.offenderDetails.offenderDetailsLoadError
});
export default connect(
    mapStateToProps,
    null
)(offenderManager);