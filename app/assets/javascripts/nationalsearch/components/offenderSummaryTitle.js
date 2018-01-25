import PropTypes from 'prop-types'
import MT from '../containers/markableTextContainer'
import moment from 'moment'

const OffenderSummaryTitle = ({showOffenderDetails, offenderId, firstName, surname, dateOfBirth}) => (
    <a className='clickable heading-large no-underline' onClick={() => showOffenderDetails(offenderId)}>
        <span><MT text={surname}/></span>
        <span>,&nbsp;</span>
        <span><MT text={firstName}/></span>
        <span>&nbsp;-&nbsp;</span>
        <span><MT text={moment(dateOfBirth, 'YYYY-MM-DD').format('DD/MM/YYYY')} isDate={true}/></span>
    </a>
)

OffenderSummaryTitle.propTypes = {
    showOffenderDetails: PropTypes.func.isRequired,
    offenderId: PropTypes.number.isRequired,
    firstName: PropTypes.string.isRequired,
    surname: PropTypes.string.isRequired,
    dateOfBirth: PropTypes.string.isRequired
};


export default OffenderSummaryTitle;