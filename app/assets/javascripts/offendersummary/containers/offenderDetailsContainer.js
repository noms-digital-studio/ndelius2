import { connect } from 'react-redux';
import offenderDetails from '../components/offenderDetails';

const mapStateToProps = state => ({
    contactDetails: state.offenderSummary.offenderDetails.contactDetails
});
export default connect(
    mapStateToProps,
    null
)(offenderDetails);