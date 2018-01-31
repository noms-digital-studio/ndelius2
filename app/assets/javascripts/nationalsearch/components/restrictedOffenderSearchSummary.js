import PropTypes from 'prop-types'
import MT from '../containers/markableTextContainer'
import AddContactLink from '../containers/addContactLinkContainer'

const RestrictedOffenderSearchSummary = ({offenderSummary, showOffenderDetails}) => (
    <li>
        <div className='panel panel-border-narrow offender-summary'>
            <a className='clickable heading-large no-underline' onClick={() => showOffenderDetails(offenderSummary.offenderId)}>
                <span>Restricted access</span>
            </a>
            <p className='no-margin bottom'>
                <span className='bold'>CRN:&nbsp;</span>
                <span className='bold margin-right'><MT text={offenderSummary.otherIds.crn}/></span>
            </p>
            <p><AddContactLink offenderId={offenderSummary.offenderId}/></p>
        </div>
    </li>
)

RestrictedOffenderSearchSummary.propTypes = {
    showOffenderDetails: PropTypes.func.isRequired,
    offenderSummary: PropTypes.shape({
        offenderId: PropTypes.number.isRequired,
        otherIds: PropTypes.shape({
            crn: PropTypes.string.isRequired
        }).isRequired
    }).isRequired
};

export default RestrictedOffenderSearchSummary