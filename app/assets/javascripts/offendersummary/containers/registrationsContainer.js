import { connect } from 'react-redux';
import registrations from '../components/registrations';
import { getOffenderRegistrations } from '../actions'

const mapStateToProps = state => ({
    registrations: state.offenderSummary.offenderRegistrations.registrations,
    fetching: state.offenderSummary.offenderRegistrations.fetching,
    error: state.offenderSummary.offenderRegistrations.loadError

});
export default connect(
    mapStateToProps,
    {getOffenderRegistrations}
)(registrations);