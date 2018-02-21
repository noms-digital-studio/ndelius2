import PropTypes from 'prop-types'
import OffenderSummaryTitle from '../containers/offenderSummaryTitleContainer'
import MT from '../containers/markableTextContainer'
import AddContactLink from '../containers/addContactLinkContainer'
import {matches} from '../../helpers/searchMatcher'

const OffenderSearchSummary = ({offenderSummary, searchTerm}) => (
    <li>
        <div role='group' className='panel panel-border-narrow offender-summary'>
            <OffenderSummaryTitle {...offenderSummary} tabIndex="1"/>
            <p className='no-margin bottom'>
                <span id='crn-label'className='bold'>CRN:&nbsp;</span>
                <span className='bold margin-right' aria-labelledby="crn-label"><MT text={offenderSummary.otherIds.crn}/></span>
                {shouldDisplay(searchTerm, offenderSummary.otherIds.pncNumber) &&
                    <span>
                        <span id='pncNumber-label' className='bold'>PNC:&nbsp;</span>
                        <span id='pncNumber' aria-labelledby="pncNumber-label" className='bold margin-right'><MT text={offenderSummary.otherIds.pncNumber}/></span>
                    </span>
                }
                {shouldDisplay(searchTerm, offenderSummary.otherIds.nomsNumber) &&
                    <span>
                        <span id='nomsNumber-label' className='bold'>NOMS:&nbsp;</span>
                        <span id='nomsNumber' aria-labelledby='nomsNumber-label' className='bold margin-right'><MT text={offenderSummary.otherIds.nomsNumber}/></span>
                    </span>
                }
                {shouldDisplay(searchTerm, offenderSummary.otherIds.niNumber) &&
                    <span>
                        <span id='niNumber-label' className='bold'>NI Number:&nbsp;</span>
                        <span id='niNumber' aria-labelledby="niNumber-label" className='bold margin-right'><MT text={offenderSummary.otherIds.niNumber}/></span>
                    </span>
                }
                {shouldDisplay(searchTerm, offenderSummary.otherIds.croNumber) &&
                    <span>
                        <span id='croNumber-label' className='bold'>CRO:&nbsp;</span>
                        <span id='croNumber' aria-labelledby="croNumber-label" className='bold margin-right'><MT text={offenderSummary.otherIds.croNumber}/></span>
                    </span>
                }
                <Risk risk={offenderSummary.offenderProfile.riskColour}/>
                <CurrentOffender current={offenderSummary.currentDisposal}/>
                <span className='margin-right'>
                    <span aria-label="Gender"><MT text={offenderSummary.gender}/>,&nbsp;</span>
                    <span aria-label="Age">{offenderSummary.age}</span>
                </span>
            </p>
            {shouldDisplay(searchTerm, offenderSummary.middleNames) &&
                <MiddleNames middleNames={offenderSummary.middleNames}/>
            }
            <AliasList aliases={offenderSummary.aliases}/>
            <PreviousSurname name={offenderSummary.previousSurname}/>
            <AddressList addresses={offenderSummary.addresses}/>
            <p><AddContactLink tabIndex="1" firstName={offenderSummary.firstName} surname={offenderSummary.surname} offenderId={offenderSummary.offenderId} rankIndex={offenderSummary.rankIndex}/></p>
        </div>
    </li>
)

OffenderSearchSummary.propTypes = {
    offenderSummary: PropTypes.shape({
        firstName: PropTypes.string.isRequired,
        surname: PropTypes.string.isRequired,
        middleNames: PropTypes.arrayOf(
            PropTypes.string.isRequired
        ),
        dateOfBirth: PropTypes.string.isRequired,
        otherIds: PropTypes.shape({
            crn: PropTypes.string.isRequired,
            pncNumber: PropTypes.string,
            nomsNumber: PropTypes.string,
            croNumber: PropTypes.string
        }).isRequired,
        risk: PropTypes.string,
        currentDisposal: PropTypes.string.isRequired,
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
        }).isRequired,
    searchTerm: PropTypes.string.isRequired
};

const Risk = ({risk}) => {
    if (risk) {
        return (<span aria-label={`risk alert colour`}  className='margin-right'>Risk <span className={`risk-icon ${mapRiskColor(risk)}`}/><span className='visually-hidden'>{risk}</span>&nbsp;|</span>)
    }
    return (<span/>)
}

const CurrentOffender = ({current}) => {
    if (current && current === '1') {
        return (<span className='margin-right'>Current offender&nbsp;|</span>)
    }
    return (<span/>)
}

const MiddleNames = ({middleNames}) => (
    <div className='no-margin bottom'>
        <span className='margin-right'>Middle Names:</span>
        {middleNames.map( (middleName, index) => (
            <span key={index}>
                <span><MT text={middleName}/></span>
                <span>&nbsp;</span>
            </span>
        ))}
    </div>
)

const AliasList = ({aliases}) => (
    <div className='no-margin bottom'>
        {aliases.map( (alias, index) => (
            <div key={index}>
                <span className='margin-right'>Alias:</span>
                <span><MT text={alias.surname}/></span>
                <span>,&nbsp;</span>
                <span><MT text={alias.firstName}/></span>
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
                <span className='margin-right' key={index}><MT text={line}/>{index + 1  < lines.length ? ',' : ''}</span>
            ))}
        </span>
    )
}

const firstAddressLine = (number='', street='') => `${number} ${street}`.trim()

const PreviousSurname = ({name}) => {
    if (name) {
        return (<div className='no-margin bottom'>
            <span className='margin-right'>Previous surname:</span>
            <span><MT text={name}/></span>
        </div>)
    }
    return (<span/>)
}

const mapRiskColor = (risk = '') => {
    switch (risk.toLowerCase()) {
        case 'red':
            return 'risk-red'
        case 'amber':
            return 'risk-amber'
        case 'green':
            return 'risk-green'
    }
    return ''
}

const shouldDisplay = (searchTerm, text = '') => matches(text, searchTerm)

export { OffenderSearchSummary as default, Address }