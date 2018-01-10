import PropTypes from 'prop-types'
import OffenderSummaryTitle from './offenderSummaryTitle.jsx'

const OffenderSearchSummary = (offenderSummary) => (
    <li>
        <div className='panel panel-border-narrow'>
            <OffenderSummaryTitle {...offenderSummary}/>
            <p className='no-margin bottom'>
                <span className='bold'>CRN:&nbsp;</span>
                <span className='bold margin-right'>{offenderSummary.crn}</span>
                <Risk risk={offenderSummary.risk}/>
                <CurrentOffender current={offenderSummary.currentOffender}/>
                <span className='margin-right'>
                    <span>{offenderSummary.gender},&nbsp;</span>
                    <span>{offenderSummary.age}</span>
                </span>
            </p>
            <AliasList aliases={offenderSummary.aliases}/>
            <PreviousSurname name={offenderSummary.previousSurname}/>
            <AddressList addresses={offenderSummary.addresses}/>
            <p><a className="clickable">Add contact</a></p>
        </div>
    </li>
)

OffenderSearchSummary.propTypes = {
    firstName: PropTypes.string.isRequired,
    surname: PropTypes.string.isRequired,
    dateOfBirth: PropTypes.string.isRequired,
    crn: PropTypes.string.isRequired,
    risk: PropTypes.string,
    currentOffender: PropTypes.bool.isRequired,
    gender: PropTypes.string.isRequired,
    age: PropTypes.number.isRequired,
    aliases: PropTypes.arrayOf(
        PropTypes.shape({
            surname: PropTypes.string.isRequired,
            firstName: PropTypes.string.isRequired
        })
    ).isRequired,
    addresses: PropTypes.arrayOf(
        PropTypes.shape({
            buildingName: PropTypes.string,
            addressNumber: PropTypes.string,
            streetName: PropTypes.string,
            town: PropTypes.string,
            county: PropTypes.string,
            postcode: PropTypes.string
        })
    ).isRequired,
    previousSurname: PropTypes.string
};

const Risk = ({risk}) => {
    if (risk) {
        return (<span className='margin-right'>Risk <span className={`risk-icon ${mapRiskColor(risk)}`}/>&nbsp;|</span>)
    }
    return (<span/>)
}

const CurrentOffender = ({current}) => {
    if (current) {
        return (<span className='margin-right'>Current offender&nbsp;|</span>)
    }
    return (<span/>)
}

const AliasList = ({aliases}) => (
    <div className='no-margin bottom'>
        {aliases.map( (alias, index) => (
            <div key={index}>
                <span className='margin-right'>Alias:</span>
                <span>{alias.surname}</span>
                <span>,&nbsp;</span>
                <span>{alias.firstName}</span>
            </div>
        ))}
        </div>

)

const AddressList = ({addresses}) => (
    <div className='no-margin bottom'>
        {addresses.map( (address, index) => (
            <div key={index}>
                <span className='margin-right'>Address:</span>
                <Address address={address}/>
            </div>
        ))}
        </div>

)

const Address = ({address}) => {
    const lines = [
        address.buildingName,
        firstAddressLine(address.addressNumber, address.streetName),
        address.town,
        address.county,
        address.postcode].filter(line => !!line);

    return (
        <span>
            {lines.map( (line, index) => (
                <span className='margin-right' key={index}>{line}{index + 1  < lines.length ? ',' : ''}</span>
            ))}
        </span>
    )
}

function firstAddressLine(number, street) {
    return `${number} ${street}`;
}
const PreviousSurname = ({name}) => {
    if (name) {
        return (<div className='no-margin bottom'>
            <span className='margin-right'>Previous surname:</span>
            <span>{name}</span>
        </div>)
    }
    return (<span/>)
}

function mapRiskColor(risk) {
    switch (risk) {
        case 'Red':
            return 'risk-red';
        case 'Amber':
            return 'risk-amber';
        case 'Green':
            return 'risk-green';
    }
    return '';

}

export default OffenderSearchSummary;