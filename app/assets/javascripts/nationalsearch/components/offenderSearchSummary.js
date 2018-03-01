import PropTypes from 'prop-types'
import OffenderSummaryTitle from '../containers/offenderSummaryTitleContainer'
import MT from '../containers/markableTextContainer'
import {matchesHighlightedField, matchesAnyHighlightedField} from './markableText'
import AddContactLink from '../containers/addContactLinkContainer'

const OffenderSearchSummary = ({offenderSummary, searchTerm}) => (
    <li>
        <div className="offenderDetailsRow clearfix">
            <div className='offenderImageContainer column-one-third'>
                {offenderSummary.oneTimeNomisRef && <img className="offenderImage" src={`offender/oneTimeNomisRef/${encodeURIComponent(offenderSummary.oneTimeNomisRef)}/image`}/>}
                {!offenderSummary.oneTimeNomisRef && <img className="offenderImage" src='assets/images/NoPhoto@2x.png'/>}
            </div>

            <div role='group' className='panel panel-border-narrow offender-summary column-two-thirds'>
                <div className='offenderTextBlock'>
                    <OffenderSummaryTitle {...offenderSummary} tabIndex="1"/>
                    <p className='margin-top'>
                        <span className='bold margin-right'>
                            <span id='crn-label'className='bold'>CRN:&nbsp;</span>
                            <span className='bold margin-right' aria-labelledby="crn-label"><MT text={offenderSummary.otherIds.crn} highlight={offenderSummary.highlight} highlightFieldName='otherIds.crn'/></span>
                        </span>
                        <Risk risk={offenderSummary.offenderProfile.riskColour}/>
                        <CurrentOffender current={offenderSummary.currentDisposal}/>
                        <span className='margin-right'>
                            <span aria-label="Gender"><MT text={offenderSummary.gender} highlight={offenderSummary.highlight} highlightFieldName='gender'/>,&nbsp;</span>
                            <span aria-label="Age">{offenderSummary.age}</span>
                        </span>
                    </p>
                    {matchesHighlightedField(offenderSummary.highlight, 'otherIds.pncNumber') &&
                    <p className='no-margin bottom'>
                        <span id='pncNumber-label'>PNC:&nbsp;</span>
                        <span id='pncNumber' aria-labelledby="pncNumber-label" className='margin-right'><MT text={offenderSummary.otherIds.pncNumber} highlight={offenderSummary.highlight} highlightFieldName='otherIds.pncNumber' /></span>
                    </p>
                    }
                    {matchesHighlightedField(offenderSummary.highlight, 'otherIds.nomsNumber') &&
                    <p className='no-margin bottom'>
                        <span id='nomsNumber-label'>NOMS:&nbsp;</span>
                        <span id='nomsNumber' aria-labelledby='nomsNumber-label' className='margin-right'><MT text={offenderSummary.otherIds.nomsNumber} highlight={offenderSummary.highlight} highlightFieldName='otherIds.nomsNumber'/></span>
                    </p>
                    }
                    {matchesHighlightedField(offenderSummary.highlight, 'otherIds.niNumber') &&
                    <p className='no-margin bottom'>
                        <span id='niNumber-label'>National Insurance Number:&nbsp;</span>
                        <span id='niNumber' aria-labelledby="niNumber-label" className='margin-right'><MT text={offenderSummary.otherIds.niNumber} highlight={offenderSummary.highlight} highlightFieldName='otherIds.niNumber'/></span>
                    </p>
                    }
                    {matchesHighlightedField(offenderSummary.highlight, 'otherIds.croNumber') &&
                    <p className='no-margin bottom'>
                        <span id='croNumber-label'>CRO:&nbsp;</span>
                        <span id='croNumber' aria-labelledby="croNumber-label" className='margin-right'><MT text={offenderSummary.otherIds.croNumber} highlight={offenderSummary.highlight} highlightFieldName='otherIds.croNumber'/></span>
                    </p>
                    }
                    {matchesHighlightedField(offenderSummary.highlight, 'middleNames') &&
                        <MiddleNames middleNames={offenderSummary.middleNames} highlight={offenderSummary.highlight} highlightFieldName='middleNames'/>
                    }
                    {matchesAnyHighlightedField(offenderSummary.highlight, ['offenderAliases.surname', 'offenderAliases.firstName']) &&
                        <AliasList aliases={offenderSummary.aliases} highlight={offenderSummary.highlight} />
                    }
                    {matchesHighlightedField(offenderSummary.highlight, 'previousSurnames') &&
                        <PreviousSurname name={offenderSummary.previousSurname} highlight={offenderSummary.highlight}/>
                    }
                    {matchesAnyHighlightedField(offenderSummary.highlight,
                        [
                            'contactDetails.addresses.buildingName',
                            'contactDetails.addresses.streetName',
                            'contactDetails.addresses.town',
                            'contactDetails.addresses.county',
                            'contactDetails.addresses.postcode']) &&
                        <AddressList addresses={offenderSummary.addresses} highlight={offenderSummary.highlight}/>
                    }
                    <p><AddContactLink tabIndex="1" firstName={offenderSummary.firstName} surname={offenderSummary.surname} offenderId={offenderSummary.offenderId} rankIndex={offenderSummary.rankIndex}/></p>
                </div>
            </div>
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

const MiddleNames = ({middleNames, highlight, highlightFieldName}) => (
    <div className='no-margin bottom'>
        <span className='margin-right'>Middle Names:</span>
        {middleNames.map( (middleName, index) => (
            <span key={index}>
                <span><MT text={middleName} highlight={highlight} highlightFieldName={highlightFieldName} /></span>
                <span>&nbsp;</span>
            </span>
        ))}
    </div>
)

const AliasList = ({aliases, highlight}) => (
    <div className='no-margin bottom'>
        {aliases.map( (alias, index) => (
            <div key={index}>
                <span className='margin-right'>Alias:</span>
                <span><MT text={alias.surname} highlight={highlight} highlightFieldName='offenderAliases.surname'/></span>
                <span>,&nbsp;</span>
                <span><MT text={alias.firstName} highlight={highlight} highlightFieldName='offenderAliases.firstName'/></span>
            </div>
        ))}
        </div>

)

const AddressList = ({addresses, highlight}) => (
    <div className='no-margin bottom'>
        {addresses.map( (address, index) => (
            <div key={index}>
                <span className='margin-right'>Address:</span>
                <Address address={address} highlight={highlight}/>
            </div>
        ))}
        </div>

)

const Address = ({address, highlight}) => {
    const lines = [
        {
            highlightFieldName: 'contactDetails.addresses.buildingName',
            text: address.buildingName
        },
        {
            highlightFieldName: 'contactDetails.addresses.streetName',
            text: firstAddressLine(address.addressNumber, address.streetName)
        },
        {
            highlightFieldName: 'contactDetails.addresses.town',
            text: address.town
        },
        {
            highlightFieldName: 'contactDetails.addresses.county',
            text: address.county
        },
        {
            highlightFieldName: 'contactDetails.addresses.postcode',
            text: address.postcode
        }].filter(line => !!line.text);

    return (
        <span>
            {lines.map( (line, index) => (
                <span className='margin-right' key={index}><MT text={line.text} highlight={highlight} highlightFieldName={line.highlightFieldName}/>{index + 1  < lines.length ? ',' : ''}</span>
            ))}
        </span>
    )
}

const firstAddressLine = (number='', street='') => `${number} ${street}`.trim()

const PreviousSurname = ({name, highlight}) => {
    if (name) {
        return (<div className='no-margin bottom'>
            <span className='margin-right'>Previous surname:</span>
            <span><MT text={name} highlight={highlight} highlightFieldName='previousSurnames'/></span>
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

export { OffenderSearchSummary as default, Address }