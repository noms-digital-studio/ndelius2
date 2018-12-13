import { connect } from 'react-redux';
import offenderDetails from '../components/offenderDetails';

const mapStateToProps = state => ({
    offenderDetails: state.offenderSummary.offenderDetails
});
export default connect(
    mapStateToProps,
    null
)(offenderDetails);