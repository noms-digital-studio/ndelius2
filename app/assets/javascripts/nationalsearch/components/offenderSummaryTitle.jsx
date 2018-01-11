import PropTypes from 'prop-types'
import MT from '../containers/markableTextContainer'

const OffenderSummaryTitle = (offenderSummary) => (
    <a className='clickable heading-large no-underline'>
        <span><MT text={offenderSummary.surname}/></span>
        <span>,&nbsp;</span>
        <span><MT text={offenderSummary.firstName}/></span>
        <span>&nbsp;-&nbsp;</span>
        <span><MT text={offenderSummary.dateOfBirth}/></span>
    </a>
)

OffenderSummaryTitle.propTypes = {
    firstName: PropTypes.string.isRequired,
    surname: PropTypes.string.isRequired,
    dateOfBirth: PropTypes.string.isRequired
};


export default OffenderSummaryTitle;