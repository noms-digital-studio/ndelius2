import { connect } from 'react-redux';
import offenderManager from '../components/offenderManager';

const mapStateToProps = state => ({
    fetching: state.offenderSummary.offenderDetails.fetching,
    error: state.offenderSummary.offenderDetails.offenderDetailsLoadError,
    offenderManager: state.offenderSummary.offenderDetails.offenderManagers
        && activeOffenderManager(state.offenderSummary.offenderDetails.offenderManagers)
});

const activeOffenderManager = offenderManagers => offenderManagers.find(manager => manager.active)
export default connect(
    mapStateToProps,
    null
)(offenderManager);