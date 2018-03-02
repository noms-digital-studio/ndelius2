import PropTypes from 'prop-types'
import AddContactLink from '../containers/addContactLinkContainer'

const RestrictedOffenderSearchSummary = ({offenderSummary, showOffenderDetails}) => (
    <li>
        <div className="offenderDetailsRow clearfix">
            <div className='offenderImageContainer'>
                <img className="offenderImage" src='assets/images/NoPhoto@2x.png'/>
            </div>
            <div role='group' className='panel panel-border-narrow offender-summary'>
                <p>
                    <a className='heading-large no-underline offender-summary-title' onClick={() => showOffenderDetails(offenderSummary.offenderId, offenderSummary.rankIndex, {})}>
                        <span>Restricted access</span>
                    </a>
                </p>
                <p>
                    <span className='bold'>CRN:&nbsp;</span>
                    <span className='bold margin-right'>{offenderSummary.otherIds.crn}</span>
                </p>
                <p><AddContactLink offenderId={offenderSummary.offenderId} rankIndex={offenderSummary.rankIndex}/></p>
            </div>
        </div>
    </li>
)

RestrictedOffenderSearchSummary.propTypes = {
    showOffenderDetails: PropTypes.func.isRequired,
    offenderSummary: PropTypes.shape({
        rankIndex: PropTypes.number.isRequired,
        offenderId: PropTypes.number.isRequired,
        otherIds: PropTypes.shape({
            crn: PropTypes.string.isRequired
        }).isRequired
    }).isRequired
};

export default RestrictedOffenderSearchSummary