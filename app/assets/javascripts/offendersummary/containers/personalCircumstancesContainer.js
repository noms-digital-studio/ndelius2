import { connect } from 'react-redux';
import personalCircumstances from '../components/personalCircumstances';
import { getOffenderPersonalCircumstances } from '../actions'

const mapStateToProps = state => ({
    circumstances: state.offenderSummary.offenderPersonalCircumstances.circumstances,
    fetching: state.offenderSummary.offenderPersonalCircumstances.fetching,
    error: state.offenderSummary.offenderPersonalCircumstances.loadError

});
export default connect(
    mapStateToProps,
    {getOffenderPersonalCircumstances}
)(personalCircumstances);