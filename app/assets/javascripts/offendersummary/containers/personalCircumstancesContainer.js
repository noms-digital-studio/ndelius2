import { connect } from 'react-redux';
import personalCircumstances from '../components/personalCircumstances';
import { getOffenderPersonalCircumstances, viewOffenderPersonalCircumstances } from '../actions'

const mapStateToProps = state => ({
    circumstances: state.offenderSummary.offenderPersonalCircumstances.circumstances,
    offenderId: state.offenderSummary.offenderDetails.offenderId,
    fetching: state.offenderSummary.offenderPersonalCircumstances.fetching,
    error: state.offenderSummary.offenderPersonalCircumstances.loadError

});
export default connect(
    mapStateToProps,
    {getOffenderPersonalCircumstances, viewOffenderPersonalCircumstances}
)(personalCircumstances);